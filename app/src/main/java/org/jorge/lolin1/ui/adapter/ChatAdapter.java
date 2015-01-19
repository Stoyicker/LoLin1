package org.jorge.lolin1.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.R;
import org.jorge.lolin1.chat.FriendManager;

import java.util.LinkedHashSet;
import java.util.Set;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final View mEmptyView;
    private final Set<Friend> mData = new LinkedHashSet<>();

    public ChatAdapter(View emptyView) {
        mEmptyView = emptyView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO ChatAdapter onCreateViewHolder
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_chat_friend, parent, Boolean.FALSE);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //TODO ChatAdapter onBindViewHolder
    }

    public void notifyFriendSetChanged() {
        //Workaround for RecyclerView bug (https://code.google.com/p/android/issues/detail?id=77232)
        mEmptyView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mData.clear();
                mData.addAll(FriendManager.getInstance().getOnlineFriends());
                ChatAdapter.super.notifyDataSetChanged();
                updateEmptyViewVisibility();
            }
        }, 200);
    }

    private void updateEmptyViewVisibility() {
        if (mData.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else
            mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
