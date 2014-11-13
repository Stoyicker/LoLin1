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

package org.jorge.lolin1.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.activity.MainActivity;
import org.jorge.lolin1.ui.adapter.FeedAdapter;

public class FeedListFragment extends Fragment implements MainActivity.IOnBackPressed, FeedAdapter.IOnItemSelectedListener, ActionMode.Callback {

    private RecyclerView mNewsView;
    private Context mContext;
    private FeedAdapter mFeedAdapter;
    private View mEmptyView;
    private FloatingActionButton mFabShareButton;
    public static final int NO_ITEM_SELECTED = -1;
    private Integer mSelectedIndex = NO_ITEM_SELECTED;
    private FloatingActionButton mFabMarkAsReadButton;
    private String TAG;
    protected static final String TAG_KEY = "TAG", ERROR_RES_ID_KEY = "ERROR";
    private int mDefaultImageId;
    private ActionMode mActionMode;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = LoLin1Application.getInstance().getContext();
        TAG = getArguments().getString(TAG_KEY);
        mDefaultImageId = getArguments().getInt(ERROR_RES_ID_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View ret = inflater.inflate(R.layout.fragment_feed_article_list, container, Boolean.FALSE);
        mFabShareButton = ((FloatingActionButton) ret.findViewById(R.id.fab_button_share));
        mFabShareButton.hide();
        mFabMarkAsReadButton = ((FloatingActionButton) ret.findViewById(R.id.fab_button_mark_as_read));
        mFabMarkAsReadButton.hide();
        mNewsView = (RecyclerView) ret.findViewById(R.id.news_article_list_view);
        final Integer BASE_TOP_PADDING = mNewsView.getPaddingTop();
        mNewsView.setOnScrollListener(new FloatingActionButton.FabRecyclerOnViewScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
                if (actionBar != null)
                    if (dy > 0) {
                        mNewsView.setPadding(0, 0, 0, 0);
                        actionBar.hide();
                    } else {
                        if (mNewsView.getChildAt(0).getTop() == 0) {
                            mNewsView.setPadding(0, BASE_TOP_PADDING, 0, 0);
                            actionBar.show();
                            mNewsView.getLayoutManager().scrollToPosition(0);
                        }
                    }
                mFeedAdapter.clearSelection();
            }
        });
        mEmptyView = ret.findViewById(android.R.id.empty);
        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with(mContext).cancelTag(TAG);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFeedAdapter =
                new FeedAdapter(
                        mContext, mFabMarkAsReadButton, mFabShareButton, this, mDefaultImageId, TAG);
        mFeedAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });
        mNewsView.setLayoutManager(new LinearLayoutManager(mContext));
        mNewsView.setItemAnimator(new DefaultItemAnimator());
        mNewsView.setAdapter(mFeedAdapter);
        checkAdapterIsEmpty();
    }

    private void checkAdapterIsEmpty() {
        if (mFeedAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public Boolean onBackPressed() {
        Boolean ret = mSelectedIndex != NO_ITEM_SELECTED;
        if (ret)
            mFeedAdapter.clearSelection();
        return ret;
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        mSelectedIndex = selectedIndex;
        if (mActionMode == null)
            mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(this);
        else {
            if (mActionMode.getCustomView() == null) {
                @SuppressLint("InflateParams") TextView tv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.action_mode_title, null);
                mActionMode.setCustomView(tv);
            }
            ((TextView) mActionMode.getCustomView()).setText(mFeedAdapter.getItem(mSelectedIndex).getTitle());
        }
    }

    @Override
    public void clearSelection() {
        if (mSelectedIndex != NO_ITEM_SELECTED) {
            mSelectedIndex = NO_ITEM_SELECTED;
            mActionMode.finish();
            mActionMode = null;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        @SuppressLint("InflateParams") TextView tv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.action_mode_title, null);
        tv.setText(mFeedAdapter.getItem(mSelectedIndex).getTitle());
        actionMode.setCustomView(tv);
        return Boolean.TRUE;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return Boolean.TRUE;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return Boolean.FALSE;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mFeedAdapter.clearSelection();
    }
}
