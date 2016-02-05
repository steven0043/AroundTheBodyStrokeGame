package com.atbsg.atbsg.logging;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.atbsg.atbsg.logging.CloudLogger;

import java.util.Random;

/**
 * Created by Steve on 21/12/2015.
 */
public class Logger {

    SharedPreferences prefs;
    public CloudLogger cloudLogger;
    final static String lastEasyKey = "lastEasyScore";
    final static String lastMediumKey = "lastMediumScore";
    final static String lastHardKey = "lastHardScore";
    final static String easyKey = "easyScore";
    final static String mediumKey = "mediumScore";
    final static String hardKey = "hardScore";
    final static String uniqueId = "uniqueId";
    final static String openKey = "openedTimes";

    public Logger(Activity acc) {
        prefs = acc.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        cloudLogger = new CloudLogger(acc);
        cloudLogger.initApi();
        //acc.getSharedPreferences("com.example.app", 0).edit().clear().commit();
    }


    public int getEasyScore() {

        int easyScore = prefs.getInt(easyKey, 0);

        return easyScore;

    }

    public int getLastEasyScore() {
        int easyScore = prefs.getInt(lastEasyKey, 0);

        return easyScore;

    }

    public int getLastMediumScore() {

        int mediumScore = prefs.getInt(lastMediumKey, 0);

        return mediumScore;

    }

    public int getLastHardScore() {

        int hardScore = prefs.getInt(lastHardKey, 0);

        return hardScore;

    }

    public int getMediumScore() {

        int mediumScore = prefs.getInt(mediumKey, 0);

        return mediumScore;

    }

    public int getHardScore() {
        int hardScore = prefs.getInt(hardKey, 0);

        return hardScore;
    }

    public String getUniqueId() {
        String id = prefs.getString(uniqueId, "");

        return id;
    }

    public int getOpened() {

        int opened = prefs.getInt(openKey, 0);

        return opened;

    }

    public void setEasyScore(int easyScore){
        prefs.edit().putInt(easyKey, easyScore).apply();
    }

    public void setLastEasyScore(int easyScore){
        cloudLogger.sendScoreToCloud("easy " + easyScore + " " + this.getUniqueId());
        prefs.edit().putInt(lastEasyKey, easyScore).apply();
    }

    public void setLastMediumScore(int mediumScore){
        cloudLogger.sendScoreToCloud("medium " + mediumScore + " " + this.getUniqueId());
        prefs.edit().putInt(lastMediumKey, mediumScore).apply();
    }

    public void setLastHardScore(int hardScore){
        cloudLogger.sendScoreToCloud("hard " + hardScore + " " + this.getUniqueId());
        prefs.edit().putInt(lastHardKey, hardScore).apply();
    }

    public void setMediumScore(int mediumScore){
        prefs.edit().putInt(mediumKey, mediumScore).apply();
    }

    public void setHardScore(int hardScore){
        prefs.edit().putInt(hardKey, hardScore).apply();
    }

    public void setUniqueId(){
        String id = generateUnique(6);
        prefs.edit().putString(uniqueId, id).apply();
    }

    public void setOpened(int opened){
        prefs.edit().putInt(openKey, opened).commit();
    }

    private String generateUnique(int len){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
