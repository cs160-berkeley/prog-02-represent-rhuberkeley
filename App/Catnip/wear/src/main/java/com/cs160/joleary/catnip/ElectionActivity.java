package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public class ElectionActivity extends Activity {

    public static final String COUNTY_TO_PHONE_KEY = "countytophonekey";
    public static final String ZIPCODE_TO_PHONE_KEY = "ziptophonekey";
    JSONArray jar;


    private TextView countyTextView, repVoteTextView, demVoteTextView;
    private String county;

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
                int index = (int) (Math.random() * jar.length());
                try {
                    JSONObject randomCty = jar.getJSONObject(index);
                    String ctyName = randomCty.getString("county-name");
                    String stateName = randomCty.getString("state-postal");
                    String display = ctyName + ", " + stateName;
                    countyTextView.setText(display);
                    demVoteTextView.setText(randomCty.getString("obama-percentage") + "%");
                    repVoteTextView.setText(randomCty.getString("romney-percentage") + "%");

                    //Load random county on phone as well
                    Context context = getApplicationContext();
                    Intent sendToPhone = new Intent(context, WatchToPhoneService.class);
                    sendToPhone.putExtra(COUNTY_TO_PHONE_KEY, display);
                    context.startService(sendToPhone);
                    Log.d("T", "Started service send to phone with name: " + display);
                } catch (JSONException j) {
                    Log.d("T", "JSONError: " + j.getMessage());
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);
        countyTextView = (TextView) findViewById(R.id.countyTextView);
        demVoteTextView = (TextView) findViewById(R.id.demVoteTextView);
        repVoteTextView = (TextView) findViewById(R.id.repVoteTextView);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        Intent intent = getIntent();
        int zipCode = intent.getIntExtra(MainActivity.ZIP_KEY, -1);
        county = intent.getStringExtra(MainActivity.COUNTY_KEY);
        if (county == null) {
            Log.d("T", "null county received from MainActivity");
        }
        countyTextView.setText(county);
        Log.d("T", "Received county string: " + county);
        String[] parseCty = county.split(", ");
        String cty = parseCty[0];
        String state = parseCty[1];
        Log.d("T", "Searching for county: " + cty + " and state: " + state);

        try {
            InputStream stream = getAssets().open("election-county-2012.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String jsonString = new String(buffer, "UTF-8");
            jar = new JSONArray(jsonString);
            for (int i = 0; i < jar.length(); i++) {
                JSONObject entry = jar.getJSONObject(i);
                if (entry.getString("state-postal").equalsIgnoreCase(state) && entry.getString("county-name").equalsIgnoreCase(cty)) {
                    Log.d("T", "Matching entry found in vote data");
                    demVoteTextView.setText(entry.getString("obama-percentage") + "%");
                    repVoteTextView.setText(entry.getString("romney-percentage") + "%");
                }
            }
        } catch (IOException i) {
            Log.d("T", "I/O exception: " + i.getMessage());
        } catch (JSONException j) {
            Log.d("T", "JSON exception: " + j.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
}
