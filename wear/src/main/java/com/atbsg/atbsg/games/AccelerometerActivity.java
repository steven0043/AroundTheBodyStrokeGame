package com.atbsg.atbsg.games;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.games.*;
import com.atbsg.atbsg.menu.SensorMenuActivity;

public class AccelerometerActivity extends WearableActivity implements SensorEventListener {

    public SensorManager mSensorManager;
    public Sensor mAccelerometer;
    boolean textBool = false;
    private int holdTime;
    public TextView mTextView;
    private boolean calibrateClick = false;
    private boolean gameStarted = false;
    public ImageView imageView;
    private long lastUpdate = 0;
    long hold = 0;

    public AccelerometerActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        setAmbientEnabled();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                textBool = true;
                imageView = (ImageView) stub.findViewById(R.id.imageView);
                loadGame();

            }
        });
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event){

        float yAcc = 0;
        long curTime = System.currentTimeMillis();


        if((curTime - lastUpdate) > 1000) {
            yAcc = event.values[1];

            if(yAcc > 8){
                holdTime = holdTime + 1001;
            }
            else{
                holdTime = 0;
            }
            if(holdTime > 3000) {
                Intent intent = new Intent(this, SensorMenuActivity.class);
                startActivity(intent);
                finish();
            }
            //checkCalibration(yAcc);
            lastUpdate = curTime;
        }
       /* DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        mTextView = (TextView) findViewById(R.id.text);
        float xAcc = 0;
        float yAcc = 0;
        float zAcc = 0;

        long curTime = System.currentTimeMillis();
        if((curTime - lastUpdate) > 3000) {
            xAcc = event.values[0];
            yAcc = event.values[1];
            zAcc = event.values[2];

            hold = hold + 3000;
            if(hold > 9000) {
                Intent intent = new Intent(this, SensorActivity.class);
                startActivity(intent);
                finish();
            }
            if (textBool == true) {
                mTextView.setText("\t\n x: " + df.format(xAcc) + "\t\n y: " + df.format(yAcc) + "\t\n z: " + df.format(zAcc));
            }
            lastUpdate = curTime;
        }*/
    }

    protected void setmTextView(String text) {
        mTextView.setText("");
        mTextView.setText(text);
    }
    public void setImageView(boolean calibrated) {
        if(calibrated && textBool){
            imageView.setImageResource(R.mipmap.tick);
            calibrateClick = true;
            setmTextView("Great! Hold it there or Press the tick to begin!");
        }else if (!calibrated && textBool) {
            setmTextView("Please Make Sure The Watch Face Is Parallel To Your Own Face.");
            imageView.setImageResource(R.mipmap.cross);
            calibrateClick = false;
        }
    }

    public void loadGame(){
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Clicked");
                if(calibrateClick){
                    //sensorListener.unregister();
                    System.out.println("Clicked tick");
                   // Intent intent = new Intent(CalibrationActivity.this, SensorActivity.class);
                   // startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void checkCalibration(float yAcc){
        if(yAcc > 8){
            holdTime = holdTime + 90;
        }
        else{
            holdTime = 0;
        }

        if(holdTime > 3000) {
            this.startGame();
        }
        else if(holdTime > 1000){
            //this.setImageView(true);
            Toast.makeText(getApplicationContext(), "Hold it there!", Toast.LENGTH_SHORT);
        }else {
            //this.setImageView(false);
        }
    }

    public void startGame(){
        System.out.println("LOADING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if(!gameStarted){
            System.out.println("LOADING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            gameStarted = true;
            //sensorListener.unregister();
            Intent intent = new Intent(this, com.atbsg.atbsg.games.SensorActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
        System.out.println("DESTROOOYYYEDD");
        mSensorManager.unregisterListener(this);
        finish();
        super.onDestroy();
    }
}

