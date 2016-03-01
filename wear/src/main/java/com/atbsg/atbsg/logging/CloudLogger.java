package com.atbsg.atbsg.logging;


import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Steve on 13/01/2016.
 */
public class CloudLogger {
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private GoogleApiClient client;
    private String nodeId;
    private Context context;
    private String contextClass = "";
    public boolean connected = false;
    //static HashMap<String, String> hm = new HashMap<String, String>();
    UserSessionData usd = new UserSessionData();
    //Logger logger;

    public CloudLogger(Context context){
        this.context=context;
        contextClass = context.getClass().getSimpleName();
        //logger = new Logger((Activity) context);
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
        System.out.println("Initializing!!!");
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

    public void playStartMessages(){
        if(contextClass.equals("MenuActivity")){
           // sendScoreToCloud("Welcome to the around the body stroke recovery game. Your starting " +
                    //"options are: how to play, game modes, my progress and settings");
        }
        if(contextClass.equals("CalibrationActivity")){
            sendScoreToCloud("Before we start, look straight ahead. Then please make sure the watch face is parallel to your own face");
        }
        if(contextClass.equals("HowActivity")){
           /* sendScoreToCloud("To play this game, strap the watch firmly on your wrist and follow the directions on screen. You can swipe right to move back a screen.");*/
        }
        if(contextClass.equals("SensorActivity")){
            //sendScoreToCloud("2game");
            sendScoreToCloud("Please move your arm up!");
        }
        if(contextClass.equals("PhoneGameActivity")){
            sendScoreToCloud("2game");
        }
    }

    /**
     * Sends message to the phone that's connected.
     */
    public void sendScoreToCloud(final String gameMode) {
        System.out.println("SENDING score null");
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("SENDING score");
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
        //System.out.println("SENDING PROGRESS null");
        final byte[] progressByte = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(progress).array();
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("SENDING PROGRESS");
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