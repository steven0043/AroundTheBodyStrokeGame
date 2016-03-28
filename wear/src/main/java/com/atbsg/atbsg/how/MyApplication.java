package com.atbsg.atbsg.how;

import android.app.Activity;
import android.app.Application;

/**
 * Created by Steven on 25/01/2016.
 *
 * Application class, helps keep track of current activities
 * for use in the voice service, to end an activity based on
 * user speech.
 */
public class MyApplication extends Application {
    private Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Activity getCurrentActivity(){
        return currentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.currentActivity = mCurrentActivity;
    }

}