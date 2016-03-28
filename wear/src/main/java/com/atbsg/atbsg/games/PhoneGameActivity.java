package com.atbsg.atbsg.games;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.WindowManager;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.how.MyApplication;
import com.atbsg.atbsg.logging.CloudLogger;

/**
 * Created by Steven on 07/02/2016.
 */
public class PhoneGameActivity extends WearableActivity {

    public CloudLogger cloudLogger;
    private PhoneGameListener sensorListener;
    protected MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cloudLogger = new CloudLogger(this);
        cloudLogger.initApi();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_game);
        setAmbientEnabled();
        myApplication = (MyApplication)this.getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        runOnUiThread(new Runnable() {
            public void run() {
                sensorListener = new PhoneGameListener((SensorManager) getSystemService(SENSOR_SERVICE), PhoneGameActivity.this);
            }
        });
    }

    /**
     * Sends the current progress to the phone for
     * use during the circle game.
     * @param values
     * @param progress
     */
    public void sendToPhone(String values, int progress){
        cloudLogger.sendProgressToPhone(values, progress);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {

    }

    @Override
     protected void onResume() {
        super.onResume();
        myApplication.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        clearCurrentActivity();
        super.onPause();
    }

    /**
     *Clear the current activity reference
     */
    private void clearCurrentActivity(){
        Activity currActivity = myApplication.getCurrentActivity();
        if (this.equals(currActivity))
            myApplication.setCurrentActivity(null);
    }

    /**
     * Activity onDestroy, unregisters the sensor listener
     */
    @Override
    protected void onDestroy() {
        //Tells the phone to exit the circle game.
        cloudLogger.sendToPhone("4");
        sensorListener.unregister();
        clearCurrentActivity();
        finish();
        super.onDestroy();
    }
}
