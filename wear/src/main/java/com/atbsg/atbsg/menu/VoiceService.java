package com.atbsg.atbsg.menu;

/**
 * Created by Steven on 28/01/2016.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.atbsg.atbsg.games.SensorActivity;
import com.atbsg.atbsg.how.HowActivity;
import com.atbsg.atbsg.logging.CloudLogger;
import com.atbsg.atbsg.logging.Logger;
import com.atbsg.atbsg.menu.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class VoiceService extends Service {
    private static AudioManager mAudioManager;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Messenger mServerMessenger = new Messenger(new IncomingHandler(this));
    public CloudLogger cloudLogger;
    private Logger logger;
    private boolean mIsListening;
    private volatile boolean mIsCountDownOn;
    private static boolean mIsStreamSolo;

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;
    private static final String TAG = "Listener";

    @Override
    public void onCreate()
    {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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

    public void startAgain(){
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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

    protected static class IncomingHandler extends Handler
    {
        private WeakReference<VoiceService> mtarget;

        IncomingHandler(VoiceService target)
        {
            mtarget = new WeakReference<VoiceService>(target);
        }

        @Override
        public void handleMessage(Message msg)
        {
            final VoiceService target = mtarget.get();

            switch (msg.what)
            {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        // turn off beep sound
                        if (!mIsStreamSolo)
                        {
                            mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
                            mIsStreamSolo = true;
                        }
                    }
                    if (!target.mIsListening)
                    {
                        target.mSpeechRecognizer.startListening(target.mSpeechRecognizerIntent);
                        target.mIsListening = true;
                        //Log.d(TAG, "message start listening"); //$NON-NLS-1$
                    }
                    break;

               /* case MSG_RECOGNIZER_CANCEL:
                    if (mIsStreamSolo)
                    {
                        mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);
                        mIsStreamSolo = false;
                    }
                    target.mSpeechRecognizer.cancel();
                    target.mIsListening = false;
                    //Log.d(TAG, "message canceled recognizer"); //$NON-NLS-1$
                    break;*/
            }
        }
    }

    // Count down timer for Jelly Bean work around
    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000)
    {

        @Override
        public void onTick(long millisUntilFinished)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish()
        {
            mIsCountDownOn = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
            try
            {
                //mServerMessenger.send(message);
                message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message);
                System.out.println("MESSAGE1 " + message);
            }
            catch (RemoteException e)
            {

            }
        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mIsCountDownOn)
        {
            mNoSpeechCountDown.cancel();
        }
        if (mSpeechRecognizer != null)
        {
            //mSpeechRecognizer.destroy();
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "onBind");  //$NON-NLS-1$

        return mServerMessenger.getBinder();
    }

    private class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            // speech input will be processed, so there is no need for count down anymore
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            Log.d(TAG, "onBeginingOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "buffer");
        }

        @Override
        public void onEndOfSpeech()
        {
           /* try {
                startAgain();
                Message message2 = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            Log.d(TAG, "onEndOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onError(int error)
        {
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            mIsListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try
            {
                mServerMessenger.send(message);
                System.out.println("MESSAGE2 " + message);
            }
            catch (RemoteException e)
            {

            }
           /* if ((error == SpeechRecognizer.ERROR_NO_MATCH)
                    || (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) || (error == MSG_RECOGNIZER_CANCEL))
            {
                try {
                    startAgain();
                    Message message2 = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                    mServerMessenger.send(message2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }*/

            Log.d(TAG, "error = " + error); //$NON-NLS-1$
        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        @Override
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mIsCountDownOn = true;
                mNoSpeechCountDown.start();

            }
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            Log.d(TAG, "onResults"); //$NON-NLS-1$
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
            /*if(data.get(0).contains("sensor")){
                startSensorActivity();
            }*/

            try {
                startAgain();
                Message message2 = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {

        }

        public void startHowActivity() {
            Intent intent = new Intent(getApplicationContext(), HowActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        public void startGameActivity() {
            Intent intent = new Intent(getApplicationContext(), CalibrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        public void startUniqueActivity() {
            Bundle b=new Bundle();
            b.putBoolean("unique", true);
            Intent intent = new Intent(getApplicationContext(), HowActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(b);
            cloudLogger.sendScoreToCloud("Your unique I.D. is " + MenuActivity.logger.getUniqueId() + ". Your physiotherapist can view your progress via the web service using this I.D.");
            startActivity(intent);
        }

        public void startGameModeActivity(int horizontalMax, int verticalMax) {
            Intent i = new Intent(getApplicationContext(), SensorActivity.class);
            i.putExtra("horizontalMax", horizontalMax);
            i.putExtra("verticalMax", verticalMax);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        public void startProgressActivity() {
            Bundle b = new Bundle();
            b.putStringArray("listItems", new String[]{"Easy High Score: " + MenuActivity.logger.getEasyScore(),
                    "Last Easy Score: " + MenuActivity.logger.getLastEasyScore(),
                    "Medium High Score: " + MenuActivity.logger.getMediumScore(),
                    "Last Medium Score: " + MenuActivity.logger.getLastMediumScore(),
                    "Hard High Score: " + MenuActivity.logger.getHardScore(),
                    "Last Hard Score: " + MenuActivity.logger.getLastHardScore()});
            cloudLogger.sendScoreToCloud("Your easy High Score is: " + MenuActivity.logger.getEasyScore() +
                    ", Your Last Easy Score is : " + MenuActivity.logger.getLastEasyScore() +
                    ", Your Medium High Score is: " + MenuActivity.logger.getMediumScore() +
                    ", Your Last Medium Score is: " + MenuActivity.logger.getLastMediumScore() +
                    ", Your Hard High Score is : " + MenuActivity.logger.getHardScore() +
                    ", Your Last Hard Score is : " + MenuActivity.logger.getLastHardScore());
            b.putBoolean("voiced" ,true);
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.putExtras(b);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
       /* Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);*/
        }

    }
}
