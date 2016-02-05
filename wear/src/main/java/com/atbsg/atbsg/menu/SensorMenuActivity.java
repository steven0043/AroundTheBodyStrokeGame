package com.atbsg.atbsg.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.games.AccelerometerActivity;
import com.atbsg.atbsg.games.GyroscopeActivity;
import com.atbsg.atbsg.games.SensorActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorMenuActivity extends Activity {

    private ListView lv;
    List<String> menu_list = new ArrayList<String>(Arrays.asList("Accelerometer", "Accelerometer No Gravity", "Gyroscope"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_menu);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                menu_list);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                lv = (ListView) findViewById(R.id.listView);
                lv.setAdapter(arrayAdapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                        String selectedFromList = (String) (lv.getItemAtPosition(myItemInt));
                        System.out.println(selectedFromList);
                        if (selectedFromList.trim().equals("Accelerometer")) {
                            startAccActivity(myView);
                        }
                        if (selectedFromList.trim().equals("Accelerometer No Gravity")) {
                            startAccNoGravActivity(myView);
                        }
                        if (selectedFromList.trim().equals("Gyroscope")) {
                            startGyroActivity(myView);
                        }
                    }
                });
            }
        });
    }

    public void startAccNoGravActivity(View view) {
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }

    public void startAccActivity(View view) {
        Intent intent = new Intent(this, AccelerometerActivity.class);
        startActivity(intent);
    }

    public void startGyroActivity(View view) {
        Intent intent = new Intent(this, GyroscopeActivity.class);
        startActivity(intent);
    }
}

