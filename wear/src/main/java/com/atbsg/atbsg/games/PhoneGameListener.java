package com.atbsg.atbsg.games;

/**
 * Created by Steven on 07/02/2016.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;


public class PhoneGameListener implements SensorEventListener {

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final PhoneGameActivity currentActivity;
    private long lastUpdate = 0;
    final float alpha = (float) 0.8;
    private double[] gravity ={0,0,0};
    private double[] linear_acceleration ={0,0,0};
    private static int mProgressStatus = 0;
    boolean moved = false;
    DirectionHelper gameHelper = new DirectionHelper();
    int horizontalMax = 950;
    int verticalMax = 2000;
    public static String currentPhoneMotion = "UP";
    float [] history = new float[2];
    String [] direction = {"NONE","NONE"};

    public PhoneGameListener(SensorManager sm, PhoneGameActivity currentActivity){
        this.currentActivity = currentActivity;
        mSensorManager = sm;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

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

            if(currentPhoneMotion.equals("LEFT") || currentPhoneMotion.equals("RIGHT")){
                gameHelper.addToHistory(linear_acceleration[0] * 2);
                //gameHelper.addToHistory(linear_acceleration[0]);
            }
            if(currentPhoneMotion.equals("UP") || currentPhoneMotion.equals("DOWN")){
                gameHelper.addToUpHistory(linear_acceleration[2]);
            }

            if(linear_acceleration[2] < -0.25 && currentPhoneMotion.equals("UP") &&
                    gameHelper.goingUp()){
                if(mProgressStatus < verticalMax && mProgressStatus >= 0) {
                    mProgressStatus = (int) (mProgressStatus + (((-linear_acceleration[2]+ 1)*2) * (((-gameHelper.getUpAverage())) * (16-((gameHelper.getHighestUpDownAverage()/3)*4)))));
                }
            }
            else if (linear_acceleration[2] > 0.25 && currentPhoneMotion.equals("DOWN")
                    && gameHelper.goingDown()){
                if(mProgressStatus > 0 && mProgressStatus <= 2000) {
                    mProgressStatus = (int) (mProgressStatus - (((linear_acceleration[2]+1)*2) * (((gameHelper.getDownAverage())) * (16-((gameHelper.getHighestUpDownAverage()/3)*4)))));
                }
            }
            else if(linear_acceleration[0] < -0.005 && currentPhoneMotion.equals("RIGHT")
                    && /*gameHelper.goingRight()*/ gameHelper.goingRight()){
                if(mProgressStatus < horizontalMax && mProgressStatus >= 0) {
                    //System.out.println(" GOING RIGHT!! \n +" + (toPositive(linear_acceleration[0]*3) * (toPositive(gameHelper.getRightAverage())*(24-((toPositive(gameHelper.getHighestLeftRightAverage())+1)*4)))));
                    //mProgressStatus = (int) (mProgressStatus + -(-linear_acceleration[0] * 5) * (gameHelper.getRightAverage() * (60 - ((gameHelper.getHighestLeftRightAverage() + 3) * 4))));
                    mProgressStatus = (int) (mProgressStatus + (((-linear_acceleration[2]+ 1)*2) * (((-gameHelper.getRightAverage())) * (16-((gameHelper.getHighestLeftRightAverage()/3)*4)))));
                }
            }
            else if (linear_acceleration[0] > 0.005 && currentPhoneMotion.equals("LEFT")
                    &&/* gameHelper.goingLeft()*/ gameHelper.goingLeft()){
                if(mProgressStatus>horizontalMax){
                    mProgressStatus = horizontalMax;
                }
                if(mProgressStatus > 0 && mProgressStatus <= 950) {
                    // System.out.println("progress " + mProgressStatus + " LEFT SUM " + (- -(-linear_acceleration[0]*5) * (gameHelper.getLeftAverage()*(60-((gameHelper.getHighestLeftRightAverage()+3)*4)))));
                    //mProgressStatus = (int) (mProgressStatus - -(-linear_acceleration[0]*5) * (gameHelper.getLeftAverage()*(60-(((-gameHelper.getHighestLeftRightAverage())+3)*4))));
                    mProgressStatus = (int) (mProgressStatus - (((linear_acceleration[2]+ 1)*2) * (((gameHelper.getLeftAverage())) * (16-((gameHelper.getHighestLeftRightAverage()/3)*4)))));
                }
            }

            currentActivity.sendToPhone("30"+currentPhoneMotion, mProgressStatus);
            lastUpdate = curTime;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void setPhoneDirection(String direction){
        currentPhoneMotion = direction;
        reset();
    }

    public static void reset(){
        if(currentPhoneMotion.equals("DOWN")){
            mProgressStatus = 2000;
        }
        if(currentPhoneMotion.equals("LEFT")) {
            mProgressStatus = 950;
        }
        else if(currentPhoneMotion.equals("RIGHT") || currentPhoneMotion.equals("UP")){
            mProgressStatus = 0;
        }
    }

    public void unregister() {
        //playSound("Your final score is " + score);
        System.out.println("DESTROOOYYYEDD");
        mProgressStatus = 0;
        currentPhoneMotion = "UP";
        mSensorManager.unregisterListener(this);
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.flush(this);
    }
}
