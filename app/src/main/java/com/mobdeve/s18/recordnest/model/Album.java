package com.mobdeve.s18.recordnest.model;

import java.util.ArrayList;

public class Album {
    private int imageId;
    private String albumTitle;
    private String artistName;
    private float avgRating;
    private ArrayList<String> trackList;

    public Album(int imageId) {
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }
}
