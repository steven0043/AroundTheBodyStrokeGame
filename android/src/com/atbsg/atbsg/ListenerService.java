package com.atbsg.atbsg;

/**
 * Created by Steven on 25/01/2016.
 */

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;


public class ListenerService extends WearableListenerService {
    private static final String UP_SOUND = "UP";
    private static final String DOWN_SOUND = "DOWN";
    private static final String LEFT_SOUND = "LEFT";
    private static final String RIGHT_SOUND = "RIGHT";
    private static final String START_SPEECH = "Welcome to the around the body stroke recovery game. Your starting " +
            "options are: how to play, game modes, my progress and settings";
    boolean scoreAdded = false;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        try {
            System.out.println("Called " + messageEvent.getPath() );
            String direction = "";
            int progress = 0;
            addScore(messageEvent.getPath());
            if (!scoreAdded && messageEvent.getPath().startsWith("1")) {
                progress = ByteBuffer.wrap(messageEvent.getData()).order(ByteOrder.LITTLE_ENDIAN).getInt();
                direction = messageEvent.getPath().substring(1);
                String score = direction.replaceAll("\\D+","");
                direction = direction.replaceAll("\\d","");
                MainActivity.updateProgressBar(direction, score, progress);
            }
            else if (!scoreAdded && messageEvent.getPath().startsWith("2")) {
                System.out.println("MODE " + messageEvent.getPath());
                String gameMode = messageEvent.getPath().substring(1);
                if(gameMode.equals("game")){
                    System.out.println("LAUNCHING");
                    MainActivity.playGame();
                }
                if(gameMode.equals("EASY")){
                    MainActivity.setMaximums(gameMode, 500, 2000);
                }
                if(gameMode.equals("MEDIUM")){
                    MainActivity.setMaximums(gameMode, 1000, 4000);
                }
                if(gameMode.equals("HARD")){
                    MainActivity.setMaximums(gameMode, 2000, 8000);
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
            else{
                if(!scoreAdded) {
                    soundChecker(messageEvent.getPath());
                }
            }
        }catch (Exception e){

        }
    }

    private void addScore(String message) {
        String[] splitMessage = message.split(" ");
        if(splitMessage.length==3) {
            String mode = splitMessage[0];
            System.out.println("mode is " + mode );
            int score = Integer.parseInt(splitMessage[1]);
            String userId = splitMessage[2];
            scoreAdded = true;
            if (mode.contains("easy")) {
                System.out.println("Adding easy" );
                new ScorePoster().execute(userId, Integer.toString(score), new Date().toString(), "Easy");
               /* ParseObject easyMode = new ParseObject("EasyMode");
                easyMode.put("userId", userId);
                easyMode.put("score", score);
                easyMode.saveInBackground();*/
            }
            if (mode.contains("medium")) {
                System.out.println("Adding medium" );
                new ScorePoster().execute(userId, Integer.toString(score), new Date().toString(), "Medium");
                /*ParseObject mediumMode = new ParseObject("MediumMode");
                mediumMode.put("userId", userId);
                mediumMode.put("score", score);
                mediumMode.saveInBackground();*/
            }
            if (mode.contains("hard")) {
                System.out.println("Adding hard" );
                new ScorePoster().execute(userId, Integer.toString(score), new Date().toString(), "Hard");
               /* ParseObject hardMode = new ParseObject("HardMode");
                hardMode.put("userId", userId);
                hardMode.put("score", score);
                hardMode.saveInBackground();*/
            }
        }
    }

    private void soundChecker(String sound){
        if(sound.equals(UP_SOUND)){
            //playUpSound();
            MainActivity.speak(UP_SOUND);
        }
        else if(sound.equals(DOWN_SOUND)){
            //playDownSound();
            MainActivity.speak(DOWN_SOUND);
        }
        else if(sound.equals(LEFT_SOUND)){
            //playLeftSound();
            MainActivity.speak(LEFT_SOUND);
        }
        else if(sound.equals(RIGHT_SOUND)){
            //playRightSound();
            MainActivity.speak(RIGHT_SOUND);
        }
        else if(sound.equals(START_SPEECH)){
            MainActivity.speak(START_SPEECH);
        }
        else{
            MainActivity.speak(sound);
        }
    }

    private boolean isDirectionVertical(String message){
        if(message.equals("UP") || message.equals("DOWN")){
            return true;
        }else{
            return false;
        }
    }
}