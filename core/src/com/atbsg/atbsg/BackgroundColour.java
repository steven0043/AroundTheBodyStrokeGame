package com.atbsg.atbsg;

/**
 * Created by Steven on 09/02/2016.
 */
public class BackgroundColour  {
    int r, g, b;

    public BackgroundColour(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setRGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }


    public void green(){
        this.setRGB(0, 128, 0);
    }

    public void amber(){
        this.setRGB(199, 136, 5);
    }

    public void blue(){
        this.setRGB(49, 160, 154);
    }

    public void red(){
        this.setRGB(255, 51, 51);
    }

}
