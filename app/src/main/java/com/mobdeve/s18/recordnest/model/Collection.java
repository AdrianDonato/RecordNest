package com.mobdeve.s18.recordnest.model;

import java.util.ArrayList;

public class Collection {
    private String collectionID; // collection title is not unique, so id is needed
    private String collectionTitle;
    private String username;
    private String description;
    private ArrayList<Album> albumsList;
    private ArrayList<String> albumIDList;


    public Collection(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    //setters
    public void setCollectionID(String collectionID) {
        this.collectionID = collectionID;
    }

    public void setAlbumIDList(ArrayList<String> albumIDList) {
        this.albumIDList = albumIDList;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //getters
    public String getCollectionID() {
        return collectionID;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Album> getAlbumsList() {
        return albumsList;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getAlbumIDList() {
        return albumIDList;
    }
}
