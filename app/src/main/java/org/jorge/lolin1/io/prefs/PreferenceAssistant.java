package org.jorge.lolin1.io.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferenceAssistant {

    public static final String PREF_PULL_TO_REFRESH_LEARNED = "PREF_PULL_TO_REFRESH_LEARNED";
    public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private PreferenceAssistant() {
        throw new IllegalStateException("Do not instantiate " + getClass().getName());
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
