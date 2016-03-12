package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailedPanel extends Activity {

    TextView nameTextView, partyTextView, termTextView, committeeTextView, billsTextView;
    ImageView portraitImageView;
    static Candidate candidate;
    static boolean received;
    static String comResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_panel);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        String cand = getIntent().getStringExtra(CandidateAdapter.CANDIDATE_KEY);
        if (cand != null) {
            if (InfoPanel.repAdapter != null) {
                candidate = InfoPanel.repAdapter.searchName(cand);
            } else {
                candidate = null;
            }
            if (candidate == null) {
                if (InfoPanel.senAdapter != null) {
                    candidate = InfoPanel.senAdapter.searchName(cand);
                } else {
                    candidate = null;
                }
                if (candidate == null) {
                    Log.d("T", "No candidate selected, cannot display DetailedPanel");
                }
            }
        }

        if (candidate != null) {
            String[] pass = {candidate.bioguideID};
            JSONObject json = null;
            try {
                json = new RetrieveCommitteesTask().execute(pass).get();
            } catch (Exception e) {
                Log.d("T", "Query fail at DetailedPanel: " + e.getMessage());
            }

            if (json == null) {
                Log.d("T", "JSON data is null at DetailedPanel");
            } else {
                Log.d("T", "Valid JSON received at DetailedPanel: " + json);
            }

            List<String> committees = new ArrayList<>();
            try {
                JSONArray jar = json.getJSONArray("results");
                for (int i = 0; i < jar.length(); i++) {
                    JSONObject jo = jar.getJSONObject(i);
                    committees.add(jo.getString("name"));
                }
            } catch (JSONException j) {
                Log.d("T", "JSON error: " + j.getMessage());
            }
            Log.d("T", "Done creating committee entries");


            String committeePrint = new String();
            int i = -1;
            while ((i = i + 1) < committees.size()) {
                committeePrint = committeePrint.concat(Integer.toString(i + 1) + ". " + committees.get(i));
                if (i < committees.size() - 1) {
                    committeePrint = committeePrint.concat("\n");
                }
            }

            try {
                json = new RetrieveBillsTask().execute(pass).get();
            } catch (Exception e) {
                Log.d("T", "Query fail at DetailedPanel: " + e.getMessage());
            }

            if (json == null) {
                Log.d("T", "JSON data is null at DetailedPanel");
            } else {
                Log.d("T", "Valid JSON received at DetailedPanel: " + json);
            }

            List<String> bills = new ArrayList<>();
            List<String> billDates = new ArrayList<>();
            try {
                JSONArray jar = json.getJSONArray("results");
                for (i = 0; i < jar.length(); i++) {
                    JSONObject jo = jar.getJSONObject(i);
                    if (jo.has("short_title") && jo.getString("short_title") != "null") {
                        bills.add(jo.getString("short_title"));
                        billDates.add(jo.getString("introduced_on"));
                    }
                }
            } catch (JSONException j) {
                Log.d("T", "JSON error: " + j.getMessage());
            }
            Log.d("T", "Done creating bill entries");


            String billsPrint = new String();
            i = -1;
            while ((i = i + 1) < bills.size()) {
                billsPrint = billsPrint.concat(billDates.get(i) + ": ");
                billsPrint = billsPrint.concat(bills.get(i));
                if (i < bills.size() - 1) {
                    billsPrint = billsPrint.concat("\n");
                }
            }
            nameTextView = (TextView) findViewById(R.id.nameTextView);
            partyTextView = (TextView) findViewById(R.id.partyTextView);
            termTextView = (TextView) findViewById(R.id.termTextView);
            committeeTextView = (TextView) findViewById(R.id.committeeTextView);
            billsTextView = (TextView) findViewById(R.id.billsTextView);
            portraitImageView = (ImageView) findViewById(R.id.portraitImageView);
            nameTextView.setText(candidate.name);
            termTextView.setText("Term ends " + candidate.termEnd);
            partyTextView.setText(candidate.party);
            committeeTextView.setText(committeePrint);
            billsTextView.setText(billsPrint);
            try {
                String[] URLs = {candidate.imageURL};
                Drawable imgDraw = new RetrieveDrawable().execute(URLs).get();
                portraitImageView.setImageDrawable(imgDraw);
            } catch (Exception e) {
                Log.d("T", "Drawable generation exception: " + e);
            }
            if (candidate.party.equalsIgnoreCase("Democrat")) {
                partyTextView.setTextColor(0xFF2196F3);
                nameTextView.setTextColor(0xFF2196F3);
            }
            if (candidate.party.equalsIgnoreCase("Republican")) {
                partyTextView.setTextColor(0xFFE53935);
                nameTextView.setTextColor(0xFFE53935);
            }
        }
    }
}
