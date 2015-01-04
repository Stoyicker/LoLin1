package org.jorge.lolin1.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jorge.lolin1.R;

import java.util.List;

/**
 * @author poliveira
 *         24/10/2014
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter
        .ViewHolder> {

    private List<NavigationItem> mData;
    private NavigationDrawerCallbacks mNavigationDrawerCallbacks;
    private int mSelectedPosition;
    private int mTouchedPosition = -1;
    private Context mContext;

    public NavigationDrawerAdapter(Context context, List<NavigationItem> data) {
        mContext = context;
        mData = data;
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks navigationDrawerCallbacks) {
        mNavigationDrawerCallbacks = navigationDrawerCallbacks;
    }

    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .list_item_navigation_drawer, viewGroup, false);
        return new ViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.textView.setText(mData.get(i).getText());
        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        if (mSelectedPosition == i || mTouchedPosition == i) {
            viewHolder.textView.setTextColor(mContext.getResources().getColor(R.color
                    .navigation_drawer_entry_text_selected));
            viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(mData.get(i)
                    .getSelectedDrawable(), null, null, null);
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color
                    .navigation_drawer_entry_background_selected));
        } else {
            viewHolder.textView.setTextColor(mContext.getResources().getColor(R.color
                    .navigation_drawer_entry_text_unselected));
            viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(mData.get(i)
                    .getStandardDrawable(), null, null, null);
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void touchPosition(int position) {
        int lastPosition = mTouchedPosition;
        mTouchedPosition = position;
        if (lastPosition >= 0)
            notifyItemChanged(lastPosition);
        if (position >= 0)
            notifyItemChanged(position);
    }

    public void selectPosition(int position) {
        int lastPosition = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener, View.OnTouchListener {
        private final NavigationDrawerAdapter mAdapter;
        public TextView textView;

        public ViewHolder(View itemView, NavigationDrawerAdapter adapter) {
            super(itemView);
            mAdapter = adapter;
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void onClick(View v) {
            if (mAdapter.mNavigationDrawerCallbacks != null)
                mAdapter.mNavigationDrawerCallbacks
                        .onNavigationDrawerItemSelected(getPosition());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mAdapter.touchPosition(getPosition());
                    return Boolean.FALSE;
                case MotionEvent.ACTION_CANCEL:
                    mAdapter.touchPosition(-1);
                    return Boolean.FALSE;
                case MotionEvent.ACTION_MOVE:
                    return Boolean.FALSE;
                case MotionEvent.ACTION_UP:
                    mAdapter.touchPosition(-1);
                    return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }
    }

    public static class NavigationItem {
        private final String mText;
        private final Drawable mStandardDrawable, mSelectedDrawable;

        public NavigationItem(String text, Drawable standardDrawable, Drawable selectedDrawable) {
            mText = text;
            mStandardDrawable = standardDrawable;
            mSelectedDrawable = selectedDrawable;
        }

        public String getText() {
            return mText;
        }

        public Drawable getStandardDrawable() {
            return mStandardDrawable;
        }

        public Drawable getSelectedDrawable() {
            return mSelectedDrawable;
        }
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }
}
