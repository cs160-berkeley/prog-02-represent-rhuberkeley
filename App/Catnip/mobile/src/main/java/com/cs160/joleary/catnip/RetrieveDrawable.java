package com.cs160.joleary.catnip;

import android.graphics.drawable.Drawable;
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
 * Created by Richard on 3/11/2016.
 */
class RetrieveDrawable extends AsyncTask<String, Void, Drawable> {
    protected Drawable doInBackground(String... imageURL) {
        try {
            InputStream input = (InputStream) new URL(imageURL[0]).getContent();
            return Drawable.createFromStream(input, "the unseen code is the deadliest");
        } catch (MalformedURLException m) {
            Log.d("T", "URL was malformed");
        } catch (IOException i) {
            Log.d("T", "I/O failure");
        }
        Log.d("T", "Returning null Drawable");
        return null;
    }
}