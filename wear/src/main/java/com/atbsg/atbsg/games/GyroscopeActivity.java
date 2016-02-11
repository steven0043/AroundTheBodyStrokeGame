package com.atbsg.atbsg.games;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atbsg.atbsg.R;

import java.text.DecimalFormat;

public class GyroscopeActivity extends WearableActivity implements SensorEventListener {

    public TextView mTextView;
    public SensorManager mSensorManager;
    public Sensor mAccelerometer;
    boolean textBool = false;
    private long lastUpdate = 0;
    private ProgressBar mProgress;
    StringBuilder builder = new StringBuilder();
    private int mProgressStatus = 0;
    float [] history = new float[2];
    String [] direction = {"NONE","NONE"};
    final float alpha = (float) 0.8;
    private double[] gravity ={0,0,0};
    private double[] linear_acceleration ={0,0,0};

    public GyroscopeActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        setAmbientEnabled();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mProgress = (ProgressBar) findViewById(R.id.progressBar);
                textBool = true;

            }
        });
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

    }

    public void onSensorChanged(SensorEvent event){
        long curTime = System.currentTimeMillis();

        /*float xChange = history[0] - event.values[0];
        float yChange = history[1] - event.values[1];

        history[0] = event.values[0];
        history[1] = event.values[1];*/

        if((curTime - lastUpdate) > 10) {

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            if (textBool == true) {
                //mTextView.setText("\t\n x: " + direction +"\t\n x: " + linear_acceleration[0] + "\t\n y: " + df.format(linear_acceleration[1]) + "\t\n z: " + df.format(linear_acceleration[2]));
                /*mTextView.setText("\t\n x: " + direction +"\t\n x: " + df.format(xAcc) + "\t\n y: " + df.format(yAcc) + "\t\n z: " + df.format(zAcc));
                doWork();*/
                 mTextView.setText("X " + linear_acceleration[0] + "\nZ " + + linear_acceleration[2]);
                 //mTextView.setText("X " + event.values[0] + "\nY " + + event.values[1] + "\nZ " + + event.values[2]);
            }
            lastUpdate = curTime;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        System.out.println("PAUSEESEDDD");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        System.out.println("DESTROOOYYYEDD");
        mSensorManager.unregisterListener(this);
        finish();
        super.onDestroy();
    }

    private void doWork(){
        mProgress.setProgress(mProgressStatus);
    }
}

