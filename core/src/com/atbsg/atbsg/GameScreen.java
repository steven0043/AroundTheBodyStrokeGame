package com.atbsg.atbsg;


import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
        speak("Follow the directions on screen and try to get the ball inside the circle!");
    }

    @Override
    public void render(float delta) {
        curTime = System.currentTimeMillis();
        drawGame();
        checkUpDown();
        checkLeftRight();
        isInside();
        holdIt();
    }

    public void drawGame(){
        Gdx.gl.glClearColor(backgroundColour.getR() / 255.0f, backgroundColour.getG() / 255.0f, backgroundColour.getB() / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        font.draw(game.batch, "" + score, 50, 1035);
        font.draw(game.batch, display, 340, 1035);
        game.batch.draw(circleArray.get(img), hole1.x, hole1.y);
        game.batch.draw(circle, circleRec.x, circleRec.y);
        game.batch.end();
    }

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

    public void updateRecs() {
        lastUpdateTime = System.currentTimeMillis();
        if(score%5==0 && score >0 && theCounter <3) {
            theCounter++;
            if (counter == 0) {
                hole1.x = MathUtils.random(200, 740);
                hole1.y = MathUtils.random(250, 875);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.x = hole1.x + ((hole1.width / 2) - 25);
                updateBool = false;
            } else if (counter == 1){
                hole1.x = MathUtils.random(200, 740);
                hole1.y = MathUtils.random(210, 750);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.x = hole1.x + ((hole1.width / 2) - 25);
                updateBool = false;
            }else if (counter == 2){
                hole1.x = MathUtils.random(200, 740);
                //hole1.y = MathUtils.random(210, 800);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.y = hole1.y - (hole1.height);
                updateBool = false;
            }
            else{
                hole1.x = MathUtils.random(20, 740);
                //hole1.y = MathUtils.random(210, 800);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.y =  hole1.y + ((hole1.height/2)-25);
                updateBool = false;
            }
        }
        else if(score > 5){
            if (counter == 0) {
                hole1.x = MathUtils.random(200, 740);
                hole1.y = MathUtils.random(250, 875);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.x = hole1.x + ((hole1.width / 2) - 25);
                updateBool = false;
            }  else if (counter == 1){
                hole1.x = MathUtils.random(200, 740);
                hole1.y = MathUtils.random(210, 750);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.x = hole1.x + ((hole1.width / 2) - 25);
                updateBool = false;
            }else if (counter == 2){
                hole1.x = MathUtils.random(200, 740);
                //hole1.y = MathUtils.random(210, 800);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.y = hole1.y - (hole1.height);
                updateBool = false;
            }
            else{
                hole1.x = MathUtils.random(20, 740);
                //hole1.y = MathUtils.random(210, 800);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.y =  hole1.y + ((hole1.height/2)-25);
                updateBool = false;
            }
        }
        else{
            if (counter == 0) {
                hole1.x = MathUtils.random(200, 740);
                hole1.y = MathUtils.random(250, 875);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.x = hole1.x + ((hole1.width / 2) - 25);
                updateBool = false;
            } else if (counter == 1){
                hole1.x = MathUtils.random(200, 740);
                hole1.y = MathUtils.random(210, 750);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.x = hole1.x + ((hole1.width / 2) - 25);
                updateBool = false;
            }else if (counter == 2){
                hole1.x = MathUtils.random(200, 740);
                //hole1.y = MathUtils.random(210, 800);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.y = hole1.y - (hole1.height);
                updateBool = false;
            }
            else{
                hole1.x = MathUtils.random(20, 740);
                //hole1.y = MathUtils.random(210, 800);
                hole1.width = recKeeper[theCounter];
                hole1.height = recKeeper[theCounter];
                img = theCounter;
                circleRec.y =  hole1.y + ((hole1.height/2)-25);
                updateBool = false;
            }
        }
        speak(gameDirections.get(counter));
    }

    public void checkUpDown(){
        if(gameDirections.get(counter).equals("UP") || gameDirections.get(counter).equals("DOWN")) {
            circleRec.y=(game.actionResolver.getVertical()/2);
            if((curTime - lastUpdate) > 1500) {
                if (gameDirections.get(counter).equals("UP") && circleRec.y > (hole1.y + (recKeeper[theCounter]-50))) {
                    backgroundColour.red();
                    score = 0;
                    counter = 0;
                    game.actionResolver.sendToPhone(gameDirections.get(counter));
                    //updateRecs();
                    display = gameDirections.get(counter);
                    reset();
                }
                if (gameDirections.get(counter).equals("DOWN") && circleRec.y < (hole1.y)) {
                    backgroundColour.red();
                    score = 0;
                    counter = 0;
                    game.actionResolver.sendToPhone(gameDirections.get(counter));
                    //updateRecs();
                    display = gameDirections.get(counter);
                    reset();
                }
            }
        }
    }

    public void checkLeftRight(){
        if(gameDirections.get(counter).equals("LEFT") || gameDirections.get(counter).equals("RIGHT")) {
            circleRec.x=game.actionResolver.getHorizontal();
            circleRec.y = hole1.y + ((hole1.height/2)-25);
            if((curTime - lastUpdate) > 1500) {
                if (gameDirections.get(counter).equals("RIGHT") && circleRec.x > ((hole1.x + recKeeper[theCounter]-50))) {
                    backgroundColour.red();
                    score = 0;
                    counter = 0;
                    game.actionResolver.sendToPhone(gameDirections.get(counter));
                    //updateRecs();
                    display = gameDirections.get(counter);
                    reset();
                }
                if (gameDirections.get(counter).equals("LEFT") && circleRec.x < hole1.x) {
                    backgroundColour.red();
                    score = 0;
                    counter = 0;
                    game.actionResolver.sendToPhone(gameDirections.get(counter));
                    //updateRecs();
                    display = gameDirections.get(counter);
                    reset();
                }
            }
        }
    }

    public void isInside(){
        if(hole1.contains(circleRec)){
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
                scoreSound.play(1);
                updateRecs();
                holdTime = 0;
                updateBool = false;
                backgroundColour.blue();
            }
        }else{
            backgroundColour.blue();
            display = gameDirections.get(counter);
            holdTime = 0;
            updateBool = false;
        }
    }

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

    public void reset(){
        backgroundColour.blue();
        hole1.width = recKeeper[theCounter];
        hole1.height = recKeeper[theCounter];
        img = 0;
        theCounter = 0;
        circleRec.x = hole1.x + ((hole1.width/2)-25);
    }

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
