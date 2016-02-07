package com.atbsg.atbsg;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
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

    public CloudLogger(Context context){
        this.context=context;
        contextClass = context.getClass().getSimpleName();
    }

    public void initApi() {
        client = getGoogleApiClient(context);
        retrieveDeviceNode();
    }

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
                    System.out.println("CONNECTED!!! " + nodeId);
                }
                client.disconnect();
            }
        }).start();
    }

    public void playStartMessages(){
        //sendScoreToCloud("Hello from phone!");
    }
    /**
     * Sends a message to the connected mobile device
     */
    public void sendScoreToCloud(final String gameMode) {
        //System.out.println("SENDING score null");
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
    public boolean isConnected(){
        return connected;
    }

}