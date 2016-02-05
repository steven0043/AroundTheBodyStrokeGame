package com.atbsg.atbsg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Steven on 01/02/2016.
 */
public class Logger {

    SharedPreferences prefs;
    final static String mutedKey = "muted";

    public Logger(Activity acc) {
        prefs = acc.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
    }

    public void setMuted(boolean muted){
        prefs.edit().putBoolean(mutedKey, muted).apply();
    }

    public boolean getMuted() {

        boolean muted = prefs.getBoolean(mutedKey, false);

        return muted;
    }
}