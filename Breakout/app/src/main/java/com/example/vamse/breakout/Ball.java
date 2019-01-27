/*Written by Vamseekrishna Kattika for CS6326.001, assignment 6,starting November 20, 2018
 * Net ID: vxk165930
 * This class contains the information regarding the ball
 */

package com.example.vamse.breakout;

import android.graphics.RectF;

import java.util.Random;

public class Ball {

    RectF rect;
    float velocityX; // com.example.vamse.breakout.Ball's x-speed
    float velocityY; // com.example.vamse.breakout.Ball's y-speed
    float ballWidth = 10;
    float ballHeight = 10;
    float radius = 15;

    public Ball(){

        // Start the ball travelling straight up at 100 pixels per second
        velocityX = 200;
        velocityY = -400;

        // Place the ball in the centre of the screen at the bottom
        // Make it a 10 pixel x 10 pixel square
        rect = new RectF();
    }

    // Set ball's speed
    public void setSpeed(int x) {

        int s = x + 1;
        velocityX = s * 100;
        velocityY = -1 * s * 200;
    }

    // return x value of center
    public float centerX() {
        return rect.left + radius;
    }

    // return y value of center
    public float centerY() {
        return rect.top - radius;
    }

    // return the ball
    public RectF getRect(){

        return rect;
    }

    // Updata the ball's position
    public void update(long fps){
        rect.left = rect.left + (velocityX / fps);
        rect.top = rect.top + (velocityY / fps);
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

    public void reverseYVelocity(){

        velocityY = -velocityY;
    }

    public void reverseXVelocity(){

        velocityX = - velocityX;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + ballWidth;
    }

    // Reset ball's position
    public void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 20 - ballHeight;
    }

}

