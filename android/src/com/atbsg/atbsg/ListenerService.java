package com.atbsg.atbsg;

/**
 * Created by Steven on 25/01/2016.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class ListenerService extends WearableListenerService {
    boolean scoreAdded = false;
    String userId = "no id";
    private String file = "mydata";
/*    GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle connectionHint) {
                    // Now you can use the Data Layer API
                }
                @Override
                public void onConnectionSuspended(int cause) {
                }
            })
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {

                }
            })
                    // Request access only to the Wearable API
            .addApi(Wearable.API)
            .build();*/

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

    public HashMap<String, String> fromDataMap(DataMap dataMap) {
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
            System.out.println("FILING" + path.getAbsolutePath());
            try {
                stream.write(fileString.getBytes());
            } finally {
                stream.close();
            }
           /* FileOutputStream fOut = null;

            fOut = this.openFileOutput(generateUnique(3),MODE_WORLD_READABLE);
            fOut.write(fileString.getBytes());
            fOut.close();
            System.out.println("FILING" + this.getFilesDir());*/}
        catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
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

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        System.out.println("DATA CHANGED");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                    System.out.println("IN CHANGED");
                    DataItem item = event.getDataItem();
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    HashMap hm = fromDataMap(dataMap);
                    MainActivity.speak("I Just saved the file.");
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
                System.out.println("DELETED");
            }
        }

        /*MainActivity.speak("MAKING FILE");
        System.out.println("DATA");*/
       /* for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/txt"))
            {
                // Get the Asset object
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset asset = dataMapItem.getDataMap().getAsset("com.example.company.key.TXT");

                ConnectionResult result =
                        mGoogleApiClient.blockingConnect(10000, TimeUnit.MILLISECONDS);
                if (!result.isSuccess()) {return;}

                // Convert asset into a file descriptor and block until it's ready
                InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                        mGoogleApiClient, asset).await().getInputStream();
                mGoogleApiClient.disconnect();
                if (assetInputStream == null) { return; }

                // Get folder for output
                MainActivity.speak("MAKING FILE");
                File sdcard = Environment.getDataDirectory();
                File dir = new File(sdcard.getAbsolutePath() + "/MyAppFolder/");
                if (!dir.exists()) { dir.mkdirs(); } // Create folder if needed

                // Read data from the Asset and write it to a file on external storage
                final File file = new File(dir, "test.txt");
                try {
                    FileOutputStream fOut = new FileOutputStream(file);
                    int nRead;
                    byte[] data = new byte[16384];
                    while ((nRead = assetInputStream.read(data, 0, data.length)) != -1) {
                        fOut.write(data, 0, nRead);
                    }

                    fOut.flush();
                    fOut.close();
                }
                catch (Exception e)
                {
                }

                // Rescan folder to make it appear
                try {
                    String[] paths = new String[1];
                    paths[0] = file.getAbsolutePath();
                    MediaScannerConnection.scanFile(this, paths, null, null);
                } catch (Exception e) {
                }
            }
        }*/
    }
}