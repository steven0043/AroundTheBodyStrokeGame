package com.atbsg.atbsg.menu;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.atbsg.atbsg.R;
import com.atbsg.atbsg.games.CalibrationActivity;
import com.atbsg.atbsg.games.PhoneGameActivity;
import com.atbsg.atbsg.games.ExerciseActivity;
import com.atbsg.atbsg.how.TextActivity;
import com.atbsg.atbsg.logging.CloudLogger;
import com.atbsg.atbsg.logging.Logger;
import com.atbsg.atbsg.logging.UserSessionData;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Steven on 19/01/2016.
 *
 * This class is specifically for all
 * list screens in the smartwatch application.
 */

public class ListActivity extends Activity implements WearableListView.ClickListener {

    String[] elements = {"How to Play", "Game Modes", "My Progress", "Settings","Play Game on Phone"};
    public static Logger logger;
    private static final String START_SPEECH = "Welcome to the around the body stroke recovery game. Your starting " +
            "options are: how to play, game modes, my progress and settings";
    boolean gameMenu = false;
    boolean scoreMenu = false;
    boolean settingsMenu = false;
    boolean viewUsersMenu = false;
    boolean deleteUsersMenu = false;
    public CloudLogger cloudLogger;
    private int mBindFlag;
    private Messenger mServiceMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        logger = new Logger(this);
        Bundle b=this.getIntent().getExtras();

        cloudLogger = new CloudLogger(this);
        if(b!=null){
            String[] array=b.getStringArray("listItems");
            boolean voiced = b.getBoolean("voiced");
            boolean delete = b.getBoolean("delete");
            elements = array;
            if(elements[0].startsWith("Easy High")){
                scoreMenu = true;
            }else if(elements[0].equals("Easy")){
                gameMenu = true;
            }else if(elements[0].startsWith("Your")){
                settingsMenu = true;
            }else if (delete){
                deleteUsersMenu = true;
            }
            else{
                viewUsersMenu = true;
            }
            if(voiced) {
                voiceDelayThenFinish();
            }
        }else{
            if(logger.getUniqueId().equals("")){
                String uniqueId = logger.generateUnique(6);
                logger.saveUserArray(uniqueId);
                logger.setCurrentUser(uniqueId);
            }else{
                logger.setOpened(logger.getOpened()+1);
            }
            Intent service = new Intent(ListActivity.this, com.atbsg.atbsg.menu.VoiceService.class);
            ListActivity.this.startService(service);
            mBindFlag = Context.BIND_ABOVE_CLIENT;
            cloudLogger = new CloudLogger(this);
            cloudLogger.initApi();
            cloudLogger.sendScoreToCloud(START_SPEECH);
        }

        WearableListView listView =
                (WearableListView) findViewById(R.id.wearable_list);

        listView.setAdapter(new Adapter(this, elements, scoreMenu));

        listView.setClickListener(this);
    }

    /**
     * Start calibration activity
     */
    public void startGameActivity() {
        Intent intent = new Intent(this, CalibrationActivity.class);
        startActivity(intent);
    }

    /**
     * Start the how to play screen
     */
    public void startHowActivity() {
        Intent intent = new Intent(this, TextActivity.class);
        cloudLogger.sendScoreToCloud("To play this game, strap the watch firmly on your wrist and follow the directions on screen. You can swipe right to move back a screen.");
        startActivity(intent);
    }

    /**
     * Start the show unique I.D. screen
     */
    public void startSettingsActivity() {
        /*logger.addToHashMap(Integer.toString(3), "fkefe");
        logger.addToHashMap(Integer.toString(1), "ddasfas");
        logger.sendText();*/
        Bundle b = new Bundle();
        b.putStringArray("listItems", new String[]{"Your Unique I.D.", "Change User", "Create New User", "Delete User"});
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void startChangeUserActivity() {
        Bundle b = new Bundle();
        b.putStringArray("listItems", logger.loadUserArray());
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void startDeleteUserActivity() {
        Bundle b = new Bundle();
        b.putStringArray("listItems", logger.loadUserArray());
        b.putBoolean("delete", true);
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void viewUniqueActivity(){
        Bundle b=new Bundle();
        b.putBoolean("unique", true);
        Intent intent = new Intent(this, TextActivity.class);
        intent.putExtras(b);
        cloudLogger.sendScoreToCloud("Your unique I.D. is " + logger.getUniqueId() + ". Your physiotherapist can view your progress via the web service using this I.D.");
        startActivity(intent);
    }

    /**
     * Start the show progress(scores) activity.
     */
    public void startProgressActivity() {
        Bundle b = new Bundle();
        b.putStringArray("listItems", new String[]{"Easy High Score: " + logger.getEasyScore(),
                "Last Easy Score: " + logger.getLastEasyScore(),
                "Medium High Score: " + logger.getMediumScore(),
                "Last Medium Score: " + logger.getLastMediumScore(),
                "Hard High Score: " + logger.getHardScore(),
                "Last Hard Score: " + logger.getLastHardScore()});
        cloudLogger.sendScoreToCloud("Your easy High Score is: " + logger.getEasyScore() +
                ", Your Last Easy Score is : " + logger.getLastEasyScore() +
                ", Your Medium High Score is: " + logger.getMediumScore() +
                ", Your Last Medium Score is: " + logger.getLastMediumScore() +
                ", Your Hard High Score is : " + logger.getHardScore() +
                ", Your Last Hard Score is : " + logger.getLastHardScore());
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    /**
     * Start a game on the watch.
     */
    public void startGameModeActivity(int horizontalMax, int verticalMax) {
        Intent i = new Intent(this, ExerciseActivity.class);
        i.putExtra("horizontalMax", horizontalMax);
        i.putExtra("verticalMax", verticalMax);
        startActivity(i);
    }

    /**
     * Start the game on the connected phone.
     */
    public void startPhoneGameActivity() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent i = new Intent(this, PhoneGameActivity.class);
        startActivity(i);
    }

    /**
     * Speak on the phone.
     * @param speech
     */
    public void speakOnPhone(String speech){
        cloudLogger.sendScoreToCloud(speech);
    }

    @Override
    protected void onDestroy() {
        if(!gameMenu && !scoreMenu && !viewUsersMenu && !settingsMenu && !deleteUsersMenu) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        super.onDestroy();
    }

    /**
     * Check which list item has been clicked.
     * @param viewHolder
     */
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

        Integer tag = (Integer) viewHolder.itemView.getTag();
        if(!gameMenu && !scoreMenu && !viewUsersMenu && !settingsMenu && !deleteUsersMenu) {
            if (tag == 0) {
                startHowActivity();
            }
            if (tag == 1) {
                startGameActivity();
            }
            if (tag == 2) {
                startProgressActivity();
            }
            if (tag == 3) {
                startSettingsActivity();
            }
            if (tag == 4) {
                startPhoneGameActivity();
            }
        }
        if(gameMenu && !scoreMenu && !viewUsersMenu && !settingsMenu && !deleteUsersMenu) {
            if (tag == 0) {
                startGameModeActivity(1000, 2000);
            }
            if (tag == 1) {
                startGameModeActivity(2000, 4000);
            }
            if (tag == 2) {
                startGameModeActivity(4000, 8000);
            }
        }
        if(settingsMenu && !scoreMenu && !gameMenu && !viewUsersMenu && !deleteUsersMenu) {
            if (tag == 0) {
                viewUniqueActivity();
            }
            if (tag == 1) {
                startChangeUserActivity();
            }
            if (tag == 2) {
                logger.saveUserArray(logger.generateUnique(6));
                startChangeUserActivity();
            }
            if (tag == 3) {
                startDeleteUserActivity();
            }
        }
        if(viewUsersMenu && !settingsMenu && !scoreMenu && !gameMenu && !deleteUsersMenu) {
            String[] array = logger.loadUserArray();
            logger.setCurrentUser(array[tag]);
            finish();
        }
        if(deleteUsersMenu && !viewUsersMenu && !settingsMenu && !scoreMenu && !gameMenu) {
            int i = tag;
            ArrayList<String> userList = new ArrayList<String>(Arrays.asList(logger.loadUserArray()));
            if(userList.size() > 1) {
                userList.remove(i);
                String[] newUserArray = userList.toArray(new String[userList.size()]);
                logger.newUserArray(newUserArray);
            }
            finish();
        }
    }

    public void voiceDelayThenFinish(){
        Handler handler = new Handler();
        Runnable r=new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        handler.postDelayed(r, 10000);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    /**
     * Custom adapter for the list.
     */
    private static final class Adapter extends WearableListView.Adapter {
        private String[] mDataset;
        private final LayoutInflater mInflater;

        public Adapter(Context context, String[] dataset, boolean scoreMenu) {
            mInflater = LayoutInflater.from(context);
            mDataset = dataset;
        }


        public static class ItemViewHolder extends WearableListView.ViewHolder {
            private TextView textView;
            public ItemViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.name);
            }
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            return new ItemViewHolder(mInflater.inflate(R.layout.list_item,null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder,
                                     int position) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            TextView view = itemHolder.textView;
            view.setText(mDataset[position]);
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        bindService(new Intent(this, com.atbsg.atbsg.menu.VoiceService.class), mServiceConnection, mBindFlag);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    /**
     * Service connection that listens out for voice commands.
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mServiceMessenger = new Messenger(service);
            Message msg = new Message();
            msg.what = com.atbsg.atbsg.menu.VoiceService.MSG_RECOGNIZER_START_LISTENING;

            try
            {
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
        }

    };

}

