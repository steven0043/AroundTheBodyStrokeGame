package com.atbsg.atbsg.games;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 15/12/2015.
 *
 * Class especially for detecting the current motion of the user
 * based on the accelerometer values
 */
public class DirectionHelper {

    List<Double> horizontalHistory;
    List<Double> verticalHistory;
    double upSum, downSum, rightSum, leftSum;

    public DirectionHelper(){
        upSum = 0;
        downSum = 0;
        rightSum = 0;
        leftSum = 0;
        horizontalHistory = new ArrayList<Double>();
        verticalHistory = new ArrayList<Double>();
    }

    /**
     * Boolean method. Checks to see whether or not the user is currently
     * going up.
     * @return boolean
     */
    public boolean goingUp(){
        upSum = 0;

        verticalHistory = getLastValues(verticalHistory);

        upSum = getAverageFromList(verticalHistory);

        return upSum < -0.05;
    }

    /**
     * Boolean method. Checks to see whether or not the user is currently
     * going down.
     * @return boolean
     */
    public boolean goingDown(){
        downSum = 0;

        verticalHistory = getLastValues(verticalHistory);

        downSum  = getAverageFromList(verticalHistory);

        return downSum > 0.05;
    }

    /**
     * Boolean method. Checks to see whether or not the user is currently
     * going right.
     * @return boolean
     */
    public boolean goingRight() {
        rightSum = 0;

        horizontalHistory = getLastValues(horizontalHistory);

        rightSum = getAverageFromList(horizontalHistory);

        return rightSum < -0.005;
    }

    /**
     * Boolean method. Checks to see whether or not the user is currently
     * going left.
     * @return boolean
     */
    public boolean goingLeft() {
        leftSum = 0;

        horizontalHistory = getLastValues(horizontalHistory);

        leftSum = getAverageFromList(horizontalHistory);

        return leftSum > 0.005;
    }

    /**
     * Adds the current accelerometer value to a list
     * to maintain a history of horizontal values.
     * @param currentValue
     */
    public void addToHorizontalHistory(double currentValue){
        horizontalHistory.add(currentValue);
    }

    /**
     * Adds the current accelerometer value to a list
     * to maintain a history of vertical values.
     * @param currentValue
     */
    public void addToVerticalHistory(double currentValue){
        verticalHistory.add(currentValue);
    }

    /**
     * Takes in a list and returns the last 5 values
     * of that list.
     * @param currentList
     * @return List<Double>
     */
    private List<Double> getLastValues(List<Double> currentList){
        if (currentList.size() > 5) {
            int index = currentList.size();
            return currentList.subList((index-4), index);
        }
        else{return currentList;}
    }

    /**
     * Takes in the list of 5 values and returns
     * a returns the average as a double.
     * @param currentList
     * @return double
     */
    private double getAverageFromList(List<Double> currentList){
        double avg = 0;
        if (!currentList.isEmpty()) {
            for (Double accValue : currentList) {
                avg += accValue;
            }
            avg = avg / currentList.size();
        }
        return avg;
    }

    /**
     * Get current up motion average.
     * @return double
     */
    public double getUpAverage(){ return upSum;}
    /**
     * Get current down motion average.
     * @return double
     */
    public double getDownAverage(){ return downSum;}
    /**
     * Get current right motion average.
     * @return double
     */
    public double getRightAverage(){ return rightSum;}
    /**
     * Get current left motion average.
     * @return double
     */
    public double getLeftAverage(){ return leftSum;}
}