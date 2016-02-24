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
    final static String userArrayKey = "userArray";
    final static String currentUserKey = "currentUser";

    public Logger(Activity acc) {
        prefs = acc.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        cloudLogger = new CloudLogger(acc);
        cloudLogger.initApi();
        //acc.getSharedPreferences("com.example.app", 0).edit().clear().commit();
    }


    /**
     * Get the easy high score.
     * @return int
     */
    public int getEasyScore() {

        int easyScore = prefs.getInt(easyKey+this.getUniqueId(), 0);

        return easyScore;

    }

    /**
     * Get the current last easy score.
     * @return int
     */
    public int getLastEasyScore() {
        int easyScore = prefs.getInt(lastEasyKey+this.getUniqueId(), 0);

        return easyScore;

    }

    /**
     * Get the current last medium score.
     * @return int
     */
    public int getLastMediumScore() {

        int mediumScore = prefs.getInt(lastMediumKey+this.getUniqueId(), 0);

        return mediumScore;

    }

    /**
     * Get the current last hard score.
     * @return int
     */
    public int getLastHardScore() {

        int hardScore = prefs.getInt(lastHardKey+this.getUniqueId(), 0);

        return hardScore;

    }

    /**
     * Get the current medium high score.
     * @return int
     */
    public int getMediumScore() {

        int mediumScore = prefs.getInt(mediumKey+this.getUniqueId(), 0);

        return mediumScore;

    }

    /**
     * Get the current hard high score.
     * @return int
     */
    public int getHardScore() {
        int hardScore = prefs.getInt(hardKey+this.getUniqueId(), 0);

        return hardScore;
    }

    /**
     * Get the current unique I.D.
     * @return String
     */
    public String getUniqueId() {
        String id = prefs.getString(currentUserKey, "");

        return id;
    }

    /**
     * Get the number of times the app has been opened.
     * @return int
     */
    public int getOpened() {

        int opened = prefs.getInt(openKey + this.getUniqueId(), 0);

        return opened;

    }

    /**
     * Set the easy high score. Store the
     * score in shared preferences.
     * @param easyScore
     */
    public void setEasyScore(int easyScore){
        prefs.edit().putInt(easyKey+this.getUniqueId(), easyScore).apply();
    }

    /**
     * Set the last easy score. Store the
     * score in shared preferences.
     * @param easyScore
     */
    public void setLastEasyScore(int easyScore){
        cloudLogger.sendScoreToCloud("easy " + easyScore + " " + this.getUniqueId());
        prefs.edit().putInt(lastEasyKey+this.getUniqueId(), easyScore).apply();
    }

    /**
     * Set the last medium score. Store the
     * score in shared preferences.
     * @param mediumScore
     */
    public void setLastMediumScore(int mediumScore){
        cloudLogger.sendScoreToCloud("medium " + mediumScore + " " + this.getUniqueId());
        prefs.edit().putInt(lastMediumKey+this.getUniqueId(), mediumScore).apply();
    }

    /**
     * Set the last hard score. Store the
     * score in shared preferences.
     * @param hardScore
     */
    public void setLastHardScore(int hardScore){
        cloudLogger.sendScoreToCloud("hard " + hardScore + " " + this.getUniqueId());
        prefs.edit().putInt(lastHardKey+this.getUniqueId(), hardScore).apply();
    }

    /**
     * Set the medium high score. Store the
     * score in shared preferences.
     * @param mediumScore
     */
    public void setMediumScore(int mediumScore){
        prefs.edit().putInt(mediumKey+this.getUniqueId(), mediumScore).apply();
    }

    /**
     * Set the hard high score. Store the
     * score in shared preferences.
     * @param hardScore
     */
    public void setHardScore(int hardScore){
        prefs.edit().putInt(hardKey+this.getUniqueId(), hardScore).apply();
    }

    /**
     * Set a unique I.D. Store the
     * I.D. in shared preferences.
     */
    public void setUniqueId(){
        String id = generateUnique(6);
        prefs.edit().putString(uniqueId+this.getUniqueId(), id).apply();
        saveUserArray(id);
    }

    public void setCurrentUser(String uniqueId){
        prefs.edit().putString(currentUserKey, uniqueId).apply();
    }

    public String getCurrentUser(){
        return prefs.getString(currentUserKey, "");
    }

    /**
     * Increments the number of times the app has been opened.
     * @param opened
     */
    public void setOpened(int opened){
        prefs.edit().putInt(openKey+this.getUniqueId(), opened).commit();
    }

    /**
     * Generate a unique I.D.
     * @param len
     * @return String
     */
    public String generateUnique(int len){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public boolean saveUserArray(String newUser) {
        String[] array = loadUserArray();
        String[] newUserArray;
        if(array == null || array.length ==0){
            newUserArray = new String[]{newUser};
        }else{
            newUserArray = new String[array.length+1];
            for(int i = 0; i< newUserArray.length-1; i++){
                newUserArray[i] = array[i];
            }
            newUserArray[newUserArray.length-1] = newUser;}
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(userArrayKey +"_size", newUserArray.length);
        for(int i=0;i<newUserArray.length;i++)
            editor.putString(userArrayKey + "_" + i, newUserArray[i]);
        return editor.commit();
    }

    public String[] loadUserArray() {
        int size = prefs.getInt(userArrayKey + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getString(userArrayKey + "_" + i, null);
        return array;
    }

    public boolean newUserArray(String[] array) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(userArrayKey +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(userArrayKey + "_" + i, array[i]);
        return editor.commit();
    }
}
