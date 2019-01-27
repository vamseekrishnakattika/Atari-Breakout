/*Written by Vamseekrishna Kattika for CS6326.001, assignment 6,starting November 20, 2018
 * Net ID: vxk165930
 * This class is to display the scores for the user. It saves the name, score and date and displays the top 10 scores
 */

package com.example.vamse.breakout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowScores extends AppCompatActivity{


    ArrayList<Score> scoreArrayList = new ArrayList<Score>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_scores);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        listView =  findViewById(R.id.listView);

        read();
        // Determine if user entered his name before jump into this activity.
        if (intent.hasExtra("totalscore") && intent.hasExtra("name")) {
            int score = intent.getIntExtra("totalscore", -1);
            String score1 = Integer.toString(score);
            String name = intent.getStringExtra("name");
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String strDate = dateFormat.format(date);
            scoreArrayList.add(new Score(name, score1,strDate));
        }
        write(scoreArrayList);

        ScoreSorter scoreSorter = new ScoreSorter(scoreArrayList);
        ArrayList<Score> scoreArrayListSorted  = scoreSorter.getSortedOutput();
        ArrayList<Score> scoreArrayListSorted10 = new ArrayList<>();

        //Limiting the displayed scores to 10
        if(scoreArrayListSorted.size()>=10){
            for(int i=0;i<10;i++){
                scoreArrayListSorted10.add(scoreArrayListSorted.get(i));
            }
        }
        else {
            scoreArrayListSorted10 = scoreArrayListSorted;
        }
        //Display the scores
        StableArrayAdapter scoreListAdapter = new StableArrayAdapter(this,R.layout.list_adapter_view,scoreArrayListSorted10);
        listView.setAdapter(scoreListAdapter);

        Button restartButton = (Button) findViewById(R.id.restart_btn);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMainActivity = new Intent(ShowScores.this, MainActivity.class);
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentMainActivity);
                finish();
            }
        });
    }

    /* Read function to read player name, score and date in file */
    public void read() {
        FileInputStream in;
        BufferedReader reader = null;
        try {
            in = openFileInput("scores"); // The file name is "scores"
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                scoreArrayList.add(new Score(data[0], data[1],data[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*Write function to store every player's name,score and date into the file*/
    public void write(ArrayList<Score> list) {
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("scores", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            for(Score ps : list) {
                writer.write(ps.getName() + "\t" + ps.getScore()+"\t" + ps.getDate() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
