//package com.atbsg.atbsg;
//
///**
// * Created by Steven on 04/02/2016.
// */
//
//import com.badlogic.gdx.InputProcessor;
//
//package com.HitThe.Spot;
//
//        import java.util.ArrayList;
//        import java.util.Iterator;
//
//        import com.badlogic.gdx.Gdx;
//        import com.badlogic.gdx.InputProcessor;
//        import com.badlogic.gdx.Preferences;
//        import com.badlogic.gdx.Screen;
//        import com.badlogic.gdx.audio.Sound;
//        import com.badlogic.gdx.graphics.GL20;
//        import com.badlogic.gdx.graphics.OrthographicCamera;
//        import com.badlogic.gdx.graphics.Texture;
//        import com.badlogic.gdx.graphics.g2d.BitmapFont;
//        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//        import com.badlogic.gdx.math.MathUtils;
//        import com.badlogic.gdx.math.Rectangle;
//        import com.badlogic.gdx.math.Vector3;
//        import com.badlogic.gdx.utils.TimeUtils;
//
//public class CrazyScreen implements Screen, InputProcessor {
//
//    final SimpleGame game;
//    OrthographicCamera camera;
//    int score, hs, r, g, b;
//    SpriteBatch batch;
//    Texture circle, holeCircle200, holeCircle150, holeCircle100, holeCircle75, home, cloudImage, snowImage;
//    int changeY, yCord, xCord, theCounter, img;
//    Sound fireSound, scoreSound;
//    boolean fire = false;
//    boolean updateBool = false;
//    boolean changeRecBool = false;
//    boolean right = true;
//    boolean left = false;
//    boolean cloudBool = false;
//    boolean snowBool = false;
//    BitmapFont font;
//    Preferences prefs;
//    UnlockHelper uh;
//    ArrayList<Rectangle> holeArray;
//    ArrayList<Texture> circleArray;
//    ArrayList<Rectangle> cloudArray;
//    ArrayList<Rectangle> snowArray;
//    int recKeeper[] = new int[] {200, 150, 100, 75};
//    Rectangle circleRec, hole1, hole2, hole3, hole4, homeRec;
//    long yTime, xTime, lastCloudTime, lastSnowTime;
//
//    public CrazyScreen(final SimpleGame gam) {
//        game = gam;
//        camera = new OrthographicCamera();
//        camera.setToOrtho(false, 610, 1080);
//        prefs = Gdx.app.getPreferences("My Preferences");
//        uh = new UnlockHelper();
//        batch = new SpriteBatch();
//        circleArray = new ArrayList<Texture>();
//        fireSound = Gdx.audio.newSound(Gdx.files.internal("tap.wav"));
//        scoreSound = Gdx.audio.newSound(Gdx.files.internal("scored.wav"));
//        font = new BitmapFont(Gdx.files.internal("sptfnt.fnt"),
//                Gdx.files.internal("sptfnt.png"), false);
//        home = new Texture(Gdx.files.internal("home.png"));
//        cloudImage = new Texture(Gdx.files.internal("cloud.png"));
//        snowImage = new Texture(Gdx.files.internal("snow.png"));
//        r = 248;
//        g = 248;
//        b = 258;
//        checkLock();
//        changeY = 1;
//        Gdx.input.setInputProcessor(this);
//        holeArray = new ArrayList<Rectangle>();
//        cloudArray = new ArrayList<Rectangle>();
//        snowArray = new ArrayList<Rectangle>();
//        createRecs();
//    }
//
//    private void yAdd() {
//        if(changeY<46){
//            changeY=changeY+1;
//        }
//        yTime = TimeUtils.nanoTime();
//
//    }
//
//    private void xAdd() {
//        if(hole1.x + hole1.width > 610){
//            right = false;
//            left = true;
//        }
//        if(hole1.x < 0){
//            right = true;
//            left = false;
//        }
//
//        if(right == true){
//            hole1.x = hole1.x + 3;
//        }
//        if(left == true){
//            hole1.x = hole1.x - 3;
//        }
//        xTime = TimeUtils.nanoTime();
//
//    }
//    private void spawnClouds() {
//        Rectangle clouds = new Rectangle();
//        clouds.x = 660;
//        clouds.y = MathUtils.random(160, 935);
//        clouds.width = 50;
//        clouds.height = 50;
//        cloudArray.add(clouds);
//        lastCloudTime = TimeUtils.nanoTime();
//    }
//    private void spawnSnow() {
//        Rectangle snow = new Rectangle();
//        snow.x = MathUtils.random(60, 545);
//        snow.y = 1150;
//        snow.width = 50;
//        snow.height = 50;
//        snowArray.add(snow);
//        lastSnowTime = TimeUtils.nanoTime();
//    }
//
//    @Override
//    public void render(float delta) {
//        Gdx.gl.glClearColor(r / 255.0f, g / 255.0f, b / 255.0f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        camera.update();
//        game.batch.setProjectionMatrix(camera.combined);
//
//        game.batch.begin();
//        if(cloudBool == true){
//            for (Rectangle cloud : cloudArray) {
//                game.batch.draw(cloudImage, cloud.x, cloud.y);
//            }
//        }
//        if(snowBool == true){
//            for (Rectangle snow : snowArray) {
//                game.batch.draw(snowImage, snow.x, snow.y);
//            }
//        }
//        font.draw(game.batch, "" + score, 25, 1035);
//        game.batch.draw(home, 20, 20);
//        game.batch.draw(circleArray.get(img), hole1.x, hole1.y);
//        game.batch.draw(circle, circleRec.x, circleRec.y);
//        game.batch.end();
//
//        if(fire == true){
//            changeY=changeY-1;
//            circleRec.y=circleRec.y+changeY;
//        }
//
//        if(circleRec.y <20 || circleRec.y > 1080){
//            circleRec.y = 20;
//            changeY = 1;
//
//            if(updateBool == true){
//                updateRecs();
//            }
//            else{
//                if(score > getCrazyScore()){
//                    setCrazyScore(score);
//                }
//                score = 0;
//                theCounter = 0;
//                img = 0;
//                widths();
//            }
//            fire = false;
//        }
//        if(hole1.contains(circleRec) && changeY == 0){
//            score = score + 1;
//            scoreSound.play(1);
//            updateBool = true;
//        }
//
//        if (TimeUtils.nanoTime() - xTime > 15000 ){
//            xAdd();
//        }
//        if (Gdx.input.isTouched() && fire == false) {
//            if (TimeUtils.nanoTime() - yTime > 15000000 ){
//                yAdd();
//            }
//        }
//        if (Gdx.input.justTouched()) {
//            Vector3 touchPos = new Vector3();
//            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//            camera.unproject(touchPos);
//            if(homeRec.contains(touchPos.x, touchPos.y)){
//                if(score > getCrazyScore()){
//                    setCrazyScore(score);
//                }
//                dispose();
//                game.setScreen(new MainMenuScreen(game));
//            }
//        }
//
//        if(cloudBool == true){
//            if (TimeUtils.nanoTime() - lastCloudTime > 2140000000)
//                spawnClouds();
//
//            Iterator<Rectangle> iter = cloudArray.iterator();
//            while (iter.hasNext()) {
//                Rectangle cloud1It = iter.next();
//                cloud1It.x -= 80 * Gdx.graphics.getDeltaTime();
//                if (cloud1It.x < -160){
//                    iter.remove();
//                }
//            }
//        }
//        if(snowBool == true){
//            if (TimeUtils.nanoTime() - lastSnowTime > 740000000)
//                spawnSnow();
//
//            Iterator<Rectangle> iter = snowArray.iterator();
//            while (iter.hasNext()) {
//                Rectangle snow1It = iter.next();
//                snow1It.y -= 100 * Gdx.graphics.getDeltaTime();
//                if (snow1It.y + 25 < 0){
//                    iter.remove();
//                }
//            }
//        }
//
//    }
//
//    public void setCrazyScore(int score){
//        hs = score;
//        prefs.putInteger("crazyScore", hs);
//        prefs.flush();
//    }
//
//    public int getCrazyScore(){
//        return prefs.getInteger("crazyScore");
//    }
//
//    public void createRecs(){
//        hole1 = new Rectangle();
//        hole1.x = MathUtils.random(200, 400);
//        hole1.y = MathUtils.random(210, 875);
//        hole1.width = 200;
//        hole1.height = 200;
//        holeArray.add(hole1);
//        circleRec = new Rectangle();
//        circleRec.x = 300;
//        circleRec.y = 20;
//        circleRec.width = 50;
//        circleRec.height = 50;
//        homeRec = new Rectangle();
//        homeRec.x = 20;
//        homeRec.y = 20;
//        homeRec.width = 100;
//        homeRec.height = 100;
//    }
//
//    public void updateRecs(){
//        if(score%5==0 && score >0 && theCounter <3){
//            theCounter++;
//            changeRecBool = true;
//
//            hole1.x = MathUtils.random(200, 400);
//            hole1.y = MathUtils.random(210, 875);
//            hole1.width = recKeeper[theCounter];
//            hole1.height = recKeeper[theCounter];
//            img = theCounter;
//            //circleRec.x = hole1.x + ((hole1.width/2)-25);
//        }
//        else if(score > 5){
//            hole1.x = MathUtils.random(200, 400);
//            hole1.y = MathUtils.random(210, 875);
//            hole1.width = recKeeper[theCounter];
//            hole1.height = recKeeper[theCounter];
//            img = theCounter;
//            //circleRec.x = hole1.x + ((hole1.width/2)-25);
//        }
//        else{
//            hole1.x = MathUtils.random(200, 400);
//            hole1.y = MathUtils.random(210, 875);
//            hole1.width = 200;
//            hole1.height = 200;
//            //circleRec.x = hole1.x + ((hole1.width/2)-25);
//        }
//        updateBool = false;
//    }
//
//    public void widths(){
//        hole1.width = recKeeper[theCounter];
//        hole1.height = recKeeper[theCounter];
//        img = 0;
//        //circleRec.x = hole1.x + ((hole1.width/2)-25);
//    }
//    public void checkLock(){
//        if(uh.getCircleType().equals("circle")){
//            circle = new Texture(Gdx.files.internal("circle.png"));
//        }
//        else if(uh.getCircleType().equals("football")){
//            circle = new Texture(Gdx.files.internal("fball.png"));
//        }
//        else if(uh.getCircleType().equals("basketball")){
//            circle = new Texture(Gdx.files.internal("bball.png"));
//        }
//        else if(uh.getCircleType().equals("bird")){
//            circle = new Texture(Gdx.files.internal("bird.png"));
//        }
//        else{
//            circle = new Texture(Gdx.files.internal("circle.png"));
//        }
//
//        if(uh.getHoleType().equals("hole")){
//            holeCircle200 = new Texture(Gdx.files.internal("200.png"));
//            holeCircle150 = new Texture(Gdx.files.internal("150.png"));
//            holeCircle100 = new Texture(Gdx.files.internal("100.png"));
//            holeCircle75 = new Texture(Gdx.files.internal("75.png"));
//            circleArray.add(holeCircle200);
//            circleArray.add(holeCircle150);
//            circleArray.add(holeCircle100);
//            circleArray.add(holeCircle75);
//        }
//        else if(uh.getHoleType().equals("net")){
//            holeCircle200 = new Texture(Gdx.files.internal("anet200.png"));
//            holeCircle150 = new Texture(Gdx.files.internal("anet150.png"));
//            holeCircle100 = new Texture(Gdx.files.internal("anet100.png"));
//            holeCircle75 = new Texture(Gdx.files.internal("anet75.png"));
//            circleArray.add(holeCircle200);
//            circleArray.add(holeCircle150);
//            circleArray.add(holeCircle100);
//            circleArray.add(holeCircle75);
//        }
//        else if(uh.getHoleType().equals("archery")){
//            holeCircle200 = new Texture(Gdx.files.internal("arch200.png"));
//            holeCircle150 = new Texture(Gdx.files.internal("arch150.png"));
//            holeCircle100 = new Texture(Gdx.files.internal("arch100.png"));
//            holeCircle75 = new Texture(Gdx.files.internal("arch75.png"));
//            circleArray.add(holeCircle200);
//            circleArray.add(holeCircle150);
//            circleArray.add(holeCircle100);
//            circleArray.add(holeCircle75);
//        }
//        else{
//            holeCircle200 = new Texture(Gdx.files.internal("200.png"));
//            holeCircle150 = new Texture(Gdx.files.internal("150.png"));
//            holeCircle100 = new Texture(Gdx.files.internal("100.png"));
//            holeCircle75 = new Texture(Gdx.files.internal("75.png"));
//            circleArray.add(holeCircle200);
//            circleArray.add(holeCircle150);
//            circleArray.add(holeCircle100);
//            circleArray.add(holeCircle75);
//        }
//        if(uh.getThemeType().equals("day")){
//            r = 48;
//            g = 232;
//            b = 221;
//            cloudBool = true;
//        }
//        else if(uh.getThemeType().equals("snow")){
//            r = 142;
//            g = 142;
//            b = 142;
//            snowBool = true;
//        }
//    }
//    @Override
//    public void resize(int width, int height) {
//    }
//
//    @Override
//    public void show() {
//        //game.actionResolver.showOrLoadInterstital();
//    }
//
//    @Override
//    public void hide() {
//    }
//
//    @Override
//    public void pause() {
//    }
//
//    @Override
//    public void resume() {
//    }
//
//    @Override
//    public void dispose() {
//        circle.dispose();
//        holeCircle200.dispose();
//        holeCircle150.dispose();
//        holeCircle100.dispose();
//        holeCircle75.dispose();
//        home.dispose();
//        font.dispose();
//        fireSound.dispose();
//        scoreSound.dispose();
//        snowImage.dispose();
//        cloudImage.dispose();
//    }
//
//    @Override
//    public boolean keyDown(int keycode) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public boolean keyUp(int keycode) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public boolean keyTyped(char character) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        // TODO Auto-generated method stub
//        fireSound.play(1);
//        fire = true;
//        return false;
//    }
//
//    @Override
//    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public boolean mouseMoved(int screenX, int screenY) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public boolean scrolled(int amount) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//}
