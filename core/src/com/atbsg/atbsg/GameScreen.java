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

public class GameScreen implements Screen {

    final ATBSG game;
    OrthographicCamera camera;
    BackgroundColour backgroundColour;
    int score, hs;
    SpriteBatch batch;
    Texture circle, holeCircle200, holeCircle150, holeCircle100, holeCircle75;
    ArrayList<String> gameDirections = new ArrayList<String>(Arrays.asList("UP", "DOWN", "RIGHT", "LEFT"));
    int theCounter, counter, img;
    Sound scoreSound;
    boolean updateBool = false;
    boolean spoken = false;
    boolean moved = false;
    BitmapFont font;
    ArrayList<Texture> circleArray;
    int recKeeper[] = new int[] {200, 150, 100, 75};
    Rectangle circleRec, hole1;
    private long lastUpdate = 0;
    private int holdTime, spokenCounter;
    String display;
    long updatedTime, lastUpdateTime, curTime;

    public GameScreen(final ATBSG gam) {
        backgroundColour = new BackgroundColour(49, 160, 154);
        circleArray = new ArrayList<Texture>();
        createRecs();
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1000, 1080);
        batch = new SpriteBatch();
        scoreSound = Gdx.audio.newSound(Gdx.files.internal("scored.wav"));
        font = new BitmapFont(Gdx.files.internal("sptfnt.fnt"),
                Gdx.files.internal("sptfnt.png"), false);
        display = "UP";
        circle = new Texture(Gdx.files.internal("circle.png"));
        speak("Follow the directions on screen and try to get the ball inside the circle, once inside the circle try to keep it there for 2 seconds.");
    }

    /**
     * Render method called every delta time.
     * @param delta
     */
    @Override
    public void render(float delta) {
        curTime = System.currentTimeMillis();
        drawGame();
        checkUpDown();
        checkLeftRight();
        isInside();
        holdIt();
    }

    /**
     * Draws the textures and fonts
     */
    public void drawGame(){
        Gdx.gl.glClearColor(backgroundColour.getR() / 255.0f, backgroundColour.getG() / 255.0f, backgroundColour.getB() / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        font.draw(game.batch, "" + score, 50, 1035);
        font.draw(game.batch, display, 340, 1035);
        if(!(gameDirections.get(counter).equals("LEFT") || gameDirections.get(counter).equals("RIGHT"))) {
        game.batch.draw(circleArray.get(img), hole1.x, hole1.y);}
        game.batch.draw(circle, circleRec.x, circleRec.y);
        game.batch.end();
    }

    /**
     * Creates the rectangles that represent the circles and the ball.
     */
    public void createRecs() {
        hole1 = new Rectangle();
        hole1.x = 380;
        //hole1.x = MathUtils.random(200, 400);
        //hole1.y = MathUtils.random(210, 875);
        hole1.y = MathUtils.random(210, 875);
        hole1.width = 200;
        hole1.height = 200;
        circleRec = new Rectangle();
        circleRec.x = hole1.x + ((hole1.width / 2) - 25);
        //circleRec.y = 20;
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
            theCounter++;}
            hole1.x = MathUtils.random(200, 740);
            hole1.y = MathUtils.random(250, 775);
            hole1.width = recKeeper[theCounter];
            hole1.height = recKeeper[theCounter];
            img = theCounter;
            circleRec.x = hole1.x + ((hole1.width / 2) - 25);
            updateBool = false;
        if(gameDirections.get(counter).equals("LEFT") || gameDirections.get(counter).equals("RIGHT")) {
            speak("Please move your arm " + gameDirections.get(counter) + " until the ball is at the other side of the screen");
        }
        else{speak(gameDirections.get(counter));}
    }

    /**
     * Update the position of the ball and check if it has gone past the
     * circle based on the direction(UP/DOWN).
     */
    public void checkUpDown(){
        if(gameDirections.get(counter).equals("UP") || gameDirections.get(counter).equals("DOWN")) {
            circleRec.y=(game.actionResolver.getVertical()/2);
            circleRec.x = hole1.x + ((hole1.width / 2) - 25);
            if((curTime - lastUpdate) > 1500) {
                if (gameDirections.get(counter).equals("UP") && circleRec.y > (hole1.y + (recKeeper[theCounter]-50))) {
                    backgroundColour.red();
                    //score = 0;
                    counter = 0;
                    game.actionResolver.sendToPhone(gameDirections.get(counter));
                    //updateRecs();
                    display = gameDirections.get(counter);
                    reset();
                }
                if (gameDirections.get(counter).equals("DOWN") && circleRec.y < (hole1.y)) {
                    backgroundColour.red();
                    //score = 0;
                    counter = 0;
                    game.actionResolver.sendToPhone(gameDirections.get(counter));
                    //updateRecs();
                    display = gameDirections.get(counter);
                    reset();
                }
            }
        }
    }

    /**
     * Update the position of the ball and check if it has gone past the
     * circle based on the direction(LEFT/RIGHT).
     */
    public void checkLeftRight(){
        if(gameDirections.get(counter).equals("LEFT") || gameDirections.get(counter).equals("RIGHT")) {
            /*if(gameDirections.get(counter).equals("LEFT") && ((curTime - lastUpdate) > 1500)){*/
                circleRec.x=game.actionResolver.getHorizontal();
            /*}else if(gameDirections.get(counter).equals("RIGHT") && ((curTime - lastUpdate) > 1500)){*/
                /*circleRec.x=game.actionResolver.getHorizontal()+500;*/
            /**//*}*/
            circleRec.y = hole1.y + ((hole1.height/2)-25);
           /* if((curTime - lastUpdate) > 1500) {
                if (gameDirections.get(counter).equals("RIGHT") && circleRec.x > ((hole1.x + recKeeper[theCounter]-50))) {
                    backgroundColour.red();
                    //score = 0;
                    counter = 0;
                    game.actionResolver.sendToPhone(gameDirections.get(counter));
                    //updateRecs();
                    display = gameDirections.get(counter);
                    reset();
                }
                if (gameDirections.get(counter).equals("LEFT") && circleRec.x < hole1.x) {
                    backgroundColour.red();
                    //score = 0;
                    counter = 0;
                    game.actionResolver.sendToPhone(gameDirections.get(counter));
                    //updateRecs();
                    display = gameDirections.get(counter);
                    reset();
                }
            }*/
        }
        if(gameDirections.get(counter).equals("LEFT") && circleRec.x < 5 && ((curTime - lastUpdate) > 1500)){
            moved = true;
           /* backgroundColour.red();
            //score = 0;
            counter = 0;
            game.actionResolver.sendToPhone(gameDirections.get(counter));
            //updateRecs();
            display = gameDirections.get(counter);
            reset();*/
        }
        if(gameDirections.get(counter).equals("RIGHT") && circleRec.x > 945  && ((curTime - lastUpdate) > 1500)){
            moved = true;
            /*backgroundColour.red();
            //score = 0;
            counter = 0;
            game.actionResolver.sendToPhone(gameDirections.get(counter));
            //updateRecs();
            display = gameDirections.get(counter);
            reset();*/
        }
    }

    /**
     * Check if the ball is inside the current circle.
     */
    public void isInside(){
        if(hole1.contains(circleRec) && !(gameDirections.get(counter).equals("LEFT") || gameDirections.get(counter).equals("RIGHT"))){
            updateBool = true;
            if(updateBool == true && holdTime > 2000){
                spoken = false;
                if(counter == 3){
                    counter = -1;
                }
                counter++;
                game.actionResolver.sendToPhone(gameDirections.get(counter));
                display = gameDirections.get(counter);
                score = score + 1;
                setCurrentGameScore(score);
                if(score > getHighScore()){
                    setHighScore(score);
                }
                scoreSound.play(1);
                updateRecs();
                holdTime = 0;
                updateBool = false;
                backgroundColour.blue();
            }
        }else{
            if(moved){
                spoken = false;
                if(counter == 3){
                    counter = -1;
                }
                counter++;
                game.actionResolver.sendToPhone(gameDirections.get(counter));
                display = gameDirections.get(counter);
                score = score + 1;
                setCurrentGameScore(score);
                if(score > getHighScore()){
                    setHighScore(score);
                }
                scoreSound.play(1);
                updateRecs();
                holdTime = 0;
                updateBool = false;
                backgroundColour.blue();
                moved = false;
            }else{
            backgroundColour.blue();
            display = gameDirections.get(counter);
            holdTime = 0;
            updateBool = false;}
        }
    }

    /**
     * If the ball is inside the circle, start the countdown from
     * 2 seconds and update the current display to show this.
     */
    public void holdIt(){
        if(((curTime - lastUpdate) > 100) && updateBool) {
            if(!spoken && spokenCounter < 3) {
                spokenCounter++;
                speak("Hold it there");
                spoken = true;
            }
            lastUpdate = curTime;
            holdTime = holdTime + 100;
            display = "" + holdTime / 1000.0;
            backgroundColour.amber();
            if(holdTime > 1400){
                backgroundColour.green();
            }
        }
    }

    /**
     * Reset the rectangles size and position if they
     * have to start again.
     */
    public void reset(){
        backgroundColour.blue();
        theCounter = 0;
        img = 0;
        hole1.width = recKeeper[theCounter];
        hole1.height = recKeeper[theCounter];
        circleRec.x = hole1.x + ((hole1.width/2)-25);
    }

    /**
     * Sets the high score for the game.
     */
    public void setHighScore(int score){
        game.actionResolver.setGameHighScore(score);
    }

    /**
     * Gets the high score for the game.
     */
    public int getHighScore(){
        return game.actionResolver.getGameHighScore();
    }

    /**
     * Sets the current game score.
     */
    public void setCurrentGameScore(int score){
        game.actionResolver.setCurrentGameScore(score);
    }

    /**
     * Speak the current game directions or informative messages.
     */
    public void speak(String message){
        game.actionResolver.speak(message);
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
     * Garbage collection. Dispose of all the texture, fonts and sounds.
     */
    @Override
    public void dispose() {
        circle.dispose();
        holeCircle200.dispose();
        holeCircle150.dispose();
        holeCircle100.dispose();
        holeCircle75.dispose();
        font.dispose();
        scoreSound.dispose();
    }
}
