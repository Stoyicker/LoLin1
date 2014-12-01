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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.ui.adapter.FeedAdapter;
import org.jorge.lolin1.ui.util.ChainableSwipeRefreshLayout;
import org.jorge.lolin1.ui.util.StickyParallaxNotifyingScrollView;
import org.jorge.lolin1.util.Interface;
import org.jorge.lolin1.util.PicassoUtils;

import java.util.concurrent.Executors;

public class FeedListFragment extends Fragment implements Interface.IOnBackPressed, Interface.IOnItemInteractionListener, ActionMode.Callback {

    private RecyclerView mNewsView;
    private Context mContext;
    private FeedAdapter mFeedAdapter;
    private View mEmptyView;
    private FloatingActionButton mFabShareButton;
    public static final int NO_ITEM_SELECTED = -1;
    private Integer mSelectedIndex = NO_ITEM_SELECTED;
    private FloatingActionButton mFabMarkAsReadButton;
    private String TAG;
    protected static final String TAG_KEY = "TAG", LM_KEY = "LAYOUT_MANAGER";
    public static final String ERROR_RES_ID_KEY = "ERROR";
    private int mDefaultImageId;
    private ActionMode mActionMode;
    protected ActionBarActivity mActivity;
    private Interface.IOnFeedArticleClickedListener mCallback;
    private Boolean mActionBarIsShowingOrShown = Boolean.TRUE;
    private final Object mActionBarLock = new Object();
    private LayoutManagerEnum mLMIndicator;
    private Boolean mIsDualPane = Boolean.FALSE;
    private FeedArticle lastClickedArticle;
    private Drawable mActionBarBackgroundDrawable;
    private final Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            final ActionBar actionBar = mActivity.getSupportActionBar();
            if (actionBar != null)
                actionBar.setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };
    private StickyParallaxNotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new StickyParallaxNotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = mHeaderView.getHeight() - mActivity.getSupportActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
            if (mFabMarkAsReadButton != null)
                if (!who.canScrollVertically(1)) {
                    mFabMarkAsReadButton.show();
                } else if (t < oldt) {
                    mFabMarkAsReadButton.show();
                } else if (t > oldt) {
                    mFabMarkAsReadButton.hide();
                }
        }
    };
    private Integer FEED_REFRESH_TIME_MILLIS;
    private ActionBar mActionBar;
    private Float mOriginalElevation;
    private View mHeaderView;
    private TextView mArticleTitleView;
    private StickyParallaxNotifyingScrollView mParallaxScrollView;
    private TextView mArticlePreviewView;
    private ChainableSwipeRefreshLayout mRefreshLayout;
    private final SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mFeedAdapter.notifyDataSetChanged();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(FEED_REFRESH_TIME_MILLIS);
                    } catch (InterruptedException e) {
                        Crashlytics.logException(e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mRefreshLayout.setRefreshing(Boolean.FALSE);
                }
            }.executeOnExecutor(Executors.newSingleThreadExecutor());
        }
    };

    protected enum LayoutManagerEnum {
        STAGGEREDGRID,
        GRID
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = LoLin1Application.getInstance().getContext();
        mActionBarBackgroundDrawable = new ColorDrawable(mContext.getResources().getColor(R.color.toolbar_background));
        Bundle args = getArguments();
        if (args == null)
            throw new IllegalStateException("FeedListFragment created without arguments");
        else {
            TAG = args.getString(TAG_KEY);
            mLMIndicator = (LayoutManagerEnum) args.getSerializable(LM_KEY);
            mDefaultImageId = args.getInt(ERROR_RES_ID_KEY);
        }
        mActivity = (ActionBarActivity) activity;
        mCallback = (Interface.IOnFeedArticleClickedListener) activity;
        FEED_REFRESH_TIME_MILLIS = mContext.getResources().getInteger(R.integer.feed_refresh_time_millis);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mIsDualPane && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
    }

    /**
     * Should be only used in dual-pane mode.
     */
    private void drawLastClickedArticle() {
        if (!mIsDualPane)
            throw new IllegalStateException("Do not use drawLastClickedArticle in modes other than dual-pane");
        PicassoUtils.loadInto(mContext, lastClickedArticle.getImageUrl(), mDefaultImageId, (android.widget.ImageView) mHeaderView, TAG);
        final String title = lastClickedArticle.getTitle();
        mHeaderView.setContentDescription(title);
        mArticleTitleView.setText(title);
        mArticlePreviewView.setText(lastClickedArticle.getPreviewText());
        mActionBar.setTitle(lastClickedArticle.getTitle());

        mActionBarBackgroundDrawable.setAlpha(0);

        mParallaxScrollView.setOnScrollChangedListener(mOnScrollChangedListener);
        mParallaxScrollView.smoothScrollTo(0, 0);

        if (!lastClickedArticle.isRead()) {
            mFabMarkAsReadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastClickedArticle.markAsRead();
                    mFabMarkAsReadButton.hide();
                }
            });

            mFabMarkAsReadButton.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(Boolean.TRUE);
        final View ret = inflater.inflate(R.layout.fragment_feed_article_list, container, Boolean.FALSE);
        mNewsView = (RecyclerView) ret.findViewById(R.id.feed_article_list_view);

        mRefreshLayout = (ChainableSwipeRefreshLayout) ret.findViewById(R.id.refreshable_layout);
        mRefreshLayout.setColorSchemeResources(R.color.material_orange_500, R.color.material_blue_500);
        TypedValue tv = new TypedValue();
        int actionBarHeight = -1;
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, Boolean.TRUE)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        if (actionBarHeight == -1)
            throw new IllegalStateException("Couldn't get the ActionBar height attribute");
        final Integer progressBarStartMargin = getResources().getDimensionPixelSize(
                R.dimen.swipe_refresh_progress_bar_start_margin), progressBarEndMargin = getResources().getDimensionPixelSize(
                R.dimen.swipe_refresh_progress_bar_end_margin);
        mRefreshLayout.setProgressViewOffset(Boolean.FALSE, actionBarHeight + progressBarStartMargin, actionBarHeight + progressBarEndMargin);
        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mRefreshLayout.setRecyclerView(mNewsView);

        mActionBar = mActivity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(Boolean.FALSE);
        mActionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mOriginalElevation = mActionBar.getElevation();
            mActionBar.setElevation(0); //So that the shadow of the ActionBar doesn't show over the title
        }

        mParallaxScrollView = (StickyParallaxNotifyingScrollView) ret.findViewById(R.id.scroll_view);
        mIsDualPane = mParallaxScrollView != null;
        mHeaderView = ret.findViewById(R.id.image);
        mArticleTitleView = (TextView) ret.findViewById(R.id.title);
        mArticlePreviewView = (TextView) ret.findViewById(android.R.id.text1);
        mFabMarkAsReadButton = ((FloatingActionButton) ret.findViewById(R.id.fab_button_mark_as_read));
        mFabMarkAsReadButton.hide();
        mNewsView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mEmptyView = ret.findViewById(android.R.id.empty);
        if (!mIsDualPane) {
            mFabShareButton = ((FloatingActionButton) ret.findViewById(R.id.fab_button_share));
            mFabShareButton.hide();
        }
        mFeedAdapter =
                new FeedAdapter(
                        mContext, mFabMarkAsReadButton, mFabShareButton, this, mDefaultImageId, mIsDualPane, TAG);
        if (mIsDualPane) {
            lastClickedArticle = mFeedAdapter.getItemCount() > 0 ? mFeedAdapter.getItem(0) : null;
            if (lastClickedArticle != null)
                drawLastClickedArticle();
            else
                ret.findViewById(R.id.news_list).setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        } else {
            final Integer BASE_TOP_PADDING = mNewsView.getPaddingTop();
            mNewsView.setOnScrollListener(new FloatingActionButton.FabRecyclerOnViewScrollListener() {

                final Integer MIN_SCROLL_TOGGLE_ACTION_BAR = mContext.getResources().getInteger(R.
                        integer.min_scroll_toggle_action_bar);

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    ActionBar actionBar = mActivity.getSupportActionBar();
                    synchronized (mActionBarLock) {
                        if (actionBar != null)
                            if (dy > MIN_SCROLL_TOGGLE_ACTION_BAR && mActionBarIsShowingOrShown) {
                                mNewsView.setPadding(0, 0, 0, 0);
                                actionBar.hide();
                                mActionBarIsShowingOrShown = Boolean.FALSE;
                            } else if (dy < -1 * MIN_SCROLL_TOGGLE_ACTION_BAR && !mActionBarIsShowingOrShown) {
                                mNewsView.setPadding(0, BASE_TOP_PADDING, 0, 0);
                                actionBar.show();
                                mActionBarIsShowingOrShown = Boolean.TRUE;
                            }
                        mFeedAdapter.clearSelection();
                    }
                }
            });
        }

        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with(mContext).cancelTag(TAG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActionBar.setElevation(mOriginalElevation);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.onBackPressed();
                return Boolean.TRUE;
            case R.id.action_browse_to:
                lastClickedArticle.requestBrowseToAction(mContext);
                return Boolean.TRUE;
            case R.id.action_share:
                lastClickedArticle.requestShareAction(mContext);
                return Boolean.TRUE;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFeedAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });
        RecyclerView.LayoutManager layoutManager;
        final Integer columnAmount = mContext.getResources().getInteger(mIsDualPane ? R.integer.dual_feed_column_amount : R.integer.feed_column_amount);
        switch (mLMIndicator) {
            case GRID:
                layoutManager = new GridLayoutManager(mContext, columnAmount);
                break;
            case STAGGEREDGRID:
                layoutManager = new StaggeredGridLayoutManager(columnAmount, StaggeredGridLayoutManager.VERTICAL);
                break;
            default:
                throw new IllegalArgumentException("Illegal LayoutManager indicator: " + mLMIndicator);
        }

        mNewsView.setLayoutManager(layoutManager);
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
            mActionMode = mActivity.startSupportActionMode(this);
        else {
            if (mActionMode.getCustomView() == null) {
                @SuppressLint("InflateParams") TextView tv = (TextView) mActivity.getLayoutInflater().inflate(R.layout.action_mode_feed_article_title, null);
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
    public void onItemClick(FeedArticle item) {
        if (mIsDualPane)
            drawLastClickedArticle();
        else {
            mCallback.onFeedArticleClicked(item, ((Object) this).getClass());
            mActivity.getSupportActionBar().show();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        @SuppressLint("InflateParams") TextView tv = (TextView) mActivity.getLayoutInflater().inflate(R.layout.action_mode_feed_article_title, null);
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

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(mContext, R.anim.move_in_from_bottom);
        } else {
            return AnimationUtils.loadAnimation(mContext, R.anim.move_out_to_bottom);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mIsDualPane && lastClickedArticle != null) {
            inflater.inflate(R.menu.actionbar_article_reader, menu);
        }
    }
}
