package com.cs160.joleary.catnip;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    // private static final String FRED_FEED = "/Fred";
    // private static final String LEXY_FEED = "/Lexy";
    public static final String CAND_INFO_KEY = "candidateinfo";

    private String infoString;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        //(here, fred vs lexy)

        if(messageEvent.getPath().equalsIgnoreCase(CAND_INFO_KEY)) {
            infoString = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(CAND_INFO_KEY, infoString);
            Log.d("T", "about to start watch MainActivity with " + infoString);
            startActivity(intent);
        }
        super.onMessageReceived(messageEvent);
    }
}