package com.mobdeve.s18.recordnest.model;

import com.mobdeve.s18.recordnest.R;

public class UserList {
    private String userName, userID, imageURL;
    private int userImage;

    public UserList(int userImage, String userName, String userID){
        this.userImage = userImage;
        this.userName = userName;
        this.userID = userID;
        this.imageURL = "placeholder";
    }

    //constructor with imageURL instead of image resource int
    public UserList(String imageURL, String userName, String userID){
        this.imageURL = imageURL;
        this.userName = userName;
        this.userID = userID;
        this.userImage = R.drawable.user;
    }

    public int getUserImage() {
        return userImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID(){return  userID;}

    public String getImageURL(){return imageURL; }

}
