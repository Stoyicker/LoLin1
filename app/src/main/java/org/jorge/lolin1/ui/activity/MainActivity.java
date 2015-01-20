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
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.auth.LoLin1AccountAuthenticator;
import org.jorge.lolin1.chat.FriendManager;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.datamodel.LoLin1Account;
import org.jorge.lolin1.datamodel.Realm;
import org.jorge.lolin1.io.database.SQLiteDAO;
import org.jorge.lolin1.io.prefs.PreferenceAssistant;
import org.jorge.lolin1.service.ChatIntentService;
import org.jorge.lolin1.ui.adapter.ChatAdapter;
import org.jorge.lolin1.ui.adapter.NavigationDrawerAdapter;
import org.jorge.lolin1.ui.fragment.ArticleReaderFragment;
import org.jorge.lolin1.ui.fragment.CommunityListFragment;
import org.jorge.lolin1.ui.fragment.NavigationDrawerFragment;
import org.jorge.lolin1.ui.fragment.NewsListFragment;
import org.jorge.lolin1.ui.fragment.SchoolListFragment;
import org.jorge.lolin1.util.Interface;
import org.jorge.lolin1.util.PicassoUtils;
import org.jorge.lolin1.util.Utils;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.Executors;

public class MainActivity extends ActionBarActivity implements Interface
        .IOnFeedArticleClickedListener, NavigationDrawerAdapter.NavigationDrawerCallbacks {

    public static final String KEY_LOLIN1_ACCOUNT = "KEY_LOLIN1_ACCOUNT";
    public static final String KEY_OPEN_CHAT = "KEY_OPEN_CHAT";
    private Context mContext;
    private Fragment[] mContentFragments;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Stack<Integer> mNavigatedIndexesStack;
    private LoLin1Account mAccount;
    private BroadcastReceiver mChatBroadcastReceiver;
    private Boolean mAlreadyInited = Boolean.FALSE;
    private View mLoginProgress;
    private ImageView mLoginStatus;
    private ChatAdapter mChatAdapter;
    private TextView mEmptyView;
    private String mTag = getClass().getName();
    private SlidingUpPanelLayout mSlidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigatedIndexesStack = new Stack<>();
        mNavigatedIndexesStack.push(0);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(Boolean.TRUE);

        mContext = LoLin1Application.getInstance().getContext();

        mAccount = getIntent().getParcelableExtra(KEY_LOLIN1_ACCOUNT);
        final Boolean comesFromNotification;
        if (comesFromNotification = mAccount == null)
            mAccount = LoLin1AccountAuthenticator.loadAccount(mContext);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer_fragment);
        setupNavigationDrawer(toolbar, mAccount);

        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id
                .sliding_layout);
        mLoginProgress = findViewById(R.id.progress_bar);
        mLoginStatus = (ImageView) findViewById(R.id.login_status_image);
        final TextView chatActionView = (TextView) findViewById(R.id.chat_view_action);
        mSlidingLayout.setDragView(chatActionView);
        mSlidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                //Unused
            }

            @Override
            public void onPanelCollapsed(View view) {
                MainActivity.this.mNavigationDrawerFragment.unlockDrawer();
                chatActionView.setText(R.string.chat_view_action_expand);
            }

            @Override
            public void onPanelExpanded(View view) {
                MainActivity.this.mNavigationDrawerFragment.lockDrawerClosed();
                chatActionView.setText(R.string.chat_view_action_collapse);
            }

            @Override
            public void onPanelAnchored(View view) {
                MainActivity.this.mNavigationDrawerFragment.lockDrawerClosed();
                chatActionView.setText(R.string.chat_view_action_collapse);
            }

            @Override
            public void onPanelHidden(View view) {
                MainActivity.this.mNavigationDrawerFragment.unlockDrawer();
                chatActionView.setText(R.string.chat_view_action_expand);
            }
        });

        setupChatView();
        if (!comesFromNotification)
            initChat();
        else {
            mNavigationDrawerFragment.asyncLoadUserImage(mAccount);
            requestChatListRefresh();
            showChatViewConnected();
        }

        if (mContentFragments == null)
            showInitialFragment();
    }

    private void setupChatView() {
        final RecyclerView chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        chatRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mEmptyView = (TextView) findViewById(android.R.id.empty);
        mChatAdapter =
                new ChatAdapter(mContext, mEmptyView, mTag);
        chatRecyclerView.setHasFixedSize(Boolean.FALSE);
        chatRecyclerView.setAdapter(mChatAdapter);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PicassoUtils.cancel(mContext, mTag);
    }

    private void initChat() {
        final View thisView = findViewById(android.R.id.content);
        registerLocalBroadcastReceiver();
        if (mAlreadyInited) {
            if (!Utils.isInternetReachable()) {
                thisView.post(new Runnable() {
                    @Override
                    public void run() {
                        showChatViewNoConnection();
                    }
                });
            } else {
                if (!ChatIntentService.isLoggedIn()) {
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            showChatViewLoading();
                        }
                    });
                    runChat();
                } else {
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            showChatViewConnected();
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
                    showChatViewNoConnection();
                }
            };
        } else {
            viewRunnable = new Runnable() {
                @Override
                public void run() {
                    showChatViewLoading();
                }
            };
            if (!ChatIntentService.isLoggedIn()) {
                runChat();
            }
        }
        thisView.post(viewRunnable);

        if (getIntent().getBooleanExtra(KEY_OPEN_CHAT, Boolean.FALSE) && !mSlidingLayout
                .isPanelExpanded()) {
            mSlidingLayout.expandPanel();
        }
    }

    private void showChatViewLoading() {
        mLoginStatus.setVisibility(View.GONE);
        mLoginStatus.setContentDescription(getString(R.string
                .chat_status_loading_content_description));
        mLoginProgress.setVisibility(View.VISIBLE);
        mEmptyView.setText(getString(R.string.chat_friend_list_empty_loading));
    }

    private void showChatViewConnected() {
        mLoginProgress.setVisibility(View.GONE);
        mLoginStatus.setVisibility(View.VISIBLE);
        mLoginStatus.setContentDescription(getString(R.string
                .chat_status_logged_in_content_description));
        mLoginStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_logged_in));
        mEmptyView.setOnClickListener(null);
        mEmptyView.setText(getString(R.string.chat_friend_list_empty_no_online_friends));
    }

    private void showChatViewNoConnection() {
        mLoginProgress.setVisibility(View.GONE);
        mLoginStatus.setVisibility(View.VISIBLE);
        mLoginStatus.setContentDescription(getString(R.string
                .chat_status_no_connection_content_description));
        mLoginStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_error));
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetReachable() && !ChatIntentService.isLoggedIn()) {
                    showChatViewLoading();
                    MainActivity.this.runChat();
                }
            }
        });
        mEmptyView.setText(getString(R.string.chat_friend_list_empty_no_connection));
    }

    private void showChatViewWrongCredentials() {
        mLoginProgress.setVisibility(View.GONE);
        mLoginStatus.setVisibility(View.VISIBLE);
        mLoginStatus.setContentDescription(getString(R.string
                .chat_status_wrong_credentials_content_description));
        mLoginStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_warning));
        mEmptyView.setOnClickListener(null);
        mEmptyView.setText(getString(R.string.chat_friend_list_empty_wrong_credentials));
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
                String l = PreferenceAssistant.readSharedString(mContext,
                        PreferenceAssistant.PREF_LANG, null);
                if (l == null || !Arrays.asList(r.getLocales()).contains(l)) {
                    l = r.getLocales()[0];
                    PreferenceAssistant.writeSharedString(mContext,
                            PreferenceAssistant.PREF_LANG,
                            l);
                }
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
        Intent intent = new Intent(mContext, ChatIntentService.class);
        if (ChatIntentService.isLoggedIn()) {
            stopService(intent);
        }
        Intent chatConnectIntent = new Intent(mContext, ChatIntentService.class);
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
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(mContext.getString(R.string.event_login_failed));
        intentFilter.addAction(mContext.getString(R.string.event_chat_overview));
        intentFilter.addAction(mContext.getString(R.string.event_login_successful));
        intentFilter.addAction(mContext.getString(R.string.event_message_received));
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mChatBroadcastReceiver, intentFilter);
    }

    private void stopChatService() {
        Intent chatDisconnectIntent = new Intent(mContext, ChatIntentService.class);
        chatDisconnectIntent.setAction(ChatIntentService.ACTION_DISCONNECT);
        startService(chatDisconnectIntent);
        mAlreadyInited = Boolean.FALSE;
        stopService(new Intent(mContext, ChatIntentService.class));
    }

    private synchronized void requestChatListRefresh() {
        FriendManager.getInstance().updateOnlineFriends();
        mChatAdapter.notifyFriendSetChanged();
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
                if (action.contentEquals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    if (!Utils.isInternetReachable()) {
                        thisView.post(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.requestChatListRefresh();
                                showChatViewNoConnection();
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
                            showChatViewWrongCredentials();
                        }
                    });
                    if (ChatIntentService.isLoggedIn()) {
                        stopChatService();
                    }
                } else if (action.contentEquals(context.getString(R.string
                        .event_login_successful))) {
                    MainActivity.this.mAlreadyInited = Boolean.TRUE;
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            mNavigationDrawerFragment.asyncLoadUserImage(mAccount);
                            requestChatListRefresh();
                            showChatViewConnected();
                        }
                    });
                }
            }

        }
    }
}
