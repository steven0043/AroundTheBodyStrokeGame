package com.atbsg.atbsg.games;

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
import com.atbsg.atbsg.logging.CloudLogger;

import java.util.ArrayList;

public class SensorActivity extends WearableActivity  {

    public TextView mTextView;
    boolean textBool = false;
    private SensorListener sensorListener;
    private ProgressBar mProgress, mProgressHorizontal;
    private boolean updatedMaximums = false;
    GameHelper gameHelper = new GameHelper();
    long lastUpdate;
    int horizontalMax = 1000;
    int verticalMax = 2000;
    public CloudLogger cloudLogger;

    public SensorActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        setAmbientEnabled();

        cloudLogger = new CloudLogger(this);
        cloudLogger.initApi();
        updatePhoneMaximums();
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
                // set the drawable as progress drawable
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
                sensorListener = new SensorListener((SensorManager) getSystemService(Context.SENSOR_SERVICE), SensorActivity.this, horizontalMax, verticalMax);
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
       //mProgress.setVisibility(View.INVISIBLE);
    }

    /**
     * Takes in the current progress value and updates the current
     * visible dialog to reflect it.
     * @param mProgressStatus
     */
    protected void updateProgressBar(int mProgressStatus){
        final int mProgressStatuss = mProgressStatus;

        //long curTime = System.currentTimeMillis();

        //System.out.println(" UPDATE " + (curTime - lastUpdate));
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
        //lastUpdate = curTime;

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
        cloudLogger.sendScoreToCloud(score);
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
    }

    @Override
    protected void onPause() {
        System.out.println("PAUSEESEDDD");
        super.onPause();
    }

    /**
     * Activity onDestroy, unregisters the sensor listener
     */
    @Override
    protected void onDestroy() {
        sensorListener.unregister();
        finish();
        super.onDestroy();
    }
}