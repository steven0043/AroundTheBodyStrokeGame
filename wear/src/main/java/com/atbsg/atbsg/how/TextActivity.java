package com.atbsg.atbsg.how;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.WindowManager;
import android.widget.TextView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.logging.CloudLogger;
import com.atbsg.atbsg.logging.Logger;

/**
 * Created by Steven on 07/02/2016.
 *
 * Activity to show text in the application.
 */

public class TextActivity extends Activity {

    private TextView mTextView;
    private boolean uniqueId = false;
    private Logger logger;
    public CloudLogger cloudLogger;
    protected MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how);
        logger = new Logger(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myApplication = (MyApplication)this.getApplicationContext();

        cloudLogger = new CloudLogger(this);
        cloudLogger.initApi();

        Bundle b=this.getIntent().getExtras();
        if(b!=null){
            uniqueId = (b.getBoolean("unique"));
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                if(uniqueId){
                    mTextView.setText("Your unique Id: " + logger.getUniqueId());
                }
            }
        });
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
        clearCurrentActivity();
        super.onDestroy();
    }
}
