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

import org.w3c.dom.Text;

import java.util.jar.Manifest;

public class ElectionActivity extends Activity {

    public static final String ZIPCODE_TO_PHONE_KEY = "ziptophonekey";

    private TextView countyTextView, repVoteTextView, demVoteTextView;

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
                countyTextView.setText("Somewhere over the rainbow");
                demVoteTextView.setText(Integer.toString((int) (Math.random() * 100)) + "%");
                repVoteTextView.setText(Integer.toString((int) (Math.random() * 100)) + "%");
                Toast.makeText(getApplicationContext(), "New random zip code: " + Integer.toString(MainActivity.zipCode), Toast.LENGTH_LONG).show();

                //Load random zip code on phone as well
                Context context = getApplicationContext();
                Intent sendToPhone = new Intent(context, WatchToPhoneService.class);
                sendToPhone.putExtra(ZIPCODE_TO_PHONE_KEY, zipCode);
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
        Toast.makeText(getApplicationContext(), "Received zip code: " + Integer.toString(zipCode), Toast.LENGTH_SHORT).show();
        if (zipCode != 94709) {
            countyTextView.setText(Integer.toString(zipCode) + " County, Some State");
            demVoteTextView.setText(Integer.toString((int) (Math.random() * 100)) + "%");
            repVoteTextView.setText(Integer.toString((int) (Math.random() * 100)) + "%");
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
