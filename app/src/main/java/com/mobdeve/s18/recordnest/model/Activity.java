package com.mobdeve.s18.recordnest.model;

public class Activity {

    private String username; //user ID instead of username
    private String title;
    private String content;
    private String date;
    private String intentFor; //used to specify intent activity (album or collection)
    private String intentID; //intent extra for either album or coll id
    private int icon;

    public Activity (String username, String title, String content, String date, int icon){
        this.username = username;
        this.title = title;
        this.content = content;
        this.date = date;
        this.icon = icon;
    }

    //constructor without icon
    public Activity (String username, String title, String content, String date){
        this.username = username;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getIntentFor(){return intentFor;}

    public void setIntentFor(String intentFor) {
        this.intentFor = intentFor;
    }

    public String getIntentID() {
        return intentID;
    }

    public void setIntentID(String intentID) {
        this.intentID = intentID;
    }
}
