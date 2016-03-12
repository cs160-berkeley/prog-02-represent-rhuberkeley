package com.cs160.joleary.catnip;

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
class RetrieveFeedTask extends AsyncTask<Integer, Void, JSONObject> {
    protected JSONObject doInBackground(Integer... zipLoad) {
        try {
            String sunlightURL = "https://congress.api.sunlightfoundation.com/legislators/locate?zip=";
            String zipString = Integer.toString(zipLoad[0]);
            while (zipString.length() < 5) {
                zipString = "0".concat(zipString);
            }
            sunlightURL = sunlightURL.concat(zipString);
            sunlightURL = sunlightURL.concat("&apikey=3ec8c0acbf6040cba45b07ce05792cb4");
            URL url = new URL(sunlightURL);
            InputStream in = url.openStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder jsonBuilder = new StringBuilder();
            String jsonPart;
            while ((jsonPart = streamReader.readLine()) != null) {
                jsonBuilder.append(jsonPart);
            }
            Log.d("T", "Received JSON data " + jsonBuilder.toString());
            return new JSONObject(jsonBuilder.toString());
        } catch (MalformedURLException m) {
            Log.d("T", "URL was malformed");
        } catch (IOException i) {
            Log.d("T", i.getMessage());
            Log.d("T", "I/O failure");
        } catch (JSONException j) {
            Log.d("T", "JSON failure");
        }
        Log.d("T", "Returning null JSON");
        return null;
    }
}