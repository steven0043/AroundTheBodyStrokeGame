package com.atbsg.atbsg;

/**
 * Created by Steven on 25/01/2016.
 */

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;


public class ListenerService extends WearableListenerService {
    boolean scoreAdded = false;
    String userId = "no id";

    /**
     * Method that receives messages from the watch.
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        try {
            System.out.println("Called " + messageEvent.getPath() );
            String direction = "";
            int progress = 0;
            addScore(messageEvent.getPath());
            if (!scoreAdded && messageEvent.getPath().startsWith("1")) {
                progress = ByteBuffer.wrap(messageEvent.getData()).order(ByteOrder.LITTLE_ENDIAN).getInt(); // Byte array to integer
                direction = messageEvent.getPath().substring(1); //Get direction
                String score = direction.replaceAll("\\D+","");
                direction = direction.replaceAll("\\d","");
                MainActivity.updateProgressBar(direction, score, progress); //Update the UI
            }
            else if (!scoreAdded && messageEvent.getPath().startsWith("2")) {
                System.out.println("MODE " + messageEvent.getPath());
                String gameMode = messageEvent.getPath().substring(1);
                if(gameMode.equals("game")){
                    System.out.println("LAUNCHING");
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
            }else if (!scoreAdded && messageEvent.getPath().startsWith("3")) {
                progress = ByteBuffer.wrap(messageEvent.getData()).order(ByteOrder.LITTLE_ENDIAN).getInt();
                direction = messageEvent.getPath().substring(1);
                String score = direction.replaceAll("\\D+","");
                direction = direction.replaceAll("\\d","");
                if(isDirectionVertical(direction)) {
                    AndroidLauncher.updateVertical(progress);
                }else{
                    AndroidLauncher.updateHorizontal(progress);
                }
            }
            else if (!scoreAdded && messageEvent.getPath().startsWith("4")) {
               /* Intent intent = new Intent("close");
                Bundle b=new Bundle();
                b.putString("userId", userId);
                intent.putExtras(b);
                getApplicationContext().sendBroadcast(intent);*/
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
            System.out.println("mode is " + mode);
            int score = Integer.parseInt(splitMessage[1]);
            userId = splitMessage[2];

            MainActivity.setUserId(userId);
            scoreAdded = true;
            if (mode.contains("easy")) {
                System.out.println("Adding easy");
                new ScorePoster().execute(userId, Integer.toString(score), new Date().toString(), "Easy");
            }
            if (mode.contains("medium")) {
                System.out.println("Adding medium" );
                new ScorePoster().execute(userId, Integer.toString(score), new Date().toString(), "Medium");
            }
            if (mode.contains("hard")) {
                System.out.println("Adding hard" );
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

    private boolean isDirectionVertical(String message){
        if(message.equals("UP") || message.equals("DOWN")){
            return true;
        }else{
            return false;
        }
    }
}