package com.atbsg.atbsg.how;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.logging.CloudLogger;
import com.atbsg.atbsg.logging.Logger;

public class HowActivity extends Activity {

    private TextView mTextView;
    private boolean uniqueId = false;
    private Logger logger;
    CloudLogger cloudLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how);
        logger = new Logger(this);


        Bundle b=this.getIntent().getExtras();
        if(b!=null){
            uniqueId = (b.getBoolean("unique"));
        }else{
            cloudLogger = new CloudLogger(this);
            cloudLogger.initApi();
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
}
