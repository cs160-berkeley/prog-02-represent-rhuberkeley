package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedPanel extends Activity {

    TextView nameTextView, partyTextView, termTextView, committeeTextView, billsTextView, datesTextView;
    ImageView portraitImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_panel);

        getActionBar().setDisplayHomeAsUpEnabled(true);

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
        switch (name) {
            case "Barbara Boxer":
                party = "Democrat";
                imageid = R.drawable.samplerep;
                termTextView.setText("Term ends Jan 03, 2017");
                break;
            case "Dianne Feinstein":
                party = "Democrat";
                imageid = R.drawable.samplerep2;
                termTextView.setText("Term ends Jan 03, 2019");
                break;
            case "Barbara Lee":
                party = "Democrat";
                imageid = R.drawable.samplerep3;
                termTextView.setText("Term ends Jan 03, 2017");
                break;
        }
        partyTextView.setText(party);
        if (imageid != -1) {
            portraitImageView.setImageResource(imageid);

        }
        if (party.equals("Democrat")) {
            partyTextView.setTextColor(0xFF2196F3);
            nameTextView.setTextColor(0xFF2196F3);
        }
        if (party.equals("Republican")) {
            partyTextView.setTextColor(0xFFE53935);
            nameTextView.setTextColor(0xFFE53935);
        }
    }
}
