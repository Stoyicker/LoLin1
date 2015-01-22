package org.jorge.cmp.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.cmp.chat.FriendManager;

import java.util.Date;

public class ChatMessageWrapper implements Parcelable {

    public String getText() {
        return text;
    }

    private final String text;
    private final Date time;
    private final Friend sender; //Null sender means it was me

    public ChatMessageWrapper(String _text, long _time) {
        this(_text, _time, null);
    }

    public ChatMessageWrapper(String _text, long _time, Friend _sender) {
        text = _text;
        time = new Date(_time);
        sender = _sender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeLong(time.getTime());
        dest.writeString(sender == null ? "" : sender.getName());
    }

    public static final Parcelable.Creator<ChatMessageWrapper> CREATOR
            = new Parcelable.Creator<ChatMessageWrapper>() {
        public ChatMessageWrapper createFromParcel(Parcel in) {
            return new ChatMessageWrapper(in);
        }

        public ChatMessageWrapper[] newArray(int size) {
            return new ChatMessageWrapper[size];
        }
    };

    private ChatMessageWrapper(Parcel in) {
        text = in.readString();
        time = new Date(in.readLong());
        String friendName = in.readString();
        if (!TextUtils.isEmpty(friendName)) {
            sender = FriendManager.getInstance().findFriendByName(friendName);
        } else {
            sender = null;
        }
    }

    public Friend getSender() {
        return sender;
    }

    public Date getTime() {
        return time;
    }
}
