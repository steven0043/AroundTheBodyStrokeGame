package com.atbsg.atbsg;


import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
/**
 * Created by Steven on 05/02/2016.
 *
 * Game Screen for the Circles Game contains
 * all the rendering and game logic.
 *
 */

public class GameScreen implements Screen {
    final ATBSG game;
    OrthographicCamera camera;
    BackgroundColour backgroundColour;
    int score;
    SpriteBatch batch;
    Texture ball, holeCircle200, holeCircle150, holeCircle100, holeCircle75;
    ArrayList<String> gameDirections = new ArrayList<String>(Arrays.asList("UP", "DOWN"));
    int imageCounter, counter, img;
    Sound scoreSound;
    boolean updateBool = false;
    boolean spoken = false;
    BitmapFont font;
    ArrayList<Texture> circleArray;
    int recKeeper[] = new int[] {200, 150, 100, 75};
    Rectangle circleRec, hole1;
    private long lastUpdate = 0;
    private int holdTime, spokenCounter;
    String display;
    long lastUpdateTime, curTime;

    public GameScreen(final ATBSG gam) {
        backgroundColour = new BackgroundColour(49, 60, 154);
        circleArray = new ArrayList<Texture>();
        createRecs();
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1000, 1080);
        batch = new SpriteBatch();
        scoreSound = Gdx.audio.newSound(Gdx.files.internal("scored.wav"));
        font = new BitmapFont(Gdx.files.internal("abstractFont.fnt"),
                Gdx.files.internal("abstractFont.png"), false);
        display = "UP";
        ball = new Texture(Gdx.files.internal("ball.png"));
        speak("Follow the directions on screen and try to get the ball inside the circle, once inside the circle try to keep it there for 2 seconds.");
    }

    /**
     * Render method called every delta time, updates game state, draws assets on screen.
     * @param delta
     */
    @Override
    public void render(float delta) {
        curTime = System.currentTimeMillis();
        drawGame();
        checkUpDown();
        isInside();
        holdIt();
    }

    /**
     * Draws the textures and fonts
     */
    public void drawGame(){
        /* Sets background colour. */
        Gdx.gl.glClearColor(backgroundColour.getR() / 255.0f, backgroundColour.getG() / 255.0f, backgroundColour.getB() / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        /* Draw all game assets*/
        game.batch.begin();
        font.draw(game.batch, "" + score, 50, 1035);
        font.draw(game.batch, display, 365, 1035);
        game.batch.draw(circleArray.get(img), hole1.x, hole1.y);
        game.batch.draw(ball, circleRec.x, circleRec.y);
        game.batch.end();
    }

    /**
     * Creates the rectangles that represent the circles and the ball.
     */
    public void createRecs() {
        hole1 = new Rectangle();
        hole1.x = 380;
        hole1.y = MathUtils.random(210, 875);
        hole1.width = 200;
        hole1.height = 200;
        circleRec = new Rectangle();
        circleRec.x = hole1.x + ((hole1.width / 2) - 25);
        circleRec.width = 50;
        circleRec.height = 50;
        holeCircle200 = new Texture(Gdx.files.internal("200.png"));
        holeCircle150 = new Texture(Gdx.files.internal("150.png"));
        holeCircle100 = new Texture(Gdx.files.internal("100.png"));
        holeCircle75 = new Texture(Gdx.files.internal("75.png"));
        circleArray.add(holeCircle200);
        circleArray.add(holeCircle150);
        circleArray.add(holeCircle100);
        circleArray.add(holeCircle75);
    }

    /**
     * Update the rectangles position and size based
     * on the current game state.
     */
    public void updateRecs() {
        lastUpdateTime = System.currentTimeMillis();
            if(score % 5 == 0){
                imageCounter++;
                if(imageCounter>3){ imageCounter = 0;}
            }
            hole1.x = MathUtils.random(200, 740);
            hole1.y = MathUtils.random(250, 775);
            hole1.width = recKeeper[imageCounter];
            hole1.height = recKeeper[imageCounter];
            img = imageCounter;
            circleRec.x = hole1.x + ((hole1.width / 2) - 25);
            updateBool = false;
       speak(gameDirections.get(counter));
    }

    /**
     * Update the position of the ball and check if it has gone past the
     * ball based on the direction(UP/DOWN).
     */
    public void checkUpDown(){
        if(gameDirections.get(counter).equals("UP") || gameDirections.get(counter).equals("DOWN")) {
            circleRec.y=(game.phoneGameInterface.getVertical()/2);
            circleRec.x = hole1.x + ((hole1.width / 2) - 25);
            if((curTime - lastUpdate) > 1500) {
                if (gameDirections.get(counter).equals("UP") && circleRec.y > (hole1.y + (recKeeper[imageCounter]-50))) {
                    counter = 0;
                    game.phoneGameInterface.sendToPhone(gameDirections.get(counter));
                    display = gameDirections.get(counter);
                    reset();
                }
                if (gameDirections.get(counter).equals("DOWN") && circleRec.y < (hole1.y)) {
                    counter = 0;
                    game.phoneGameInterface.sendToPhone(gameDirections.get(counter));
                    display = gameDirections.get(counter);
                    reset();
                }
            }
        }
    }


    /**
     * Check if the ball is inside the current ball.
     */
    public void isInside(){
        /* Only up and down motions have a ball, if so check if inside*/
        if(hole1.contains(circleRec) && !(gameDirections.get(counter).equals("LEFT") || gameDirections.get(counter).equals("RIGHT"))){
            updateBool = true;
            /* Check if ball has been inside the ball for more than 2 seconds*/
            if(updateBool == true && holdTime > 2000){
                spoken = false;
                if((counter == 1)){
                    counter = -1;
                }
                counter++;
                game.phoneGameInterface.sendToPhone(gameDirections.get(counter)); //Tell the watch to change current direction
                display = gameDirections.get(counter); //Change the displayed text to the current motion
                score = score + 1; //Ball has been inside ball for more than 2 seconds, increment score
                setCurrentGameScore(score);
                if(score > getHighScore()){
                    setHighScore(score); //Set new high score if current game score is greater than current high score
                }
                scoreSound.play(1); //Play the 'ding' sound that signifies an increase in score
                updateRecs(); //Update the game rectangles
                holdTime = 0;
                updateBool = false;
                backgroundColour.blue();
            }
        }else{
            // Else set background to blue and carry on
            backgroundColour.blue();
            display = gameDirections.get(counter);
            holdTime = 0;
            updateBool = false;
        }
    }

    /**
     * If the ball is inside the ball, start the countdown from
     * 2 seconds and update the current display to show this.
     */
    public void holdIt(){
        if(((curTime - lastUpdate) > 100) && updateBool) {
            /* Tell the user to hold there arm steady if ball is inside ball */
            if(!spoken && spokenCounter < 1) {
                spokenCounter++;
                speak("Hold it there");
                spoken = true;
            }
            lastUpdate = curTime;
            holdTime = holdTime + 100;
            display = "" + holdTime / 1000.0;
            backgroundColour.amber(); // Set background colour to amber, to notify user they're in the ball
            if(holdTime > 1400){
                backgroundColour.green(); // For last .6 seconds, set background colour to green, to notify user they have nearly scored a point
            }
        }
    }

    /**
     * Reset the rectangles size and position if they
     * have to start again.
     */
    public void reset(){
        backgroundColour.blue();
        hole1.width = recKeeper[imageCounter];
        hole1.height = recKeeper[imageCounter];
        circleRec.x = hole1.x + ((hole1.width/2)-25);
    }

    /**
     * Sets the high score for the game.
     */
    public void setHighScore(int score){
        game.phoneGameInterface.setGameHighScore(score);
    }

    /**
     * Gets the high score for the game.
     */
    public int getHighScore(){
        return game.phoneGameInterface.getGameHighScore();
    }

    /**
     * Sets the current game score.
     */
    public void setCurrentGameScore(int score){
        game.phoneGameInterface.setCurrentGameScore(score);
    }

    /**
     * Speak the current game directions or informative messages.
     */
    public void speak(String message){
        game.phoneGameInterface.speak(message);
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

    /**
     * Garbage collection. Dispose of all the textures, fonts and sounds.
     */
    @Override
    public void dispose() {
        ball.dispose();
        holeCircle200.dispose();
        holeCircle150.dispose();
        holeCircle100.dispose();
        holeCircle75.dispose();
        font.dispose();
        scoreSound.dispose();
    }
}
