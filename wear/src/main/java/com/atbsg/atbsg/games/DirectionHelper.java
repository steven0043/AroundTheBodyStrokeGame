package com.atbsg.atbsg.games;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 15/12/2015.
 */
public class DirectionHelper {

    List<Double> history;
    List<Double> upHistory;
    double sum, highestUpDownAverage, highestLeftRightAverage;
    public DirectionHelper(){
        sum = 0;
        highestUpDownAverage = 0;
        highestLeftRightAverage = 0;
        history = new ArrayList<Double>();
        upHistory = new ArrayList<Double>();
    }

    public boolean goingUp(){
        if (upHistory.size() > 10) {
            List<Double> subList = upHistory.subList(0, 9);
            upHistory = subList;
        }

        if (!upHistory.isEmpty()) {
            for (Double mark : upHistory) {
                sum += mark;
                System.out.println("value " + mark);
            }
            sum = sum / upHistory.size();
            if(sum>highestUpDownAverage){
                highestUpDownAverage = sum;
            }
            System.out.println("UP SUM " + -sum);
        }
        if (-sum < -0.18) {
            return true;
        } else {
            return false;
        }
    }

    public boolean goingDown(){
        if (upHistory.size() > 10) {
            List<Double> subList = upHistory.subList(0, 9);
            upHistory = subList;
        }

        if (!upHistory.isEmpty()) {
            for (Double mark : upHistory) {
                sum += mark;
                System.out.println("value " + mark);
            }
            sum = sum / upHistory.size();
            if(sum>highestUpDownAverage){
                highestUpDownAverage = sum;
            }
            //System.out.println("SUM " + -sum);
        }
        if (sum > 0.18) {
            return true;
        } else {
            return false;
        }
    }
    public boolean goingLeft() {
        if (history.size() > 10) {
            List<Double> subList = history.subList(0, 9);
            history = subList;
        }
        //double sum = 0;
        if (!history.isEmpty()) {
            for (Double mark : history) {
                sum += mark;
            }
            sum = sum / history.size();
            if(sum>highestLeftRightAverage){
                highestLeftRightAverage = sum;
            }
            System.out.println("LEFT SUM " + sum);
            if(sum>0){
                sum *= -1;
            }
        }
        if (-sum > 0.005) {
            return true;
        } else {
            return false;
        }
    }

    public boolean goingRight() {
        if (history.size() > 10) {
            List<Double> subList = history.subList(0, 9);
            history = subList;
        }
        //double sum = 0;
        if (!history.isEmpty()) {
            for (Double mark : history) {
                sum += mark;
            }
            sum = sum / history.size();
            if(sum>highestLeftRightAverage){
                highestLeftRightAverage = sum;
            }
            System.out.println("RIGHT SUM " + sum);
        }
        if (sum < -0.005) {
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
    public double getUpAverage(){ return -sum;}
    public double getRightAverage(){ return sum;}
    public double getLeftAverage(){ return -sum;}
    public double getDownAverage(){ return sum;}
    public double getHighestUpDownAverage(){ return highestUpDownAverage;}
    public double getHighestLeftRightAverage(){ return highestLeftRightAverage;}
}
