package com.atbsg.atbsg.games;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Steven on 12/11/2015.
 */
public class GameHelper {
    ArrayList<String> gameDirections = new ArrayList<String>(Arrays.asList("UP", "DOWN", "RIGHT", "LEFT"));
    ArrayList<String> allDirections = new ArrayList<String>(Arrays.asList("UP", "DOWN", "RIGHT", "LEFT"));
    Random rand = new Random();
    int i = 0;
    public GameHelper(){

    }

    public ArrayList<String> getGameDirections() {
        return gameDirections;
    }

    public ArrayList<String> getAllDirections() {
        return allDirections;
    }

    public String getNextDirections() {
        return gameDirections.get(0);
    }

    public String newRandomDirection(){
       /* if(gameDirections.get(0).equals("UP")){
            return "UP";
        }else{
            return "DOWN";
        }*/
        return allDirections.get(rand.nextInt(3));
    }

    public void addDirection(){
        ///gameDirections.add(this.newRandomDirection());
        gameDirections.add(allDirections.get(i));
        if(i == 3){
            i=0;
        }else {
            i++;
        }
    }

    private boolean checkCurrent(String direction){
        if(getNextDirections().equals(direction)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isUp(){
        return checkCurrent("UP");
    }

    public boolean isDown(){
        return checkCurrent("DOWN");
    }

    public boolean isRight(){
        return checkCurrent("RIGHT");
    }

    public boolean isLeft(){
        return checkCurrent("LEFT");
    }

    public void remove(){
        gameDirections.remove(0);
    }

    public boolean correctDirection(String userDirection){
        if(this.getNextDirections().equals(userDirection)){
            return true;
        }
        else{
            return false;
        }
    }

}
