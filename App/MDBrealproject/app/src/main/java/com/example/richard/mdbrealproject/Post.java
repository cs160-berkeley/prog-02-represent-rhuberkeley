package com.example.richard.mdbrealproject;

import android.graphics.Bitmap;

/**
 * Created by Richard on 3/9/2016.
 */
public class Post {
    Bitmap image;
    String place;
    String desc;

    public Post(Bitmap img, String plc, String dsc) {
        this.image = img;
        this.place = plc;
        this.desc = dsc;
    }
}
