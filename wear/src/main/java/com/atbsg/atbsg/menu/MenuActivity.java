package com.atbsg.atbsg.menu;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.atbsg.atbsg.R;
import com.atbsg.atbsg.games.CalibrationActivity;
import com.atbsg.atbsg.games.PhoneGameActivity;
import com.atbsg.atbsg.games.SensorActivity;
import com.atbsg.atbsg.how.HowActivity;
import com.atbsg.atbsg.logging.CloudLogger;
import com.atbsg.atbsg.logging.Logger;

import java.util.ArrayList;

public class MenuActivity extends Activity implements WearableListView.ClickListener {

    private ListView lv;
    String[] elements = {"How To Play", "Game Modes", "My Progress", "Settings", "Sensor Data", "Play Game"};
    public static Logger logger;
    private static final String START_SPEECH = "Welcome to the around the body stroke recovery game. Your starting " +
            "options are: how to play, game modes, my progress and settings";
    boolean gameMenu = false;
    boolean scoreMenu = false;
    boolean settingsMenu = false;
    boolean viewUsersMenu = false;
    public CloudLogger cloudLogger;
    private int mBindFlag;
    private Messenger mServiceMessenger;
    private SpeechRecognizer sr;
    private static final String TAG = "Listener";
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        logger = new Logger(this);
        //sr = SpeechRecognizer.createSpeechRecognizer(this);
        //sr.setRecognitionListener(new listener());
        Bundle b=this.getIntent().getExtras();
        //displaySpeechRecognizer();
        //startService();

        cloudLogger = new CloudLogger(this);
        if(b!=null){
            String[] array=b.getStringArray("listItems");
            boolean voiced = b.getBoolean("voiced");
            elements = array;
            if(elements[0].startsWith("Easy High")){
                scoreMenu = true;
            }else if(elements[0].equals("Easy")){
                gameMenu = true;
            }else if(elements[0].startsWith("Your")){
                settingsMenu = true;
            }else{
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
            Intent service = new Intent(MenuActivity.this, VoiceService.class);
            MenuActivity.this.startService(service);
            mBindFlag = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH ? 0 : Context.BIND_ABOVE_CLIENT;
            cloudLogger = new CloudLogger(this);
            cloudLogger.initApi();
            cloudLogger.sendScoreToCloud(START_SPEECH);
        }
        // Get the list component from the layout of the activity
        WearableListView listView =
                (WearableListView) findViewById(R.id.wearable_list);

        // Assign an adapter to the list
        listView.setAdapter(new Adapter(this, elements, scoreMenu));

        // Set a click listener
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
        Intent intent = new Intent(this, HowActivity.class);
        cloudLogger.sendScoreToCloud("To play this game, strap the watch firmly on your wrist and follow the directions on screen. You can swipe right to move back a screen.");
        startActivity(intent);
    }

    /**
     * Start the show unique I.D. screen
     */
    public void startSettingsActivity() {
        Bundle b = new Bundle();
        b.putStringArray("listItems", new String[]{"Your Unique I.D.", "Change User", "Create New User"});
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void startChangeUserActivity() {
        Bundle b = new Bundle();
        b.putStringArray("listItems", logger.loadUserArray());
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }


    public void viewUniqueActivity(){
        Bundle b=new Bundle();
        b.putBoolean("unique", true);
        Intent intent = new Intent(this, HowActivity.class);
        intent.putExtras(b);
        cloudLogger.sendScoreToCloud("Your unique I.D. is " + logger.getUniqueId() + ". Your physiotherapist can view your progress via the web service using this I.D.");
        startActivity(intent);
    }


    public void listenOut(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.aroundbodygame.stroke.aroundthebodystrokegame.menu");
        sr.startListening(intent);
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
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtras(b);
        startActivity(intent);
       /* Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);*/
    }

    public void startSensorActivity() {
        Intent intent = new Intent(this, SensorMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Start a game on the watch.
     */
    public void startGameModeActivity(int horizontalMax, int verticalMax) {
        Intent i = new Intent(this, SensorActivity.class);
        i.putExtra("horizontalMax", horizontalMax);
        i.putExtra("verticalMax", verticalMax);
        startActivity(i);
    }

    /**
     * Start the game on the connected phone.
     */
    public void startPhoneGameActivity() {
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
        if(!gameMenu && !scoreMenu && !viewUsersMenu && !settingsMenu) {
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
        String text = (String) viewHolder.itemView.getTag(tag);
        System.out.println("Text " + text);
        if(!gameMenu && !scoreMenu && !viewUsersMenu && !settingsMenu) {
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
                startSensorActivity();
            }
            if (tag == 5) {
                startPhoneGameActivity();
            }
        }
        if(gameMenu && !scoreMenu) {
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
        if(settingsMenu && !scoreMenu && !gameMenu && !viewUsersMenu) {
            if (tag == 0) {
                viewUniqueActivity();
            }
            if (tag == 1) {
                startChangeUserActivity();
            }
            if (tag == 2) {
                logger.saveUserArray(logger.generateUnique(6));
            }
        }
        if(viewUsersMenu && !settingsMenu && !scoreMenu && !gameMenu) {
            String[] array = logger.loadUserArray();
            logger.setCurrentUser(array[tag]);
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
        private final Context mContext;
        private final LayoutInflater mInflater;
        private static boolean scoreMenu = false;

        // Provide a suitable constructor (depends on the kind of dataset)
        public Adapter(Context context, String[] dataset, boolean scoreMenu) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mDataset = dataset;
            this.scoreMenu = scoreMenu;
        }

        // Provide a reference to the type of views you're using
        public static class ItemViewHolder extends WearableListView.ViewHolder {
            private TextView textView;
            public ItemViewHolder(View itemView) {
                super(itemView);
                // find the text view within the custom item's layout
                textView = (TextView) itemView.findViewById(R.id.name);
            }
        }

        // Create new views for list items
        // (invoked by the WearableListView's layout manager)
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            // Inflate our custom layout for list items
            return new ItemViewHolder(mInflater.inflate(R.layout.list_item,null));
        }

        // Replace the contents of a list item
        // Instead of creating new views, the list tries to recycle existing ones
        // (invoked by the WearableListView's layout manager)
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder,
                                     int position) {
            // retrieve the text view
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            TextView view = itemHolder.textView;
            // replace text contents
            view.setText(mDataset[position]);
            // replace list item's metadata
            holder.itemView.setTag(position);
        }

        // Return the size of your dataset
        // (invoked by the WearableListView's layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            //Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            //Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            //Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            //Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            //Log.d(TAG, "error " + error);
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            if(data.get(0).contains("how")){
                startHowActivity();
            }
            if(data.get(0).contains("game")){
                startGameActivity();
            }
            if(data.get(0).contains("progress")){
                startProgressActivity();
            }
            if(data.get(0).contains("settings")){
                startSettingsActivity();
            }
            if(data.get(0).contains("sensor")){
                startSensorActivity();
            }
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    public void startService() {
        startService(new Intent(MenuActivity.this, VoiceService.class));
    }

    public void stopService() {
        stopService(new Intent(getBaseContext(), VoiceService.class));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        bindService(new Intent(this, VoiceService.class), mServiceConnection, mBindFlag);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        Log.d(TAG, "onStop");
        /*if (mServiceMessenger != null)
        {
            unbindService(mServiceConnection);
            mServiceMessenger = null;
        }*/
    }

    /**
     * Service connection that listens out for voice commands.
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            if (true) {Log.d(TAG, "onServiceConnected");} //$NON-NLS-1$

            mServiceMessenger = new Messenger(service);
            Message msg = new Message();
            msg.what = VoiceService.MSG_RECOGNIZER_START_LISTENING;

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
        {/*
            if (true) {Log.d(TAG, "onServiceDisconnected");} //$NON-NLS-1$
            mServiceMessenger = null;*/
        }

    };

}

