package com.atbsg.atbsg.how;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import com.google.android.gms.wearable.Node;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Steven on 25/01/2016.
 */
public class MyApplication extends Application {

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final String MESSAGE = "Hello Wear!";

    private GoogleApiClient client;
    private String nodeId;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("IN APPLICATION");
       // initApi();
        //sendToast();
    }

    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }


    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
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
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */
    private void sendToast() {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE, null);
                    client.disconnect();
                    System.out.println("SENDING TOAST");
                }
            }).start();
        }
    }
}