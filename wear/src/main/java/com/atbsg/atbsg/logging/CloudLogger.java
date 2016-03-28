package com.atbsg.atbsg.logging;


import android.app.Activity;
import android.content.Context;

import com.atbsg.atbsg.menu.ListActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Steven on 13/01/2016.
 *
 * This class is specifically for connecting and
 * communicating with the phone.
 */


public class CloudLogger {
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private GoogleApiClient client;
    private String nodeId;
    private Context context;
    private String contextClass = "";
    public boolean connected = false;
    UserSessionData usd = new UserSessionData();
    private Logger logger;

    public CloudLogger(Context context){
        this.context=context;
        contextClass = context.getClass().getSimpleName();
    }

    /**
     * Initiates the connection to the phone
     */
    public void initApi() {
        client = getGoogleApiClient(context);
        retrieveDeviceNode();
    }

    /**
     * Gets the Google API client
     * @param context
     * @return the created Google API client
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                    playStartMessages();
                    connected = true;
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * Based on the activity or screen name that connected to the phone, play an appropriate message.
     */
    public void playStartMessages(){
        if(contextClass.equals("ListActivity")){
            if(ListActivity.screenName.equals("main")){
                sendToPhone("Welcome to the around the body stroke recovery game. Your starting " +
                        "options are: how to play, game modes, my progress, settings and play game on phone");
            }
            if(ListActivity.screenName.equals("settings")){
                sendToPhone("Your options are: Your unique I.D., change user, create new user and delete user");
            }
            if(ListActivity.screenName.equals("view")){
                sendToPhone("Here, you can select the user you wish to change to");
            }
            if(ListActivity.screenName.equals("delete")){
                sendToPhone("Here, you can select the user you wish to delete");
            }
            if(ListActivity.screenName.equals("progress")) {
                logger = new Logger((Activity) context);
                sendToPhone("Your easy High Score is: " + logger.getEasyScore() +
                        ", Your Last Easy Score is : " + logger.getLastEasyScore() +
                        ", Your Medium High Score is: " + logger.getMediumScore() +
                        ", Your Last Medium Score is: " + logger.getLastMediumScore() +
                        ", Your Hard High Score is : " + logger.getHardScore() +
                        ", Your Last Hard Score is : " + logger.getLastHardScore());
            }
        }
        if(contextClass.equals("CalibrationActivity")){
            sendToPhone("Before we start, look straight ahead. Then please make sure the watch face is parallel to your own face");
        }
        if (contextClass.equals("TextActivity")) {
            logger = new Logger((Activity) context);
            if(ListActivity.screenName.equals("unique")){
                sendToPhone("Your unique I.D. is " + logger.getUniqueId() + ". Your physiotherapist can view your progress via the web service using this I.D.");
            }else{
               sendToPhone("To play this game, strap the watch firmly on your wrist and follow the directions on screen. You can swipe right to move back a screen.");
            }
        }
        if(contextClass.equals("ExerciseActivity")){
            sendToPhone("Please move your arm up!");
        }
        if(contextClass.equals("PhoneGameActivity")){
            logger = new Logger((Activity) context);
            sendToPhone("5game"+logger.getUniqueId());
        }
    }

    /**
     * Sends message to the phone that's connected.
     */
    public void sendToPhone(final String gameMode) {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, gameMode, null);
                    client.disconnect();
                }
            }).start();
        }
    }

    /**
     * Sends message to the phone that's connected.
     */
    public void sendProgressToPhone(final String direction, int progress) {
        final byte[] progressByte = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(progress).array();
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, direction, progressByte);
                    client.disconnect();
                }
            }).start();
        }
    }

    /**
     * Returns a boolean indicating if a connection has been
     * established with the phone.
     * @return connected
     */
    public boolean isConnected(){
        return connected;
    }

    /**
     * Convert HashMap to DateMap for sending to phone.
     * This was for the user evaluation phase.
     * @param hashMap
     * @return
     */
    public PutDataMapRequest toDataMap(HashMap<String, String> hashMap) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/file"+generateUnique(3));

        DataMap dataMap = new DataMap();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            putDataMapReq.getDataMap().putString(entry.getKey(), entry.getValue());
        }
        return putDataMapReq;
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
     * Used for evaluation phase to send data file to phone for saving
     */
    public void sendTextFile()
    {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("SENDING PROGRESS");
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    PutDataRequest putDataReq = toDataMap(usd.getMap()).asPutDataRequest();
                    Wearable.DataApi.putDataItem(client, putDataReq);
                    client.disconnect();
                }
            }).start();
        }
    }

    public void addToHashMap(String key, String value){
         usd.addToHashMap(key, value);
    }
}