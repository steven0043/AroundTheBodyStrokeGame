package com.atbsg.atbsg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Steven on 01/02/2016.
 *
 * This class is specifically for local storage for
 * scores and user preferences on the smartphone
 */
public class Logger {

    SharedPreferences prefs;
    final static String mutedKey = "muted";
    final static String gameKey = "game";
    final static String gameLastKey = "gameLast";

    public Logger(Activity acc) {
        prefs = acc.getSharedPreferences(
                "com.atbsg.atbsg", Context.MODE_PRIVATE);
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
        String currentUser = MainActivity.getUserId();
        prefs.edit().putInt(gameKey + currentUser, score).apply();
    }

    /**
     * Get the phone games high score.
     * @return highScore
     */
    public int getGameHighScore() {
        String currentUser = MainActivity.getUserId();
        int highScore = prefs.getInt(gameKey + currentUser, 0);

        return highScore;
    }

    /**
     * Sets the circles game latest score.
     * @param score
     */
    public void setGameLastScore(int score){
        String currentUser = MainActivity.getUserId();
        prefs.edit().putInt(gameLastKey+currentUser, score).apply();
    }

    /**
     * Get the circle games latest score.
     * @return highScore
     */
    public int getGameLastScore() {
        String currentUser = MainActivity.getUserId();
        int highScore = prefs.getInt(gameLastKey+currentUser, 0);
        return highScore;
    }

}