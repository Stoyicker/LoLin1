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
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.adapter.NewsAdapter;

public class NewsListFragment extends Fragment {

    private RecyclerView mNewsView;
    private Context mContext;
    private RecyclerView.Adapter mNewsAdapter;
    private View mEmptyView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = LoLin1Application.getInstance().getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_news_article_list, container, Boolean.FALSE);
        mNewsView = (RecyclerView) ret.findViewById(R.id.news_article_list_view);
        mEmptyView = ret.findViewById(android.R.id.empty);
        return ret;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        (mNewsAdapter = new NewsAdapter()).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });
        mNewsView.setLayoutManager(new LinearLayoutManager(mContext));
        mNewsView.setItemAnimator(new DefaultItemAnimator());
        mNewsView.setAdapter(mNewsAdapter);
        checkAdapterIsEmpty();
    }

    private void checkAdapterIsEmpty() {
        if (mNewsAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }
}
