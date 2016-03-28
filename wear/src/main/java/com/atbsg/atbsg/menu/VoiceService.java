package com.atbsg.atbsg.menu;

/**
 * Created by Steven on 28/01/2016.
 *
 * A service that listens out for user voice
 * commands throughout.
 */

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.atbsg.atbsg.games.CalibrationActivity;
import com.atbsg.atbsg.games.ExerciseActivity;
import com.atbsg.atbsg.games.PhoneGameActivity;
import com.atbsg.atbsg.how.MyApplication;
import com.atbsg.atbsg.how.TextActivity;
import com.atbsg.atbsg.logging.CloudLogger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class VoiceService extends Service {
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private Messenger voiceBinder = new Messenger(new IncomingHandler(this));
    public CloudLogger cloudLogger;
    private boolean listening;
    static final int MSG_RECOGNIZER_START_LISTENING = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        cloudLogger = new CloudLogger(getApplicationContext());
        cloudLogger.initApi();
    }

    public void startAgain() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        speechRecognizer.startListening(speechIntent);
    }

    protected static class IncomingHandler extends Handler {
        private WeakReference<VoiceService> mtarget;

        IncomingHandler(VoiceService target) {
            mtarget = new WeakReference<VoiceService>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            final VoiceService target = mtarget.get();

            switch (msg.what) {
                case MSG_RECOGNIZER_START_LISTENING:
                    if (!target.listening) {
                        target.speechRecognizer.startListening(target.speechIntent);
                        target.listening = true;
                    }
                    break;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return voiceBinder.getBinder();
    }

    private class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            listening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try {
                voiceBinder.send(message);
                System.out.println("MESSAGE2 " + message);
            } catch (RemoteException e) {

            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        /**
         * Start activity based on the voice results
         * @param results
         */
        @Override
        public void onResults(Bundle results) {
            String str = new String();
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            System.out.println("Words said " + data.get(0));
            for (int i = 0; i < data.size(); i++) {
                str += data.get(i);
            }
            if (data.get(0).contains("how")) {
                startHowActivity();
            }
            if (data.get(0).contains("game") && !data.get(0).contains("phone")) {
                startGameActivity();
            }
            if (data.get(0).contains("progress")) {
                startProgressActivity();
            }
            if (data.get(0).contains("settings")) {
                startUniqueActivity();
            }
            if (data.get(0).contains("easy")) {
                cloudLogger.sendToPhone("2HARD");
                startGameModeActivity(500, 2000);
            }
            if (data.get(0).contains("medium")) {
                cloudLogger.sendToPhone("2MEDIUM");
                startGameModeActivity(1000, 4000);
            }
            if (data.get(0).contains("hard")) {
                cloudLogger.sendToPhone("2HARD");
                startGameModeActivity(2000, 8000);
            }
            if (data.get(0).contains("phone")) {
                startPhoneGameActivity();
            }
            if (data.get(0).contains("back")) {
                finishCurrentScreen();
            }
            if (data.get(0).contains("what") && data.get(0).contains("options")) {
                speakOptions();
            }
            try { //Start listening again after getting current listener's results
                startAgain();
                Message message2 = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                voiceBinder.send(message2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        /**
         * Lets the user know their options based on the screen they're currently viewing.
         */
        public void speakOptions(){
            Activity currentActivity = ((MyApplication)getApplicationContext()).getCurrentActivity();
            String contextClass = currentActivity.getClass().getName();
            String back = "Your only option is to go back";
            if(contextClass.contains("ListActivity")){
                if(ListActivity.screenName.equals("main")){
                cloudLogger.sendToPhone("Your options are: how to play, game modes, my progress, settings and play game on phone");}
                else if(ListActivity.screenName.equals("settings")){
                    cloudLogger.sendToPhone("Your options are: Your unique I.D., change user, create new user and delete user");
                }
                else if(ListActivity.screenName.equals("game")){
                    cloudLogger.sendToPhone("Your options are: easy, medium and hard");
                }
                else if(ListActivity.screenName.equals("view")){
                    cloudLogger.sendToPhone("Your option is to select your user or to go back");
                }
                else if(ListActivity.screenName.equals("delete")){
                    cloudLogger.sendToPhone("Your option is to delete a user or to go back");
                }
                else if(ListActivity.screenName.equals("progress")) {
                    cloudLogger.sendToPhone(back);
                }
            }
            if(contextClass.contains("CalibrationActivity")){
                cloudLogger.sendToPhone(back + " or complete the calibration phase");
            }
            if(contextClass.contains("TextActivity")){
                cloudLogger.sendToPhone(back);
            }
            if(contextClass.contains("ExerciseActivity")){
                cloudLogger.sendToPhone("Your option is to keep playing or to go back");
            }
            if (contextClass.contains("PhoneGameActivity")) {
                cloudLogger.sendToPhone("Your option is to keep playing or to go back");
            }
        }

        /**
         * Start the game on the connected phone.
         */
        public void startPhoneGameActivity() {
            Intent i = new Intent(getApplicationContext(), PhoneGameActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cloudLogger.sendToPhone("Please look at your phone to see this game!");
            startActivity(i);
        }

        /**
         * Start the how to play screen
         */
        public void startHowActivity() {
            Intent intent = new Intent(getApplicationContext(), TextActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cloudLogger.sendToPhone("To play this game, strap the watch firmly on your wrist and follow the directions on screen. You can swipe right to move back a screen.");
            startActivity(intent);
        }

        /**
         * Start calibration activity
         */
        public void startGameActivity() {
            Intent intent = new Intent(getApplicationContext(), CalibrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        /**
         * Start the view unique I.D. screen
         */
        public void startUniqueActivity() {
            Bundle b = new Bundle();
            b.putBoolean("unique", true);
            Intent intent = new Intent(getApplicationContext(), TextActivity.class);
            intent.putExtras(b);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cloudLogger.sendToPhone("Your unique I.D. is " + ListActivity.logger.getUniqueId() + ". Your physiotherapist can view your progress via the web service using this I.D.");
            startActivity(intent);
        }

        /**
         * Start a game on the watch.
         */
        public void startGameModeActivity(int horizontalMax, int verticalMax) {
            Intent i = new Intent(getApplicationContext(), ExerciseActivity.class);
            i.putExtra("horizontalMax", horizontalMax);
            i.putExtra("verticalMax", verticalMax);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        /**
         * Allow the user to go back
         */
        public void finishCurrentScreen(){
            Activity currentActivity = ((MyApplication)getApplicationContext()).getCurrentActivity();
            currentActivity.finish();
        }

        /**
         * Start the show progress(scores) activity.
         */
        public void startProgressActivity() {
            Bundle b = new Bundle();
            b.putStringArray("listItems", new String[]{"Easy High Score: " + ListActivity.logger.getEasyScore(),
                    "Last Easy Score: " + ListActivity.logger.getLastEasyScore(),
                    "Medium High Score: " + ListActivity.logger.getMediumScore(),
                    "Last Medium Score: " + ListActivity.logger.getLastMediumScore(),
                    "Hard High Score: " + ListActivity.logger.getHardScore(),
                    "Last Hard Score: " + ListActivity.logger.getLastHardScore()});
            cloudLogger.sendToPhone("Your easy High Score is: " + ListActivity.logger.getEasyScore() +
                    ", Your Last Easy Score is : " + ListActivity.logger.getLastEasyScore() +
                    ", Your Medium High Score is: " + ListActivity.logger.getMediumScore() +
                    ", Your Last Medium Score is: " + ListActivity.logger.getLastMediumScore() +
                    ", Your Hard High Score is : " + ListActivity.logger.getHardScore() +
                    ", Your Last Hard Score is : " + ListActivity.logger.getLastHardScore());
            b.putBoolean("voiced", true);
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            intent.putExtras(b);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}
