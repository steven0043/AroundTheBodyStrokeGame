package com.atbsg.atbsg.menu;

/**
 * Created by Steven on 28/01/2016.
 *
 * A service that listens out for user voice
 * commands throughout.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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
import android.util.Log;

import com.atbsg.atbsg.games.CalibrationActivity;
import com.atbsg.atbsg.games.ExerciseActivity;
import com.atbsg.atbsg.how.TextActivity;
import com.atbsg.atbsg.logging.CloudLogger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class VoiceService extends Service {
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Messenger mServerMessenger = new Messenger(new IncomingHandler(this));
    public CloudLogger cloudLogger;
    private boolean mIsListening;

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    private static final String TAG = "Listener";

    @Override
    public void onCreate() {
        super.onCreate();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        cloudLogger = new CloudLogger(getApplicationContext());
        cloudLogger.initApi();
    }

    public void startAgain() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
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
                    if (!target.mIsListening) {
                        target.mSpeechRecognizer.startListening(target.mSpeechRecognizerIntent);
                        target.mIsListening = true;
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
        return mServerMessenger.getBinder();
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
            mIsListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try {
                mServerMessenger.send(message);
                System.out.println("MESSAGE2 " + message);
            } catch (RemoteException e) {

            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onResults(Bundle results) {
            Log.d(TAG, "onResults"); //$NON-NLS-1$
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            if (data.get(0).contains("how")) {
                startHowActivity();
            }
            if (data.get(0).contains("game")) {
                startGameActivity();
            }
            if (data.get(0).contains("progress")) {
                startProgressActivity();
            }
            if (data.get(0).contains("settings")) {
                startUniqueActivity();
            }
            if (data.get(0).contains("easy")) {
                startGameModeActivity(500, 2000);
            }
            if (data.get(0).contains("medium")) {
                startGameModeActivity(1000, 4000);
            }
            if (data.get(0).contains("hard")) {
                startGameModeActivity(2000, 8000);
            }

            try {
                startAgain();
                Message message2 = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        public void startHowActivity() {
            Intent intent = new Intent(getApplicationContext(), TextActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        public void startGameActivity() {
            Intent intent = new Intent(getApplicationContext(), CalibrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        public void startUniqueActivity() {
            Bundle b = new Bundle();
            b.putBoolean("unique", true);
            Intent intent = new Intent(getApplicationContext(), TextActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(b);
            cloudLogger.sendScoreToCloud("Your unique I.D. is " + ListActivity.logger.getUniqueId() + ". Your physiotherapist can view your progress via the web service using this I.D.");
            startActivity(intent);
        }

        public void startGameModeActivity(int horizontalMax, int verticalMax) {
            Intent i = new Intent(getApplicationContext(), ExerciseActivity.class);
            i.putExtra("horizontalMax", horizontalMax);
            i.putExtra("verticalMax", verticalMax);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        public void startProgressActivity() {
            Bundle b = new Bundle();
            b.putStringArray("listItems", new String[]{"Easy High Score: " + ListActivity.logger.getEasyScore(),
                    "Last Easy Score: " + ListActivity.logger.getLastEasyScore(),
                    "Medium High Score: " + ListActivity.logger.getMediumScore(),
                    "Last Medium Score: " + ListActivity.logger.getLastMediumScore(),
                    "Hard High Score: " + ListActivity.logger.getHardScore(),
                    "Last Hard Score: " + ListActivity.logger.getLastHardScore()});
            cloudLogger.sendScoreToCloud("Your easy High Score is: " + ListActivity.logger.getEasyScore() +
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
