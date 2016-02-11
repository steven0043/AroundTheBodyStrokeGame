package com.atbsg.atbsg;

/**
 * Created by Steven on 04/02/2016.
 */
public interface ActionResolver {
    public int getVertical();
    public int getHorizontal();
    public void setVertical(int vertical);
    public void sendToPhone(String message);
    public void speak(String speech);
}