/*Written by Vamseekrishna Kattika for CS6326.001, assignment 6,starting November 20, 2018
 * Net ID: vxk165930
 * This class contains the information regarding the paddle
 */

package com.example.vamse.breakout;

import android.graphics.RectF;

public class Paddle {

    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our paddle will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speedthat the paddle will move
    private float speed;

    // Which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the paddle moving and in which direction
    private int moving = STOPPED;

    int screenX;

    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(int screenX, int screenY){
        // 130 pixels wide and 20 pixels high
        length = screenX/5;
        height = 20;

        reset(screenX, screenY);

        // How fast is the paddle in pixels per second
        speed = 800;

        this.screenX = screenX;
    }

    // This is a getter method to make the rectangle that
    // defines our paddle available in BreakoutView class
    public RectF getRect(){

        return rect;
    }

    // This method will be used to change/set if the paddle is going left, right or nowhere
    public void setMovementState(int state){

        moving = state;
    }

    // This update method will be called from update in BreakoutView
    // It determines if the paddle needs to move and changes the coordinates
    // contained in rect if necessary
    public void update(long fps){

        if(moving == RIGHT){
            x = x + speed / fps;
            if (x >= screenX - length - 2) {
                x = screenX - length - 2;
            }
        }
        if(moving == LEFT){
            x = x - speed / fps; //moving pixels per frame
            if (x <= 2) {
                x = 2;
            }
        }
        rect.left = x;
        rect.right = x + length;
    }
    //Reset the paddle
    public void reset(int screenX, int screenY) {
        // Start paddle in roughly the sceen centre
        x = screenX / 2 - 100;
        y = screenY  - height;

        rect = new RectF(x, y, x + length, y + height);
    }
}
