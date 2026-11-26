package com.example.pillreminder.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public void saveStringValue(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void saveBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void saveIntegerValue(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getIntegerValue(String key) {
        int value = sharedPreferences.getInt(key, 0);
        if (key.equals("snooze") && value <= 0) {
            return 5;
        }
        return value;
    }

    public boolean checkExistsKey(String key) {
        return sharedPreferences.contains(key);
    }

}
