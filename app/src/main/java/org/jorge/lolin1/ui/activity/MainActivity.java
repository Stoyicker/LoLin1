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
import android.support.v7.app.ActionBarActivity;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.ui.fragment.ArticleReaderFragment;
import org.jorge.lolin1.ui.fragment.FeedListFragment;
import org.jorge.lolin1.ui.fragment.NewsListFragment;
import org.jorge.lolin1.ui.util.Interface;

public class MainActivity extends ActionBarActivity implements Interface.IOnFeedArticleClickedListener {

    private Context mContext;
    private Fragment[] mContentFragments;
    private Integer mActiveFragment = 0;
    private Fragment mArticleReaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = LoLin1Application.getInstance().getApplicationContext();
        if (getSupportFragmentManager().getFragments() == null)
            showInitialFragment();
    }

    private void showInitialFragment() {
        if (mContentFragments == null)
            mContentFragments = new Fragment[1];
        if (mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            getSupportFragmentManager().beginTransaction().
                    add(R.id.content_fragment_container, findNewsListFragment()).commit();
    }

    private Fragment findNewsListFragment() {
        if (mContentFragments[0] == null)
            mContentFragments[0] = NewsListFragment.newInstance(mContext);
        return mContentFragments[0];
    }

    private Fragment prepareArticleReaderFragment(FeedArticle article, Class c) {
        if (mArticleReaderFragment == null)
            mArticleReaderFragment = ArticleReaderFragment.instantiate(mContext, ArticleReaderFragment.class.getName());
        Bundle args = new Bundle();
//        args.putParcelable(article); TODO Make FeedArticle implement Parcelable
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
        if (mContentFragments != null && mContentFragments[mActiveFragment] != null && mContentFragments[mActiveFragment] instanceof Interface.IOnBackPressed) {
            handled = ((Interface.IOnBackPressed) mContentFragments[mActiveFragment]).onBackPressed();
        }
        if (!handled) {
            super.onBackPressed();
        }
    }
}
