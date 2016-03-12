package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ikL3IlLrW4YVVtxOLJpyXj3T9";
    private static final String TWITTER_SECRET = "Bn3oaTMxdpp4KVZuXQyJcITOKUB3sT7WK9i9F9Ep5mENehLQ1x";

    private Button zipCodeButton;
    private Button currentLocButton;
    private GoogleApiClient mGoogleApiClient;
    private Location currentLoc;
    private Address localInfo;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        zipCodeButton = (Button) findViewById(R.id.zipCodeButton);
        currentLocButton = (Button) findViewById(R.id.currentLocButton);
        zipCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zipIntent = new Intent(getBaseContext(), ZipEntry.class);
                startActivity(zipIntent);
            }
        });

        currentLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (currentLoc == null) {
                        Log.d("T", "Received null location");
                        System.exit(0);
                    }
                    localInfo = geocoder.getFromLocation(currentLoc.getLatitude(), currentLoc.getLongitude(), 1).get(0);
                } catch (IOException i) {
                    Log.d("T", "Failed to get current location");
                    System.exit(0);
                } catch (SecurityException i) {
                    Log.d("T", "Insufficient permissions");
                    System.exit(0);
                }
                Intent infoIntent = new Intent(getBaseContext(), InfoPanel.class);
                InfoPanel.zipLoad = Integer.parseInt(localInfo.getPostalCode());
                startActivity(infoIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("T", "Api Client is suspended!");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("T", "Api Client is connected!");
        try {
            LocationRequest locationRequest = LocationRequest.create();
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLoc = location;
                    Log.d("T", "Location updated");
                }
            };
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
            } catch (SecurityException s) {
                Log.d("T", "Failed to request location updates. Insufficient permissions.");
                System.exit(0);
            }
            Log.d("T", "Successfully requested location updates");
        } catch (SecurityException s) {
            Log.d("T", "Insufficient permissions");
            System.exit(0);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("T", "Api Client failed to connect!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }
}
