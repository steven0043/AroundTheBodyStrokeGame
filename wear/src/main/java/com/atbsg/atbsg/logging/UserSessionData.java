
package com.atbsg.atbsg.logging;

import android.os.Environment;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class UserSessionData {

    static HashMap<String, String> hm = new HashMap<String, String>();

    public UserSessionData(){

    }

    public static void clear(){
        hm.clear();
    }

    public void addToHashMap(String key, String value){
        hm.put(key, value);
    }

    public static HashMap<String, String>  getMap(){
        return hm;
    }
}

