/*Written by Vamseekrishna Kattika for CS6326.001, assignment 6,starting November 20, 2018
 * Net ID: vxk165930
 * This is the main activity that is responsible for the game play
 */

package com.example.vamse.breakout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    BreakoutView breakoutView;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    //To record last time and last x value to respond Sensor
    private long lastTime;
    private float lastX;

    //StartTime to calculate the time spent
    private long startTime;
    private boolean isStart;

    // The players paddle
    Paddle paddle;

    // A ball
    Ball ball;

    // The bricks limit is 200
    Brick[] bricks = new Brick[200];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Initialize gameView and set it as the view
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);

        // Intialize the accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) !=null ) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Here is our implementation of BreakoutView
    // It is an inner class.
    // Note how the final closing curly brace }
    // is inside the BreakoutGame class

    // Notice we implement runnable so we have
    // A thread and can override the run method.
    class BreakoutView extends SurfaceView implements Runnable {

        // This is our thread
        Thread thread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder holder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // Game is paused at the start
        boolean paused = true;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // This is used to help calculate the fps
        private long thisFrameTime;

        // The size of the screen in pixels
        int screenX;
        int screenY;

        // The initial row of bricks is 4
        int numBricks = 0;
        int brickRow = 4;

        // For sound FX
        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;

        // The score
        int score = 0;

        // The speed
        int speed = 1;

        // Lives
        int lives = 3;

        //previous and current position to calculate the direction of paddle silde
        float mPosX;
        float mCurrentPosX;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public BreakoutView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.

            super(context);

            // Initialize the holder and paint objects
            holder = getHolder();
            paint = new Paint();

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            paddle = new Paddle(screenX, screenY);

            // Create a ball
            ball = new Ball();

            // Load the sounds

            // This SoundPool is deprecated but don't worry
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

            try{
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Load our fx in memory ready for use
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

            }catch(IOException e){
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }

            createBricksAndRestart();
        }

        public void createBricksAndRestart(){

            // Put the paddle back to the start
            paddle.reset(screenX, screenY);
            // Put the ball back to the start
            ball.reset(screenX, screenY);
            ball.setSpeed(speed);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 16;

            // Build a wall of bricks
            numBricks = 0;
            for(int row = 0; row < brickRow; row ++ ){
                for(int column = 0; column < 8; column ++ ){
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    // Set different color based on different layers.
                    switch (row) {
                        case 0: {
                            bricks[numBricks].setColor(brickColor.RED);
                            break;
                        }
                        case 1: {
                            bricks[numBricks].setColor(brickColor.YELLOW);
                            break;
                        }
                        case 2: {
                            bricks[numBricks].setColor(brickColor.GREEN);
                            break;
                        }
                        case 3: {
                            bricks[numBricks].setColor(brickColor.BLUE);
                            break;
                        }
                    }
                    numBricks ++;
                }
            }
            // if game over reset scores and lives
            score = 0;
            speed = 1;
            lives = 3;
        }

        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {

            // Move the paddle if required
            paddle.update(fps);

            ball.update(fps);

            // Check for ball colliding with a brick
            for(int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()){
                    if(RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        switch (bricks[i].getColor()) {
                            case RED: {
                                bricks[i].setColor(brickColor.YELLOW);
                                break;
                            }
                            case YELLOW: {
                                bricks[i].setColor(brickColor.GREEN);
                                break;
                            }
                            case GREEN: {
                                bricks[i].setColor(brickColor.BLUE);
                                break;
                            }
                            case BLUE: {
                                bricks[i].setInvisible();
                            }
                        }
                        ball.reverseYVelocity();
                        score = score + 5;
                        // If the score is 100, 200, 300... increase the ball's speed.
                        if (score % 100 == 0) {
                            speed ++;
                            ball.setSpeed(speed);
                        }
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }

            // Check for ball colliding with paddle
            if(RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                //make sure that the ball resumes its upwards journey starting two pixels away from the top of the paddle
                ball.clearObstacleY(paddle.getRect().top - 2);
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }

            // When the ball is under the bottom of screen
            if(ball.getRect().bottom > screenY){
                ball.clearObstacleY(screenY);

                // Check for ball colliding with paddle
                if(RectF.intersects(paddle.getRect(), ball.getRect())) {
                    ball.setRandomXVelocity();
                    ball.reverseYVelocity();
                    //make sure that the ball resumes its upwards journey starting two pixels away from the top of the paddle
                    ball.clearObstacleY(paddle.getRect().top - 2);
                    soundPool.play(beep1ID, 1, 1, 0, 0, 1);
                }
                else {

                    // Lose a life
                    lives --;
                    soundPool.play(loseLifeID, 1, 1, 0, 0, 1);

                    // If ball's life is zero, finish this activity and start Finish activity
                    if(lives == 0){
                        paused = true;
                        Intent intent = new Intent(MainActivity.this, AddScore.class);
                        // Pass the score and time duration to Finish Acitvity
                        intent.putExtra("score", score);
                        intent.putExtra("time", System.currentTimeMillis() - startTime);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        // Bounce the ball back when it hits the bottom of screen
                        // Reset the paddle
                        paddle.reset(screenX, screenY);
                        // Put the ball back to the start
                        ball.reset(screenX, screenY);
                        ball.reverseYVelocity();
                        //ball.clearObstacleY(screenY - 2);
                    }

                }
            }

            // Bounce the ball back when it hits the top of screen
            if(ball.getRect().top < 0){
                ball.reverseYVelocity();
                ball.clearObstacleY(12);
                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits left wall bounce
            if(ball.getRect().left < 0){
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }
            // If the ball hits right wall bounce
            if(ball.getRect().right > screenX - 10){
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);

                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // // If user clear all the bricks, finish this activity and start Finish activity
            if(score == numBricks/brickRow * 50){
                paused = true;
                Intent intent = new Intent(MainActivity.this, AddScore.class);
                // Pass the score and time duration to Finish Acitvity
                intent.putExtra("score", score);
                intent.putExtra("time", System.currentTimeMillis() - startTime);
                startActivity(intent);
                finish();
            }

        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (holder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = holder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255, 50, 50, 50));

                // Choose the brush color for drawing, etc ball and paddle
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Draw the paddle
                canvas.drawRect(paddle.getRect(), paint);

                // Draw the ball
                canvas.drawCircle(ball.centerX(), ball.centerY(), ball.radius, paint);

                // Draw the bricks if visible
                for(int i = 0; i < numBricks; i++){
                    if(bricks[i].getVisibility()) {
                        switch (bricks[i].getColor()) {
                            case RED: {
                                // Change the brush color for drawing
                                paint.setColor(Color.argb(200, 255, 0, 0));
                                break;
                            }
                            case YELLOW: {
                                paint.setColor(Color.argb(200, 255, 255, 0));
                                break;
                            }
                            case GREEN: {
                                paint.setColor(Color.argb(200, 0, 255, 0));
                                break;
                            }
                            case BLUE: {
                                paint.setColor(Color.argb(200, 0, 0, 255));
                                break;
                            }
                        }
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                //draw the HUD
                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // Draw the score
                paint.setTextSize(50);
                canvas.drawText("Score: " + score + "  Speed: " + speed + "  Lives: " + lives, 20, 50, paint);

                // Draw everything to the screen
                holder.unlockCanvasAndPost(canvas);
            }
        }

        @Override
        public void run() {
            while (playing) {
                // Capture the current time in milliseconds in startFrameTime                 l
                long currentFrameTime = System.currentTimeMillis();
                // Update the frame
                if(!paused){//in touchEvent, set paused false
                    update();
                }
                // Draw the frame
                draw();
                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                thisFrameTime = System.currentTimeMillis() - currentFrameTime;
                if (thisFrameTime >= 1) {
                    fps = 1000 / thisFrameTime;
                }
            }
        }
        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        // it's still running!!!!
        public void pause() {
            playing = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }
        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            thread = new Thread(this);
            thread.start(); //Start the game.
        }
        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Player has touched the screen
                case MotionEvent.ACTION_DOWN: {
                    paused = false;
                    if (!isStart) {
                        startTime = System.currentTimeMillis();
                        isStart = true;
                    }
                    mPosX = motionEvent.getX();
                }

                // Player has slided the screen
                case MotionEvent.ACTION_MOVE: {

                    mCurrentPosX = motionEvent.getX();
                    if (mCurrentPosX - mPosX > 0) {
                        paddle.setMovementState(paddle.RIGHT);
                    } else if (mCurrentPosX - mPosX < 0) {
                        paddle.setMovementState(paddle.LEFT);
                    }
                    mPosX = mCurrentPosX;
                    break;
                }

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP: {
                    paddle.setMovementState(paddle.STOPPED);
                    break;
                }
            }
            return true;
        }
    }
    // This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {

        super.onResume();
        // Tell the gameView resume method to execute
        breakoutView.resume();
        if(accelerometer != null ) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            Log.i("Info", "Accelerometer registered");
        }

    }


    // This method executes when the player quits the game, like click home button, not back button.
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        breakoutView.pause();
        if(accelerometer != null ) {
            sensorManager.unregisterListener(this);
            Log.i("Info", "Accelerometer unregistered");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];

            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastTime) > 500) {
                float difference = (x - lastX);

                lastTime = currentTime;
                lastX = x;

                if(!breakoutView.paused){

                    // Get the current speed of ball
                    double degreeD = ((int)(difference * 5)) * Math.PI / 180;
                    float xVelocity = ball.velocityX;
                    float yVelocity = ball.velocityY;

                    // If user decline the tablet, the direction of ball will be changed.
                    ball.velocityX = (float)(xVelocity * (Math.cos(degreeD))) - (float)(yVelocity * Math.sin(degreeD));
                    ball.velocityY = (float)(yVelocity * (Math.cos(degreeD))) + (float)(xVelocity * Math.sin(degreeD));
                }
            }
        }
    }
}