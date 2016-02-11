package com.atbsg.atbsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.Locale;

public class AndroidLauncher extends AndroidApplication implements ActionResolver {
	static int vertical, horizontal;
	CloudLogger cloudLogger;
	TextToSpeech t1;
	Logger logger;

	public AndroidLauncher(){

	}

	protected static void updateVertical(int mProgressStatus){
		vertical = mProgressStatus;
	}

	protected static void updateHorizontal(int mProgressStatus){
		horizontal = mProgressStatus;
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cloudLogger = new CloudLogger(this);
		cloudLogger.initApi();
		logger = new Logger(this);
		t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					t1.setLanguage(Locale.UK);
				}
			}
		});
		registerReceiver(closeGame, new IntentFilter("close"));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new ATBSG(this), config);
	}

	@Override
	public int getVertical() {
		return vertical;
	}

	@Override
	public int getHorizontal() {
		return horizontal;
	}

	@Override
	public void setVertical(int vertical) {
		AndroidLauncher.vertical=vertical;
	}

	@Override
	public void sendToPhone(String message) {
		cloudLogger.sendScoreToCloud(message);
	}

	@Override
	public void speak(String speech){
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

	@Override
	protected void onDestroy () {
		unregisterReceiver(closeGame);
		super.onDestroy();
	}
	private final BroadcastReceiver closeGame = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
}