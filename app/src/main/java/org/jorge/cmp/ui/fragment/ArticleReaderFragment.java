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

package org.jorge.cmp.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import org.jorge.cmp.LoLin1Application;
import org.jorge.cmp.R;
import org.jorge.cmp.datamodel.FeedArticle;
import org.jorge.cmp.datamodel.LoLin1Account;
import org.jorge.cmp.datamodel.Realm;
import org.jorge.cmp.io.database.SQLiteDAO;
import org.jorge.cmp.io.prefs.PreferenceAssistant;
import org.jorge.cmp.ui.component.StickyParallaxNotifyingScrollView;
import org.jorge.cmp.util.PicassoUtils;

import java.util.Arrays;
import java.util.concurrent.Executors;

public class ArticleReaderFragment extends Fragment {

    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static Class mClass;
    private Context mContext;
    private int mDefaultImageId;
    private String TAG;
    private FeedArticle mArticle;
    public static final String KEY_ARTICLE = "ARTICLE";
    private ActionBarActivity mActivity;
    @SuppressWarnings("FieldCanBeLocal")
    private Drawable mActionBarBackgroundDrawable;
    private LoLin1Account mAccount;
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

    public static Fragment newInstance(Context context, FeedArticle article, Class c,
                                       LoLin1Account acc) {
        Bundle args = new Bundle();
        args.putParcelable(ArticleReaderFragment.KEY_ARTICLE, article);
        int errorResId = R.drawable.feed_article_image_placeholder;
        args.putInt(FeedListFragment.ERROR_RES_ID_KEY, errorResId);
        args.putParcelable(ArticleReaderFragment.KEY_ACCOUNT, acc);
        mClass = c;

        return ArticleReaderFragment.instantiate(context, ArticleReaderFragment.class.getName(),
                args);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = LoLin1Application.getInstance().getContext();
        mActivity = (ActionBarActivity) activity;
        final Bundle args = getArguments();
        if (args == null)
            throw new IllegalStateException("ArticleReader created without arguments");
        mArticle = args.getParcelable(KEY_ARTICLE);
        TAG = mArticle.getUrl();
        mDefaultImageId = args.getInt(FeedListFragment.ERROR_RES_ID_KEY);
        mAccount = args.getParcelable(KEY_ACCOUNT);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_article_reader, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
            case R.id.homeAsUp:
                mActivity.onBackPressed();
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(Boolean.TRUE);
        final View ret = inflater.inflate(R.layout.fragment_article_reader, container,
                Boolean.FALSE);
        View mHeaderView = ret.findViewById(R.id.image);
        PicassoUtils.loadInto(mContext, mArticle.getImageUrl(), mDefaultImageId,
                (android.widget.ImageView) mHeaderView, TAG);
        final String title = mArticle.getTitle();
        mHeaderView.setContentDescription(title);
        ((TextView) ret.findViewById(R.id.title)).setText(title);
        WebView contentView = (WebView) ret.findViewById(android.R.id.text1);
        WebSettings webViewSettings = contentView.getSettings();
        contentView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(Boolean.TRUE);
        webViewSettings.setBuiltInZoomControls(Boolean.FALSE);
        contentView.setBackgroundColor(0x00000000); //I wonder why the default background is white
        contentView.loadData(mArticle.getPreviewText(), "text/html; charset=UTF-8", "UTF-8");

        mActionBar = mActivity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(Boolean.TRUE);
        mActionBarBackgroundDrawable = new ColorDrawable(mContext.getResources().getColor(R.color
                .toolbar_background));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        mActionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mOriginalElevation = mActionBar.getElevation();
            mActionBar.setElevation(0); //So that the shadow of the ActionBar doesn't show over
            // the article title
        }
        mActionBar.setTitle(mActivity.getString(R.string.section_title_article_reader));

        StickyParallaxNotifyingScrollView scrollView = (StickyParallaxNotifyingScrollView) ret
                .findViewById(R.id.scroll_view);
        scrollView.setOnScrollChangedListener(mOnScrollChangedListener);
        scrollView.smoothScrollTo(0, 0);

        if (!mArticle.isRead()) {
            mMarkAsReadFab = (FloatingActionButton) ret.findViewById(R.id.fab_button_mark_as_read);
            mMarkAsReadFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markArticleAsRead(mArticle);
                    if (mMarkAsReadFab.isShown())
                        mMarkAsReadFab.hide();
                }

                private void markArticleAsRead(FeedArticle article) {
                    final Realm r = Realm.getInstanceByRealmId(ArticleReaderFragment.this
                            .mAccount.getRealmEnum());
                    String l = PreferenceAssistant.readSharedString(mContext,
                            PreferenceAssistant.PREF_LANG, null);
                    if (l == null || !Arrays.asList(r.getLocales()).contains(l)) {
                        l = r.getLocales()[0];
                        PreferenceAssistant.writeSharedString(mContext,
                                PreferenceAssistant.PREF_LANG,
                                l);
                    }
                    final Class newsClass = NewsListFragment.class;
                    final Class communityClass = CommunityListFragment.class;
                    final Class schoolClass = SchoolListFragment.class;
                    String tableName;
                    if (mClass == newsClass) {
                        if (l == null || !Arrays.asList(r.getLocales()).contains(l)) {
                            l = r.getLocales()[0];
                            PreferenceAssistant.writeSharedString(mContext,
                                    PreferenceAssistant.PREF_LANG,
                                    l);
                        }
                        tableName = SQLiteDAO.getNewsTableName(r, l);
                    } else if (mClass == communityClass) {
                        tableName = SQLiteDAO.getCommunityTableName();
                    } else if (mClass == schoolClass) {
                        tableName = SQLiteDAO.getSchoolTableName();
                    } else {
                        throw new IllegalArgumentException("Feed list fragment class " + mClass
                                .getCanonicalName() + " not recognized.");
                    }
                    new AsyncTask<Object, Void, Void>() {
                        @Override
                        protected Void doInBackground(Object... params) {
                            SQLiteDAO.getInstance().markArticleAsRead((FeedArticle) params[0],
                                    (String) params[1]);
                            return null;
                        }
                    }.executeOnExecutor(Executors.newSingleThreadExecutor(), article, tableName);
                    article.markAsRead();
                }
            });

            mMarkAsReadFab.hide();
            mMarkAsReadFab.setVisibility(View.VISIBLE);

            showMarkAsReadFabIfItProceeds();
        }
        return ret;
    }

    private void showMarkAsReadFabIfItProceeds() {
        if (!mArticle.isRead())
            mMarkAsReadFab.show();
    }

    private StickyParallaxNotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener =
            new StickyParallaxNotifyingScrollView.OnScrollChangedListener() {
                public void onScrollChanged(ScrollView who, int l, int t, int oldL,
                                            int oldT) {
                    if (mMarkAsReadFab != null)
                        if (!who.canScrollVertically(1) || !who.canScrollVertically(-1)) {
                            showMarkAsReadFabIfItProceeds();
                        } else if (t < oldT) {
                            showMarkAsReadFabIfItProceeds();
                        } else if (t > oldT) {
                            if (mMarkAsReadFab.isShown())
                                mMarkAsReadFab.hide();
                        }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        PicassoUtils.cancel(mContext, TAG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActionBar.setElevation(mOriginalElevation);
        }
    }
}
