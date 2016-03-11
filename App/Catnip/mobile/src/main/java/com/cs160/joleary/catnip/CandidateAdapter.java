package com.cs160.joleary.catnip;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Richard on 2/29/2016.
 */
public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<Candidate> candidateArray;

    public static final String CANDIDATE_KEY = "candidate";

    public CandidateAdapter(Context context, ArrayList<Candidate> candidates) {
        this.context = context;
        candidateArray = candidates;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_panel, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final Candidate candidate = candidateArray.get(position);

        holder.tweetTextView.setBackgroundColor(0xF0D6D7D7);
        holder.nameTextView.setText(candidate.name);
        holder.partyTextView.setText(candidate.party);
        holder.tweetTextView.setText(candidate.tweet);
        holder.imageView.setImageResource(candidate.imageId);
        holder.pageTextView.setText(Integer.toString(position + 1) + " of " + Integer.toString(getItemCount()));
        if (candidate.party.equals("Democrat")) {
            holder.nameTextView.setTextColor(0xFF2196F3);
            holder.partyTextView.setTextColor(0xFF2196F3);
        }
        if (candidate.party.equals("Republican")) {
            holder.nameTextView.setTextColor(0xFFE53935);
            holder.partyTextView.setTextColor(0xFFE53935);
        }
        holder.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setData(Uri.parse("mailto:" + candidate.email));
                context.startActivity(emailIntent);
            }
        });

        holder.websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(candidate.website);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(webIntent);
            }
        });

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moreIntent = new Intent(context, DetailedPanel.class);
                DetailedPanel.candidate = candidate;
                moreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(moreIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return candidateArray.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView pageTextView;
        TextView nameTextView;
        TextView partyTextView;
        ImageButton emailButton;
        ImageButton websiteButton;
        TextView tweetTextView;
        ImageView imageView;
        Button moreButton;

        public CustomViewHolder (View view) {
            super(view);
            this.nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            this.partyTextView = (TextView) view.findViewById(R.id.partyTextView);
            this.emailButton = (ImageButton) view.findViewById(R.id.emailButton);
            this.websiteButton = (ImageButton) view.findViewById(R.id.websiteButton);
            this.tweetTextView = (TextView) view.findViewById(R.id.tweetTextView);
            this.imageView = (ImageView) view.findViewById(R.id.candidateImageView);
            this.moreButton = (Button) view.findViewById(R.id.moreButton);
            this.pageTextView = (TextView) view.findViewById(R.id.pageTextView);
        }
    }
}
