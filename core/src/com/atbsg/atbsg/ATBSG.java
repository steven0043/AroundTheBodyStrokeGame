package com.atbsg.atbsg;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Steven on 09/02/2016.
 *
 * Class that's called to start the Circles Game.
 *
 */

public class ATBSG extends Game {
	public PhoneGameInterface phoneGameInterface;
	public SpriteBatch batch;

	public ATBSG(PhoneGameInterface phoneGameInterface){
		this.phoneGameInterface = phoneGameInterface;
	}

	/**
	 * Creates the batch for the sprites and textures
	 * and sets the screen to the GameScreen.
	 */
	public void create() {
		batch = new SpriteBatch();
		this.setScreen(new GameScreen(this));
	}

	/**
	 * Render.
	 */
	public void render() {
		super.render();
	}

	/**
	 * Dispose the batch on exit.
	 */
	public void dispose() {
		batch.dispose();
	}

}