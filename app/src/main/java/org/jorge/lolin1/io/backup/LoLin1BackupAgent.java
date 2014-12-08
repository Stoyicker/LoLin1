package org.jorge.lolin1.io.backup;

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

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupManager;
import android.app.backup.FileBackupHelper;
import android.app.backup.RestoreObserver;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.io.database.SQLiteDAO;

import java.io.File;
import java.io.IOException;

public class LoLin1BackupAgent extends BackupAgentHelper {

    private static final String PREFERENCES_BACKUP_KEY = "PREFERENCES_BACKUP_KEY",
            DATABASE_BACKUP_KEY = "DATABASE_BACKUP_KEY";
    private static BackupManager mBackupManager;

    @Override
    public void onCreate() {
        final Context context = LoLin1Application.getInstance().getContext();
        mBackupManager = new BackupManager(context);


        final String[] backupablePreferences = context.getResources().getStringArray(R.array
                .backupable_preference_keys);
        final SharedPreferencesBackupHelper sharedPreferencesBackupHelper = new
                SharedPreferencesBackupHelper(context, backupablePreferences);
        addHelper(PREFERENCES_BACKUP_KEY, sharedPreferencesBackupHelper);

        final File databaseFile = context.getDatabasePath(context.getString(R.string
                .database_name));
        if (databaseFile != null && databaseFile.exists()) {
            FileBackupHelper database = new FileBackupHelper(context,
                    databaseFile.getAbsolutePath());
            addHelper(DATABASE_BACKUP_KEY, database);
        }
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        synchronized (SQLiteDAO.DB_LOCK) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        synchronized (SQLiteDAO.DB_LOCK) {
            super.onRestore(data, appVersionCode, newState);
        }
    }

    public static void requestBackup() {
        mBackupManager.dataChanged();
    }

    public static void restoreBackup() {
        mBackupManager.requestRestore(new RestoreObserver() {
            @Override
            public void restoreStarting(int numPackages) {
                super.restoreStarting(numPackages);
            }
        });
    }
}