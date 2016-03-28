package com.atbsg.atbsg.games;

/**
 * Created by Steven on 08/03/2016.
 */
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameHelperJUnitTests {

    GameHelper gameHelper;

    @Before
    public void setUp() {
        gameHelper = new GameHelper();
    }

    /**
     * JUnit test for checking the newRandomDirections() method.
     */
    @Test
    public void testNewRandomDirection() {
        assertTrue(gameHelper.getAllDirections().contains(gameHelper.newRandomDirection()));
    }

    /**
     * JUnit test for checking the getNextDirection() method
     * returns the expected next game direction.
     */
    @Test
    public void testNextDirection() {
        String nextDirection = "UP";
        assertTrue(gameHelper.getNextDirection().equals(nextDirection));
    }

    /**
     * JUnit test for checking the getGameDirections() method
     * returns the expected size of directions.
     */
    @Test
    public void testGameDirectionSize() {
        int gameDirectionSize = gameHelper.getGameDirections().size();
        assertTrue(gameDirectionSize == 4);
    }

    /**
     * JUnit test for checking the addDirection() method.
     */
    @Test
    public void testAddDirection() {
        GameHelper gameHelperInstance = new GameHelper();
        int gameDirectionSize = 0;
        gameHelperInstance.addDirection();
        gameDirectionSize = gameHelperInstance.getGameDirections().size();
        assertTrue(gameDirectionSize == 5);
        gameHelperInstance.addDirection();
        gameDirectionSize = gameHelperInstance.getGameDirections().size();
        assertTrue(gameDirectionSize == 6);
        gameHelperInstance.addDirection();
        gameDirectionSize = gameHelperInstance.getGameDirections().size();
        assertTrue(gameDirectionSize == 7);
        gameHelperInstance.addDirection();
        gameDirectionSize = gameHelperInstance.getGameDirections().size();
        assertTrue(gameDirectionSize == 8);
    }


    /**
     * JUnit test for checking the correctDirection() and is direction methods
     * used during the game modes.
     */
    @Test
    public void testDirectionChecker() {
        assertTrue(gameHelper.correctDirection("UP"));
        assertTrue(gameHelper.isUp());
        assertFalse(gameHelper.correctDirection("RIGHT"));
        assertFalse(gameHelper.isDown());
        gameHelper.remove();
        assertTrue(gameHelper.correctDirection("DOWN"));
        assertTrue(gameHelper.isDown());
        assertFalse(gameHelper.correctDirection("UP"));
        assertFalse(gameHelper.isLeft());
        gameHelper.remove();
        assertTrue(gameHelper.correctDirection("RIGHT"));
        assertTrue(gameHelper.isRight());
        assertFalse(gameHelper.correctDirection("LEFT"));
        assertFalse(gameHelper.isDown());
        gameHelper.remove();
        assertTrue(gameHelper.correctDirection("LEFT"));
        assertTrue(gameHelper.isLeft());
        assertFalse(gameHelper.correctDirection("RIGHT"));
        assertFalse(gameHelper.isUp());
    }
}