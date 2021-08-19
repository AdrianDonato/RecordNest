package com.mobdeve.s18.recordnest.model;

public class UserList {
    private String userName;
    private int userImage;

    public UserList(int userImage, String userName ){
        this.userImage = userImage;
        this.userName = userName;
    }

    public int getUserImage() {
        return userImage;
    }

    public String getUserName() {
        return userName;
    }

}
