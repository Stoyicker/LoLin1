package org.jorge.lolin1.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.R;
import org.jorge.lolin1.anim.ExpandableViewHoldersUtil;
import org.jorge.lolin1.chat.FriendManager;
import org.jorge.lolin1.io.prefs.PreferenceAssistant;
import org.jorge.lolin1.util.PicassoUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final View mEmptyView;
    private final Set<Friend> mData = new LinkedHashSet<>();
    private final Context mContext;
    private final String mTag;
    private final String BASE_PROFILE_ICON_URL;
    private final String DEFAULT_PROFILE_IMG_URL;
    private final ExpandableViewHoldersUtil.KeepOneH<ViewHolder> keepOne = new
            ExpandableViewHoldersUtil
                    .KeepOneH<>();

    public ChatAdapter(Context context, View emptyView, String tag) {
        mEmptyView = emptyView;
        mContext = context;
        mTag = tag;
        BASE_PROFILE_ICON_URL = mContext.getString(R.string
                .profile_icon_url_pattern);
        DEFAULT_PROFILE_IMG_URL =
                String.format(Locale.ENGLISH,
                        BASE_PROFILE_ICON_URL, PreferenceAssistant.readSharedString(mContext,
                                PreferenceAssistant.PREF_LAST_PROFILE_ICON_VERSION, "null"),
                        "0");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_chat_friend, parent, Boolean.FALSE);
        return new ViewHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        keepOne.bind(viewHolder, i);

        viewHolder.profileImageView.setImageDrawable(null);
        Friend item = getItem(i);
        if (item != null) {
            if (viewHolder.profileImageView.getDrawable() == null) {
                final String imgUrl = String.format(Locale.ENGLISH,
                        BASE_PROFILE_ICON_URL, PreferenceAssistant.readSharedString(mContext,
                                PreferenceAssistant.PREF_LAST_PROFILE_ICON_VERSION, "null"),
                        item.getStatus().getProfileIconId());
                PicassoUtils.loadInto(mContext, imgUrl, DEFAULT_PROFILE_IMG_URL,
                        viewHolder.profileImageView, mTag);
                viewHolder.profileImageView.setContentDescription(item.getName());
            }

            viewHolder.userNameView.setText(item.getName());

            viewHolder.statusView.setText(item.getStatus().getStatusMessage());
        }
    }

    @Nullable
    private synchronized Friend getItem(Integer i) {
        for (Iterator<Friend> it = mData.iterator(); it.hasNext(); ) {
            if (i == 0) {
                return it.next();
            }
            i--;
            it.next();
        }
        return null;
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


    public class ViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener, ExpandableViewHoldersUtil.Expandable {

        private final TextView userNameView, statusView;
        private final ImageView profileImageView;
        private final ViewGroup mChatArea;
        private final EditText mInputArea;
        private final Context mContext;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            userNameView = (TextView) itemView.findViewById(R.id.user_name);
            statusView = (TextView) itemView.findViewById(R.id.user_status);
            profileImageView = (ImageView) itemView.findViewById(R.id.user_image);
            mChatArea = (ViewGroup) itemView.findViewById(R.id.chat_expand_area);
            mInputArea = (EditText) itemView.findViewById(android.R.id.inputArea);
            mContext = context;
        }

        @Override
        public void onClick(final View view) {
            final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mInputArea.getWindowToken(), 0);
            keepOne.toggle(this);
            notifyItemChanged(getPosition());
            mInputArea.clearFocus();
        }

        @Override
        public View getExpandView() {
            return mChatArea;
        }
    }
}
