package com.cs160.joleary.catnip;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

//   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String LOAD_CANDIDATE = "/load_candidate";
    private static final String LOAD_ZIP = "/load_zip";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(LOAD_CANDIDATE) ) {

            // Value contains the String we sent over in WatchToPhoneService
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("T", "Received message from watch to start detailed view with: " + value);

            Intent detailedIntent = new Intent(getApplicationContext(), DetailedPanel.class);
            detailedIntent.putExtra(CandidateAdapter.CANDIDATE_KEY, value);
            detailedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(detailedIntent);

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else if ( messageEvent.getPath().equalsIgnoreCase(LOAD_ZIP) ) {

            // Value contains the String we sent over in WatchToPhoneService
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("T", "Received message from watch to start info panel view with: " + value);

            Intent infoIntent = new Intent(getApplicationContext(), InfoPanel.class);
            infoIntent.putExtra(ZipEntry.ZIPCODE_KEY, Integer.parseInt(value));
            infoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(infoIntent);

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
