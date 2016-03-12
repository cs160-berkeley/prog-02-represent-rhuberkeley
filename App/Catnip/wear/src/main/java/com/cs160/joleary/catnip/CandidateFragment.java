package com.cs160.joleary.catnip;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Richard on 3/11/2016.
 */
public class CandidateFragment extends CardFragment {
    String name, description;
    Context context;

    public CandidateFragment() {
        this.name = "No Name";
        this.description = "Descriptionless";
        this.context = null;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void setArguments(Bundle args) {
        this.name = args.getString(WatchCandidateAdapter.WATCH_NAME_KEY);
        this.description = args.getString(WatchCandidateAdapter.WATCH_DESC_KEY);
        super.setArguments(args);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View cardView = inflater.inflate(R.layout.watch_panel, container, false);
        TextView titleTextView = (TextView) cardView.findViewById(R.id.titleTextView);
        TextView descriptionTextView = (TextView) cardView.findViewById(R.id.descriptionTextView);

        titleTextView.setText(this.name);
        descriptionTextView.setText(this.description);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null) {
                    Intent voteIntent = new Intent(context, ElectionActivity.class);
                    voteIntent.putExtra(MainActivity.ZIP_KEY, MainActivity.zipCode);
                    voteIntent.putExtra(MainActivity.COUNTY_KEY, MainActivity.county);
                    voteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(voteIntent);
                }
            }
        });
        return cardView;
    }

}
