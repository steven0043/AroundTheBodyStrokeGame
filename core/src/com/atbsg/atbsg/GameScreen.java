package com.atbsg.atbsg;


import java.util.ArrayList;
import java.util.Iterator;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

    final ATBSG game;
    OrthographicCamera camera;
    int score, hs;
    SpriteBatch batch;
    Texture circle, holeCircle200, holeCircle150, holeCircle100, holeCircle75, home, cloudImage, snowImage, brickImage;
    int changeY, yCord, xCord, theCounter, img, r, g, b;
    Sound fireSound, scoreSound;
    boolean fire = false;
    boolean updateBool = false;
    boolean changeRecBool = false;
    boolean right = true;
    boolean left = false;
    BitmapFont font;
    Preferences prefs;
    ArrayList<Rectangle> holeArray;
    ArrayList<Texture> circleArray;
    ArrayList<Rectangle> cloudArray;
    ArrayList<Rectangle> snowArray;
    Array<Rectangle> brickArray;
    int recKeeper[] = new int[] {200, 150, 100, 75};
    Rectangle circleRec;
    long yTime,xTime, lastDropTime;

    public GameScreen(final ATBSG gam) {
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 610, 1080);
        prefs = Gdx.app.getPreferences("My Preferences");
        batch = new SpriteBatch();
        circleArray = new ArrayList<Texture>();
        fireSound = Gdx.audio.newSound(Gdx.files.internal("tap.wav"));
        scoreSound = Gdx.audio.newSound(Gdx.files.internal("scored.wav"));
        font = new BitmapFont(Gdx.files.internal("sptfnt.fnt"),
                Gdx.files.internal("sptfnt.png"), false);
        r = 49;
        g = 60;
        b = 154;
        brickImage = new Texture(Gdx.files.internal("brick.png"));
        changeY = 1;
        brickArray = new Array<Rectangle>();
        createRecs();
        spawnBricks();
    }

    private void spawnBricks() {
        Rectangle bricks = new Rectangle();
        bricks.x = 650;
        bricks.y = MathUtils.random(0, 1000);
        bricks.width = 70;
        bricks.height = 20;
        brickArray.add(bricks);
        lastDropTime = TimeUtils.nanoTime();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(r / 255.0f, g / 255.0f, b / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        font.draw(game.batch, "" + score, 25, 1035);
        for (Rectangle brick : brickArray) {
            game.batch.draw(brickImage, brick.x, brick.y);
        }
        game.batch.draw(circle, circleRec.x, circleRec.y);
        game.batch.end();

        circleRec.y=(game.actionResolver.getVertical()/2);

        if(/*circleRec.y <0 || */circleRec.y == 1000){
            score = score + 1;
            scoreSound.play(1);
            updateBool = true;
            circleRec.y = 20;
            game.actionResolver.setVertical(20);
            if(score > getNormScore()){
                setNormScore(score);
            }//f
        }

        Iterator<Rectangle> iter = brickArray.iterator();
        while (iter.hasNext()) {
            Rectangle pipe1It = iter.next();
            pipe1It.x -= 200 * Gdx.graphics.getDeltaTime();
            if (pipe1It.x + 70 < 0){
                iter.remove();}
            if (pipe1It.overlaps(circleRec)) {
                if(score > getNormScore()){
                    setNormScore(score);
                }
                startAgain();
                iter.remove();
            }
        }
        if (TimeUtils.nanoTime() - lastDropTime > 900000000)
            spawnBricks();
    }

    public void startAgain(){
        score = 0;
        circleRec.y = 20;
        game.actionResolver.setVertical(20);
        if(score > getNormScore()){
            setNormScore(score);
        }
    }
    public void setNormScore(int score){
        hs = score;
        prefs.putInteger("normScore", hs);
        prefs.flush();
    }

    public int getNormScore(){
        return prefs.getInteger("normScore");
    }

    public void createRecs(){
        circleRec = new Rectangle();
        circleRec.x = 300;
        circleRec.y = 20;
        circleRec.width = 50;
        circleRec.height = 50;
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
        home.dispose();
        font.dispose();
        fireSound.dispose();
        scoreSound.dispose();
        snowImage.dispose();
        cloudImage.dispose();
    }
}
