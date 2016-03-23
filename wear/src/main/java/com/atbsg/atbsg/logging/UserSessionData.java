
package com.atbsg.atbsg.logging;

import java.util.HashMap;

/**
 * Created by Steven on 03/03/2016.
 *
 * Class that stored the hashmap of participant session data.
 */
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

