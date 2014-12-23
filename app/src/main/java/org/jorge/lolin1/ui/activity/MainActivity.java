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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.datamodel.Realm;
import org.jorge.lolin1.io.database.SQLiteDAO;
import org.jorge.lolin1.ui.adapter.NavigationDrawerAdapter;
import org.jorge.lolin1.ui.fragment.ArticleReaderFragment;
import org.jorge.lolin1.ui.fragment.CommunityListFragment;
import org.jorge.lolin1.ui.fragment.NavigationDrawerFragment;
import org.jorge.lolin1.ui.fragment.NewsListFragment;
import org.jorge.lolin1.ui.fragment.SchoolListFragment;
import org.jorge.lolin1.util.Interface;

import java.util.Stack;
import java.util.concurrent.Executors;

public class MainActivity extends ActionBarActivity implements Interface
        .IOnFeedArticleClickedListener, NavigationDrawerAdapter.NavigationDrawerCallbacks {

    private Context mContext;
    private Fragment[] mContentFragments;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Stack<Integer> mNavigatedIndexesStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigatedIndexesStack = new Stack<>();
        mNavigatedIndexesStack.push(0);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(Boolean.TRUE);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer_fragment);
        //TODO Pass the right user data, probably through a Bundle from the LoginActivity
        setupNavigationDrawer(toolbar, "http://ddragon.leagueoflegends.com/cdn/4.20" +
                ".1/img/profileicon/547.png", "Stoyicker", "EUW");

        mContext = LoLin1Application.getInstance().getApplicationContext();
        if (mContentFragments == null)
            showInitialFragment();
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
            mContentFragments[0] = NewsListFragment.newInstance(mContext);
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
            //TODO Pass the right data
            final Realm r = Realm.getInstanceByRealmId(Realm.RealmEnum.EUW);
            if (c == newsClassName) {
                //TODO Pass the right data
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

        intent.putExtra(ArticleReaderFragment.ARTICLE_KEY, article);
        intent.putExtra(ArticleReaderActivity.READER_LIST_FRAGMENT_CLASS, c);

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
                // backpress
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

    public void setupNavigationDrawer(Toolbar toolbar, String userPhotoId, String userName,
                                      String realm) {
        mNavigationDrawerFragment.setup(R.id.navigation_drawer_fragment,
                (DrawerLayout) findViewById(R.id.navigation_drawer), toolbar, userPhotoId,
                userName, realm);
    }


}
