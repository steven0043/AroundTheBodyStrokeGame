package com.atbsg.atbsg.games;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.atbsg.atbsg.logging.Logger;

/**
 * Created by Steven on 13/12/2015.
 */
public class SensorListener implements SensorEventListener {

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final SensorActivity currentActivity;
    private long lastUpdate = 0;
    final float alpha = (float) 0.8;
    private int score = 0;
    private double[] gravity ={0,0,0};
    private double[] linear_acceleration ={0,0,0};
    Vibrator v;
    private String direction = "";
    private Logger logger;
    private int mProgressStatus = 0;
    boolean moved = false;
    DirectionHelper directionHelper = new DirectionHelper();
    GameHelper gameHelper = new GameHelper();
    int horizontalMax = 1000;
    int verticalMax = 2000;

    public SensorListener(SensorManager sm, SensorActivity currentActivity, int horizontalMax, int verticalMax){
        mSensorManager = sm;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.currentActivity = currentActivity;
        this.horizontalMax = horizontalMax;
        this.verticalMax = verticalMax;
        logger = new Logger(currentActivity);
        v = (Vibrator) currentActivity.getSystemService(Context.VIBRATOR_SERVICE);
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

            checkCompletedMovement();

            updateView();

            lastUpdate = curTime;
        }
    }

    private void updateView(){
        currentActivity.addProgressToPhone("1"+score+ gameHelper.getNextDirections(), mProgressStatus);
        currentActivity.updateProgressBar(mProgressStatus);
    }

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

    private void applyUpWeighting(){
        if(mProgressStatus < verticalMax ) {
            mProgressStatus = (int) (mProgressStatus + (((-linear_acceleration[2]+ 1)*2) * (((-directionHelper.getUpAverage())) * (16-((directionHelper.getHighestUpDownAverage()/3)*4)))));
        }
        else{
            direction = "UP";
            moved = true;
        }
    }

    private void applyDownWeighting(){
        if(mProgressStatus > 0) {
            mProgressStatus = (int) (mProgressStatus - (((linear_acceleration[2]+1)*2) * (((directionHelper.getDownAverage())) * (16-((directionHelper.getHighestUpDownAverage()/3)*4)))));
        }else{
            direction = "DOWN";
            moved = true;
        }
    }

    private void applyLeftWeighting(){
        if(mProgressStatus>horizontalMax){
            mProgressStatus = horizontalMax;
        }
        if(mProgressStatus > 0) {
            double highestAvgWeight =  toPositive(16 - ((directionHelper.getHighestLeftRightAverage() / 3) * 4));
            double currentAvgWeight = toPositive(directionHelper.getLeftAverage());
            double currentValueWeight = toPositive(((linear_acceleration[2]+ 1)*2));
            mProgressStatus = (int) (mProgressStatus - (currentAvgWeight * (currentAvgWeight* highestAvgWeight)));
        }else {
            direction = "LEFT";
            moved = true;
        }
    }

    private void applyRightWeighting(){
        if(mProgressStatus < horizontalMax) {
            double highestAvgWeight =  toPositive(16 - ((directionHelper.getHighestLeftRightAverage() / 3) * 4));
            double currentAvgWeight = toPositive(-directionHelper.getRightAverage());
            double currentValueWeight = toPositive(((-linear_acceleration[2] + 1) * 2));
            mProgressStatus = (int) (mProgressStatus + (currentAvgWeight * (currentAvgWeight* highestAvgWeight)));
        }else {
            direction = "RIGHT";
            moved = true;
        }
    }
    private boolean goingUp(){
        return linear_acceleration[2] < -0.05 && gameHelper.isUp() &&
                directionHelper.goingUp();
    }

    private boolean goingDown(){
        return linear_acceleration[2] > 0.05 && gameHelper.isDown()
                && directionHelper.goingDown();
    }

    private boolean goingLeft(){
        return linear_acceleration[0] > 0.005 && gameHelper.isLeft()
                && directionHelper.goingLeft();
    }

    private boolean goingRight(){
        return linear_acceleration[0] < -0.05 && gameHelper.isRight()
                && directionHelper.goingRight();
    }

    public void addToAverages(){
        if(gameHelper.isLeft() || gameHelper.isRight()) {
            directionHelper.addToHistory(linear_acceleration[0] * 2);
        }
        if(gameHelper.isUp() || gameHelper.isDown()){
            directionHelper.addToUpHistory(linear_acceleration[2]);
        }
    }

    public void checkCompletedMovement(){
        if(!(direction.equals("") && moved)){
            if(gameHelper.correctDirection(direction)){
                gameHelper.addDirection();
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

    public void playSound(String direction){
        currentActivity.addScoreToCloud(direction);
    }

    public double toPositive(double number){
        return Math.abs(number);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void changeVisibility(String direction){
        if(direction.equals("UP") || direction.equals("DOWN")){
            currentActivity.makeHorizontalInvisible();
            currentActivity.makeVerticalVisible();
        }
        if(direction.equals("RIGHT") || direction.equals("LEFT")){
            currentActivity.makeHorizontalVisible();
            currentActivity.makeVerticalInvisible();
        }
    }

    public void adjustProgressValue(){
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
    public void unregister() {
        if(verticalMax == 2000){
            logger.setLastEasyScore(score);
            //cloudLogger.addEasyCompletion("JohnDoe", score);
            //currentActivity.addScoreToCloud("easy " + score + " " + logger.getUniqueId());
            System.out.println("Add Easy");
        }
        if(verticalMax == 4000){
            logger.setLastMediumScore(score);
            //currentActivity.addScoreToCloud("medium " + score + " " + logger.getUniqueId());
            System.out.println("Add Medium");
            //cloudLogger.addMediumCompletion("JohnDoe", score);
        }
        if(verticalMax == 8000){
            logger.setLastHardScore(score);
            //currentActivity.addScoreToCloud("hard " + score + " " + logger.getUniqueId());
            System.out.println("Add Hard");
            //cloudLogger.addHardCompletion("JohnDoe", score);
        }
        //playSound("Your final score is " + score);
        System.out.println("DESTROOOYYYEDD");
        mSensorManager.unregisterListener(this);
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.flush(this);
    }
}
