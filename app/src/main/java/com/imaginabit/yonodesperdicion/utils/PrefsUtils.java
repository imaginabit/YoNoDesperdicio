package com.imaginabit.yonodesperdicion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 *
 */
public class PrefsUtils {
    public static final String KEY_FIRST_TIME = "first_time";
    public static final String KEY_FIRST_TIME_OFFERS = "first_time_offers";

    // Editor

    public static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.edit();
    }

    // Commit

    public static boolean commit(Context context, String key, boolean value) {
        SharedPreferences.Editor ed = getSharedPreferencesEditor(context);
        ed.putBoolean(key, value);
        return ed.commit();
    }

    public static boolean commit(SharedPreferences.Editor ed, String key, boolean value) {
        ed.putBoolean(key, value);
        return ed.commit();
    }

    public static boolean commit(Context context, String key, long value) {
        SharedPreferences.Editor ed = getSharedPreferencesEditor(context);
        ed.putLong(key, value);
        return ed.commit();
    }

    public static boolean commit(Context context, String key, int value) {
        SharedPreferences.Editor ed = getSharedPreferencesEditor(context);
        ed.putInt(key, value);
        return ed.commit();
    }

    // Get

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, defaultValue);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, defaultValue);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, defaultValue);
    }

    // Remove

    public static boolean remove(Context context, String key) {
        SharedPreferences.Editor ed = getSharedPreferencesEditor(context);
        ed.remove(key);
        return ed.commit();
    }
}
