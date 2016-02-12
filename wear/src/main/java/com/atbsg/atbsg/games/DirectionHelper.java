package com.atbsg.atbsg.games;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 15/12/2015.
 */
public class DirectionHelper {

    List<Double> history;
    List<Double> upHistory;
    double upSum, downSum, rightSum, leftSum, highestUpDownAverage, highestLeftRightAverage;

    public DirectionHelper(){
        upSum = 0;
        downSum = 0;
        rightSum = 0;
        leftSum = 0;
        highestUpDownAverage = 0;
        highestLeftRightAverage = 0;
        history = new ArrayList<Double>();
        upHistory = new ArrayList<Double>();
    }

    public boolean goingUp(){
        highestUpDownAverage = 0;
        upSum = 0;
        if (upHistory.size() > 5) {
            int index = upHistory.size();
            List<Double> subList = upHistory.subList((index-4), index);
            upHistory = subList;
        }

        if (!upHistory.isEmpty()) {
            for (Double mark : upHistory) {
                upSum += mark;
                //System.out.println("value " + mark);
            }
            upSum = upSum / upHistory.size();
            if(upSum>highestUpDownAverage){
                highestUpDownAverage = upSum;
            }
            if(-upSum>highestUpDownAverage){
                highestUpDownAverage = -upSum;
            }
            //System.out.println("UP SUM " + -sum);
        }
        if (upSum < -0.05) {
            return true;
        } else {
            return false;
        }
    }

    public boolean goingDown(){
        highestUpDownAverage = 0;
        downSum = 0;
        if (upHistory.size() > 5) {
            int index = upHistory.size();
            List<Double> subList = upHistory.subList((index-4), index);
            upHistory = subList;
        }

        if (!upHistory.isEmpty()) {
            for (Double mark : upHistory) {
                downSum += mark;
                //System.out.println("value " + mark);
            }
            downSum = downSum / upHistory.size();
            if(downSum>highestUpDownAverage){
                highestUpDownAverage = downSum;
            }
            if(-downSum>highestUpDownAverage){
                highestUpDownAverage = -downSum;
            }
            //System.out.println("UP SUM " + -sum);
        }
        if (downSum > 0.05) {
            return true;
        } else {
            return false;
        }
    }

    public boolean goingRight() {
        highestLeftRightAverage = 0;
        rightSum = 0;
        if (history.size() > 5) {
            int index = history.size();
            List<Double> subList = history.subList((index-4), index);
            history = subList;
        }

        if (!history.isEmpty()) {
            for (Double mark : history) {
                rightSum += mark;
                System.out.println("r value " + mark);
            }
            rightSum = rightSum / history.size();
            if(rightSum>highestLeftRightAverage){
                highestLeftRightAverage = rightSum;
            }
            if(-rightSum>highestLeftRightAverage){
                highestLeftRightAverage = -rightSum;
            }
            //System.out.println("RIGHT SUM " + rightSum);
        }
        if (rightSum < -0.05) {
            return true;
        } else {
            return false;
        }
    }

    public boolean goingLeft() {
        leftSum = 0;
        highestLeftRightAverage = 0;
        if (history.size() > 5) {
            int index = history.size();
            List<Double> subList = history.subList((index-4), index);
            history = subList;
        }

        if (!history.isEmpty()) {
            for (Double mark : history) {
                leftSum += mark;
                //System.out.println("value " + mark);
            }
            leftSum = leftSum / history.size();
            if(leftSum>highestLeftRightAverage){
                highestLeftRightAverage = leftSum;
            }
            if(-leftSum>highestLeftRightAverage){
                highestLeftRightAverage = -leftSum;
            }
            //System.out.println("UP SUM " + -sum);
        }
        if (leftSum > 0.05) {
            return true;
        } else {
            return false;
        }
    }

    public void addToHistory(double currentValue){
        history.add(currentValue);
    }
    public void addToUpHistory(double currentValue){
        upHistory.add(currentValue);
    }
    public double getUpAverage(){ return upSum;}
    public double getDownAverage(){ return downSum;}
    public double getRightAverage(){ return rightSum;}
    public double getLeftAverage(){ return leftSum;}
    public double getHighestUpDownAverage(){ return highestUpDownAverage;}
    public double getHighestLeftRightAverage(){ return highestLeftRightAverage;}
}