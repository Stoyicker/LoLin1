package org.jorge.lolin1.chat;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.datamodel.ChatMessageWrapper;
import org.jorge.lolin1.ui.adapter.ChatRoomAdapter;

public final class ChatHistoryManager {

    private static LruCache<String, ChatRoomAdapter> CHAT_ADAPTER_CACHE;

    private ChatHistoryManager() throws IllegalAccessException {
        throw new IllegalAccessException("Do not instantiate " + ChatHistoryManager.class.getName
                ());
    }

    public static void setup(Context context) {
        final int memClass = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;
        CHAT_ADAPTER_CACHE = new LruCache<>(cacheSize);
    }

    public static ChatRoomAdapter getChatRoomByFriend(Friend f, RecyclerView... recyclerView) {
        ChatRoomAdapter ret;
        final String friendName;
        if ((ret = CHAT_ADAPTER_CACHE.get(friendName = f.getName())) == null) {
            if (recyclerView == null || recyclerView.length < 1)
                throw new IllegalStateException("A ChatRoomAdapter was not found for the given " +
                        "key " + f.getName() + " but a recyclerView was not provided to create a " +
                        "new one.");
            ret = new ChatRoomAdapter(recyclerView[0]);
            CHAT_ADAPTER_CACHE.put(friendName, ret);
        }
        return ret;
    }

    public static synchronized void addMessageToFriendChat(ChatMessageWrapper msg,
                                                           Friend chatSubject) {
        final ChatRoomAdapter currentAdapter = getChatRoomByFriend(chatSubject);
        currentAdapter.addItem(msg);
        currentAdapter.notifyItemAdded();
    }
}
