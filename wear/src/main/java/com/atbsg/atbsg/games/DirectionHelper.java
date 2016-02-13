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

    public boolean goingUp(){
        highestCurrentAverage = 0;
        upSum = 0;

        verticalHistory = getLastValues(verticalHistory);

        upSum = getAverageFromList(verticalHistory);

        calculateCurrentAverage(upSum);
        //System.out.println("UP SUM " + -sum);

        return upSum < -0.05;
    }

    public boolean goingDown(){
        highestCurrentAverage = 0;
        downSum = 0;

        verticalHistory = getLastValues(verticalHistory);

        downSum  = getAverageFromList(verticalHistory);

        calculateCurrentAverage(downSum);

        return downSum > 0.05;
    }

    public boolean goingRight() {
        highestCurrentAverage = 0;
        rightSum = 0;

        horizontalHistory = getLastValues(horizontalHistory);

        rightSum = getAverageFromList(horizontalHistory);

        calculateCurrentAverage(rightSum);

        return rightSum < -0.05;
    }

    public boolean goingLeft() {
        leftSum = 0;
        highestCurrentAverage = 0;

        horizontalHistory = getLastValues(horizontalHistory);

        leftSum = getAverageFromList(horizontalHistory);

        calculateCurrentAverage(leftSum);

        return leftSum > 0.05;
    }

    public void addToHorizontalHistory(double currentValue){
        horizontalHistory.add(currentValue);
    }
    public void addToVerticalHistory(double currentValue){
        verticalHistory.add(currentValue);
    }

    private List<Double> getLastValues(List<Double> currentList){
        if (currentList.size() > 5) {
            int index = currentList.size();
            return currentList.subList((index-4), index);
        }
        else{return currentList;}
    }

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

    private void calculateCurrentAverage(double currentAverage){
        double currentAvg = Math.abs(currentAverage);

        if(currentAvg>highestCurrentAverage){
            highestCurrentAverage = currentAvg;
        }
    }
    public double getUpAverage(){ return upSum;}
    public double getDownAverage(){ return downSum;}
    public double getRightAverage(){ return rightSum;}
    public double getLeftAverage(){ return leftSum;}
    public double getHighestCurrentAverage(){ return highestCurrentAverage;}
}