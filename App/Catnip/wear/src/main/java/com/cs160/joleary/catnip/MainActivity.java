package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.Arrays;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ikL3IlLrW4YVVtxOLJpyXj3T9";
    private static final String TWITTER_SECRET = "Bn3oaTMxdpp4KVZuXQyJcITOKUB3sT7WK9i9F9Ep5mENehLQ1x";


    public static final String ZIP_KEY = "zipkey";
    public static final String COUNTY_KEY = "countykey";

    GridViewPager pager;
    ImageButton voteButton;
    static int zipCode = 0;
    static String county = "null";
    static int spacing = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        Intent watchListenerIntent = new Intent(getApplicationContext(), WatchListenerService.class);
        startService(watchListenerIntent);
        Log.d("T", "Watch service is listening");


        Intent watchInfo = getIntent();
        String infos = watchInfo.getStringExtra(WatchListenerService.CAND_INFO_KEY);
        final WatchCandidateAdapter data = new WatchCandidateAdapter(this, getFragmentManager());
        if (infos != null) {
            String[] info = infos.split("/");
            spacing = info.length / 3;

            for (int i = 0; i < info.length / 3; i++) {
                WatchCandidateAdapter.Page dataEntry = new WatchCandidateAdapter.Page();
                dataEntry.name = info[i];
                dataEntry.party = info[i + spacing];
                dataEntry.title = info[i + 2 * spacing];
                while (data.PAGES[0].length <= i) {
                    data.PAGES[0] = Arrays.copyOf(data.PAGES[0], data.PAGES[0].length + 1);
                }
                data.PAGES[0][i] = dataEntry;
            }

            zipCode = Integer.parseInt(info[info.length - 2]);
            county = info[info.length - 1];
            Log.d("T", "Watch received zip code: " + info[info.length - 2] + " and county: " + county);

            pager = (GridViewPager) findViewById(R.id.pager);
            pager.setAdapter(data);
        }
        voteButton = (ImageButton) findViewById(R.id.voteButton);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zipCode != 0) {
                    Intent voteIntent = new Intent(getBaseContext(), ElectionActivity.class);
                    voteIntent.putExtra(ZIP_KEY, zipCode);
                    voteIntent.putExtra(COUNTY_KEY, county);
                    startActivity(voteIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a location on your phone.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (infos != null) {
            voteButton.setVisibility(View.GONE);
        }
        DotsPageIndicator dots = (DotsPageIndicator) findViewById(R.id.dots);
        dots.setPager(pager);
        dots.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, int i1, float v, float v1, int i2, int i3) {

            }

            @Override
            public void onPageSelected(int i, int i1) {
                Context context = getApplicationContext();
                Intent sendToPhone = new Intent(context, WatchToPhoneService.class);
                sendToPhone.putExtra(WatchCandidateAdapter.WATCH_NAME_KEY, data.PAGES[i][i1].name);
                context.startService(sendToPhone);
                Log.d("T", "Started service send to phone with name: " + data.PAGES[i][i1].name);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}