package com.mobdeve.s18.recordnest.model;

import java.util.ArrayList;

public class Album {
    private int imageId;
    private String albumID;
    private String albumArtURL;
    private String albumName;
    private String artist;
    private String genre;
    private float avgRating;
    private int ratingsCount;
    private int year;

    private ArrayList<String> trackList;
    private ArrayList<String> reviewList;

    public Album(int imageId, String albumName, String artist) {
        this.imageId = imageId;
        this.albumName = albumName;
        this.artist = artist;

        //this.trackList = trackList;
    }

    //setters
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setAlbumID(String albumID){this.albumID = albumID;}

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public void setAlbumArtURL(String albumArtURL) {
        this.albumArtURL = albumArtURL;
    }

    //getters
    public int getImageId() {
        return imageId;
    }

    public String getAlbumID() {
        return albumID;
    }

    public String getAlbumName(){
        return albumName;
    }

    public String getArtist(){
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public int getYear() {
        return year;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public String getAlbumArtURL(){
        return albumArtURL;
    }

    /*
    public String getTracklist(){
        return trackList;
    }

 */

}
