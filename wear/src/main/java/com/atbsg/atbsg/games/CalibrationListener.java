package com.atbsg.atbsg.games;

/**
 * Created by Steven on 13/01/2016.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.atbsg.atbsg.games.*;


public class CalibrationListener implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private CalibrationActivity currentActivity;
    private long lastUpdate = 0;
    private int holdTime;


    public CalibrationListener(SensorManager sm, CalibrationActivity currentActivity){
        mSensorManager = sm;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.currentActivity = currentActivity;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Implemented method, gets called every time the
     * registered accelerometer sensor is changed.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        float yAcc = 0;
        System.out.println("STILL LISTENING !!");
        long curTime = System.currentTimeMillis();
        if((curTime - lastUpdate) > 90) {
            yAcc = event.values[1];
            checkCalibration(yAcc);
            lastUpdate = curTime;
            System.out.println("STILL LISTENING !!");
        }
    }

    /**
     * Checks to see if the user is holding the watch
     * face parallel to their own by checking the
     * current 'y' value.
     * @param yAcc
     */
    private void checkCalibration(float yAcc){
        if(yAcc > 8){
            holdTime = holdTime + 90;
        }
        else{
            holdTime = 0;
        }

        if(holdTime > 3000) {
            currentActivity.startGame();
        }
        else if(holdTime > 1000){
            currentActivity.setImageView(true);
        }else {
            currentActivity.setImageView(false);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Unregisters the accelerometer from the sensor manager.
     */
    public void unregister() {
        System.out.println("DESTROOOYYYEDD");
        mSensorManager.unregisterListener(this);
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.flush(this);
    }
}
