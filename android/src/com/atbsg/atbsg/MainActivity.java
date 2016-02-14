package com.atbsg.atbsg;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.Date;
import java.util.Locale;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;


public class MainActivity extends AppCompatActivity{
    static TextToSpeech t1;
    private static ProgressBar mProgress, mProgressHorizontal;
    public static TextView mTextView, mTextViewDifficulty;
    int horizontalMax = 1000;
    public static String currentMode = "";
    int verticalMax = 2000;
    private static Logger logger;
    public static AndroidLauncher game = new AndroidLauncher();
    static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    /*    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        System.out.println("On Create!!");
        logger = new Logger(this);
        mContext = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {

        }catch (Exception e){

        }
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        mTextView = (TextView) findViewById(R.id.text);
        mTextViewDifficulty = (TextView) findViewById(R.id.textDifficulty);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgressHorizontal = (ProgressBar) findViewById(R.id.progressBar2);
        Drawable draw = getResources().getDrawable(R.drawable.custom_progressbar);
        Drawable hozDraw = getResources().getDrawable(R.drawable.custom_progressbarhorizontal);
        // set the drawable as progress drawable
        mProgress.setProgressDrawable(draw);
        mProgressHorizontal.setProgressDrawable(hozDraw);
        mProgressHorizontal.setMax(horizontalMax);
        mProgress.setMax(verticalMax);
        //makeHorizontalInvisible();
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    /**
     * Updates the view on the phone based on the direction,
     * score and dialog progress sent from the watch.
     * @param direction
     * @param score
     * @param mProgressStatus
     */
    protected static void updateProgressBar(String direction, String score, int mProgressStatus){
        final int mProgressStatuss = mProgressStatus;
        if((direction.equals("UP") || direction.equals("DOWN"))){
            mProgress.setProgress(mProgressStatuss);
        }
        if((direction.equals("LEFT") || direction.equals("RIGHT"))){
            mProgressHorizontal.setProgress(mProgressStatuss);
        }
        if(!mTextView.getText().equals(direction)){

            mTextView.setText(direction + " | " + score);}
        //lastUpdate = curTime;

    }

    /**
     * Sets the maximums on the progress dialog based on the
     * current game mode being played by the watch.
     * @param gameMode
     * @param horizontalMax
     * @param verticalMax
     */
    protected static void setMaximums(String gameMode, int horizontalMax, int verticalMax ) {
        mProgressHorizontal.setMax(horizontalMax);
        mProgress.setMax(verticalMax);
        currentMode = gameMode;
        mTextViewDifficulty.setText("Difficulty: " + gameMode);
    }

    /**
     * Updates the current progress of the horizontal dialog.
     * @param mProgressStatus
     */
    protected void updateHorizontalProgressBar(int mProgressStatus){
        mProgressHorizontal.setProgress(mProgressStatus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(logger.getMuted()) {
            menu.getItem(0).setChecked(true);
        }else{
            menu.getItem(0).setChecked(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mute) {
            if(item.isChecked()){
                item.setChecked(false);
                logger.setMuted(false);
            }else{
                item.setChecked(true);
                logger.setMuted(true);
            }
            //new ScorePoster().execute("0", Integer.toString(3), new Date().toString(), "Easy");
            return true;
        }

        if(id==R.id.game){
            playGame();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPause(){
        super.onPause();
    }

    /**
     * Opens up the game mode when selected on the phone.
     */
    public static void playGame(){
        Intent intent = new Intent(mContext, game.getClass());
        mContext.startActivity(intent);
    }

    /**
     * Plays speech through the phone.
     * @param speech
     */
    public static void speak(String speech){
        try {
            System.out.println("SPEAKING " + speech);
            if(!logger.getMuted()) {
                t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
            }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
        }*/
        }catch (Exception e){

        }
    }

    /**
     * Prevents the user from exiting the app, this is used to keep
     * the app running while the games are being played on the watch.
     */
    @Override
    public void onBackPressed() {

    }

}
