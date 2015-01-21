package org.jorge.lolin1.ui.adapter;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.ChatMessageWrapper;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    private static final Integer MESSAGE_TYPE_SENT_BY_ME = 0, MESSAGE_TYPE_SENT_BY_OTHER = 1;
    private final List<ChatMessageWrapper> mData = new LinkedList<>();
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm",
            Locale.ENGLISH);
    private final RecyclerView mRecyclerView;

    public ChatRoomAdapter(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(viewType ==
                        MESSAGE_TYPE_SENT_BY_ME ? R.layout
                        .list_item_chat_room_sent_by_me : R.layout
                        .list_item_chat_room_sent_by_other,
                parent, Boolean.FALSE);
        return new ChatRoomAdapter.ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        final ChatMessageWrapper messageWrapper = getItem(position);

        if (messageWrapper == null)
            throw new IllegalArgumentException("Illegal message index requested " + position + " " +
                    "on list" +
                    " sized " + mData.size());
        return messageWrapper.getSender() == null ? MESSAGE_TYPE_SENT_BY_ME :
                MESSAGE_TYPE_SENT_BY_OTHER;
    }

    @Override
    public void onBindViewHolder(ChatRoomAdapter.ViewHolder holder, int position) {
        final ChatMessageWrapper messageWrapper = getItem(position);

        if (messageWrapper != null) {
            holder.mContentsView.setText(messageWrapper.getText());
            holder.mTimeView.setText(TIMESTAMP_FORMAT.format(messageWrapper.getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Nullable
    private synchronized ChatMessageWrapper getItem(Integer i) {
        if (i < 0 || i > mData.size())
            throw new IllegalArgumentException("Illegal message index requested " + i + " on list" +
                    " sized " + mData.size());
        return mData.get(i);
    }

    public void addItem(ChatMessageWrapper chatMessageWrapper) {
        mData.add(chatMessageWrapper);
    }

    public void notifyItemAdded() {
        new AsyncTask<Void, Void, Void>() {

            final Integer lastPos = mData.size() - 1;

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyItemChanged(lastPos);
                mRecyclerView.smoothScrollToPosition(lastPos);
            }
        }.executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mContentsView, mTimeView;

        public ViewHolder(View v) {
            super(v);
            mContentsView = (TextView) v.findViewById(R.id.contents_view);
            mTimeView = (TextView) v.findViewById(R.id.time_view);
        }
    }
}
