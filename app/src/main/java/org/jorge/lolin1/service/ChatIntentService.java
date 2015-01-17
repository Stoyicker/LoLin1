package org.jorge.lolin1.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.LoLChat;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.LoLin1Account;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;

/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by Jorge Antonio Diaz-Benito Soriano.
 */
public class ChatIntentService extends IntentService {

    public static final String ACTION_CONNECT = "CONNECT", ACTION_DISCONNECT = "DISCONNECT";
    public static final String KEY_MESSAGE_CONTENTS = "MESSAGE_CONTENTS";
    public static final String KEY_MESSAGE_SOURCE = "SOURCE_FRIEND";
    private static final String EXTRA_KEY_LOLIN1_ACCOUNT = "EXTRA_KEY_LOLIN1_ACCOUNT";
    private final IBinder mBinder = new ChatBinder();
    private static LoLChat api;
    private SmackAndroid mSmackAndroid;
    private static Boolean isConnected = Boolean.FALSE;
    private AsyncTask<LoLin1Account, Void, Void> mLoginTask;

    public ChatIntentService() {
        super(ChatIntentService.class.getName());
    }

    @SuppressWarnings("unused")
    static List<Friend> getOnlineFriends() {
        return api.getOnlineFriends();
    }

    @SuppressWarnings("unused")
    public static Boolean isLoggedIn() {
        return isConnected;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isConnected = Boolean.FALSE;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            throw new IllegalArgumentException(
                    "No intent found");
        }
        if (TextUtils.isEmpty(intent.getAction())) {
            throw new IllegalArgumentException(
                    "Empty action is not supported");
        }
        final LoLin1Account acc = intent.getParcelableExtra(EXTRA_KEY_LOLIN1_ACCOUNT);
        switch (intent.getAction()) {
            case ACTION_CONNECT:
                connect(acc);
                break;
            case ACTION_DISCONNECT:
                disconnect();
                break;
            default:
                throw new IllegalArgumentException(
                        "Action " + intent.getAction() + " not yet supported");
        }
    }

    public class ChatBinder extends Binder {
        @SuppressWarnings("unused")
        public ChatIntentService getService() {
            return ChatIntentService.this;
        }
    }

    private void connect(final LoLin1Account acc) {
        mSmackAndroid = LoLChat.init(getApplicationContext());
        mLoginTask = new AsyncTask<LoLin1Account, Void, Void>() {
            @Override
            protected Void doInBackground(LoLin1Account... params) {
                Boolean loginSuccess =
                        login(params[0]);
                if (loginSuccess) {
                    launchBroadcastLoginSuccessful();
                    setUpChatOverviewListener();
                } else {
                    launchBroadcastLoginUnsuccessful();
                }
                return null;
            }
        };
        mLoginTask.executeOnExecutor(Executors.newSingleThreadExecutor(), acc);
    }

    private void setUpChatOverviewListener() {
        api.addFriendListener(new FriendListener() {

            @Override
            public void onFriendLeave(Friend friend) {
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendJoin(Friend friend) {
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendAvailable(Friend friend) {
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendAway(Friend friend) {
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendBusy(Friend friend) {
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendStatusChange(Friend friend) {
                ChatIntentService.this.launchBroadcastFriendEvent();
            }
        });

        api.addChatListener(new ChatListener() {
            @Override
            public void onMessage(Friend friend, String message) {
// TODO On message received listener
// ChatMessageWrapper messageWrapper = new ChatMessageWrapper(message,
//                        System.currentTimeMillis(), friend);
//
//                ChatBundleManager.addMessageToFriendChat(messageWrapper, friend);
//
//                launchBroadcastMessageReceived(message, friend.getName());
//
//                if (!LoLin1Utils.getCurrentForegroundActivityClass(getApplicationContext())
//                        .contentEquals((ChatRoomActivity.class.getName()))) {
//                    ChatNotificationManager.createOrUpdateMessageReceivedNotification
//                            (getApplicationContext(), message, friend);
//                }
            }
        });
    }

    @SuppressWarnings("unused")
    private void launchBroadcastMessageReceived(String message,
                                                String sourceFriendName) {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.event_message_received));
        intent.putExtra(KEY_MESSAGE_CONTENTS, message);
        intent.putExtra(KEY_MESSAGE_SOURCE, sourceFriendName);
        sendLocalBroadcast(intent);
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void launchBroadcastLoginSuccessful() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.event_login_successful));
        sendLocalBroadcast(intent);
    }

    private void launchBroadcastLoginUnsuccessful() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.event_login_failed));
        sendLocalBroadcast(intent);
    }

    private void launchBroadcastFriendEvent() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.event_chat_overview));
        sendLocalBroadcast(intent);
    }

    private Boolean login(final LoLin1Account acc) {
        ChatServer chatServer;
        chatServer = ChatServer.valueOf(acc.getRealmEnum().name().toUpperCase(Locale.ENGLISH));
        try {
            api = new LoLChat(chatServer, Boolean.FALSE);
        } catch (IOException e) {
            Crashlytics.logException(e);
            e.printStackTrace(System.err);
            if (!(e instanceof SSLException)) {
                launchBroadcastLoginUnsuccessful();
            }
            return Boolean.FALSE;
        }
        Boolean loginSuccess = Boolean.FALSE;
        try {
            loginSuccess = api.login(acc.getUsername(), acc.getPassword());
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        if (loginSuccess) {
            api.reloadRoster();
            isConnected = Boolean.TRUE;
            return Boolean.TRUE;
        } else {
            isConnected = Boolean.FALSE;
            return Boolean.FALSE;
        }
    }

    private void disconnect() {
        //All the null checks are necessary because this method is run when an account is added
        // from out of the app as well
        try {
            if (mLoginTask != null)
                mLoginTask.get(); // Disconnecting in the middle of a login may be troublesome
        } catch (InterruptedException | ExecutionException e) {
            Crashlytics.logException(e);
        }
        try {
            if (api != null) {
                api.disconnect();
                api = null;
            }
        } catch (SmackException.NotConnectedException e) {
            Crashlytics.logException(e);
        }
        if (mSmackAndroid != null)
            mSmackAndroid.onDestroy();
        isConnected = Boolean.FALSE;
    }
}