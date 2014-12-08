package org.jorge.lolin1.io.database;

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

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.jorge.lolin1.BuildConfig;
import org.jorge.lolin1.R;
import org.jorge.lolin1.io.backup.LoLin1BackupAgent;

public class SQLiteDAO extends RobustSQLiteOpenHelper {

    public static final Object DB_LOCK = new Object();
    private final int LOLIN1_V1_59_DB_VERSION;
    private static Context mContext;
    private static SQLiteDAO singleton;

    private SQLiteDAO(Context _context) {
        super(_context, _context.getString(R.string.database_name), null, BuildConfig.VERSION_CODE);
        mContext = _context;
        LOLIN1_V1_59_DB_VERSION = mContext.getResources().getInteger(R.integer
                .lolin1_v1_v59_db_version);
    }

    public synchronized static void setup(Context _context) {
        if (singleton == null) {
            singleton = new SQLiteDAO(_context);
            mContext = _context;
        }
    }

    public synchronized static SQLiteDAO getInstance() {
        if (singleton == null)
            throw new IllegalStateException("SQLiteDAO.setup(Context) must be called before " +
                    "trying to retrieve the instance.");
        return singleton;
    }

    @Override
    public void onRobustUpgrade(SQLiteDatabase db, int oldVersion,
                                int newVersion) throws SQLiteException {
        if (oldVersion == LOLIN1_V1_59_DB_VERSION) {
            //Clean the stored data which is no longer necessary.
            //Stored news and account data will be lost,
            // but neither of them are an issue.
            final Resources resources = mContext.getResources();

            final String[] oldTableNames = resources.getStringArray(R.array
                    .lolin1_v1_59_news_table_names);

            synchronized (DB_LOCK) {
                for (String oldTableName : oldTableNames)
                    db.execSQL("DROP TABLE IF EXISTS " + oldTableName);
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);


        synchronized (DB_LOCK) {
            //TODO onCreate SQLiteDAO
        }
    }
}
