package com.atbsg.atbsg;

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication implements ActionResolver {
	static int vertical, horizontal;
	CloudLogger cloudLogger;

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
}