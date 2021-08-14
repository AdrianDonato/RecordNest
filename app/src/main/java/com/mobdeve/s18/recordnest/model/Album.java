package com.mobdeve.s18.recordnest.model;

import java.util.ArrayList;

public class Album {
    private int imageId;
    private int albumID;
    private String albumName;
    private String artist;
    private float avgRating;

    private ArrayList<String> trackList;

    public Album(int imageId, String albumName, String artist) {
        this.imageId = imageId;
        this.albumName = albumName;
        this.artist = artist;
    }

    public int getImageId() {
        return imageId;
    }

    public String getAlbumName(){
        return albumName;
    }

    public String getArtist(){
        return artist;
    }
}
