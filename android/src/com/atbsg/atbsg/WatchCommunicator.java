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
 * Created by Steven on 13/01/2016.
 *
 * This class is specifically for connecting and
 * communicating with the watch.
 */
public class WatchCommunicator {
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private GoogleApiClient client;
    private String nodeId;
    private Context context;
    public boolean connected = false;

    public WatchCommunicator(Context context){
        this.context=context;
    }

    /**
     * Initiates the connection to the watch
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
                    connected = true;
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * Sends message to the watch that's connected.
     */
    public void sendToWatch(final String gameMode) {
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
     * Returns a boolean indicating if a connection has been
     * established with the watch.
     * @return connected
     */
    public boolean isConnected(){
        return connected;
    }

}