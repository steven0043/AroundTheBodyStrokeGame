package com.atbsg.atbsg.games;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 15/12/2015.
 */
public class DirectionHelper {

    List<Double> horizontalHistory;
    List<Double> verticalHistory;
    double upSum, downSum, rightSum, leftSum, highestCurrentAverage;

    public DirectionHelper(){
        upSum = 0;
        downSum = 0;
        rightSum = 0;
        leftSum = 0;
        highestCurrentAverage = 0;
        horizontalHistory = new ArrayList<Double>();
        verticalHistory = new ArrayList<Double>();
    }

    /**
     * Boolean method. Checks to see whether or not the user is currently
     * going up.
     * @return boolean
     */
    public boolean goingUp(){
        highestCurrentAverage = 0;
        upSum = 0;

        verticalHistory = getLastValues(verticalHistory);

        upSum = getAverageFromList(verticalHistory);

        calculateCurrentAverage(upSum);
        //System.out.println("UP SUM " + -sum);

        return upSum < -0.05;
    }

    /**
     * Boolean method. Checks to see whether or not the user is currently
     * going down.
     * @return boolean
     */
    public boolean goingDown(){
        highestCurrentAverage = 0;
        downSum = 0;

        verticalHistory = getLastValues(verticalHistory);

        downSum  = getAverageFromList(verticalHistory);

        calculateCurrentAverage(downSum);

        return downSum > 0.05;
    }

    /**
     * Boolean method. Checks to see whether or not the user is currently
     * going right.
     * @return boolean
     */
    public boolean goingRight() {
        highestCurrentAverage = 0;
        rightSum = 0;

        horizontalHistory = getLastValues(horizontalHistory);

        rightSum = getAverageFromList(horizontalHistory);

        calculateCurrentAverage(rightSum);

        return rightSum < -0.05;
    }

    /**
     * Boolean method. Checks to see whether or not the user is currently
     * going left.
     * @return boolean
     */
    public boolean goingLeft() {
        leftSum = 0;
        highestCurrentAverage = 0;

        horizontalHistory = getLastValues(horizontalHistory);

        leftSum = getAverageFromList(horizontalHistory);

        calculateCurrentAverage(leftSum);

        return leftSum > 0.05;
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
     * Checks to see if the current average is greater
     * than the current highest.
     * @param currentAverage
     */
    private void calculateCurrentAverage(double currentAverage){
        double currentAvg = Math.abs(currentAverage);

        if(currentAvg>highestCurrentAverage){
            highestCurrentAverage = currentAvg;
        }
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
    /**
     * Get current highest average.
     * @return double
     */
    public double getHighestCurrentAverage(){ return highestCurrentAverage;}
}