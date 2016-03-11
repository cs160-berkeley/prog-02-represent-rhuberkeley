package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DetailedPanel extends Activity {

    TextView nameTextView, partyTextView, termTextView, committeeTextView, billsTextView, datesTextView;
    ImageView portraitImageView;
    static Candidate candidate;
    static boolean received;
    static String comResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_panel);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        received = false; //notification var for when API receives candidate data
        if (candidate == null) {
            Log.d("T", "No candidate selected, cannot display DetailedPanel");
        }
        String[] pass = {candidate.bioguideID};
        new GetDetailedFeedTask().execute(pass);
        Log.d("T", "Waiting on JSON retrieval for bioguide id " + candidate.bioguideID);
        while (!received) {
            //wait for data to be retrieved
        }
        Log.d("T", "Received JSON data on InfoPanel screen");
        if (comResults == null || comResults.length() == 0) {
            Log.d("T", "JSON data for committees is null");
        } else {
            Log.d("T", "JSON received: " + comResults);
        }

        String[] committeeData = comResults.split("name\":");
        List<String> committees = new ArrayList<>();
        for (String split : committeeData) {
            committees.add(split.split("\",")[0].split("\"")[1]);
        }
        committees.remove(0);

        String committeePrint = new String();
        int i = 0;
        while (i < 5 && i < committees.size()) {
            committeePrint = committeePrint.concat(committees.get(i) + "\n");
        }
        committeePrint.trim();

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        partyTextView = (TextView) findViewById(R.id.partyTextView);
        termTextView = (TextView) findViewById(R.id.termTextView);
        committeeTextView = (TextView) findViewById(R.id.committeeTextView);
        billsTextView = (TextView) findViewById(R.id.billsTextView);
        portraitImageView = (ImageView) findViewById(R.id.portraitImageView);

        Intent localIntent = getIntent();
        String name = localIntent.getStringExtra(CandidateAdapter.CANDIDATE_KEY);

        nameTextView.setText(name);
        String party = "None";
        int imageid = -1;
        termTextView.setText("Term ends " + candidate.termEnd);
        partyTextView.setText(candidate.party);
        committeeTextView.setText(committeePrint);
        if (imageid != -1) {
            portraitImageView.setImageResource(imageid);

        }
        if (party.equalsIgnoreCase("Democrat")) {
            partyTextView.setTextColor(0xFF2196F3);
            nameTextView.setTextColor(0xFF2196F3);
        }
        if (party.equalsIgnoreCase("Republican")) {
            partyTextView.setTextColor(0xFFE53935);
            nameTextView.setTextColor(0xFFE53935);
        }
    }
}
