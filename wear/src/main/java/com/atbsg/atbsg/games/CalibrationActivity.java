package com.atbsg.atbsg.games;


import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.logging.CloudLogger;
import com.atbsg.atbsg.menu.ListActivity;

/**
 * Created by Steven.
 *
 * Activity used to show the screen used during
 * the calibration screen.
 */

public class CalibrationActivity extends WearableActivity {

    public TextView mTextView;
    public ImageView imageView;
    boolean textBool = false;
    private boolean calibrateClick = false;
    private boolean gameStarted = false;
    private boolean spoken = false;
    public CalibrationListener sensorListener;
    CloudLogger cloudLogger;

    public CalibrationActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        cloudLogger = new CloudLogger(this);
        cloudLogger.initApi();
        setAmbientEnabled();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        System.out.println("Adding Calibration" );
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                imageView = (ImageView) stub.findViewById(R.id.imageView);
                loadGame();
                textBool = true;

            }
        });
        //Create a new sensor listener.
        sensorListener = new CalibrationListener((SensorManager)getSystemService(Context.SENSOR_SERVICE), this);
    }

    /**
     * Set and change the text view
     * @param text
     */
    protected void setmTextView(String text) {
        mTextView.setText("");
        mTextView.setText(text);
    }

    /**
     * Change the imageView based on whether the user
     * has completed the calibration procedure.
     * @param calibrated
     */
    public void setImageView(boolean calibrated) {
        final boolean calibratedd = calibrated;
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                if(calibratedd && textBool){
                    imageView.setImageResource(R.mipmap.tick);
                    calibrateClick = true;
                    setmTextView("Great! Hold it there!");
                    if(!spoken) {
                        cloudLogger.sendScoreToCloud("Great! Hold it there for 2 more seconds!");
                        spoken = true;
                    }
                }else if (!calibratedd && textBool) {
                    setmTextView("Before we start, look straight ahead. Then please make sure the watch face is parallel to your own face");
                    imageView.setImageResource(R.mipmap.cross);
                    calibrateClick = false;
                    spoken = false;
                }
            }
        });
    }

    /**
     * Loads the game.
     */
    public void loadGame(){
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Clicked");
                if (calibrateClick) {
                    //sensorListener.unregister();
                    System.out.println("Clicked tick");
                    Intent intent = new Intent(CalibrationActivity.this, ExerciseActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * Loads the game menu.
     */
    public void startGame(){
        if(!gameStarted){
            gameStarted = true;
            sensorListener.unregister();
            Bundle b=new Bundle();
            b.putStringArray("listItems", new String[]{"Easy", "Medium", "Hard"});
            cloudLogger.sendScoreToCloud("Your game options are: easy, medium and hard");
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
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