package com.atbsg.atbsg.games;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.logging.CloudLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhoneGameActivity extends WearableActivity {


    private TextView mTextView;
    public CloudLogger cloudLogger;
    private PhoneGameListener sensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cloudLogger = new CloudLogger(this);
        cloudLogger.initApi();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_game);
        setAmbientEnabled();
        mTextView = (TextView) findViewById(R.id.text);
        runOnUiThread(new Runnable() {
            public void run() {
                sensorListener = new PhoneGameListener((SensorManager) getSystemService(SENSOR_SERVICE), PhoneGameActivity.this);
            }
        });
    }

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
    protected void onDestroy() {
        cloudLogger.sendScoreToCloud("4");
        sensorListener.unregister();
        finish();
        super.onDestroy();
    }
}
