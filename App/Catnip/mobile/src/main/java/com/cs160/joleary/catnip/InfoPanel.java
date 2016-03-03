package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class InfoPanel extends Activity {
    public static final String CAND_PARTIES_KEY = "candidateparties";
    public static final String CAND_NAMES_KEY = "candidatenames";
    public static final String CAND_TITLES_KEY = "candidatetitles";
    public static final String CAND_INFO_KEY = "candidateinfo";
    public static final String ZIP_KEY = "zipkey";
    public static int zipLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_panel);

        RecyclerView topRecyclerView = (RecyclerView) findViewById(R.id.topRecyclerView);
        topRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Candidate> candidates = new ArrayList<>();

        Candidate candidate1 = new Candidate("Barbara Boxer", "Democrat", "senator@boxer.senate.gov", "boxer.senate.gov", "I'm totally Barbara Boxer", R.drawable.samplerep);
        candidates.add(candidate1);

        Candidate candidate2 = new Candidate("Dianne Feinstein", "Democrat", "senator@feinstein.senate.gov", "feinstein.senate.gov", "I'm totally Dianne Feinstein", R.drawable.samplerep2);
        candidates.add(candidate2);

        CandidateAdapter candidateAdapter = new CandidateAdapter(getApplicationContext(), candidates);

        topRecyclerView.setAdapter(candidateAdapter);

        RecyclerView botRecyclerView = (RecyclerView) findViewById(R.id.botRecyclerView);
        botRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Candidate> representatives = new ArrayList<>();

        Candidate representative1 = new Candidate("Barbara Lee", "Democrat", "nottherealaddress@barbaralee.fake", "lee.house.gov", "My website is dumb and gives a 404 when you try to find my e-mail", R.drawable.samplerep3);
        representatives.add(representative1);

        CandidateAdapter representativeAdapter = new CandidateAdapter(getApplicationContext(), representatives);

        botRecyclerView.setAdapter(representativeAdapter);

        ArrayList<String> candidateTitles = new ArrayList<>();
        ArrayList<String> candidateNames = new ArrayList<>();
        ArrayList<String> candidateParties = new ArrayList<>();

        for (Candidate cand : candidates) {
            candidateTitles.add("Senator");
            candidateNames.add(cand.name);
            candidateParties.add(cand.party);
        }
        for (Candidate cand : representatives) {
            candidateTitles.add("Representative");
            candidateNames.add(cand.name);
            candidateParties.add(cand.party);
        }
        Intent sendToWatch = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendToWatch.putExtra(CAND_NAMES_KEY, candidateNames);
        sendToWatch.putExtra(CAND_PARTIES_KEY, candidateParties);
        sendToWatch.putExtra(CAND_TITLES_KEY, candidateTitles);
        Intent intent = getIntent();
        int load = intent.getIntExtra(ZipEntry.ZIPCODE_KEY, -1);
        if (load != -1) {
            zipLoad = load;
        }
        sendToWatch.putExtra(ZIP_KEY, zipLoad);
        startService(sendToWatch);
        Log.d("T", "Sent data to watch main: " + Integer.toString(zipLoad));

        Intent listenForWatch = new Intent(getApplicationContext(), PhoneListenerService.class);
        startService(listenForWatch);
        Log.d("T", "Phone is listening for watch candidate info");

        Toast.makeText(getApplicationContext(), "Received zip code: " + Integer.toString(zipLoad), Toast.LENGTH_SHORT).show();
    }
}
