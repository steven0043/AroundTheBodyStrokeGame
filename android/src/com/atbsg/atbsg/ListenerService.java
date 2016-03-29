package com.atbsg.atbsg;

/**
 * Created by Steven on 25/01/2016.
 *
 * This class is for receiving messages and data from the watch.
 */

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Random;

public class ListenerService extends WearableListenerService {
    String userId = "no id";
    private static final String ADD_SCORE_KEY = "0";
    private static final String PROGRESS_MESSAGE_KEY = "1";
    private static final String MODE_MESSAGE_KEY = "2";
    private static final String GAME_MESSAGE_KEY = "3";
    private static final String CLOSE_GAME_MESSAGE_KEY = "4";
    private static final String CIRCLES_GAME_MESSAGE_KEY = "5";

    /**
     * Method that receives messages from the watch.
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        try {
            System.out.println("Called " + messageEvent.getPath());
            String direction = "";
            int progress = 0;

            if (messageEvent.getPath().startsWith(ADD_SCORE_KEY)) {
                addScore(messageEvent.getPath());
            }
            else if (messageEvent.getPath().startsWith(PROGRESS_MESSAGE_KEY)) {
                progress = ByteBuffer.wrap(messageEvent.getData()).order(ByteOrder.LITTLE_ENDIAN).getInt(); // Byte array to integer
                direction = messageEvent.getPath().substring(1); //Get direction
                String score = direction.replaceAll("\\D+","");
                direction = direction.replaceAll("\\d","");
                MainActivity.updateProgressBar(direction, score, progress); //Update the UI
            }
            else if (messageEvent.getPath().startsWith(MODE_MESSAGE_KEY)) { // Tell the main activity the game mode and progress values
                System.out.println("MODE " + messageEvent.getPath());
                String gameMode = messageEvent.getPath().substring(1);
                if(gameMode.equals("game")){
                    MainActivity.playGame();
                }
                if(gameMode.equals("EASY")){
                    MainActivity.setMaximums(gameMode, 1000, 2000);
                }
                if(gameMode.equals("MEDIUM")){
                    MainActivity.setMaximums(gameMode, 2000, 4000);
                }
                if(gameMode.equals("HARD")){
                    MainActivity.setMaximums(gameMode, 4000, 8000);
                }
            }else if (messageEvent.getPath().startsWith(GAME_MESSAGE_KEY)) {//Update circles game x/y values
                progress = ByteBuffer.wrap(messageEvent.getData()).order(ByteOrder.LITTLE_ENDIAN).getInt();
                direction = messageEvent.getPath().substring(1);
                String score = direction.replaceAll("\\D+","");
                direction = direction.replaceAll("\\d","");
                if(isDirectionVertical(direction)) {
                    GameActivity.updateVertical(progress);
                }else{
                    GameActivity.updateHorizontal(progress);
                }
            }
            else if (messageEvent.getPath().startsWith(CLOSE_GAME_MESSAGE_KEY)) {//Close circles game
                sendBroadcast(new Intent("close"));
            }
            else if(messageEvent.getPath().startsWith(CIRCLES_GAME_MESSAGE_KEY) ){ //Open circles game
                String userId = messageEvent.getPath().substring(messageEvent.getPath().length()-6);
                MainActivity.setUserId(userId);
                MainActivity.playGame();
            }
            else{ //If it does not start with a key, then it's to be spoke via the phone
                speak(messageEvent.getPath());
            }
        }catch (Exception e){

        }
    }

    /**
     * Posts score to the MySQL table.
     * @param message
     */
    private void addScore(String message) {
        String[] splitMessage = message.split(" ");
        if(splitMessage.length==3) {
            String mode = splitMessage[0].substring(1);
            int score = Integer.parseInt(splitMessage[1]);
            userId = splitMessage[2];

            MainActivity.setUserId(userId);

            if (mode.contains("easy")) {
                new ScorePoster().execute(userId, Integer.toString(score), "Easy");
            }
            if (mode.contains("medium")) {
                new ScorePoster().execute(userId, Integer.toString(score), "Medium");
            }
            if (mode.contains("hard")) {
                new ScorePoster().execute(userId, Integer.toString(score), "Hard");
            }
        }
    }

    /**
     * Takes in the sound to be spoken as a String,
     * and it's spoken through the phone.
     * @param sound
     */
    private void speak(String sound){
        MainActivity.speak(sound);
    }

    /**
     * Check if message sent is vertical.
     * @param message
     * @return boolean
     */
    private boolean isDirectionVertical(String message){
        return message.equals("UP") || message.equals("DOWN");
    }

    /**
     * Check if message sent is horizontal.
     * @param message
     * @return boolean
     */
    private boolean isDirectionHorizontal(String message){
        return message.equals("LEFT") || message.equals("RIGHT");
    }

    /**
     * This method takes is specifically for the evaluation data.
     * It takes in a DataMap sent from the watch, translates it to a
     * hashmap and saves file using the hashmap values.
     * @param dataMap
     * @return
     */
    public void saveFile(DataMap dataMap) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        String fileString = "";

        for (String key : dataMap.keySet()) {
            hashMap.put(key, dataMap.getString(key));
        }
        for(int i = 0; i<hashMap.size(); i++){
            fileString = fileString + "\n" + hashMap.get(Integer.toString(i));
        }
        try{
            File path = getApplicationContext().getExternalFilesDir(null);
            File file = new File(path, "user"+generateUnique(4));
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(fileString.getBytes());
            } finally {
                stream.close();
            }
           }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Method for user evaluation phase, listens out for the data
     * containing the users time statistics.
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                    DataItem item = event.getDataItem();
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    saveFile(dataMap);
                    MainActivity.speak("I Just saved the file.");
            }
        }
    }
}