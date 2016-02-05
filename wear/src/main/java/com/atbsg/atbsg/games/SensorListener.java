package com.atbsg.atbsg.games;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.atbsg.atbsg.games.*;
import com.atbsg.atbsg.games.SensorActivity;
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
    private float [] history = new float[3];
    private Logger logger;
    private float twoSeconds = 2000;
    private int mProgressStatus = 0;
    boolean moved = false;
    DirectionHelper gameHelper = new DirectionHelper();
    EasyGame easyGame = new EasyGame();
    int horizontalMax = 500;
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

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            if(easyGame.getGameDirections().get(0).equals("LEFT") || easyGame.getGameDirections().get(0).equals("RIGHT")){
                gameHelper.addToHistory(linear_acceleration[0]);
            }
            if(easyGame.getGameDirections().get(0).equals("UP") || easyGame.getGameDirections().get(0).equals("DOWN")){
                gameHelper.addToUpHistory(linear_acceleration[2]);
            }
            if(linear_acceleration[2] < -0.25 && easyGame.getGameDirections().get(0).equals("UP") &&
                    gameHelper.goingUp()){
                if(mProgressStatus < verticalMax ) {
                    mProgressStatus = (int) (mProgressStatus + -(-linear_acceleration[2]*3) * (gameHelper.getUpAverage()*(24-((gameHelper.getHighestUpDownAverage()+1)*4))));
                }
                else{
                    direction = "UP";
                    moved = true;
                }
            }
            else if (linear_acceleration[2] > 0.25 && easyGame.getGameDirections().get(0).equals("DOWN")
                    && gameHelper.goingDown()){
                if(mProgressStatus > 0) {
                    mProgressStatus = (int) (mProgressStatus - -(-linear_acceleration[2]*3) * (gameHelper.getDownAverage()*(24-((gameHelper.getHighestUpDownAverage()+1)*4))));
                }else{
                    direction = "DOWN";
                    moved = true;
                }
            }
            else if(linear_acceleration[0] < -0.005 && easyGame.getGameDirections().get(0).equals("RIGHT")
                    && gameHelper.goingRight()){
                if(mProgressStatus < horizontalMax) {
                    //System.out.println(" GOING RIGHT!! \n +" + (toPositive(linear_acceleration[0]*3) * (toPositive(gameHelper.getRightAverage())*(24-((toPositive(gameHelper.getHighestLeftRightAverage())+1)*4)))));
                    mProgressStatus = (int) (mProgressStatus + -(-linear_acceleration[0]*5) * (gameHelper.getRightAverage()*(60-((gameHelper.getHighestLeftRightAverage()+3)*4))));
                }else {
                    direction = "RIGHT";
                    moved = true;
                    //System.out.println("RIGHT " + xChange);
                }
            }
            else if (linear_acceleration[0] > 0.005 && easyGame.getGameDirections().get(0).equals("LEFT")
                    && gameHelper.goingLeft()){
                if(mProgressStatus>horizontalMax){
                    mProgressStatus = horizontalMax;
                }
                if(mProgressStatus > 0) {
                   // System.out.println("progress " + mProgressStatus + " LEFT SUM " + (- -(-linear_acceleration[0]*5) * (gameHelper.getLeftAverage()*(60-((gameHelper.getHighestLeftRightAverage()+3)*4)))));
                    mProgressStatus = (int) (mProgressStatus - -(-linear_acceleration[0]*5) * (gameHelper.getLeftAverage()*(60-(((-gameHelper.getHighestLeftRightAverage())+3)*4))));
                }else {
                    direction = "LEFT";
                    moved = true;
                    //System.out.println("LEFT " + xChange);
                }
            }
            if(!(direction.equals("") && moved)){
                if(easyGame.correctDirection(direction)){
                    easyGame.addDirection();
                    easyGame.remove();
                    score++;
                    changeVisibility(easyGame.getGameDirections().get(0));
                    playSound(easyGame.getGameDirections().get(0));
                    currentActivity.setmTextView(easyGame.getGameDirections(), score);
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
            System.out.println("Progress " + mProgressStatus);
            currentActivity.addProgressToPhone("1"+score+easyGame.getGameDirections().get(0), mProgressStatus);
            currentActivity.updateProgressBar(mProgressStatus);
            lastUpdate = curTime;
        }
    }

    public void playSound(String direction){
        currentActivity.addScoreToCloud(direction);
    }

    public int toPositive(double number){
        return Math.abs((int) number);
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
        if(easyGame.getNextDirections().equals("DOWN")){
            mProgressStatus = verticalMax;
        }
        if(easyGame.getNextDirections().equals("LEFT")) {
            mProgressStatus = horizontalMax;
        }
        else if(easyGame.getNextDirections().equals("RIGHT") || easyGame.getNextDirections().equals("UP")){
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
