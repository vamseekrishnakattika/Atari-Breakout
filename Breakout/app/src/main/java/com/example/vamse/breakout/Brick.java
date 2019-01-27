/*Written by Vamseekrishna Kattika for CS6326.001, assignment 6,starting November 20, 2018
 * Net ID: vxk165930
 * This class hlods the information regarding the bricks
 */

package com.example.vamse.breakout;

import android.graphics.RectF;

public class Brick {

    private RectF rect;

    private boolean isVisible;

    private brickColor color; // There are four colors, red, blue,yellow and green


    public Brick(int row, int column, int width, int height){

        isVisible = true;

        int padding = 1;

        color = brickColor.RED;

        // Set the postions of every one bricks
        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    // Return brick
    public RectF getRect() {

        return rect;
    }

    // Set brick's visibility to determine if there is touching with ball
    public void setInvisible() {

        isVisible = false;
    }

    public boolean getVisibility() {
        return isVisible;
    }

    // Return the brick's color
    public brickColor getColor() {
        return color;
    }

    // Set brick's color
    public void setColor(brickColor color) {
        this.color = color;
    }
}
