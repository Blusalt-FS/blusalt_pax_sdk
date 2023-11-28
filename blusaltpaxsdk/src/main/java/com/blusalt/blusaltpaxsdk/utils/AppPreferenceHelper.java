package com.blusalt.blusaltpaxsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferenceHelper {
    private static final String DEFAULT_VALUE = "";
    private static final String PREF_NAME = "MPOSStorage";

    SharedPreferences sharedPreferences;

    public AppPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setSharedPreferenceString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getSharedPreferenceString(String key) {
        return sharedPreferences.getString(key, DEFAULT_VALUE);
    }

    public void setSharedPreferenceBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getSharedPreferenceBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}

