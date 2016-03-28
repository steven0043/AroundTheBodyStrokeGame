package com.atbsg.atbsg.games;

/**
 * Created by Steven on 07/02/2016.
 *
 * A sensor listener for the Circles Game, much like that of the watch game except
 * there is two way communication between watch and phone.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.atbsg.atbsg.logging.Logger;

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
    Logger logger;

    public PhoneGameListener(SensorManager sm, PhoneGameActivity currentActivity){
        this.currentActivity = currentActivity;
        logger = new Logger(currentActivity);
        logger.initialiseCommunication();
        mSensorManager = sm;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Implemented method, gets called every time the
     * registered accelerometer sensor is changed.
     * @param event
     */
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

    /**
     * Isolate and remove gravity from the sensor events values.
     * @param event
     */
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

    /**
     * Add the current sensor values to a list of historical values
     * based on the current direction required by the game mode.
     */
    public void addToAverages(){
        if(currentPhoneMotion.equals("LEFT") || currentPhoneMotion.equals("RIGHT")){
            directionHelper.addToHorizontalHistory(linear_acceleration[0] * 2);
        }
        if(currentPhoneMotion.equals("UP") || currentPhoneMotion.equals("DOWN")){
            directionHelper.addToVerticalHistory(linear_acceleration[2]);
        }
    }

    /**
     * Check if user is moving their arm up.
     * @return boolean
     */
    public boolean goingUp(){
        return linear_acceleration[2] < -0.25 && currentPhoneMotion.equals("UP") &&
                directionHelper.goingUp();
    }

    /**
     * Apply the up weighting to the current progress value.
     */
    public void applyUpWeighting(){
        if(mProgressStatus < verticalMax && mProgressStatus >= 0) {
            double highestAvgWeight =  toPositive((16-((directionHelper.getUpAverage()/3)*4)));
            double currentAvgWeight = toPositive((-directionHelper.getUpAverage()));
            double currentValueWeight = toPositive(((-linear_acceleration[2]+1)*2));
            mProgressStatus = (int) (mProgressStatus + (currentValueWeight * (currentAvgWeight * highestAvgWeight)));
        }
    }

    /**
     * Check if user is moving their arm down.
     * @return boolean
     */
    public boolean goingDown(){
        return linear_acceleration[2] > 0.25 && currentPhoneMotion.equals("DOWN")
                && directionHelper.goingDown();
    }

    /**
     * Apply the down weighting to the current progress value.
     */
    public void applyDownWeighting(){
        if(mProgressStatus > 0 && mProgressStatus <= 2000) {
            double highestAvgWeight =  toPositive((16-((directionHelper.getDownAverage()/3)*4)));
            double currentAvgWeight = toPositive(directionHelper.getDownAverage());
            double currentValueWeight = toPositive(((linear_acceleration[2]+1)*2));
            mProgressStatus = (int) (mProgressStatus - (currentValueWeight * (currentAvgWeight * highestAvgWeight)));
        }
    }

    /**
     * Check if user is moving their arm right.
     * @return boolean
     */
    public boolean goingRight(){
        return linear_acceleration[0] < -0.005 && currentPhoneMotion.equals("RIGHT")
                && directionHelper.goingRight();
    }

    /**
     * Apply the right weighting to the current progress value.
     */
    public void applyRightWeighting(){
        if(mProgressStatus < horizontalMax && mProgressStatus >= 0) {
            double highestAvgWeight =  toPositive(16 - ((directionHelper.getRightAverage() / 3) * 4));
            double currentAvgWeight = toPositive(-directionHelper.getRightAverage());
            mProgressStatus = (int) (mProgressStatus + (currentAvgWeight * (currentAvgWeight * highestAvgWeight)));
        }
    }

    /**
     * Check if user is moving their arm left.
     * @return boolean
     */
    public boolean goingLeft(){
        return linear_acceleration[0] > 0.005 && currentPhoneMotion.equals("LEFT")
                && directionHelper.goingLeft();
    }

    /**
     * Apply the left weighting to the current progress value.
     */
    public void applyLeftWeighting(){
        if(mProgressStatus>horizontalMax){
            mProgressStatus = horizontalMax;
        }
        if(mProgressStatus > 0 && mProgressStatus <= 950) {
            double highestAvgWeight = toPositive(16 - ((directionHelper.getLeftAverage() / 3) * 4));
            double currentAvgWeight = toPositive(directionHelper.getLeftAverage());
            mProgressStatus = (int) (mProgressStatus - (currentAvgWeight * (currentAvgWeight* highestAvgWeight)));
        }
    }

    /**
     * Update the game values on the phone.
     */
    public void updateView(){
        currentActivity.sendToPhone("30"+currentPhoneMotion, mProgressStatus);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Receives the current direction from the phone and
     * assigns it to our class variable.
     * @param direction
     */
    public static void setPhoneDirection(String direction){
        currentPhoneMotion = direction;
        reset();
    }

    /**
     * Reset the progress values based on the direction
     * we have changed to.
     */
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

    /**
     * Turn the double parameter to a postive number
     * @param number
     * @return double
     */
    public double toPositive(double number){
        return Math.abs(number);
    }

    /**
     * Unregisters the accelerometer from the sensor manager.
     */
    public void unregister() {
        logger.endCirclesGame();
        mProgressStatus = 0;
        currentPhoneMotion = "UP";
        mSensorManager.unregisterListener(this);
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.flush(this);
    }
}
