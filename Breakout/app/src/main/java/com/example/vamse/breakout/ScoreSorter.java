/*Written by Vamseekrishna Kattika for CS6326.001, assignment 5,starting October 27, 2018
 * Net ID: vxk165930
 * This class takes Array list objects of scores and sorts them
 */

package com.example.vamse.breakout;

import java.util.ArrayList;
import java.util.Collections;

public class ScoreSorter {

    ArrayList<Score> newScore = new ArrayList<>();
    public ScoreSorter(ArrayList<Score> newScore){
        this.newScore = newScore;
    }
    public ArrayList<Score> getSortedOutput(){
        Collections.sort(newScore);
        return newScore;
    }
}
