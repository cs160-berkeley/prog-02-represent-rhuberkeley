package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends Activity {

    public static final String ZIP_KEY = "zipkey";
    GridViewPager pager;
    ImageButton voteButton;
    static int zipCode = 0;
    static int spacing = 0;
    private SensorManager mSensorManager;
    private float mAccel; //borrowed acceleration computation algorithm from Stack Overflow
    private float mAccelCurrent;
    private float mAccelLast;
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.2f + delta;
            if (mAccel > 10) {
                int zipCode = (int) (Math.random() * 99999);
                MainActivity.zipCode = zipCode;

                //Load random zip code on phone as well
                Context context = getApplicationContext();
                Intent sendToPhone = new Intent(context, WatchToPhoneService.class);
                sendToPhone.putExtra(ElectionActivity.ZIPCODE_TO_PHONE_KEY, zipCode);
                context.startService(sendToPhone);
                Log.d("T", "Started service send to phone with name: " + zipCode);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        Intent watchListenerIntent = new Intent(getApplicationContext(), WatchListenerService.class);
        startService(watchListenerIntent);
        Log.d("T", "Watch service is listening");


        Intent watchInfo = getIntent();
        String infos = watchInfo.getStringExtra(WatchListenerService.CAND_INFO_KEY);
        if (infos != null) {
            String[] info = infos.split("/");
            spacing = info.length / 3;

            final WatchCandidateAdapter data = new WatchCandidateAdapter(this, getFragmentManager());
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
            while (data.PAGES[0].length <= spacing) { //fill in filler page for ElectionView
                data.PAGES[0] = Arrays.copyOf(data.PAGES[0], data.PAGES[0].length + 1);
                data.PAGES[0][data.PAGES[0].length - 1] = new WatchCandidateAdapter.Page();

            }
            data.PAGES[0][spacing] = new WatchCandidateAdapter.Page();

            zipCode = Integer.parseInt(info[info.length - 1]);
            data.zipCode = zipCode;

            pager = (GridViewPager) findViewById(R.id.pager);
            pager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, int i1, float v, float v1, int i2, int i3) {
                }

                @Override
                public void onPageSelected(int i, int i1) {
                    Context context = getApplicationContext();
                    if (i1 == spacing) {
                        Intent voteIntent = new Intent(context, ElectionActivity.class);
                        voteIntent.putExtra(MainActivity.ZIP_KEY, MainActivity.zipCode);
                        voteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(voteIntent);
                        pager.scrollTo(i, i1 - 1);
                    } else {
                        Intent sendToPhone = new Intent(context, WatchToPhoneService.class);
                        sendToPhone.putExtra(WatchCandidateAdapter.WATCH_NAME_KEY, data.PAGES[i][i1].name);
                        context.startService(sendToPhone);
                        Log.d("T", "Started service send to phone with name: " + data.PAGES[i][i1].name);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });
            pager.setAdapter(data);
        }
        voteButton = (ImageButton) findViewById(R.id.voteButton);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zipCode != 0) {
                    Intent voteIntent = new Intent(getBaseContext(), ElectionActivity.class);
                    voteIntent.putExtra(ZIP_KEY, zipCode);
                    startActivity(voteIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a location on your phone.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (infos != null) {
            voteButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "Showing candidates from zip code: " + Integer.toString(MainActivity.zipCode), Toast.LENGTH_SHORT).show();
    }
}