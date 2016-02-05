package com.atbsg.atbsg;

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.atbsg.atbsg.ATBSG;

public class AndroidLauncher extends AndroidApplication implements ActionResolver {
	static int up;

	public AndroidLauncher(){

	}

	protected static void updateUp(int mProgressStatus){
		up = mProgressStatus;
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new ATBSG(this), config);
	}

	@Override
	public int getVertical() {
		return up+1;
	}

	@Override
	public int getHorizontal() {
		return 340;
	}

	@Override
	public void setVertical(int vertical) {
		up=vertical;
	}
}