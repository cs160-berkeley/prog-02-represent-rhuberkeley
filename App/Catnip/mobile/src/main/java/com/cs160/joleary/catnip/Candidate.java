package com.cs160.joleary.catnip;

/**
 * Created by Richard on 2/29/2016.
 */
public class Candidate {

    public String name;
    public String party;
    public String email;
    public String website;
    public String tweet;
    public String bioguideID;
    public String termEnd;
    public int imageId;

    public Candidate(String name, String party, String email, String website, String tweet, String termEnd, String bioID, int imageId) {
        this.name = name;
        this.party = party;
        this.email = email;
        this.website = website;
        this.tweet = tweet;
        this.imageId = imageId;
        this.bioguideID = bioID;
        this.termEnd = termEnd;
    }
}