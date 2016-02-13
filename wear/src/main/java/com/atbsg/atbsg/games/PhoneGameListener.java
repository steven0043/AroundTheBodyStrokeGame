package com.atbsg.atbsg.games;

/**
 * Created by Steven on 07/02/2016.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PhoneGameListener implements SensorEventListener {

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final PhoneGameActivity currentActivity;
    private long lastUpdate = 0;
    final float alpha = (float) 0.8;
    private double[] gravity ={0,0,0};
    private double[] linear_acceleration ={0,0,0};
    private static int mProgressStatus = 0;
    DirectionHelper directionHelper = new DirectionHelper();
    int horizontalMax = 950;
    int verticalMax = 2000;
    public static String currentPhoneMotion = "UP";

    public PhoneGameListener(SensorManager sm, PhoneGameActivity currentActivity){
        this.currentActivity = currentActivity;
        mSensorManager = sm;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long curTime = System.currentTimeMillis();

        if((curTime - lastUpdate) > 10) {

            removeGravity(event);

            addToAverages();

            if(goingUp()){
                applyUpWeighting();
            }
            else if (goingDown()){
                applyDownWeighting();
            }
            else if(goingRight()){
                applyRightWeighting();
            }
            else if (goingLeft()) {
                applyLeftWeighting();
            }

            updateView();

            lastUpdate = curTime;
        }
    }

    public void removeGravity(SensorEvent event){
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
    }

    public void addToAverages(){
        if(currentPhoneMotion.equals("LEFT") || currentPhoneMotion.equals("RIGHT")){
            directionHelper.addToHorizontalHistory(linear_acceleration[0] * 2);
        }
        if(currentPhoneMotion.equals("UP") || currentPhoneMotion.equals("DOWN")){
            directionHelper.addToVerticalHistory(linear_acceleration[2]);
        }
    }

    public boolean goingUp(){
        return linear_acceleration[2] < -0.25 && currentPhoneMotion.equals("UP") &&
                directionHelper.goingUp();
    }

    public void applyUpWeighting(){
        if(mProgressStatus < verticalMax && mProgressStatus >= 0) {
            mProgressStatus = (int) (mProgressStatus + (((-linear_acceleration[2]+ 1)*2) * (((-directionHelper.getUpAverage())) * (16-((directionHelper.getHighestCurrentAverage()/3)*4)))));
        }
    }

    public boolean goingDown(){
        return linear_acceleration[2] > 0.25 && currentPhoneMotion.equals("DOWN")
                && directionHelper.goingDown();
    }

    public void applyDownWeighting(){
        if(mProgressStatus > 0 && mProgressStatus <= 2000) {
            mProgressStatus = (int) (mProgressStatus - (((linear_acceleration[2]+1)*2) * (((directionHelper.getDownAverage())) * (16-((directionHelper.getHighestCurrentAverage()/3)*4)))));
        }
    }

    public boolean goingRight(){
        return linear_acceleration[0] < -0.005 && currentPhoneMotion.equals("RIGHT")
                && /*directionHelper.goingRight()*/ directionHelper.goingRight();
    }

    public void applyRightWeighting(){
        if(mProgressStatus < horizontalMax && mProgressStatus >= 0) {
            double highestAvgWeight =  Math.abs(16-((directionHelper.getHighestCurrentAverage()/3)*4));
            double currentAvgWeight = Math.abs(-directionHelper.getRightAverage());
            double currentValueWeight = Math.abs(((-linear_acceleration[2]+1)*2));
            mProgressStatus = (int) (mProgressStatus + (currentValueWeight * (currentAvgWeight * highestAvgWeight)));
        }
    }

    public boolean goingLeft(){
        return linear_acceleration[0] > 0.005 && currentPhoneMotion.equals("LEFT")
                && directionHelper.goingLeft();
    }

    public void applyLeftWeighting(){
        if(mProgressStatus>horizontalMax){
            mProgressStatus = horizontalMax;
        }
        if(mProgressStatus > 0 && mProgressStatus <= 950) {
            double highestAvgWeight =  Math.abs(16-((directionHelper.getHighestCurrentAverage()/3)*4));
            double currentAvgWeight = Math.abs(directionHelper.getLeftAverage());
            double currentValueWeight = Math.abs(((linear_acceleration[2]+ 1)*2));
            mProgressStatus = (int) (mProgressStatus - (currentAvgWeight * (currentAvgWeight* highestAvgWeight)));
        }
    }

    public void updateView(){
        currentActivity.sendToPhone("30"+currentPhoneMotion, mProgressStatus);
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
