package com.cs160.joleary.catnip;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 2/29/2016.
 */
public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<Candidate> candidateArray;

    public static final String CANDIDATE_KEY = "candidate";
    public static final String SPACING = "      ";

    public CandidateAdapter(Context context, ArrayList<Candidate> candidates) {
        this.context = context;
        candidateArray = candidates;
    }

    public Candidate searchName(String name) {
        for (Candidate can : candidateArray) {
            if (can.name.equalsIgnoreCase(name)) {
                return can;
            }
        }
        return null;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_panel, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Candidate candidate = candidateArray.get(position);

        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                twitterApiClient.getStatusesService().userTimeline(null, candidate.tweet, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        for (Tweet tweet : listResult.data) {
                            holder.tweetTextView.setText(SPACING + tweet.text);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
                new MyTwitterApiClient(session).getUsersService().show(null, candidate.tweet, true,
                        new Callback<User>() {
                            @Override
                            public void success(Result<User> result) {
                                try {
                                    candidate.imageURL = result.data.profileImageUrlHttps.replace("_normal", "");
                                    Log.d("T", candidate.imageURL);
                                    String[] URLs = {candidate.imageURL};
                                    Drawable imgDraw = new RetrieveDrawable().execute(URLs).get();
                                    holder.imageView.setImageDrawable(imgDraw);
                                } catch (Exception e) {
                                    Log.d("T", "Drawable generation exception: " + e);
                                }
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                Log.d("T", "Twitter exception: " + exception);
                            }
                        });
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });

        holder.tweetTextView.setBackgroundColor(0xF0D6D7D7);
        holder.nameTextView.setText(candidate.name);
        holder.partyTextView.setText(candidate.party);
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
                DetailedPanel.candidate = candidate;
                Intent moreIntent = new Intent(context, DetailedPanel.class);
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
