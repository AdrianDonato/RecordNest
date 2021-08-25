package com.mobdeve.s18.recordnest.model;

public class UserList {
    private String userName, userID;
    private int userImage;

    public UserList(int userImage, String userName, String userID){
        this.userImage = userImage;
        this.userName = userName;
        this.userID = userID;
    }

    public int getUserImage() {
        return userImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID(){return  userID;}



}
