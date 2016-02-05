/*
package com.copter.flight;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    final SimpleGame game;

    Texture brickImage, redPipe, purplePipe;
    Texture pipe2Image, redPipe2, purplePipe2;
    Texture copterImage, mainImage, cloudImage;
    Texture squareImage, redSquare, purpleSquare, bullet;
    Sound tap, scored, die;
    int changeY, changeX;
    Music choppaMusic;
    MainMenuScreen gameOver;
    OrthographicCamera camera;
    Rectangle copter;
    Array<Rectangle> brickArray;
    long lastRenderTime, scoreTime, lastDropTime, lastCloudTime;
    boolean already, textureBool, touch;
    int score, pacer, textureChooser, pipeWidth;
    int highScore, cloudX, cloudY;
    public BitmapFont font;
    Preferences prefs;

    public GameScreen(final SimpleGame gam) {
        this.game = gam;
        prefs = Gdx.app.getPreferences("My Preferences");
        // load the images for the dog and the bucket, 55x55 pixels each
        mainImage = new Texture(Gdx.files.internal("background.png"));
        copterImage = new Texture(Gdx.files.internal("copter.png"));
        cloudImage = new Texture(Gdx.files.internal("cloud.png"));
        brickImage = new Texture(Gdx.files.internal("bricks.png"));

        tap = Gdx.audio.newSound(Gdx.files.internal("tap.wav"));
        scored = Gdx.audio.newSound(Gdx.files.internal("scored.wav"));
        die = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        choppaMusic = Gdx.audio.newMusic(Gdx.files.internal("choppa.mp3"));
        choppaMusic.setLooping(true);
        choppaMusic.play();
        font = new BitmapFont(Gdx.files.internal("whitetext.fnt"),
                Gdx.files.internal("whitetext.png"), false);
        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 360);

        copter = new Rectangle();
        copter.x = 180;
        copter.y = 180;
        copter.width = 60;
        copter.height = 40;
        cloudX = 660;
        cloudY = MathUtils.random(0, 295);
        brickArray = new Array<Rectangle>();
        touch = false;
        score = 0;
        spawnPipes();
    }

    private void spawnPipes() {
        Rectangle bricks = new Rectangle();
        bricks.x = 650;
        bricks.y = MathUtils.random(0, 295);
        bricks.width = 25;
        bricks.height = 60;
        already = false;
        brickArray.add(bricks);
        lastDropTime = TimeUtils.nanoTime();
        scoreTime = TimeUtils.nanoTime();

    }


    @Override
    public void render(float delta) {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor( 97 / 255.0f, 152 / 255.0f, 249 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        game.batch.draw(cloudImage, cloudX, cloudY);
        if(score > 0){
            font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            font.draw(game.batch, "" + score , 550, 329);
        }
        game.batch.draw(copterImage, copter.x, copter.y);
        for (Rectangle raindrop : brickArray) {
            game.batch.draw(brickImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            copter.y = copter.y + 5;

        }
        else{
            copter.y = copter.y - 5;
        }

        cloudX = cloudX - 3;
        if(cloudX < -150){
            cloudX = 660;
            cloudY = MathUtils.random(0, 222);
        }

        if (copter.y < 0){
            die.play();
            choppaMusic.stop();
            if(score > prefs.getInteger("highscore")){
                setBestScore(score);
            }
            game.setScreen(new ScoreScreen(game, score));}
        if (copter.y > 320){
            die.play();
            choppaMusic.stop();
            if(score > prefs.getInteger("highscore")){
                setBestScore(score);
            }
            dispose();
            game.setScreen(new ScoreScreen(game, score));}

        if (TimeUtils.nanoTime() - lastDropTime > 800000000)
            spawnPipes();

        Iterator<Rectangle> iter = brickArray.iterator();
        while (iter.hasNext()) {
            Rectangle pipe1It = iter.next();
            pipe1It.x -= 200 * Gdx.graphics.getDeltaTime();
            if (pipe1It.x + 25 < 0){
                iter.remove();}
            if(pipe1It.x < copter.x){
                if(already == false){
                    //scored.play();
                    score++;
                    already = true;
                }
            }
            if (pipe1It.overlaps(copter)) {
                die.play();
                choppaMusic.stop();
                if(score > prefs.getInteger("highscore")){
                    setBestScore(score);
                }
                dispose();
                game.setScreen(new ScoreScreen(game, score));
                //meowSound.play();
                iter.remove();
            }
        }

    }

    public void setBestScore(int x){
        highScore = x;
        prefs.putInteger("highscore", highScore);
        // bulk update your preferences
        prefs.flush();
    }
    public int getBestScore(){
        return prefs.getInteger("highscore");
    }
    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        mainImage.dispose();
        copterImage.dispose();
        cloudImage.dispose();
        brickImage.dispose();
        tap.dispose();
        scored.dispose();
        choppaMusic.dispose();
        font.dispose();
    }
}
*/
