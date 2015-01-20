package org.jorge.lolin1.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import org.jorge.lolin1.LoLin1Application;

import java.util.List;

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

public abstract class Utils {

    public static Boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static Boolean isInternetReachable() {
        final Context context = LoLin1Application.getInstance().getContext();
        Boolean ret;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI),
                dataNetworkInfo =
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Boolean isWifiConnected =
                (wifiNetworkInfo == null) ? Boolean.FALSE : wifiNetworkInfo.isConnected(),
                isDataConnected =
                        (dataNetworkInfo == null) ? Boolean.FALSE :
                                dataNetworkInfo.isConnected();
        ret = isWifiConnected || isDataConnected;

        return ret;
    }

    public static Boolean isRunningOnForeground(Context context) {
        if (Utils.isMainThread())
            throw new IllegalStateException("Cannot call isRunningOnForeground in the main thread" +
                    ".");

        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null) {
            return Boolean.FALSE;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo
                    .IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
