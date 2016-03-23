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
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class ListenerService extends WearableListenerService {
    boolean scoreAdded = false;
    String userId = "no id";
    private static final String PROGRESS_MESSAGE_KEY = "1";
    private static final String MODE_MESSAGE_KEY = "2";
    private static final String GAME_MESSAGE_KEY = "3";
    private static final String CLOSE_GAME_MESSAGE_KEY = "4";

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
            addScore(messageEvent.getPath());
            if (!scoreAdded && messageEvent.getPath().startsWith(PROGRESS_MESSAGE_KEY)) {
                progress = ByteBuffer.wrap(messageEvent.getData()).order(ByteOrder.LITTLE_ENDIAN).getInt(); // Byte array to integer
                direction = messageEvent.getPath().substring(1); //Get direction
                String score = direction.replaceAll("\\D+","");
                direction = direction.replaceAll("\\d","");
                System.out.println("Updating");
                MainActivity.updateProgressBar(direction, score, progress); //Update the UI
            }
            else if (!scoreAdded && messageEvent.getPath().startsWith(MODE_MESSAGE_KEY)) {
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
            }else if (!scoreAdded && messageEvent.getPath().startsWith(GAME_MESSAGE_KEY)) {
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
            else if (!scoreAdded && messageEvent.getPath().startsWith(CLOSE_GAME_MESSAGE_KEY)) {
                sendBroadcast(new Intent("close"));
            }
            else{
                if(!scoreAdded) {
                    soundChecker(messageEvent.getPath());
                }
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
            String mode = splitMessage[0];
            int score = Integer.parseInt(splitMessage[1]);
            userId = splitMessage[2];

            MainActivity.setUserId(userId);
            scoreAdded = true;
            if (mode.contains("easy")) {
                new ScorePoster().execute(userId, Integer.toString(score), new Date().toString(), "Easy");
            }
            if (mode.contains("medium")) {
                new ScorePoster().execute(userId, Integer.toString(score), new Date().toString(), "Medium");
            }
            if (mode.contains("hard")) {
                new ScorePoster().execute(userId, Integer.toString(score), new Date().toString(), "Hard");
            }
        }
    }

    /**
     * Takes in the sound to be spoken as a String,
     * and it's spoken through the phone.
     * @param sound
     */
    private void soundChecker(String sound){
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