package com.cs160.joleary.catnip;

/**
 * Created by Richard on 3/10/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Richard on 3/9/2016.
 */
class RetrieveCommitteesTask extends AsyncTask<String, Void, JSONObject> {
    protected JSONObject doInBackground(String... bioID) {
        try {
            String sunlightURL = "https://congress.api.sunlightfoundation.com/committees?member_ids=";
            sunlightURL = sunlightURL.concat(bioID[0]);
            Log.d("T", "Sending DetailedPanel request for bioID " + bioID[0]);
            sunlightURL = sunlightURL.concat("&apikey=3ec8c0acbf6040cba45b07ce05792cb4");
            URL url = new URL(sunlightURL);
            InputStream in = url.openStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder jsonBuilder = new StringBuilder();
            String jsonPart;
            while ((jsonPart = streamReader.readLine()) != null) {
                jsonBuilder.append(jsonPart);
            }
            String res = jsonBuilder.toString();
            Log.d("T", "Received JSON data " + res);
            return new JSONObject(jsonBuilder.toString());
        } catch (MalformedURLException m) {
            Log.d("T", "URL was malformed");
        } catch (IOException i) {
            Log.d("T", "I/O failure: " + i.getMessage());
        } catch (JSONException j) {
            Log.d("T", "JSON retrieval failure: " + j.getMessage());
        }
        Log.d("T", "Returning null JSON");
        return null;
    }
}