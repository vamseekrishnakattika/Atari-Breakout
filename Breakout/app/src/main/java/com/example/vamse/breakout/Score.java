/*Written by Vamseekrishna Kattika for CS6326.001, assignment 5,starting October 27, 2018
 * Net ID: vxk165930
 * This class is for storing the score details and also implements comparable so that the objects can be
 * compared later to sort them based on score, date time and name
 */


package com.example.vamse.breakout;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Score implements Comparable<Score>{
    private String name;
    private String score;
    private String date;

    public Score(String name, String score, String date) {
        this.name = name;
        this.score = score;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /*This overridden method compares two score objects based on score, date time and name
     * When two scores are same the comparison is done based on date and time
     * If the score and date time are also equal which is highly impossible then the comparison is done based on name*/
    @Override
    public int compareTo(Score newScore){

        int score1 = Integer.parseInt(this.getScore());
        int score2 = Integer.parseInt(newScore.getScore());
        /*Comparison based on scores*/
        if(score1 < score2){
            return 1;
        }
        else if(score1 == score2){
            /*If scores are equal compare based on date time values*/
            Date date1=null;
            Date date2=null;
            try {
                date1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(this.getDate());
                date2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(newScore.getDate());
            } catch (Exception e) {
                e.printStackTrace();
            }

            int value = date2.compareTo(date1);
            if(value != 0){
                return value;
            }
            else {
                /*If date time are also equal compare the scores based on name in alphabetical order*/
                String name1 = this.getName();
                String name2 = newScore.getName();
                int rVal = name1.compareToIgnoreCase(name2);
                if(rVal < 0){
                    return -1;
                }
                else if(rVal > 0){
                    return 1;
                }
                else{
                    return 0;
                }
            }
        }
        else{
            return -1;
        }
    }

}
