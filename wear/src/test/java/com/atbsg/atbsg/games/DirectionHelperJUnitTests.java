package com.atbsg.atbsg.games;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Steven on 09/03/2016.
 */

public class DirectionHelperJUnitTests {


    @Before
    public void setUp() {

    }

    /**
     * JUnit test for checking the goingUp() method.
     */
    @Test
    public void testGoingUp() {
        DirectionHelper directionHelper = new DirectionHelper();
        /*Populate the vertical history list with values similar to accelerometer data
         that are expected to satisfy the going up clauses */
        for(int i = 0; i<5; i++){
            directionHelper.addToVerticalHistory(-0.06);
        }
        assertTrue(directionHelper.goingUp());

        assertTrue(directionHelper.getUpAverage() == -0.06);//Test to see if the correct average values are returned.

        /*Populate the vertical history list with values similar to accelerometer data
         that are expected to not satisfy the going up clauses */
        for(int i = 0; i<5; i++){
            directionHelper.addToVerticalHistory(-0.05);
        }
        assertFalse(directionHelper.goingUp());

        assertFalse(directionHelper.getUpAverage() == -0.06);//False assertion test.
    }

    /**
     * JUnit test for checking the goingDown() method.
     */
    @Test
    public void testGoingDown() {
        DirectionHelper directionHelper = new DirectionHelper();
        /*Populate the list with values similar to accelerometer data
         that are expected to satisfy the going down clauses */
        for(int i = 0; i<5; i++){
            directionHelper.addToVerticalHistory(0.06);
        }
        assertTrue(directionHelper.goingDown());

        assertTrue(directionHelper.getDownAverage() == 0.06);//Test to see if the correct average values are returned.

        /*Populate the vertical history list with values similar to accelerometer data
         that are expected to not satisfy the going down clauses */
        for(int i = 0; i<5; i++){
            directionHelper.addToVerticalHistory(0.05);
        }
        assertFalse(directionHelper.goingDown());

        assertFalse(directionHelper.getDownAverage() == -0.06);//False assertion test.
    }

    /**
     * JUnit test for checking the goingRight() method.
     */
    @Test
    public void testGoingRight() {
        DirectionHelper directionHelper = new DirectionHelper();
        /*Populate the list with values similar to accelerometer data
         that are expected to satisfy the going right clauses */
        for(int i = 0; i<5; i++){
            directionHelper.addToHorizontalHistory(-0.006);
        }
        assertTrue(directionHelper.goingRight());

        assertTrue(directionHelper.getRightAverage() == -0.006);//Test to see if the correct average values are returned.

        /*Populate the vertical history list with values similar to accelerometer data
         that are expected to not satisfy the going right clauses */
        for(int i = 0; i<5; i++){
            directionHelper.addToHorizontalHistory(0.006);
        }
        assertFalse(directionHelper.goingRight());

        assertFalse(directionHelper.getRightAverage() == -0.006);//False assertion test.
    }

    /**
     * JUnit test for checking the goingLeft() method.
     */
    @Test
    public void testGoingLeft() {
        DirectionHelper directionHelper = new DirectionHelper();
        /*Populate the list with values similar to accelerometer data
         that are expected to satisfy the going left clauses */
        for(int i = 0; i<5; i++){
            directionHelper.addToHorizontalHistory(0.006);
        }
        assertTrue(directionHelper.goingLeft());

        assertTrue(directionHelper.getLeftAverage() == 0.006);//Test to see if the correct average values are returned.
        assertTrue(directionHelper.getHighestCurrentAverage() == 0.006);

        /*Populate the vertical history list with values similar to accelerometer data
         that are expected to not satisfy the going left clauses */
        for(int i = 0; i<5; i++){
            directionHelper.addToHorizontalHistory(0.005);
        }
        assertFalse(directionHelper.goingLeft());

        assertFalse(directionHelper.getLeftAverage() == 0.006);//False assertion test.
    }
}