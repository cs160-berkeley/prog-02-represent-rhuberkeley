package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarException;

public class InfoPanel extends Activity {
    public static final String CAND_PARTIES_KEY = "candidateparties";
    public static final String CAND_NAMES_KEY = "candidatenames";
    public static final String CAND_TITLES_KEY = "candidatetitles";
    public static final String CAND_INFO_KEY = "candidateinfo";
    public static final String ZIP_KEY = "zipkey";
    public static final String COUNTY_KEY = "countykey";
    public static int zipLoad;
    public static String county;

    static CandidateAdapter repAdapter;
    static CandidateAdapter senAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_panel);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent info = getIntent();
        int intdata;
        if ((intdata = info.getIntExtra(ZipEntry.ZIPCODE_KEY, -1)) != -1) {
            zipLoad = intdata;
        }

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String ctydata;
        if ((ctydata = info.getStringExtra(COUNTY_KEY)) != null) {
            try {
                Address countyRet = geocoder.getFromLocationName(ctydata, 1).get(0);
                if (countyRet != null) {
                Address locationRet = geocoder.getFromLocation(countyRet.getLatitude(), countyRet.getLongitude(), 1).get(0);
                    if (locationRet != null) {
                        Log.d("T", "Retrieved location: " + locationRet.getPostalCode() + " from data " + ctydata);
                        zipLoad = Integer.parseInt(locationRet.getPostalCode());
                    }
                }
            } catch (IOException i) {
                Log.d("T", "I/O exception: " + i.getMessage());
            }
        }

        boolean candidatesLoaded = false;

        JSONObject json = null;
        Integer[] pass = {zipLoad};
        try {
            json = new RetrieveFeedTask().execute(pass).get();
        } catch (Exception e) {
            Log.d("T", "Query fail: " + e.getMessage());
        }

        if (json == null) {
            Log.d("T", "JSON data is null");
        } else {
            Log.d("T", "Valid JSON received: " + json + " from zip code input " + Integer.toString(zipLoad));
        }
        List<String> bioguideIDs = new ArrayList<>();
        List<String> firstNames = new ArrayList<>();
        List<String> lastNames = new ArrayList<>();
        List<String> twitters = new ArrayList<>();
        List<String> parties = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> websites = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        List<String> termEnds = new ArrayList<>();
        try {
            JSONArray jar = json.getJSONArray("results");
            if (jar.length() == 0) {
                candidatesLoaded = false;
            } else {
                candidatesLoaded = true;
            }
            for (int i = 0; i < jar.length(); i++) {
                JSONObject jo = jar.getJSONObject(i);
                bioguideIDs.add(jo.getString("bioguide_id"));
                firstNames.add(jo.getString("first_name"));
                lastNames.add(jo.getString("last_name"));
                twitters.add(jo.getString("twitter_id"));
                String partyAbbv = jo.getString("party");
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
                    default:
                        parties.add("Other");
                        break;
                }
                titles.add(jo.getString("title"));
                websites.add(jo.getString("website"));
                emails.add(jo.getString("oc_email"));
                termEnds.add(jo.getString("term_end"));
            }
        } catch (JSONException j) {
            Log.d("T", "JSON error: " + j.getMessage());
        }

        ArrayList<Candidate> candidates = new ArrayList<>();
        ArrayList<Candidate> representatives = new ArrayList<>();

        Log.d("T", "Retrieved " + Integer.toString(firstNames.size()) + " candidates total");
        for (int i = 0; i < firstNames.size(); i++) {
            Candidate cand = new Candidate(firstNames.get(i) + " " + lastNames.get(i), parties.get(i), emails.get(i), websites.get(i), twitters.get(i),termEnds.get(i), bioguideIDs.get(i), "null");
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

        senAdapter = new CandidateAdapter(getApplicationContext(), candidates);
        topRecyclerView.setAdapter(senAdapter);
        repAdapter = new CandidateAdapter(getApplicationContext(), representatives);
        botRecyclerView.setAdapter(repAdapter);

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
        String state = "null";
        try {
            List<Address> countyRet = geocoder.getFromLocationName(Integer.toString(zipLoad), 1);
            county = countyRet.get(0).getLocality();
            state = countyRet.get(0).getAdminArea();
        } catch (IOException i) {
            Log.d("T", "I/O Exception: " + i.getMessage());
        }

        if (candidatesLoaded) {
            Map<String, String> states = new HashMap<String, String>();
            states.put("Alabama","AL");
            states.put("Alaska","AK");
            states.put("Alberta","AB");
            states.put("American Samoa","AS");
            states.put("Arizona","AZ");
            states.put("Arkansas","AR");
            states.put("Armed Forces (AE)","AE");
            states.put("Armed Forces Americas","AA");
            states.put("Armed Forces Pacific","AP");
            states.put("British Columbia","BC");
            states.put("California","CA");
            states.put("Colorado","CO");
            states.put("Connecticut","CT");
            states.put("Delaware","DE");
            states.put("District Of Columbia","DC");
            states.put("Florida","FL");
            states.put("Georgia","GA");
            states.put("Guam","GU");
            states.put("Hawaii","HI");
            states.put("Idaho","ID");
            states.put("Illinois","IL");
            states.put("Indiana","IN");
            states.put("Iowa","IA");
            states.put("Kansas","KS");
            states.put("Kentucky","KY");
            states.put("Louisiana","LA");
            states.put("Maine","ME");
            states.put("Manitoba","MB");
            states.put("Maryland","MD");
            states.put("Massachusetts","MA");
            states.put("Michigan","MI");
            states.put("Minnesota","MN");
            states.put("Mississippi","MS");
            states.put("Missouri","MO");
            states.put("Montana","MT");
            states.put("Nebraska","NE");
            states.put("Nevada","NV");
            states.put("New Brunswick","NB");
            states.put("New Hampshire","NH");
            states.put("New Jersey","NJ");
            states.put("New Mexico","NM");
            states.put("New York","NY");
            states.put("Newfoundland","NF");
            states.put("North Carolina","NC");
            states.put("North Dakota","ND");
            states.put("Northwest Territories","NT");
            states.put("Nova Scotia","NS");
            states.put("Nunavut","NU");
            states.put("Ohio","OH");
            states.put("Oklahoma","OK");
            states.put("Ontario","ON");
            states.put("Oregon","OR");
            states.put("Pennsylvania","PA");
            states.put("Prince Edward Island","PE");
            states.put("Puerto Rico","PR");
            states.put("Quebec","QC");
            states.put("Rhode Island","RI");
            states.put("Saskatchewan","SK");
            states.put("South Carolina","SC");
            states.put("South Dakota","SD");
            states.put("Tennessee","TN");
            states.put("Texas","TX");
            states.put("Utah","UT");
            states.put("Vermont","VT");
            states.put("Virgin Islands","VI");
            states.put("Virginia","VA");
            states.put("Washington","WA");
            states.put("West Virginia","WV");
            states.put("Wisconsin","WI");
            states.put("Wyoming","WY");
            states.put("Yukon Territory", "YT");

            if (ctydata != null) {
                county = ctydata;
            } else {
                if (county == "null") {
                    county = state;
                } else if (states.get(state) != null) {
                    county = county + ", " + states.get(state);
                } else {
                    county = county + ", " + state;
                }
            }

            Log.d("T", "Retrieving data for " + county + " which is in state " + state);

            Intent sendToWatch = new Intent(getApplicationContext(), PhoneToWatchService.class);
            sendToWatch.putExtra(CAND_NAMES_KEY, candidateNames);
            sendToWatch.putExtra(CAND_PARTIES_KEY, candidateParties);
            sendToWatch.putExtra(CAND_TITLES_KEY, candidateTitles);
            sendToWatch.putExtra(ZIP_KEY, zipLoad);
            sendToWatch.putExtra(COUNTY_KEY, county);
            startService(sendToWatch);
            Log.d("T", "Sent location to watch main: zip code: " + Integer.toString(zipLoad) + " county name: " + county);
        }

        Intent listenForWatch = new Intent(getApplicationContext(), PhoneListenerService.class);
        startService(listenForWatch);
        Log.d("T", "Phone is listening for watch candidate info");
    }
}
