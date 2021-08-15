package com.mobdeve.s18.recordnest.model;

import java.util.ArrayList;

public class Collection {
    private int collectionID; // collection title is not unique, so id is needed
    private String collectionTitle;
    private String username;
    private ArrayList<Album> albumsList;


    public Collection(String collectionTitle /*int collectionID,  String username, ArrayList<Album> albumsList*/) {
        //this.collectionID = collectionID;
        this.collectionTitle = collectionTitle;
        //this.username = username;
        //this.albumsList = albumsList;
    }

    public int getCollectionID() {
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


}
