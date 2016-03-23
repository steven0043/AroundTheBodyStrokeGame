package com.atbsg.atbsg;

/**
 * Created by Steven on 15/02/2016.
 *
 * This class is specifically for launching the
 * circles game and providing methods for communication
 * between the watch and the game.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.Date;
import java.util.Locale;

public class GameActivity extends AndroidApplication implements PhoneGameInterface {
	static int vertical, horizontal;
	WatchCommunicator watchCommunicator;
	TextToSpeech t1;
	Logger logger;
	private int currentGameScore = 0;
	private String userId = "no id";

	public GameActivity(){

	}

	/**
	 * Updates current vertical coordinate based on the watches
	 * accelerometer.
	 * @param mProgressStatus
	 */
	protected static void updateVertical(int mProgressStatus){
		vertical = mProgressStatus;
	}

	/**
	 * Updates current horizontal coordinate based on the watches
	 * accelerometer.
	 * @param mProgressStatus
	 */
	protected static void updateHorizontal(int mProgressStatus){
		horizontal = mProgressStatus;
	}

	/**
	 * Activity's onCreate method
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		watchCommunicator = new WatchCommunicator(this);
		watchCommunicator.initApi();
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

	/**
	 * Get vertical coordinate for game
	 * @return int current vertical number based on
	 * watch accelerometer
	 */
	@Override
	public int getVertical() {
		return vertical;
	}

	/**
	 * Get horizontal coordinate for game
	 * @return int current horizontal number based on
	 * watch accelerometer
	 */
	@Override
	public int getHorizontal() {
		return horizontal;
	}

	/**
	 * Sets the current vertical coordinate for the
	 * phone game based on watch accelerometer
	 * @param vertical
	 */
	@Override
	public void setVertical(int vertical) {
		GameActivity.vertical=vertical;
	}

	/**
	 * Send a message to the watch
	 * this is used to let the watch know
	 * when to change the direction of movement.
	 * @param message
	 */
	@Override
	public void sendToPhone(String message) {
		watchCommunicator.sendToWatch(message);
	}

	/**
	 * Plays speech through the phone.
	 * @param speech
	 */
	@Override
	public void speak(String speech){
		try {
			System.out.println("SPEAKING " + speech);
			if(!logger.getMuted()) {
				t1.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
			}
		}catch (Exception e){

		}
	}

	/**
	 * Set the game high score in shared preferences
	 * @param score
	 */
	@Override
	public void setGameHighScore(int score) {
		logger.setGameHighScore(score);
	}

	/**
	 * Get the game high score in shared preferences
	 * @return
	 */
	@Override
	public int getGameHighScore() {
		return logger.getGameHighScore();
	}

	/**
	 * Set the latest game score in shared preferences
	 * @param score
	 */
	@Override
	public void setCurrentGameScore(int score) {
		currentGameScore = score;
	}

	@Override
	protected void onDestroy () {
		new ScorePoster().execute(MainActivity.getUserId(), Integer.toString(currentGameScore), new Date().toString(), "Game");
		unregisterReceiver(closeGame);
		super.onDestroy();
	}

	/**
	 * Broadcast receiver, listening to when the user has exited
	 * the game on their watch
	 */
	private final BroadcastReceiver closeGame = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("BROADCAST");
			finish();
		}
	};
}