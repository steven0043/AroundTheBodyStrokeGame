package com.atbsg.atbsg.games;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Steven on 12/11/2015.
 *
 * A helper class for the directions
 * used in each game.
 */
public class GameHelper {
    ArrayList<String> gameDirections = new ArrayList<String>(Arrays.asList("UP", "DOWN", "RIGHT", "LEFT"));
    ArrayList<String> allDirections = new ArrayList<String>(Arrays.asList("UP", "DOWN", "RIGHT", "LEFT"));
    Random rand = new Random();
    int i = 0;
    public GameHelper(){

    }

    /**
     * Get the current list of game directions.
     * @return ArrayList<String>
     */
    public ArrayList<String> getGameDirections() {
        return gameDirections;
    }

    /**
     * Get the list of all game directions.
     * @return ArrayList<String>
     */
    public ArrayList<String> getAllDirections() {
        return allDirections;
    }

    /**
     * Get the next direction.
     * @return String
     */
    public String getNextDirection() {
        return gameDirections.get(0);
    }

    /**
     * Generate a random direction.
     * @return String
     */
    public String newRandomDirection(){
        return allDirections.get(rand.nextInt(3));
    }

    /**
     * Add new direction to the current list of
     * game directions.
     */
    public void addDirection(){
        ///gameDirections.add(this.newRandomDirection());
        gameDirections.add(allDirections.get(i));
        if(i == 3){
            i=0;
        }else {
            i++;
        }
    }

    /**
     * Add new random direction to the current list of
     * game directions.
     */
    public void addRandomDirection(){
        gameDirections.add(this.newRandomDirection());
        if(i == 3){
            i=0;
        }else {
            i++;
        }
    }

    /**
     * Checks if the direction parameter is the
     * next one.
     * @param direction
     * @return boolean
     */
    private boolean checkCurrent(String direction){
        return direction.equals(getNextDirection());
    }

    /**
     * Checks if current direction is up.
     * @return boolean
     */
    public boolean isUp(){
        return checkCurrent("UP");
    }

    /**
     * Checks if current direction is down.
     * @return boolean
     */
    public boolean isDown(){
        return checkCurrent("DOWN");
    }

    /**
     * Checks if current direction is right.
     * @return boolean
     */
    public boolean isRight(){
        return checkCurrent("RIGHT");
    }

    /**
     * Checks if current direction is left.
     * @return boolean
     */
    public boolean isLeft(){
        return checkCurrent("LEFT");
    }

    /**
     * Remove the current direction.
     */
    public void remove(){
        gameDirections.remove(0);
    }

    /**
     * Checks if the current user direction that has been
     * moved is the current direction in the list of
     * game directions.
     * @param userDirection
     * @return boolean
     */
    public boolean correctDirection(String userDirection){
        return this.getNextDirection().equals(userDirection);
    }

}
