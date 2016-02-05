package com.atbsg.atbsg.games;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atbsg.atbsg.R;

import java.text.DecimalFormat;

public class GyroscopeActivity extends WearableActivity implements SensorEventListener {

    public TextView mTextView;
    public SensorManager mSensorManager;
    public Sensor mAccelerometer;
    boolean textBool = false;
    private long lastUpdate = 0;
    private double[] gravity ={0,0,0};
    String direction = "";
    private double[] linear_acceleration ={0,0,0};
    float [] history = new float[3];
    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private final float[] rotationCurrent = new float[4];
    private static final int PROGRESS = 0x1;

    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();


    public GyroscopeActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        setAmbientEnabled();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mProgress = (ProgressBar) findViewById(R.id.progressBar);
                textBool = true;

            }
        });
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onSensorChanged(SensorEvent event){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        mTextView = (TextView) findViewById(R.id.text);

        float xAcc = 0;
        float yAcc = 0;
        float zAcc = 0;
        final float alpha = (float) 0.8;
        long curTime = System.currentTimeMillis();
        if((curTime - lastUpdate) > 90) {
            xAcc = event.values[0];
            yAcc = event.values[1];
            zAcc = event.values[2];

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            float xChange = (float) (history[0] - linear_acceleration[0]);
            float yChange = (float) (history[1] - linear_acceleration[1]);
            float zChange = (float) (history[2] - linear_acceleration[2]);

            history[0] = (float) linear_acceleration[0];
            history[1] = (float) linear_acceleration[1];
            history[2] = (float) linear_acceleration[2];

            if(linear_acceleration[2] < -0.45
                    && zChange > 0.02
                    ){
                direction = "UP";
                System.out.println("UP " + zChange);
                mProgressStatus++;
            }
            else if (linear_acceleration[2] > 0.45
                    && zChange < -0.02
                    ){
                direction = "DOWN";
                System.out.println("DOWN " + zChange);
                mProgressStatus--;
            }
            else if(linear_acceleration[0] < -0.14
                    && xChange < -0.02
                    ){
                direction = "RIGHT";
                System.out.println("RIGHT " + xChange);
            }
            else if (linear_acceleration[0] > 0.14
                    && xChange < -0.02
                    ){
                direction = "LEFT";
                System.out.println("LEFT " + xChange);
            }
            else {
                direction = "";
            }

            if (textBool == true) {
                //mTextView.setText("\t\n x: " + direction +"\t\n x: " + linear_acceleration[0] + "\t\n y: " + df.format(linear_acceleration[1]) + "\t\n z: " + df.format(linear_acceleration[2]));
                mTextView.setText("\t\n x: " + direction +"\t\n x: " + df.format(xAcc) + "\t\n y: " + df.format(yAcc) + "\t\n z: " + df.format(zAcc));
                doWork();
            }
            lastUpdate = curTime;
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

    private void doWork(){
        mProgress.setProgress(mProgressStatus);
    }
}

/*
package com.atbsg.atbsg.games;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class GyroscopeActivity extends Activity implements SensorEventListener {

    Float azimut;  // View to draw a compass

    public class CustomDrawableView extends View {
        Paint paint = new Paint();
        public CustomDrawableView(Context context) {
            super(context);
            paint.setColor(0xff00ff00);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
        };

        protected void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;
            canvas.drawLine(centerx, 0, centerx, height, paint);
            canvas.drawLine(0, centery, width, centery, paint);
            // Rotate the canvas with the azimut
            if (azimut != null)
                canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
            paint.setColor(0xff0000ff);
            canvas.drawLine(centerx, -1000, centerx, +1000, paint);
            canvas.drawLine(-1000, centery, 1000, centery, paint);
            canvas.drawText("N", centerx+5, centery-10, paint);
            canvas.drawText("S", centerx-10, centery+15, paint);
            paint.setColor(0xff00ff00);
        }
    }

    CustomDrawableView mCustomDrawableView;
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);    // Register the sensor listeners
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
            }
        }
        mCustomDrawableView.invalidate();
    }
}*/
