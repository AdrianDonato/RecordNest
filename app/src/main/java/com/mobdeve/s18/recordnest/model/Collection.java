package com.mobdeve.s18.recordnest.model;

import java.util.ArrayList;

public class Collection {
    private int collectionID; // collection title is not unique, so id is needed
    private String collectionTitle;
    private String username;
    private ArrayList<Album> albumsList;
}
