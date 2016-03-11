package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarException;

public class InfoPanel extends Activity {
    public static final String CAND_PARTIES_KEY = "candidateparties";
    public static final String CAND_NAMES_KEY = "candidatenames";
    public static final String CAND_TITLES_KEY = "candidatetitles";
    public static final String CAND_INFO_KEY = "candidateinfo";
    public static final String ZIP_KEY = "zipkey";
    public static int zipLoad;
    public static boolean received;
    public static String results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_panel);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        received = false; //notification var for when API receives candidate data
        Integer[] pass = {zipLoad};
        new RetrieveFeedTask().execute(pass);
        Log.d("T", "Waiting on JSON retrieval for zip code " + Integer.toString(zipLoad));
        while (!received) {
            //wait for data to be retrieved
        }
        Log.d("T", "Received JSON data on InfoPanel screen");
        if (results == null || results.length() == 0) {
            Log.d("T", "JSON data is null");
        } else {
            Log.d("T", "JSON received: " + results);
        }

        String[] bgData = results.split("bioguide_id\":");
        String[] fnData = results.split("first_name\":");
        String[] lnData = results.split("last_name\":");
        String[] twData = results.split("twitter_id\":");
        String[] ptData = results.split("party\":");
        String[] ttData = results.split("title\":");
        String[] wsData = results.split("website\":");
        String[] emData = results.split("oc_email\":");
        String[] teData = results.split("term_end\":");
        List<String> bioguideIDs = new ArrayList<>();
        List<String> firstNames = new ArrayList<>();
        List<String> lastNames = new ArrayList<>();
        List<String> twitters = new ArrayList<>();
        List<String> parties = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> websites = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        List<String> termEnds = new ArrayList<>();
        for (String nameSplit : bgData) {
            bioguideIDs.add(nameSplit.split("\",")[0].split("\"")[1]);
        }
        bioguideIDs.remove(0);
        for (String nameSplit : fnData) {
            firstNames.add(nameSplit.split("\",")[0].split("\"")[1]);
        }
        firstNames.remove(0);
        for (String nameSplit : lnData) {
            lastNames.add(nameSplit.split("\",")[0].split("\"")[1]);
        }
        lastNames.remove(0);
        for (String nameSplit : twData) {
            twitters.add(nameSplit.split("\",")[0].split("\"")[1]);
        }
        twitters.remove(0);
        for (String nameSplit : ptData) {
            String partyAbbv = nameSplit.split("\",")[0].split("\"")[1];
            switch (partyAbbv) {
                case "D":
                    parties.add("Democrat");
                    break;
                case "R":
                    parties.add("Republican");
                    break;
                case "I":
                    parties.add("Independent");
                    break;
            }
        }
        for (String nameSplit : ttData) {
            titles.add(nameSplit.split("\",")[0].split("\"")[1]);
        }
        titles.remove(0);
        for (String nameSplit : wsData) {
            websites.add(nameSplit.split("\",")[0].split("\"")[1]);
        }
        websites.remove(0);
        for (String nameSplit : emData) {
            emails.add(nameSplit.split("\",")[0].split("\"")[1]);
        }
        emails.remove(0);
        for (String nameSplit : teData) {
            termEnds.add(nameSplit.split("\",")[0].split("\"")[1]);
        }
        termEnds.remove(0);
        for (String firstName : firstNames) {
            Log.d("T", "First name data: " + firstName);
        }

        ArrayList<Candidate> candidates = new ArrayList<>();
        ArrayList<Candidate> representatives = new ArrayList<>();

        Log.d("T", "Retrieved " + Integer.toString(firstNames.size()) + " candidates total");
        for (int i = 0; i < firstNames.size(); i++) {
            Candidate cand = new Candidate(firstNames.get(i) + " " + lastNames.get(i), parties.get(i), emails.get(i), websites.get(i), twitters.get(i),termEnds.get(i), bioguideIDs.get(i), R.drawable.samplerep);
            if (titles.get(i).equalsIgnoreCase("Rep")) {
                representatives.add(cand);
            } else if (titles.get(i).equalsIgnoreCase("Sen")){
                candidates.add(cand);
            }
        }

        RecyclerView topRecyclerView = (RecyclerView) findViewById(R.id.topRecyclerView);
        topRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        RecyclerView botRecyclerView = (RecyclerView) findViewById(R.id.botRecyclerView);
        botRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        CandidateAdapter candidateAdapter = new CandidateAdapter(getApplicationContext(), candidates);
        topRecyclerView.setAdapter(candidateAdapter);
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
    }
}
