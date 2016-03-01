package com.atbsg.atbsg.games;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Steven on 29/02/2016.
 */
public class MagnetometerData implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    public double[] magentometer ={0,0,0};
    private long lastUpdate = 0;

    public MagnetometerData(Context context){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        register();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1){
        // TODO

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        // TODO
        long curTime = System.currentTimeMillis();
        if((curTime - lastUpdate) > 10) {
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                addMagnetometer(event);
                //System.out.println("MAGNETOMETER");
            }
        }
        lastUpdate = curTime;
    }

    /**
     * Update the phones view to reflect the progress values, direction and score.
     */
    private void addMagnetometer(SensorEvent event){
        magentometer[0] = event.values[0];
        magentometer[1] = event.values[1];
        magentometer[2] = event.values[2];
    }

    public double[] getMagentometer(){
        return magentometer;
    }

    public double getX(){
        return magentometer[0];
    }
    public double getY(){
        return magentometer[1];
    }
    public double getZ(){
        return magentometer[2];
    }

    public void register(){
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregister(){
        mSensorManager.unregisterListener(this);
    }
}