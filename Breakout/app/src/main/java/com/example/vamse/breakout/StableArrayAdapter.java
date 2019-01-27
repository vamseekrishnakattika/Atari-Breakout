/*Written by Vamseekrishna Kattika for CS6326.001, assignment 5,starting October 27, 2018
 * Net ID: vxk165930
 * This is an adapter to show the scores in a list view
 */

package com.example.vamse.breakout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StableArrayAdapter extends ArrayAdapter<Score> {
    private Context cxContext;
    int rsResource;

    public StableArrayAdapter(Context context, int resource, ArrayList<Score> objects) {
        super(context, resource, objects);
        cxContext = context;
        rsResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String score = getItem(position).getScore();
        String date = getItem(position).getDate();

        Score scoreDetails = new Score(name,score,date);
        LayoutInflater inflater = LayoutInflater.from(cxContext);
        convertView = inflater.inflate(rsResource,parent,false);

        TextView tvName = (TextView) convertView.findViewById(R.id.tviewName);
        TextView tvScore = (TextView) convertView.findViewById(R.id.tviewScore);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tviewDate);
        tvName.setText(name);
        tvScore.setText(score);
        tvDate.setText(date);

        return convertView;
    }
}

