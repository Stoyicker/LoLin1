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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.FeedArticle;

import util.PicassoUtils;

public class ArticleReaderFragment extends Fragment {

    private Context mContext;
    private int mDefaultImageId;
    private String TAG;
    private FeedArticle mArticle = new FeedArticle();
    private static final String ARTICLE_KEY = "ARTICLE";
    private ActionBarActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = LoLin1Application.getInstance().getContext();
//        mArticle = (FeedArticle) getArguments().getParcelable(ARTICLE_KEY); TODO Make the class implement parcelable
        TAG = mArticle.getUrl();
        mActivity = (ActionBarActivity) activity;
        mDefaultImageId = getArguments().getInt(FeedListFragment.ERROR_RES_ID_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View ret = inflater.inflate(R.layout.fragment_article_reader, container, Boolean.FALSE);
        final ImageView imageView = (ImageView) ret.findViewById(R.id.image);
        PicassoUtils.loadInto(mContext, mArticle.getImageUrl(), mDefaultImageId, imageView, TAG);
        final String title = mArticle.getTitle();
        imageView.setContentDescription(title);
        ((TextView) ret.findViewById(R.id.title)).setText(title);
        ((TextView) ret.findViewById(android.R.id.text1)).setText(mArticle.getPreviewText());
        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with(mContext).cancelTag(TAG);
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
