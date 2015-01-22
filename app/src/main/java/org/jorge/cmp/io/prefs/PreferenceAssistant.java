package org.jorge.cmp.io.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

public final class PreferenceAssistant {

    public static final String PREF_PULL_TO_REFRESH_LEARNED = "PREF_PULL_TO_REFRESH_LEARNED";
    public static final String PREF_USER_LEARNED_DRAWER = "PREF_USER_LEARNED_DRAWER";
    public static final String PREF_LANG = "PREF_LANG";
    public static final String PREF_LAST_PROFILE_ICON_VERSION = "PREF_LAST_PROFILE_ICON_VERSION";

    private PreferenceAssistant() {
        throw new UnsupportedOperationException("Do not instantiate " + getClass().getName());
    }

    public static void writeSharedString(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedString(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static void writeSharedBoolean(Context ctx, String settingName, Boolean settingValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(settingName, settingValue);
        editor.apply();
    }

    public static Boolean readSharedBoolean(Context ctx, String settingName, Boolean defaultValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getBoolean(settingName, defaultValue);
    }
}
