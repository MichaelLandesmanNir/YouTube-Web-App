package com.example.newyoutube;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;

import com.google.gson.Gson;

public class SharedPreferencesUtil {

    private static final String PREF_NAME = "temp_prefs";

    public static void saveData(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void saveObject(Context context, String key, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.apply();
    }
    public static <T> T getObject(Context context, String key, Class<T> classOfT) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        return gson.fromJson(json, classOfT);
    }

    public static String getData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void clearData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
