package org.jorge.lolin1.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.activity.MainActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class ChatNotificationManager {

    private static Map<String, Integer> NOTIFICATION_ID_MAP = Collections.synchronizedMap(new
            HashMap<String, Integer>());
    private static Map<String, String> LAST_NOTIFICATION_CONTENTS = Collections.synchronizedMap
            (new HashMap<String, String>());


    public static synchronized void createOrUpdateMessageReceivedNotification(Context context,
                                                                              String contents,
                                                                              Friend friend) {
        String name, previousNotificationContents, newContents;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        boolean notificationFound = NOTIFICATION_ID_MAP.containsKey(name = friend.getName());
        if (TextUtils.isEmpty(name)) {
            name = context.getString(R.string.app_name);
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id;
        if (!notificationFound) {
            builder = new NotificationCompat.Builder(context);
            id = NOTIFICATION_ID_MAP.size();
            NOTIFICATION_ID_MAP.put(name, id);
            previousNotificationContents = "";
        } else {
            id = NOTIFICATION_ID_MAP.get(name);
            previousNotificationContents = LAST_NOTIFICATION_CONTENTS.get(name);
        }
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(name);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(newContents =
                previousNotificationContents + "\n" + contents));
        builder.setAutoCancel(Boolean.TRUE);
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra(MainActivity.KEY_OPEN_CHAT, Boolean.TRUE);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(id, builder.build());
        LAST_NOTIFICATION_CONTENTS.put(name, newContents);
    }

    public static synchronized void dismissNotificationsForFriend(Context context,
                                                                  String friendName) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (NOTIFICATION_ID_MAP.containsKey(friendName)) {
            notificationManager.cancel(NOTIFICATION_ID_MAP.remove(friendName));
            LAST_NOTIFICATION_CONTENTS.remove(friendName);
        }
    }
}
