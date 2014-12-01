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
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.ui.adapter.NavigationDrawerAdapter;
import org.jorge.lolin1.ui.fragment.ArticleReaderFragment;
import org.jorge.lolin1.ui.fragment.FeedListFragment;
import org.jorge.lolin1.ui.fragment.NavigationDrawerFragment;
import org.jorge.lolin1.ui.fragment.NewsListFragment;
import org.jorge.lolin1.util.Interface;

import java.util.Stack;

public class MainActivity extends ActionBarActivity implements Interface.IOnFeedArticleClickedListener, NavigationDrawerAdapter.NavigationDrawerCallbacks {

    private Context mContext;
    private Fragment[] mContentFragments;
    private Fragment mArticleReaderFragment;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Stack<Integer> mNavigatedIndexesStack;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigatedIndexesStack = new Stack<>();
        mNavigatedIndexesStack.push(0);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(Boolean.TRUE);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        enableNavigationDrawer();

        mContext = LoLin1Application.getInstance().getApplicationContext();
        if (mContentFragments == null)
            showInitialFragment();
    }

    private void showInitialFragment() {
        if (mContentFragments == null) {
            mContentFragments = new Fragment[4];
        }
        if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            getSupportFragmentManager().beginTransaction().
                    add(R.id.content_fragment_container, findNewsListFragment()).commit();
    }

    private Fragment findNewsListFragment() {
        if (mContentFragments[0] == null)
            mContentFragments[0] = NewsListFragment.newInstance(mContext);
        return mContentFragments[0];
    }

    private Fragment findCommunityFragment() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    private Fragment findSchoolFragment() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    private Fragment findChatFragment() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    private Fragment prepareArticleReaderFragment(FeedArticle article, Class c) {
        if (mArticleReaderFragment == null)
            mArticleReaderFragment = ArticleReaderFragment.instantiate(mContext, ArticleReaderFragment.class.getName());
        Bundle args = new Bundle();
        args.putParcelable(ArticleReaderFragment.ARTICLE_KEY, article);
        int errorResId;
        if (c == NewsListFragment.class)
            errorResId = R.drawable.news_article_placeholder;
        else
            throw new IllegalArgumentException("Class " + c.getName() + " doesn't correspond to a feed reader");
        args.putInt(FeedListFragment.ERROR_RES_ID_KEY, errorResId);
        mArticleReaderFragment.setArguments(args);

        return mArticleReaderFragment;
    }

    @Override
    public void onFeedArticleClicked(FeedArticle item, Class c) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content_fragment_container, prepareArticleReaderFragment(item, c)).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        Boolean handled = Boolean.FALSE;
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            handled = Boolean.TRUE;
        }
        if (!handled && mContentFragments != null && mContentFragments[mNavigatedIndexesStack.peek()] != null && mContentFragments[mNavigatedIndexesStack.peek()] instanceof Interface.IOnBackPressed) {
            handled = ((Interface.IOnBackPressed) mContentFragments[mNavigatedIndexesStack.peek()]).onBackPressed();
            if (!handled) {
                super.onBackPressed();
                if (mNavigatedIndexesStack.size() > 1) {
                    mNavigatedIndexesStack.pop();
                }
                mNavigationDrawerFragment.selectItem(mNavigatedIndexesStack.peek());
            }
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mNavigationDrawerFragment == null || mNavigationDrawerFragment.getPosition() == position) {
            return;
        }
        mNavigationDrawerFragment.selectItem(position);
        Fragment target;
        mNavigatedIndexesStack.push(position);
        switch (position) {
            case 0:
                target = findNewsListFragment();
                break;
            case 1:
                target = findCommunityFragment();
                break;
            case 2:
                target = findSchoolFragment();
                break;
            case 3:
                target = findChatFragment();
                break;
            default:
                throw new IllegalArgumentException("Menu with id " + position + " not found.");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment_container, target).addToBackStack(null).commit();
    }

    public void enableNavigationDrawer() {
        mNavigationDrawerFragment.setup(R.id.navigation_drawer_fragment, (DrawerLayout) findViewById(R.id.navigation_drawer), mToolbar);
    }
}
