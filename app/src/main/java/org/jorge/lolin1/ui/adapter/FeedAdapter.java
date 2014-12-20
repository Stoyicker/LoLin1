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

package org.jorge.lolin1.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.io.database.SQLiteDAO;
import org.jorge.lolin1.util.Interface;
import org.jorge.lolin1.util.PicassoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private final List<FeedArticle> items = new ArrayList<>();
    private final Context mContext;
    private int mDefaultImageId;
    private final Interface.IOnItemInteractionListener mCallback;
    private final Object mTag;
    private final String mTableName;

    public FeedAdapter(Context context, Interface.IOnItemInteractionListener
            onItemSelectedListener, Integer defaultImageId, Object _tag, String tableName) {
        this.mContext = context;
        this.mDefaultImageId = defaultImageId;
        this.mCallback = onItemSelectedListener;
        mTag = _tag;
        mTableName = tableName;
        requestDataLoad();
    }

    public void requestDataLoad() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... tableNames) {
                items.clear();
                items.addAll(SQLiteDAO.getInstance().getFeedArticlesFromTable(tableNames[0]));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyDataSetChanged();
            }
        }.executeOnExecutor(Executors.newSingleThreadExecutor(), mTableName);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .list_item_feed_article, viewGroup, Boolean.FALSE);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onItemClick(items.get(i));
            }
        });
        viewHolder.imageView.setImageDrawable(null);
        FeedArticle item = items.get(i);
        if (!item.isRead()) {
            viewHolder.titleView.setTextSize(mContext.getResources().getInteger(R.integer
                    .feed_article_on_list_title_unread));
            viewHolder.titleView.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            viewHolder.titleView.setTextSize(mContext.getResources().getInteger(R.integer
                    .feed_article_on_list_title_read));
            viewHolder.titleView.setTypeface(null, Typeface.NORMAL);
        }
        final String title = item.getTitle();
        viewHolder.titleView.setText(title);
        if (viewHolder.imageView.getDrawable() == null) {
            PicassoUtils.loadInto(mContext, item.getImageUrl(), mDefaultImageId,
                    viewHolder.imageView, mTag);
            viewHolder.imageView.setContentDescription(title);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public FeedArticle getItem(Integer i) {
        return items.get(i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(android.R.id.title);
            imageView = (ImageView) itemView.findViewById(android.R.id.icon);
        }
    }
}