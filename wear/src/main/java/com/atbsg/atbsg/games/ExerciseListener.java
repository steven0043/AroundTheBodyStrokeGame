package com.atbsg.atbsg.games;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.atbsg.atbsg.logging.Logger;
import com.atbsg.atbsg.logging.UserSessionData;

/**
 * Created by Steven on 13/12/2015.
 *
 * Class that listens out for the accelerometer data during the game
 * modes on the smartwatch and through the help of other classes,
 * updates the Exercise Activity accordingly.
 */
public class ExerciseListener implements SensorEventListener {

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final ExerciseActivity currentActivity;
    private long lastUpdate = 0;
    private long lastDataUpdate = 0;
    final float alpha = (float) 0.8;
    private int score = 0;
    private double[] gravity = {0,0,0};
    private double[] linear_acceleration = {0,0,0};
    Vibrator v;
    private String direction = "";
    private Logger logger;
    private int mProgressStatus = 0;
    boolean moved = false;
    DirectionHelper directionHelper = new DirectionHelper();
    GameHelper gameHelper = new GameHelper();
    int horizontalMax = 1000;
    int verticalMax = 2000;
    String fileSend = "";
    String allFileSend = "";
    long tStart = System.currentTimeMillis();
    int i = UserSessionData.getMap().size();
    MagnetometerData magnetometerData;

    public ExerciseListener(SensorManager sm, ExerciseActivity currentActivity, int horizontalMax, int verticalMax){
        mSensorManager = sm;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.currentActivity = currentActivity;
        this.horizontalMax = horizontalMax;
        this.verticalMax = verticalMax;
        logger = new Logger(currentActivity);
        v = (Vibrator) currentActivity.getSystemService(Context.VIBRATOR_SERVICE);
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

            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
                removeGravity(event);

                addToAverages();
                //collectUserData(); was used during user evaluation to collect data.

                if (goingUp()) {
                    applyUpWeighting();
                } else if (goingDown()) {
                    applyDownWeighting();
                } else if (goingRight()) {
                    applyRightWeighting();
                } else if (goingLeft()) {
                    applyLeftWeighting();
                }

                checkCompletedMovement();

                updateView();
            }
            lastUpdate = curTime;

        }
    }

    /**
     * Update the phones view to reflect the progress values, direction and score.
     */
    private void updateView(){
        currentActivity.addProgressToPhone("1"+score+ gameHelper.getNextDirections(), mProgressStatus);
        currentActivity.updateProgressBar(mProgressStatus);
    }

    /**
     * Isolate and remove gravity from the sensor events values.
     * @param event
     */
    private void removeGravity(SensorEvent event){
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
     * Apply the up weighting to the current progress value.
     */
    private void applyUpWeighting(){
        if(mProgressStatus < verticalMax ) {
            double highestAvgWeight =  toPositive((16-((directionHelper.getUpAverage()/3)*4))); //This is to help deal with accelerometer value differences across different devices
            double currentAvgWeight = toPositive((-directionHelper.getUpAverage()));
            double currentValueWeight = toPositive(((-linear_acceleration[2]+1)*2));
            mProgressStatus = (int) (mProgressStatus + (currentValueWeight * (currentAvgWeight * highestAvgWeight)));
        }
        else{
            direction = "UP";
            moved = true;
        }
    }

    /**
     * Apply the down weighting to the current progress value.
     */
    private void applyDownWeighting(){
        if(mProgressStatus > 0) {
            double highestAvgWeight =  toPositive((16-((directionHelper.getDownAverage()/3)*4)));
            double currentAvgWeight = toPositive(directionHelper.getDownAverage());
            double currentValueWeight = toPositive(((linear_acceleration[2]+1)*2));
            mProgressStatus = (int) (mProgressStatus - (currentValueWeight * (currentAvgWeight * highestAvgWeight)));
        }else{
            direction = "DOWN";
            moved = true;
        }
    }

    /**
     * Apply the left weighting to the current progress value.
     */
    private void applyLeftWeighting(){
        if(mProgressStatus>horizontalMax){
            mProgressStatus = horizontalMax;
        }
        if(mProgressStatus > 0) {
            double highestAvgWeight =  toPositive(16 - ((directionHelper.getLeftAverage() / 3) * 4));
            double currentAvgWeight = toPositive(directionHelper.getLeftAverage());
            mProgressStatus = (int) (mProgressStatus - (currentAvgWeight * (currentAvgWeight* highestAvgWeight)));
        }else {
            direction = "LEFT";
            moved = true;
        }
    }

    /**
     * Apply the right weighting to the current progress value.
     */
    private void applyRightWeighting(){
        if(mProgressStatus < horizontalMax) {
            double highestAvgWeight =  toPositive(16 - ((directionHelper.getRightAverage() / 3) * 4));
            double currentAvgWeight = toPositive(-directionHelper.getRightAverage());
            mProgressStatus = (int) (mProgressStatus + (currentAvgWeight * (currentAvgWeight * highestAvgWeight)));
        }else {
            direction = "RIGHT";
            moved = true;
        }
    }

    /**
     * Check if user is moving their arm up.
     * @return boolean
     */
    private boolean goingUp(){
        return linear_acceleration[2] < -0.05 && gameHelper.isUp() &&
                directionHelper.goingUp();
    }

    /**
     * Check if user is moving their arm down.
     * @return boolean
     */
    private boolean goingDown(){
        return linear_acceleration[2] > 0.05 && gameHelper.isDown()
                && directionHelper.goingDown();
    }

    /**
     * Check if user is moving their arm left.
     * @return boolean
     */
    private boolean goingLeft(){
        return linear_acceleration[0] > 0.005 && gameHelper.isLeft()
                && directionHelper.goingLeft();
    }

    /**
     * Check if user is moving their arm right.
     * @return boolean
     */
    private boolean goingRight(){
        return linear_acceleration[0] < -0.005 && gameHelper.isRight()
                && directionHelper.goingRight();
    }

    /**
     * Add the current sensor values to a list of historical values
     * based on the current direction required by the game mode.
     */
    public void addToAverages(){
        if(gameHelper.isLeft() || gameHelper.isRight()) {
            directionHelper.addToHorizontalHistory(linear_acceleration[0] * 2);
        }
        if(gameHelper.isUp() || gameHelper.isDown()){
            directionHelper.addToVerticalHistory(linear_acceleration[2]);
        }
    }

    public void collectUserData(){
        int keepCollecting = 0;
        if(verticalMax == 2000){
            keepCollecting = 12;
        }
        else if(verticalMax == 8000){
           keepCollecting = 4;
        }else {keepCollecting = 12; }
        long nowTime = System.currentTimeMillis();
        if((nowTime - lastDataUpdate) > 500) {
            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;
            double percentage = 0;
            double status = (double) mProgressStatus;
            if(gameHelper.isUp() || gameHelper.isDown()){
                double vertical = (double) verticalMax;
                percentage = ((status/vertical) * 100.0);
            }
            if(gameHelper.isLeft() || gameHelper.isRight()) {
                double horizontal = (double) horizontalMax;
                percentage = ((status/horizontal) * 100.0);
            }
            fileSend = fileSend + "\n" + gameHelper.getNextDirections() + "\nAccelerometer" + ": \nX[" + linear_acceleration[0] + "]" +
                    "\nY[" + linear_acceleration[1] + "]" + "\nZ[" + linear_acceleration[2] + "]"
                    + "\n\nMagnetometer" + ": \nX[" + magnetometerData.getX() + "]" +
                    "\nY[" + magnetometerData.getY() + "]" + "\nZ[" + magnetometerData.getZ() + "]"
                    + "\n\nScore: " + score
                    + "\nProgress: " + percentage + "%"
                    + "\nTime on this direction: " + elapsedSeconds + " seconds";
            allFileSend = allFileSend + fileSend;
            lastDataUpdate = nowTime;
            if(score < keepCollecting) {
                logger.addToHashMap(Integer.toString(i), fileSend);
            }
            fileSend = "";
            i++;
        }
    }

    /**
     * Check if the user has completed the current indicated direction
     */
    private void checkCompletedMovement(){
        if(!(direction.equals("") && moved)){
            if(gameHelper.correctDirection(direction)){
                /*
                   The first 20 directions will follow the same pattern
                   following this there will be some random directions included
                   to mix it up.
                 */
                if((score>15)&& (score % 5 == 0)){gameHelper.addRandomDirection();}
                else{gameHelper.addDirection();}
                gameHelper.remove();
                score++;
                changeVisibility(gameHelper.getNextDirections());
                playSound(gameHelper.getNextDirections());
                currentActivity.setmTextView(gameHelper.getGameDirections(), score);
                moved = false;
                direction = "";
                this.adjustProgressValue();
                v.vibrate(100);
                if(score>logger.getEasyScore() && verticalMax == 2000){
                    logger.setEasyScore(score);
                }
                if(score>logger.getMediumScore() && verticalMax == 4000){
                    logger.setMediumScore(score);
                }
                if(score>logger.getHardScore() && verticalMax == 8000){
                    logger.setHardScore(score);
                }
            }
        }
    }

    /**
     * Speak the current direction.
     * @param direction
     */
    private void playSound(String direction){
        currentActivity.addScoreToCloud(direction);
    }

    /**
     * Turn the double parameter to a postive number
     * @param number
     * @return double
     */
    private double toPositive(double number){
        return Math.abs(number);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Change the progress bar visibility on the watch based on
     * the current direction.
     * @param direction
     */
    private void changeVisibility(String direction){
        if(direction.equals("UP") || direction.equals("DOWN")){
            currentActivity.makeHorizontalInvisible();
            currentActivity.makeVerticalVisible();
        }
        if(direction.equals("RIGHT") || direction.equals("LEFT")){
            currentActivity.makeHorizontalVisible();
            currentActivity.makeVerticalInvisible();
        }
    }

    /**
     * Adjust the progress value.
     */
    private void adjustProgressValue(){
        if(gameHelper.isDown()){
            mProgressStatus = verticalMax;
        }
        if(gameHelper.isLeft()) {
            mProgressStatus = horizontalMax;
        }
        else if(gameHelper.isRight() || gameHelper.isUp()){
            mProgressStatus = 0;
        }
    }

    /**
     * Called when the user exits the game. Save their score in
     * shared preferences and unregisters the acceleromter.
     */
    public void unregister() {
        if(verticalMax == 2000){
            logger.setLastEasyScore(score);
        }
        if(verticalMax == 4000){
            logger.setLastMediumScore(score);

        }
        if(verticalMax == 8000){
            logger.setLastHardScore(score);

        }

        mSensorManager.unregisterListener(this);
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.flush(this);
    }
}