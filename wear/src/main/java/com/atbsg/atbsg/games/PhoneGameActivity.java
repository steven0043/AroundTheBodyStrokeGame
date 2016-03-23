package com.atbsg.atbsg.games;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.logging.CloudLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Steven on 07/02/2016.
 */
public class PhoneGameActivity extends WearableActivity {

    public CloudLogger cloudLogger;
    private PhoneGameListener sensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cloudLogger = new CloudLogger(this);
        cloudLogger.initApi();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_game);
        setAmbientEnabled();
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

    /**
     * Activity onDestroy, unregisters the sensor listener
     */
    @Override
    protected void onDestroy() {
        //Tells the phone to exit the circle game.
        cloudLogger.sendScoreToCloud("4");
        sensorListener.unregister();
        finish();
        super.onDestroy();
    }
}
