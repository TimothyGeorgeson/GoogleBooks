package com.example.googlebooks.utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
Caches 3 most recent searches, each with a timeout (currently at 20 seconds for easier testing)
 */
public class CacheManager {
    private SharedPreferences sharedPrefs;
    private static final String PREFS = "prefs";
    private static final String QUERY1 = "query1";
    private static final String QUERY2 = "query2";
    private static final String QUERY3 = "query3";
    private static final String TIME1 = "time1";
    private static final String TIME2 = "time2";
    private static final String TIME3 = "time3";
    private static final long TIME_LIMIT = 20000;

    public CacheManager(Context context) {
        sharedPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void updateCache(String query, long time) {
        SharedPreferences.Editor editor = sharedPrefs.edit();

        String query1 = sharedPrefs.getString(QUERY1, "");
        String query2 = sharedPrefs.getString(QUERY2, "");
        String query3 = sharedPrefs.getString(QUERY3, "");

        long last1 = getLastTime(TIME1);
        long last2 = getLastTime(TIME2);
        long last3 = getLastTime(TIME3);

        //update pref if query already exists, or if no time has been stored yet
        if (last1 == 0 || query.equals(query1)) {
            editor.putString(QUERY1, query);
            editor.putLong(TIME1, time).apply();
        } else if (last2 == 0 || query.equals(query2)) {
            editor.putString(QUERY2, query);
            editor.putLong(TIME2, time).apply();
        } else if (last3 == 0 || query.equals(query3)) {
            editor.putString(QUERY3, query);
            editor.putLong(TIME3, time).apply();
        } else { //if all 3 cache spots are occupied, overwrite the oldest spot
            if (last1 <= last2 && last1 <= last3) {
                editor.putString(QUERY1, query);
                editor.putLong(TIME1, time).apply();
            } else if (last2 <= last1 && last2 <= last3) {
                editor.putString(QUERY2, query);
                editor.putLong(TIME2, time).apply();
            } else {
                editor.putString(QUERY3, query);
                editor.putLong(TIME3, time).apply();
            }
        }
    }

    private long getLastTime(String key) {
        return sharedPrefs.getLong(key, 0);
    }

    public boolean isCacheValid(String query) {
        long currentTime = System.currentTimeMillis();
        long lastCacheTime;

        if (query.equals(sharedPrefs.getString(QUERY1, ""))) {
            lastCacheTime = getLastTime(TIME1);
        } else if (query.equals(sharedPrefs.getString(QUERY2, ""))) {
            lastCacheTime = getLastTime(TIME2);
        } else if (query.equals(sharedPrefs.getString(QUERY3, ""))) {
            lastCacheTime = getLastTime(TIME3);
        } else {
            return false;
        }

        long timeDiff = currentTime - lastCacheTime;

        return timeDiff < TIME_LIMIT;
    }
}
