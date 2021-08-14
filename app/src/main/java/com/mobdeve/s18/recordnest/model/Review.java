package com.mobdeve.s18.recordnest.model;

public class Review {
    private int reviewID;
    private int rating;
    private String username;
    private String reviewContent;
    private int userImageId;

    public Review (/*int reviewID,*/ int rating,  int userImageId, String username, String reviewContent){
        /*this.reviewID = reviewID;*/
        this.rating = rating;
        this.userImageId = userImageId;
        this.username = username;
        this.reviewContent = reviewContent;
    }

    public int getReviewID() {
        return reviewID;
    }

    public int getRating() {
        return rating;
    }

    public String getUsername() {
        return username;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public int getUserImageId() {
        return userImageId;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }
}
