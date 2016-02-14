package com.atbsg.atbsg;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ATBSG extends Game {
	public ActionResolver actionResolver;
	public SpriteBatch batch;

	public ATBSG(ActionResolver actionResolver){
		this.actionResolver = actionResolver;
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