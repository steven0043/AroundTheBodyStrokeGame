package com.atbsg.atbsg;

/**
 * Created by Steven on 09/02/2016.
 *
 * Simple class to represent background colours
 * used in the game.
 */
public class BackgroundColour  {
    int r, g, b;

    /**
     * Constructor, sets the colour, using RGB.
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
     * Sets the RGB colour
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
     * Get the 'R' value.
     * @return
     */
    public int getR() {
        return r;
    }

    /**
     * Get the 'G' value.
     * @return
     */
    public int getG() {
        return g;
    }

    /**
     * Get the 'B' value.
     * @return
     */
    public int getB() {
        return b;
    }

    /**
     * Quick method the set the colour to green.
     */
    public void green(){
        this.setRGB(0, 128, 0);
    }

    /**
     * Quick method the set the colour to amber.
     */
    public void amber(){
        this.setRGB(199, 136, 5);
    }

    /**
     * Quick method the set the colour to blue.
     */
    public void blue(){
        this.setRGB(49, 60, 154);
    }

    /**
     * Quick method the set the colour to red.
     */
    public void red(){
        this.setRGB(255, 51, 51);
    }

}
