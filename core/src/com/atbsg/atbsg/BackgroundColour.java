package com.atbsg.atbsg;

/**
 * Created by Steven on 09/02/2016.
 */
public class BackgroundColour  {
    int r, g, b;

    /**
     *
     * @param r
     * @param g
     * @param b
     */
    public BackgroundColour(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     *
     * @param r
     * @param g
     * @param b
     */
    public void setRGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     *
     * @return
     */
    public int getR() {
        return r;
    }

    /**
     *
     * @return
     */
    public int getG() {
        return g;
    }

    /**
     *
     * @return
     */
    public int getB() {
        return b;
    }

    /**
     *
     */
    public void green(){
        this.setRGB(0, 128, 0);
    }

    /**
     *
     */
    public void amber(){
        this.setRGB(199, 136, 5);
    }

    /**
     *
     */
    public void blue(){
        this.setRGB(49, 160, 154);
    }

    /**
     *
     */
    public void red(){
        this.setRGB(255, 51, 51);
    }

}
