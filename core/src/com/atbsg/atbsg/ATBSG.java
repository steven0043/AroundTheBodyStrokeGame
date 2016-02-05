package com.atbsg.atbsg;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ATBSG extends Game {
	public ActionResolver actionResolver;
	public SpriteBatch batch;
	public BitmapFont font;

	public ATBSG(ActionResolver actionResolver){
		this.actionResolver = actionResolver;
	}

	public void create() {
		batch = new SpriteBatch();
		this.setScreen(new GameScreen(this));
	}

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		//font.dispose();
	}

}