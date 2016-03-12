package com.cs160.joleary.catnip;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

/**
 * Created by joleary on 2/19/16.
 */
public class PhoneToWatchService extends Service {

    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final Bundle extras = intent.getExtras();
            if (extras == null) {
                Log.d("T", "Received null extras from phone to watch");
                getApplicationContext().stopService(intent);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mApiClient.connect();
                    String message = "";
                    ArrayList<String> iterate = extras.getStringArrayList(InfoPanel.CAND_NAMES_KEY);
                    for (String str : iterate) {
                        message = message + str + "/";
                    }
                    iterate = extras.getStringArrayList(InfoPanel.CAND_PARTIES_KEY);
                    for (String str : iterate) {
                        message = message + str + "/";
                    }
                    iterate = extras.getStringArrayList(InfoPanel.CAND_TITLES_KEY);
                    for (String str : iterate) {
                        message = message + str + "/";
                    }
                    message = message + Integer.toString(extras.getInt(InfoPanel.ZIP_KEY)) + "/";
                    message = message + extras.getString(InfoPanel.COUNTY_KEY);
                    sendMessage(InfoPanel.CAND_INFO_KEY, message);
                }
            }).start();
            return START_STICKY;
        }
        return -1;
    }

    @Override //remember, all services need to implement an IBiner
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();
    }

}
