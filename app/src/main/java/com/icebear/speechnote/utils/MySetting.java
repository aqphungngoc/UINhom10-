package com.icebear.speechnote.utils;


import android.content.Context;
import android.content.SharedPreferences;


public class MySetting {
    public static final String SETTINGS = "settingss";
    public static final String KEY_REMOVE_ADS = "remove_ads";
    public static final String KEY_OPEN_FIRST = "fisrt_open";
    public static final String KEY_RATE_APP = "rate_app";

    // put open first app
    public static void putOpenFirst(Context context, boolean isShow) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_OPEN_FIRST, isShow);
        editor.commit();
    }

    public static boolean isOpenFirst(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_OPEN_FIRST, true);
    }


    // rated app
    public static int isRateApp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_RATE_APP, 0);
    }

    public static boolean putRateApp(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_RATE_APP, value);
        return editor.commit();
    }

    public static void putRemoveAds(Context context, boolean removeAds) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_REMOVE_ADS, removeAds);
        editor.commit();
    }

    public static boolean isRemoveAds(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_REMOVE_ADS, false);
    }



}
