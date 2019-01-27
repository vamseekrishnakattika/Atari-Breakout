/*Written by Vamseekrishna Kattika for CS6326.001, assignment 6,starting November 20, 2018
 * Net ID: vxk165930
 * This activity gets score from the main activity and takes name of the user and passes the info to ShowScores activity to save and display them
 * The user can add his scores to high scores using Scores button or can also discard the current score and restart the game using restart button
 */

package com.example.vamse.breakout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class AddScore extends AppCompatActivity {

    private int score;
    private float time;
    private int totalScore;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TextView scoreView = (TextView) findViewById(R.id.score_text);

        Intent intent = getIntent();
        // Get the score from MainActivity
        score = intent.getIntExtra("score", -1);
        // Put the score on score text view.
        scoreView.append(" "+ score + "");

        TextView timeView = (TextView) findViewById(R.id.time);
        // Get the time from MainActivity
        time = intent.getLongExtra("time", -1);
        time /= 1000; // Convert million second to second
        // Put the time on time text view.
        timeView.append(" "+time + "s");

        TextView totalScoreView = (TextView) findViewById(R.id.totalscore_text);
        // Calculate the total score, it equals score * time
        totalScore = Math.round(score * time);
        totalScoreView.append(" "+totalScore + "");

        // Restart Button which finishes this activity, and starts Main Activity.
        Button restartButton = (Button) findViewById(R.id.restart_button);

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMainActivity = new Intent(AddScore.this, MainActivity.class);
                startActivity(intentMainActivity);
                finish();
            }
        });

        // Scores Button which finishes this activity, and starts ShowScores Activity.
        Button scoreButoon = (Button) findViewById(R.id.score_button);

        scoreButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentShowScoresActivity = new Intent(AddScore.this, ShowScores.class);
                intentShowScoresActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                EditText name = (EditText) findViewById(R.id.name_text);
                if (name.getText() != null && name.getText().length() != 0) {
                    // Pass the total score and player name to ShowScores Activity.
                    intentShowScoresActivity.putExtra("totalscore", totalScore);
                    intentShowScoresActivity.putExtra("name", name.getText().toString());
                    startActivity(intentShowScoresActivity);
                    finish();
                }
                else{
                    String strName = name.getText().toString();
                    if(strName.length()<1 || strName.length()>30){
                        String message;
                        message = "The name should not be empty";
                        Toast.makeText(AddScore.this,message,Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
