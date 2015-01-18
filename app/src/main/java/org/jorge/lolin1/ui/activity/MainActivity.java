/*
 * This file is part of LoLin1.
 *
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by Jorge Antonio Diaz-Benito Soriano.
 */

package org.jorge.lolin1.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.chat.FriendManager;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.datamodel.LoLin1Account;
import org.jorge.lolin1.datamodel.Realm;
import org.jorge.lolin1.io.database.SQLiteDAO;
import org.jorge.lolin1.service.ChatIntentService;
import org.jorge.lolin1.ui.adapter.NavigationDrawerAdapter;
import org.jorge.lolin1.ui.fragment.ArticleReaderFragment;
import org.jorge.lolin1.ui.fragment.CommunityListFragment;
import org.jorge.lolin1.ui.fragment.NavigationDrawerFragment;
import org.jorge.lolin1.ui.fragment.NewsListFragment;
import org.jorge.lolin1.ui.fragment.SchoolListFragment;
import org.jorge.lolin1.util.Interface;
import org.jorge.lolin1.util.Utils;

import java.util.Stack;
import java.util.concurrent.Executors;

public class MainActivity extends ActionBarActivity implements Interface
        .IOnFeedArticleClickedListener, NavigationDrawerAdapter.NavigationDrawerCallbacks {

    public static final String EXTRA_KEY_LOLIN1_ACCOUNT = "EXTRA_KEY_LOLIN1_ACCOUNT";
    private Context mContext;
    private Fragment[] mContentFragments;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Stack<Integer> mNavigatedIndexesStack;
    private LoLin1Account mAccount;
    private BroadcastReceiver mChatBroadcastReceiver;
    private Boolean mAlreadyInited = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigatedIndexesStack = new Stack<>();
        mNavigatedIndexesStack.push(0);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(Boolean.TRUE);

        mAccount = getIntent().getParcelableExtra(EXTRA_KEY_LOLIN1_ACCOUNT);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer_fragment);
        setupNavigationDrawer(toolbar, mAccount);

        final SlidingUpPanelLayout mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id
                .sliding_layout);
        mSlidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                //Unused
            }

            @Override
            public void onPanelCollapsed(View view) {
                MainActivity.this.mNavigationDrawerFragment.unlockDrawer();
            }

            @Override
            public void onPanelExpanded(View view) {
                MainActivity.this.mNavigationDrawerFragment.lockDrawerClosed();
            }

            @Override
            public void onPanelAnchored(View view) {
                MainActivity.this.mNavigationDrawerFragment.lockDrawerClosed();
            }

            @Override
            public void onPanelHidden(View view) {
                MainActivity.this.mNavigationDrawerFragment.unlockDrawer();
            }
        });

        mContext = LoLin1Application.getInstance().getApplicationContext();

        initChat();

        if (mContentFragments == null)
            showInitialFragment();
    }

    private void initChat() {
        final View thisView = findViewById(android.R.id.content);
        registerLocalBroadcastReceiver();
        if (mAlreadyInited) {
            if (!Utils.isInternetReachable()) {
                thisView.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO showViewNoConnection();
                    }
                });
            } else {
                if (!ChatIntentService.isLoggedIn()) {
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            //TODO showViewLoading();
                        }
                    });
                    runChat();
                } else {
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            //TODO showViewConnected();
                        }
                    });
                }
            }
            return;
        }
        final Runnable viewRunnable;
        if (!Utils.isInternetReachable()) {
            viewRunnable = new Runnable() {
                @Override
                public void run() {
                    //TODO showViewNoConnection();
                }
            };
        } else {
            viewRunnable = new Runnable() {
                @Override
                public void run() {
                    //TODO showViewLoading();
                }
            };
            if (!ChatIntentService.isLoggedIn()) {
                runChat();
            }
        }
        thisView.post(viewRunnable);
    }

    private void showInitialFragment() {
        if (mContentFragments == null) {
            mContentFragments = new Fragment[4];
        }
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content_fragment_container, findNewsListFragment())
                .commitAllowingStateLoss();
    }

    private Fragment findNewsListFragment() {
        if (mContentFragments[0] == null)
            mContentFragments[0] = NewsListFragment.newInstance(mContext, Realm
                    .getInstanceByRealmId(mAccount.getRealmEnum()));
        return mContentFragments[0];
    }

    private Fragment findCommunityListFragment() {
        if (mContentFragments[1] == null)
            mContentFragments[1] = CommunityListFragment.newInstance(mContext);
        return mContentFragments[1];
    }

    private Fragment findSchoolListFragment() {
        if (mContentFragments[2] == null)
            mContentFragments[2] = SchoolListFragment.newInstance(mContext);
        return mContentFragments[2];
    }

    private Fragment findChatFragment() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (getResources().getConfiguration().orientation != newConfig.orientation) {
            mNavigationDrawerFragment.selectItem(mNavigatedIndexesStack.peek());
            mNavigatedIndexesStack.pop(); //Remove the element that was added because of the
            // call to selectItem
        }

        super.onConfigurationChanged(newConfig);
    }

    /**
     * Launches an article reader.
     *
     * @param article The article information.
     * @param c       The class of the article list, so that the errorResId can be deducted
     *                in the ArticleReaderFragment constructor.
     */
    private void launchArticleReader(FeedArticle article, Class c) {
        //Link-type post auto-parsing
        if (article.getPreviewText().contentEquals("null")) {
            article.requestBrowseToAction(mContext);
            article.markAsRead();
            final Class newsClassName = NewsListFragment.class;
            final Class communityClassName = CommunityListFragment.class;
            final Class schoolClassName = SchoolListFragment.class;
            String tableName;
            final Realm r = Realm.getInstanceByRealmId(mAccount.getRealmEnum());
            if (c == newsClassName) {
                //TODO Pass the right data (implement locale handling)
                final String l = r.getLocales()[0];
                tableName = SQLiteDAO.getNewsTableName(r, l);
            } else if (c == communityClassName) {
                tableName = SQLiteDAO.getCommunityTableName();
            } else if (c == schoolClassName) {
                tableName = SQLiteDAO.getSchoolTableName();
            } else {
                throw new IllegalArgumentException("Feed list fragment class " + c
                        .getCanonicalName() + " not recognized.");
            }
            new AsyncTask<Object, Void, Void>() {
                @Override
                protected Void doInBackground(Object... params) {
                    SQLiteDAO.getInstance().markArticleAsRead((FeedArticle) params[0],
                            SQLiteDAO.getNewsTableName((Realm) params[1],
                                    (String) params[2]));
                    return null;
                }
            }.executeOnExecutor(Executors.newSingleThreadExecutor(), article, r, tableName);
            return;
        }

        Intent intent = new Intent(mContext, ArticleReaderActivity.class);

        intent.putExtra(ArticleReaderFragment.KEY_ARTICLE, article);
        intent.putExtra(ArticleReaderActivity.READER_LIST_FRAGMENT_CLASS, c);
        intent.putExtra(ArticleReaderFragment.KEY_ACCOUNT, mAccount);

        startActivity(intent);
        overridePendingTransition(R.anim.move_in_from_bottom, R.anim.move_out_to_bottom);
    }

    @Override
    public void onFeedArticleClicked(FeedArticle item, Class c) {
        launchArticleReader(item, c);
    }

    @Override
    public void onBackPressed() {
        Boolean handled = Boolean.FALSE;
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            handled = Boolean.TRUE;
        }
        if (!handled && mContentFragments != null && mContentFragments[mNavigatedIndexesStack
                .peek()] != null && mContentFragments[mNavigatedIndexesStack.peek()] instanceof
                Interface.IOnBackPressed) {
            handled = ((Interface.IOnBackPressed) mContentFragments[mNavigatedIndexesStack.peek()
                    ]).onBackPressed();
        }
        if (!handled) {
            super.onBackPressed();
            if (mNavigatedIndexesStack.size() > 1) {
                mNavigatedIndexesStack.pop();
                mNavigationDrawerFragment.selectItem(mNavigatedIndexesStack.peek());
                mNavigatedIndexesStack.pop(); //Remove the element that was added because of the
                // call to selectItem
            } else
                finish();
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mNavigationDrawerFragment == null || mNavigationDrawerFragment.getPosition() ==
                position) {
            return;
        }
        Fragment target;
        mNavigatedIndexesStack.push(position);
        switch (position) {
            case 0:
                target = findNewsListFragment();
                break;
            case 1:
                target = findCommunityListFragment();
                break;
            case 2:
                target = findSchoolListFragment();
                break;
            case 3:
                target = findChatFragment();
                break;
            default:
                throw new IllegalArgumentException("Menu with id " + position + " not found.");
        }
        final Fragment targetAsFinal = target;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(R.id
                                .content_fragment_container,
                        targetAsFinal).addToBackStack(null).commitAllowingStateLoss();
            }
        });
    }

    public void setupNavigationDrawer(Toolbar toolbar, LoLin1Account acc) {
        mNavigationDrawerFragment.setup(R.id.navigation_drawer_fragment,
                (DrawerLayout) findViewById(R.id.navigation_drawer), toolbar, acc);
    }

    private void runChat() {
        Intent intent = new Intent(getApplicationContext(), ChatIntentService.class);
        if (ChatIntentService.isLoggedIn()) {
            stopService(intent);
        }
        Intent chatConnectIntent = new Intent(getApplicationContext(), ChatIntentService.class);
        chatConnectIntent.setAction(ChatIntentService.ACTION_CONNECT);
        chatConnectIntent.putExtra(ChatIntentService.EXTRA_KEY_LOLIN1_ACCOUNT, mAccount);
        startService(chatConnectIntent);
    }

    private void registerLocalBroadcastReceiver() {
        if (mChatBroadcastReceiver != null) {
            return;
        }
        mChatBroadcastReceiver = new ChatOverviewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction(mContext.getString(R.string.event_login_failed));
        intentFilter.addAction(mContext.getString(R.string.event_chat_overview));
        intentFilter.addAction(mContext.getString(R.string.event_login_successful));
        intentFilter.addAction(mContext.getString(R.string.event_message_received));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mChatBroadcastReceiver, intentFilter);
    }

    private void stopChatService() {
        Intent chatDisconnectIntent = new Intent(getApplicationContext(), ChatIntentService.class);
        chatDisconnectIntent.setAction(ChatIntentService.ACTION_DISCONNECT);
        startService(chatDisconnectIntent);
        mAlreadyInited = Boolean.FALSE;
        stopService(new Intent(getApplicationContext(), ChatIntentService.class));
    }

    private synchronized void requestChatListRefresh() {
        FriendManager.getInstance().updateOnlineFriends();
        //TODO ((ChatOverviewSupportFragment) mPagerAdapter.getItem(0)).notifyChatEvent();
    }

    public class ChatOverviewBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (action.contentEquals(context.getString(R.string.event_chat_overview))) {
                MainActivity.this.requestChatListRefresh();
            } else {
                final View thisView =
                        findViewById(android.R.id.content);
                if (action.contentEquals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    if (!Utils.isInternetReachable()) {
                        thisView.post(new Runnable() {
                            @Override
                            public void run() {
                                //TODO showViewNoConnection();
                            }
                        });
                        if (ChatIntentService.isLoggedIn()) {
                            stopChatService();
                        }
                    } else {
                        MainActivity.this.runChat();
                    }
                } else if (action.contentEquals(context.getString(R.string.event_login_failed))) {
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            //TODO showViewWrongCredentials(); or such
                        }
                    });
                    if (ChatIntentService.isLoggedIn()) {
                        stopChatService();
                    }
                } else if (action.contentEquals(context.getString(R.string
                        .event_login_successful))) {
                    MainActivity.this.mAlreadyInited = Boolean.TRUE;
                    FriendManager.getInstance().updateOnlineFriends();
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            mNavigationDrawerFragment.asyncLoadUserImage(mAccount);
                            //TODO showViewConnected(); or such
                        }
                    });
                }
            }

        }
    }

}
