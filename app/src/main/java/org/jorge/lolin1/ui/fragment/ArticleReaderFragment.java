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

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.ui.activity.MainActivity;
import org.jorge.lolin1.ui.util.StickyParallaxNotifyingScrollView;
import org.jorge.lolin1.util.PicassoUtils;

public class ArticleReaderFragment extends Fragment {

    private Context mContext;
    private int mDefaultImageId;
    private String TAG;
    private FeedArticle mArticle = new FeedArticle();
    private static final String ARTICLE_KEY = "ARTICLE";
    private MainActivity mActivity;
    private Drawable mActionBarBackgroundDrawable;
    private View mHeaderView;
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
    private ActionBar mActionBar;
    private float mOriginalElevation;
    private FloatingActionButton mMarkAsReadFab;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = LoLin1Application.getInstance().getContext();
        Bundle args = getArguments();
        if (args == null)
            throw new IllegalStateException("ArticleReader created without arguments");
//        mArticle = (FeedArticle) args.getParcelable(ARTICLE_KEY); TODO Make the class implement parcelable
        TAG = mArticle.getUrl();
        mActivity = (MainActivity) activity;
        mDefaultImageId = getArguments().getInt(FeedListFragment.ERROR_RES_ID_KEY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_article_reader, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.onBackPressed();
                return Boolean.TRUE;
            case R.id.action_browse_to:
                mArticle.requestBrowseToAction(mContext);
                return Boolean.TRUE;
            case R.id.action_share:
                mArticle.requestShareAction(mContext);
                return Boolean.TRUE;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(Boolean.TRUE);
        final View ret = inflater.inflate(R.layout.fragment_article_reader, container, Boolean.FALSE);
        mHeaderView = ret.findViewById(R.id.image);
        PicassoUtils.loadInto(mContext, mArticle.getImageUrl(), mDefaultImageId, (android.widget.ImageView) mHeaderView, TAG);
        final String title = mArticle.getTitle();
        mHeaderView.setContentDescription(title);
        ((TextView) ret.findViewById(R.id.title)).setText(title);
        ((TextView) ret.findViewById(android.R.id.text1)).setText(mArticle.getPreviewText());

        mActionBar = mActivity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(Boolean.TRUE);
        mActionBarBackgroundDrawable = new ColorDrawable(mContext.getResources().getColor(R.color.action_bar_background));
        mActionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mOriginalElevation = mActionBar.getElevation();
            mActionBar.setElevation(0); //So that the shadow of the ActionBar doesn't show over the title
        }
        mActionBar.setTitle(mActivity.getString(R.string.section_title_article_reader));

        mActionBarBackgroundDrawable.setAlpha(0);
        StickyParallaxNotifyingScrollView scrollView = (StickyParallaxNotifyingScrollView) ret.findViewById(R.id.scroll_view);
        scrollView.setOnScrollChangedListener(mOnScrollChangedListener);
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, Boolean.TRUE)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            scrollView.setTopOffset(actionBarHeight - mContext.getResources().getInteger(R.integer.action_bar_extra_bottom_article_reader));
        } else
            throw new IllegalStateException("ActionBar size not found");
        scrollView.smoothScrollTo(0, 0);

        if (!mArticle.isRead()) {
            mMarkAsReadFab = (FloatingActionButton) ret.findViewById(R.id.fab_button_mark_as_read);
            mMarkAsReadFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mArticle.markAsRead();
                    mMarkAsReadFab.hide();
                }
            });

            mMarkAsReadFab.show();
        }
        return ret;
    }

    private StickyParallaxNotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new StickyParallaxNotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = mHeaderView.getHeight() - mActivity.getSupportActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
            if (mMarkAsReadFab != null)
                if (!who.canScrollVertically(1)) {
                    mMarkAsReadFab.show();
                } else if (t < oldt) {
                    mMarkAsReadFab.show();
                } else if (t > oldt) {
                    mMarkAsReadFab.hide();
                }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with(mContext).cancelTag(TAG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActionBar.setElevation(mOriginalElevation);
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(mContext, R.anim.move_in_from_bottom);
        } else {
            return AnimationUtils.loadAnimation(mContext, R.anim.move_out_to_bottom);
        }
    }
}
