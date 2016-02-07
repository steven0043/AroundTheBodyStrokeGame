package com.atbsg.atbsg.logging;

/**
 * Created by Steven on 06/02/2016.
 */

import com.atbsg.atbsg.games.PhoneGameListener;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println("Called wear" + messageEvent.getPath());
        if(messageEvent.getPath().equals("reset")){
            PhoneGameListener.reset();
        }else{
            PhoneGameListener.setPhoneDirection(messageEvent.getPath());
        }
    }
}