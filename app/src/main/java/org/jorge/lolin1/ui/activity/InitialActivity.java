package org.jorge.lolin1.ui.activity;

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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.Realm;
import org.jorge.lolin1.io.backup.LoLin1BackupAgent;
import org.jorge.lolin1.io.database.SQLiteDAO;
import org.jorge.lolin1.io.file.FileOperations;
import org.jorge.lolin1.receiver.FeedScheduleBroadcastReceiver;

import java.io.File;

import io.fabric.sdk.android.Fabric;

public class InitialActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = initContext();
        initTracking(context);
        initRealms(context);
        requestBackupRestore(context);
        flushCacheIfNecessary(context);
        initDatabase(context);
        scheduleFeedServices(context);

        launchHomeActivity();
    }

    private void scheduleFeedServices(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, FeedScheduleBroadcastReceiver.class);
        intent.setAction("org.jorge.lolin1.feed_harvest_schedule_filter");
        sendBroadcast(intent); //So we make sure it runs once at the very beginning
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                context.getResources().getInteger(R.integer.news_fetch_interval_millis),
                pi);
    }

    private void initRealms(Context context) {
        Realm.initRealms(context);
    }

    private void requestBackupRestore(Context context) {
        LoLin1BackupAgent.restoreBackup(context);
    }

    private void initDatabase(Context context) {
        SQLiteDAO.setup(context);
    }

    private void initTracking(Context context) {
        Fabric.with(context, new Crashlytics());
    }

    private void launchHomeActivity() {
        final Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(homeIntent);
    }

    private Context initContext() {
        Context ret;
        LoLin1Application.getInstance().setContext(ret = getSupportActionBar().getThemedContext());

        return ret;
    }

    private void flushCacheIfNecessary(Context context) {
        File cacheDir;
        final Integer CACHE_SIZE_LIMIT_BYTES = 1048576; //1MB, fair enough
        if ((cacheDir = context.getCacheDir()).length() >
                CACHE_SIZE_LIMIT_BYTES) {
            FileOperations.recursivelyDelete(cacheDir);
        }
    }
}
