package com.atbsg.atbsg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.Locale;

/**
 * Created by Steven
 *
 * The Main Activity for the smartphone app.
 * Contains the progress bars that act as a second
 * screen for the smartwatch.
 */

public class MainActivity extends AppCompatActivity{
    private static ProgressBar mProgress, mProgressHorizontal;
    public static TextView mTextView, mTextViewDifficulty;
    int horizontalMax = 1000;
    public static String currentMode = "";
    int verticalMax = 2000;
    private static Logger logger;
    public static GameActivity game = new GameActivity();
    private static Context mContext;
    private boolean ethics = false;
    private static boolean maximumsSet = false;
    private static String userId = "no id";
    static TextToSpeech textToSpeech;
    private final String webService = "https://devweb2014.cis.strath.ac.uk/~emb12161/WAD/ATBSG/ATBSG.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger = new Logger(this);
        mContext = this; //Keep context variable in order to start Circles Game from Listener Service

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen on, so it doesn't dim when not touched.

        textToSpeech =new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        }); //Initialise textToSpeech to English(UK)

        /**
         * Initialise text views and progress bars based on their XML.
         */
        mTextView = (TextView) findViewById(R.id.text);
        mTextViewDifficulty = (TextView) findViewById(R.id.textDifficulty);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgressHorizontal = (ProgressBar) findViewById(R.id.progressBar2);
        Drawable draw = getResources().getDrawable(R.drawable.custom_progressbar);
        Drawable hozDraw = getResources().getDrawable(R.drawable.custom_progressbarhorizontal);

        mProgress.setProgressDrawable(draw);
        mProgressHorizontal.setProgressDrawable(hozDraw);
        mProgressHorizontal.setMax(horizontalMax);
        mProgress.setMax(verticalMax);
        if(ethics){userEthicsAgreement();}
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
            if(mProgressStatuss > mProgress.getMax()+800 && !maximumsSet){
                mProgress.setMax(mProgress.getMax()*2);
            }
        }
        if((direction.equals("LEFT") || direction.equals("RIGHT"))){
            mProgressHorizontal.setProgress(mProgressStatuss);
            if(mProgressStatus > mProgressHorizontal.getMax()+800 && !maximumsSet){
                mProgressHorizontal.setMax(mProgressHorizontal.getMax()*2);
            }
        }

        if(!mTextView.getText().equals(direction)){

            mTextView.setText(direction + " | " + score);}
    }


    /**
     * Sets the maximums on the progress dialog based on the
     * current game mode being played by the watch.
     * @param gameMode
     * @param horizontalMax
     * @param verticalMax
     */
    protected static void setMaximums(String gameMode, int horizontalMax, int verticalMax ) {
        maximumsSet = true;
        mProgressHorizontal.setMax(horizontalMax);
        mProgress.setMax(verticalMax);
        currentMode = gameMode;
        mTextViewDifficulty.setText(gameMode);
    }

    /**
     * Updates the current progress of the horizontal dialog.
     * @param mProgressStatus
     */
    protected void updateHorizontalProgressBar(int mProgressStatus){
        if(mProgressStatus > mProgressHorizontal.getMax()+50){
            mProgressHorizontal.setMax(mProgressHorizontal.getMax()*2);
        }
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
        }

        if(id==R.id.game){
            circlesGameInfo();
        }
        if(id==R.id.highScore){
            circlesGameHighScore();
        }
        if(id==R.id.info){
            appInfo();
        }
        if(id==R.id.webService){
            Uri uri = Uri.parse(webService);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
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
     * Sets the userId
     */
    public static void setUserId(String user){
        userId = user;
    }

    /**
     * Returns userId
     * @return userId
     */
    public static String getUserId(){
        return userId;
    }

    /**
     * Plays speech through the phone.
     * @param speech
     */
    public static void speak(String speech){
        try {
            if(!logger.getMuted()) {
                textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
            }
        }catch (Exception e){

        }
    }

    /**
     * Agreement dialog for participants in the user evaluation.
     */
    public void userEthicsAgreement(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Participant Information and Consent");

        final TextView information = new TextView(this);
        final CheckBox agree = new CheckBox(this);
        UserEthics userEthics = new UserEthics();

        information.setText(userEthics.getUserEthics());
        information.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        information.setVerticalScrollBarEnabled(true);
        information.setMovementMethod(new ScrollingMovementMethod());

        agree.setText("I Have Read and Agree To Participate");
        agree.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        LinearLayout ll=new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        ll.addView(agree);
        ll.addView(information);

        builder.setView(ll);
        builder.setCancelable(false);

        builder.setPositiveButton("I've made my decision", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!agree.isChecked()) {
                    System.exit(0);
                }
            }

        });
        builder.show();
    }

    /**
     * Dialog that provides a reference to the user tasks
     * for participants in the evaluation.
     */
    public void userTasks(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Tasks");

        final TextView information = new TextView(this);
        UserEthics userEthics = new UserEthics();

        information.setText(userEthics.getUserTasks());
        information.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        information.setVerticalScrollBarEnabled(true);
        information.setMovementMethod(new ScrollingMovementMethod());

        LinearLayout ll=new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        ll.addView(information);

        builder.setView(ll);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        builder.show();
    }

    /**
     * A dialog that shows the high score of the circles game.
     */
    private void circlesGameHighScore(){
        new AlertDialog.Builder(this)
                .setTitle("Circles Game High Score")
                .setMessage("User I.D. - " + userId + "\nYour high score is: " + logger.getGameHighScore()+
                        "\nYour last score was: " + logger.getGameLastScore())
                .show();
    }

    /**
     * A dialog that shows information regarding the circles game.
     */
    private void circlesGameInfo(){
        new AlertDialog.Builder(this)
                .setTitle("Circles Game Information")
                .setMessage("To play this game, on your smartwatch select 'Play Game on Phone' from the menu.\n" +
                        "You must move your arm and try to hold the ball inside the circle for 2 seconds.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playGame();
                    }

                })
                .show();
    }

    /**
     * A dialog that shows information regarding the circles game.
     */
    private void appInfo(){
        new AlertDialog.Builder(this)
                .setTitle("How to Play")
                .setMessage(("This application acts as a second screen for the application on your smartwatch" +
                        " please go to the smartwatch application.\n\n" +
                        "For voice commands you must say the name of the option as it appears on the smartwatch.\n" +
                        "You can say 'back' to go back a screen.\n" + "You can say 'what are my options' to hear your voice command options.\n\n" +
                        "You can mute the in app voice by checking the mute button in the menu."))
                .show();
    }

    /**
     * Prevents the user from exiting the app, this is used to keep
     * the app running while the games are being played on the watch.
     */
    @Override
    public void onBackPressed() {

    }

}
