package com.atbsg.atbsg.games;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.games.*;
import com.atbsg.atbsg.logging.CloudLogger;

import java.util.ArrayList;

public class SensorActivity extends WearableActivity  {

    public TextView mTextView;
    boolean textBool = false;
    private SensorListener sensorListener;
    private ProgressBar mProgress, mProgressHorizontal;
    private boolean updatedMaximums = false;
    EasyGame easyGame = new EasyGame();
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
                setmTextView(easyGame.getGameDirections(), 0);
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

    protected void setmTextView(ArrayList<String> directions, int score) {
        mTextView.setText("");
        mTextView.setText(directions.get(0) + " | " + score);
       //mProgress.setVisibility(View.INVISIBLE);
    }

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

    protected void updateHorizontalProgressBar(int mProgressStatus){
        if (textBool == true) {
            mProgressHorizontal.setProgress(mProgressStatus);
        }
    }

    protected void makeVerticalVisible(){
        mProgress.setVisibility(View.VISIBLE);
    }

    protected void makeHorizontalVisible(){
        mProgressHorizontal.setVisibility(View.VISIBLE);
    }

    protected void makeVerticalInvisible(){
        mProgress.setVisibility(View.INVISIBLE);
    }

    protected void makeHorizontalInvisible(){
        mProgressHorizontal.setVisibility(View.INVISIBLE);
    }

    public void addScoreToCloud(String score){
        cloudLogger.sendScoreToCloud(score);
    }

    public void addProgressToPhone(String direction, int score){
        cloudLogger.sendProgressToPhone(direction, score);
    }

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

    @Override
    protected void onDestroy() {
        sensorListener.unregister();
        finish();
        super.onDestroy();
    }
}