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
    final static String gameKey = "game";

    public Logger(Activity acc) {
        prefs = acc.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
    }

    /**
     * Set the phone to be muted. Store the
     * choice in shared preferences.
     * @param muted
     */
    public void setMuted(boolean muted){
        prefs.edit().putBoolean(mutedKey, muted).apply();
    }

    /**
     * Check to see if the user has previously
     * muted the phones voice commands.
     * @return muted
     */
    public boolean getMuted() {

        boolean muted = prefs.getBoolean(mutedKey, false);

        return muted;
    }

    /**
     * Sets the phone games high score.
     * @param score
     */
    public void setGameHighScore(int score){
        prefs.edit().putInt(gameKey, score).apply();
    }

    /**
     * Get the phone games high score.
     * @return highScore
     */
    public int getGameHighScore() {

        int highScore = prefs.getInt(gameKey, 0);

        return highScore;
    }


}