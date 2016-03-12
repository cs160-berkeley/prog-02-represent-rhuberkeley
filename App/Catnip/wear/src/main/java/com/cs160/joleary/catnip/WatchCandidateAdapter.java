package com.cs160.joleary.catnip;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.Arrays;

public class WatchCandidateAdapter extends FragmentGridPagerAdapter {

    public final static String WATCH_NAME_KEY = "watchnamekey";
    public final static String WATCH_DESC_KEY = "watchdesckey";


    private final Context context;
    private final FragmentManager fm;

    public WatchCandidateAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }

    public static class Page {
        String name;
        String party;
        String title;
    }

    public final Page[][] PAGES = {{}};

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
        String title = page.title;
        String name = page.name;
        String party = page.party;

        if (title == null) {
            title = "None";
        }
        if (name == null) {
            name = "None";
        }
        if (party == null) {
            party = "None";
        }

        CandidateFragment fragment = new CandidateFragment();
        Bundle args = new Bundle();
        args.putString(WATCH_NAME_KEY, name);
        args.putString(WATCH_DESC_KEY, party + "\n" + title);
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }

    @Override
    public int getRowCount() {
        return PAGES.length;
    }

    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }

    public Drawable getBackgroundForPage(int row, int column) {
        return WatchCandidateAdapter.BACKGROUND_NONE;
    }

    @Override
    public Fragment instantiateItem(ViewGroup container, int row, int column) {
        return super.instantiateItem(container, row, column);
    }
}