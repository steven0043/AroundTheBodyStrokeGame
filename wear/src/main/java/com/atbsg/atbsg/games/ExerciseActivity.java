package com.atbsg.atbsg.games;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.how.MyApplication;
import com.atbsg.atbsg.logging.CloudLogger;

import java.util.ArrayList;

/**
 * Created by Steven.
 *
 * Activity that represents the game modes on the smartwatch.
 * Contains the progress bars.
 *
 */

public class ExerciseActivity extends WearableActivity  {

    public TextView mTextView;
    boolean textBool = false;
    private ExerciseListener exerciseListener;
    private ProgressBar mProgress, mProgressHorizontal;
    private boolean updatedMaximums = false;
    GameHelper gameHelper = new GameHelper();
    int horizontalMax = 1000;
    int verticalMax = 2000;
    public CloudLogger cloudLogger;
    protected MyApplication myApplication;

    public ExerciseActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        setAmbientEnabled();

        cloudLogger = new CloudLogger(this);
        cloudLogger.initApi();
        //updatePhoneMaximums();
        myApplication = (MyApplication)this.getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            horizontalMax = extras.getInt("horizontalMax");
            verticalMax = extras.getInt("verticalMax");
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mProgress = (ProgressBar) findViewById(R.id.progressBar);
                mProgressHorizontal = (ProgressBar) findViewById(R.id.progressBar2);
                Drawable draw = getResources().getDrawable(R.drawable.custom_progressbar);
                Drawable hozDraw = getResources().getDrawable(R.drawable.custom_progressbarhorizontal);
                mProgress.setProgressDrawable(draw);
                mProgressHorizontal.setProgressDrawable(hozDraw);
                mProgressHorizontal.setMax(horizontalMax);
                mProgress.setMax(verticalMax);
                setmTextView(gameHelper.getGameDirections(), 0);
                textBool = true;
                makeHorizontalInvisible();

            }
        });
        runOnUiThread(new Runnable() {
            public void run() {
                exerciseListener = new ExerciseListener((SensorManager) getSystemService(Context.SENSOR_SERVICE), ExerciseActivity.this, horizontalMax, verticalMax);
            }
        });
    }

    /**
     * Updates the text view with the direction and current score.
     * @param directions
     * @param score
     */
    protected void setmTextView(ArrayList<String> directions, int score) {
        mTextView.setText("");
        mTextView.setText(directions.get(0) + " | " + score);
    }

    /**
     * Takes in the current progress value and updates the current
     * visible dialog to reflect it.
     * @param mProgressStatus
     */
    protected void updateProgressBar(int mProgressStatus){
        final int mProgressStatuss = mProgressStatus;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textBool == true) {
                    if (mProgress.getVisibility() == View.VISIBLE) {
                        mProgress.setProgress(mProgressStatuss);
                    } else if (mProgressHorizontal.getVisibility() == View.VISIBLE) {
                        mProgressHorizontal.setProgress(mProgressStatuss);
                    }
                }
            }
        });
    }

    /**
     * Makes the vertical progress bar visible.
     */
    protected void makeVerticalVisible(){
        mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Makes the horizontal progress bar visible.
     */
    protected void makeHorizontalVisible(){
        mProgressHorizontal.setVisibility(View.VISIBLE);
    }

    /**
     * Makes the vertical progress bar invisible.
     */
    protected void makeVerticalInvisible(){
        mProgress.setVisibility(View.INVISIBLE);
    }

    /**
     * Makes the horizontal progress bar visible.
     */
    protected void makeHorizontalInvisible(){
        mProgressHorizontal.setVisibility(View.INVISIBLE);
    }

    /**
     * Send the current score to the phone
     * @param score
     */
    public void addScoreToCloud(String score){
        cloudLogger.sendToPhone(score);
    }

    /**
     * Send the current progress value to the phone.
     * @param direction
     * @param score
     */
    public void addProgressToPhone(String direction, int score){
        cloudLogger.sendProgressToPhone(direction, score);
    }

    /**
     * Indicate to the phone which game mode is currently
     * being played.
     */
    public void declareGameModeToPhone(){
        if(verticalMax == 2000){
           addScoreToCloud("2EASY");
        }
        if(verticalMax == 4000){
            addScoreToCloud("2MEDIUM");
        }
        if(verticalMax == 8000){
            addScoreToCloud("2HARD");
        }
    }

    /**
     * Handler that is called until the
     * phone is notified of the game mode.
     */
    public void updatePhoneMaximums(){
        Handler handler = new Handler();
        Runnable r=new Runnable() {
            @Override
            public void run() {
                if(cloudLogger.isConnected() && !updatedMaximums){
                    declareGameModeToPhone();
                    updatedMaximums = true;
                }
            }
        };
        handler.postDelayed(r, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myApplication.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        clearCurrentActivity();
        super.onPause();
    }

    /**
     *Clear the current activity reference
     */
    private void clearCurrentActivity(){
        Activity currActivity = myApplication.getCurrentActivity();
        if (this.equals(currActivity))
            myApplication.setCurrentActivity(null);
    }

    /**
     * Activity onDestroy, unregisters the sensor listener
     */
    @Override
    protected void onDestroy() {
        exerciseListener.unregister();
        clearCurrentActivity();
        finish();
        super.onDestroy();
    }
}