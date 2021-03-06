package com.mobdeve.s18.recordnest;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.adapter.ReviewAdapter;
import com.mobdeve.s18.recordnest.adapter.TracklistAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityAlbumProfileBinding;
import com.mobdeve.s18.recordnest.model.Album;
import com.mobdeve.s18.recordnest.model.Review;
import com.mobdeve.s18.recordnest.model.Tracklist;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AlbumProfileActivity extends AppCompatActivity {

    private ActivityAlbumProfileBinding binding;

    private ImageView imgViewAlbum;

    private TextView nameViewAlbum, artistViewAlbum, yearViewAlbum, genreViewAlbum, avgRatingViewAlbum;
    private RatingBar ratingViewAlbum;
    private EditText reviewETAlbum;

    private Button btnReviewAlbum, btnApprove, btnDelete;

    private LinearLayout content, reviewInput, moderatorOptions;

    Animation topAnim, bottomAnim;

    public TracklistAdapter tracklistAdapter;

    public ReviewAdapter reviewAdapter;

    private FirebaseFirestore fStore;
    private DocumentReference albumDocRef;
    private StorageReference albumCoverStorage;
    private FirebaseUser mUser;

    private String mUserID;
    private String mUsername;
    private String obtainedId;


    private Album albumDisplayed;
    private ArrayList<Tracklist> tracklistDisplayed;
    private ArrayList<String> trackString;
    private ArrayList<String> arrayListCollection;
    private ArrayList<String> collIDs;
    private ArrayList<Review> reviewList;
    private int userReviewIndex;
    private boolean isInColl; //for checking if the album is in a collection
    private boolean isModerator; //checks if current user is a moderator

    //Dialog myDialog;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    Button btn_add_to_col, btn_close, btn_add;;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlbumProfileBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        content = findViewById(R.id.content);
        reviewInput = findViewById(R.id.review_input_con);

        reviewInput.setAnimation(bottomAnim);
        content.setAnimation(topAnim);

        this.imgViewAlbum = findViewById(R.id.iv_view_album);
        this.nameViewAlbum = findViewById(R.id.tv_album_name);
        this.artistViewAlbum = findViewById(R.id.tv_album_artist);
        this.yearViewAlbum = findViewById(R.id.tv_album_year);
        this.genreViewAlbum = findViewById(R.id.tv_album_genre);
        this.avgRatingViewAlbum = findViewById(R.id.tv_album_avgrating);
        this.moderatorOptions = findViewById(R.id.ll_modoptions);
        this.btnApprove = findViewById(R.id.btn_album_approve);
        this.btnDelete = findViewById(R.id.btn_album_delete);

        //hide moderator options first
        this.moderatorOptions.setVisibility(View.GONE);

        //get review views
        this.ratingViewAlbum = findViewById(R.id.rb_review_rating);
        this.reviewETAlbum = findViewById(R.id.et_review_content);
        this.btnReviewAlbum = findViewById(R.id.button_submit_review);

        btnReviewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });

        Intent i = getIntent();

        //Firestore Initialization
        fStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserID = mUser.getUid();
        mUsername = mUser.getDisplayName();

        int cover = i.getIntExtra(AlbumAdapter.KEY_PICTURE, R.drawable.vinyl);
        obtainedId = i.getStringExtra(AlbumAdapter.KEY_ID);

        //sets the data of the album (albumDisplayed) then sets data to the layout views
        setAlbumData(obtainedId, cover);
        //sets the data of all reviews made for the album
        initializeDataReview();

        btn_add_to_col = findViewById(R.id.btn_album_add_to_collection);
        initializeAddtoCollection();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);

        bottomNavigationView.setSelectedItemId(R.id.invisible);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(),SearchUserActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    //initializes tracklist model using the retrieved string arraylist from database
    public ArrayList<Tracklist> initializeDataTrack(ArrayList<String> tracklist) {

        ArrayList<Tracklist> data = new ArrayList<>();
        for(int i = 0; i < tracklist.size(); i++){
            data.add(new Tracklist(tracklist.get(i)));
        }

        return data;
    }

    //function for getting list of reviews for the album
    public void initializeDataReview() {
        reviewList = new ArrayList<>();
        fStore.collection("Review").whereEqualTo("AlbumID", obtainedId).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String reviewID = documentSnapshot.getId();
                        String retrievedRating = documentSnapshot.getString("Rating");
                        double retRatingDbl = Double.parseDouble(retrievedRating);
                        int retRatingInt = (int) retRatingDbl;
                        reviewList.add(new Review(retRatingInt,
                                R.drawable.album1, documentSnapshot.getString("UserID"),
                                documentSnapshot.getString("ReviewContent")));
                        reviewList.get(reviewList.size()-1).setReviewIDString(reviewID);
                    }
                    fStore.collection("UserDetails").document(mUserID).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot snapshot = task.getResult();
                                        if((snapshot.getString("Type").equals("Moderator"))
                                                ||(snapshot.getString("Type").equals("Admin"))){
                                            isModerator = true;
                                        } else {
                                            isModerator = false;
                                        }
                                        setEditReview();
                                        initializeReviewAdapter();
                                    } else {
                                        Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //this function passes data from firebase to an Album class (albumDisplayed)
    public void setAlbumData (String albumID, int coverData){
        albumDocRef = fStore.collection("Albums").document(albumID);
        albumDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot value = task.getResult();
                    String retAlbumId = value.getId();
                    String retAlbumTitle = value.getString("Title");
                    String retArtistName = value.getString("Artist");
                    String retAlbumGenre = value.getString("Genre");
                    String retAlbumImg = value.getString("ImageURL");
                    String retAlbumApproved = value.getString("Approved");
                    int retAlbumYear = value.getLong("Year").intValue();
                    double retAvgRating = value.getDouble("AvgRating");
                    int retRatingCount = value.getLong("RatingCount").intValue();
                    int retAccRating = value.getLong("AccRatings").intValue();
                    ArrayList<String> retTracklist = (ArrayList<String>) value.get("Tracklist");

                    albumDisplayed = new Album(coverData, retAlbumTitle, retArtistName);
                    albumDisplayed.setAlbumID(retAlbumId);
                    albumDisplayed.setGenre(retAlbumGenre);
                    albumDisplayed.setYear(retAlbumYear);
                    albumDisplayed.setAvgRating(retAvgRating);
                    albumDisplayed.setRatingsCount(retRatingCount);
                    albumDisplayed.setAccRatingScore(retAccRating);
                    albumDisplayed.setAlbumArtURL(retAlbumImg);
                    trackString = retTracklist;

                    setProfileViewData(coverData, retAlbumApproved);
                    initializeTrackAdapter();
                } else {
                    Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //this function sets the data of the views on album profile
    public void setProfileViewData(int coverData, String approval){
        int cover = coverData;//albumDisplayed.getImageId();
        String name = albumDisplayed.getAlbumName();
        String artist = albumDisplayed.getArtist();
        String artLink = albumDisplayed.getAlbumArtURL();
        int albumYear = albumDisplayed.getYear();
        int rateCount = albumDisplayed.getRatingsCount();
        double avgRating = albumDisplayed.getAvgRating();
        String genre = albumDisplayed.getGenre();
        albumCoverStorage = FirebaseStorage.getInstance().getReferenceFromUrl(artLink);

        Glide.with(this).load(artLink).into(imgViewAlbum);
        this.nameViewAlbum.setText(name);
        this.artistViewAlbum.setText(artist);
        this.yearViewAlbum.setText(Integer.toString(albumYear));
        this.genreViewAlbum.setText(genre);

        if(approval.equals("No")){
            btn_add_to_col.setVisibility(View.GONE);
            moderatorOptions.setVisibility(View.VISIBLE);
            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveAlbum();
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAlbum();
                }
            });
        }

        //set intent when year is clicked
        this.yearViewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intentYear = Integer.toString(albumYear).substring(0,3) + "0s";

                Intent i = new Intent(v.getContext(), SearchCollectionActivity.class);

                i.putExtra("FROM_ACTIVITY", "year");
                i.putExtra("KEY_YEAR_NAME", intentYear);
                v.getContext().startActivity(i);
            }
        });

        //set intent when genre is clicked
        this.genreViewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SearchCollectionActivity.class);

                i.putExtra("FROM_ACTIVITY", "genre");
                i.putExtra("KEY_GENRE_NAME", genre);
                v.getContext().startActivity(i);
            }
        });

        //set intent when artist name is clicked
        this.artistViewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String artistAlph = String.valueOf(artist.charAt(0));
                Intent i = new Intent(v.getContext(), SearchCollectionActivity.class);

                i.putExtra("FROM_ACTIVITY", "artist");
                i.putExtra("KEY_ARTIST_NAME", artistAlph);
                v.getContext().startActivity(i);
            }
        });

        //don't display average rating if no review has been made yet
        if(rateCount > 0) {
            //format average to 2 decimal places only
            this.avgRatingViewAlbum.setText(String.format("%.2f", avgRating));
        } else {
            this.avgRatingViewAlbum.setText("No Ratings Yet");
        }
    }

    //function which initializes tracklist adapter
    //initialize track adapter only when data for tracklist has been retrieved
    public void initializeTrackAdapter(){
        tracklistAdapter = new TracklistAdapter(getApplicationContext(), initializeDataTrack(trackString));
        binding.rvTracklist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvTracklist.setAdapter(tracklistAdapter);
    }

    //function which initializes reviewlist adapter
    public void initializeReviewAdapter(){
        reviewAdapter = new ReviewAdapter(this, reviewList);
        reviewAdapter.setHideDelete(!isModerator);
        reviewAdapter.setAlbumReviewed(albumDisplayed);
        binding.rvReview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvReview.setAdapter(reviewAdapter);
    }

    //function for submitting a review to the firestore database
    public void submitReview(){
        if(checkIfReviewed()){
            //edit review
            String reviewContent = reviewETAlbum.getText().toString().trim();
            String rating = String.valueOf(ratingViewAlbum.getRating());
            String reviewId = reviewList.get(userReviewIndex).getReviewIDString();

            int RatingsCount = albumDisplayed.getRatingsCount();
            int oldRating = reviewList.get(userReviewIndex).getRating();
            int newAccRatings = albumDisplayed.getAccRatingScore() - oldRating;
            newAccRatings = newAccRatings + Math.round(ratingViewAlbum.getRating());
            double newAvgRating = (double) newAccRatings / RatingsCount;

            Map<String, Object> updatedRating = new HashMap<>();
            updatedRating.put("AccRatings", newAccRatings);
            updatedRating.put("AvgRating", newAvgRating);
            updatedRating.put("RatingCount", RatingsCount);

            Map<String, Object> reviewSubmitted = new HashMap<>();
            reviewSubmitted.put("UserID", mUserID);
            reviewSubmitted.put("AlbumID", obtainedId);
            reviewSubmitted.put("Rating", rating);
            reviewSubmitted.put("ReviewContent", reviewContent);
            reviewSubmitted.put("UserImageURL", "placeholder");

            fStore.collection("Review").document(reviewId).update(reviewSubmitted).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //update album's rating data after successfully submitting review
                        fStore.collection("Albums").document(obtainedId).update(updatedRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //create a new activity for feed
                                    Map<String, Object> newActivity = new HashMap<>();
                                    if(reviewContent.equals("")){
                                        newActivity.put("ActivityTitle", "updated their rating of " + albumDisplayed.getAlbumName()
                                        + " to " + rating + " stars!");
                                        newActivity.put("ExtraContent", "");
                                    } else {
                                        newActivity.put("ActivityTitle", "updated their review for " + albumDisplayed.getAlbumName()
                                        + "!");
                                        newActivity.put("ExtraContent", '\"' + reviewContent + '\"');
                                    }
                                    newActivity.put("UserID", mUserID);
                                    newActivity.put("IntentFor", "Album");
                                    newActivity.put("IntentID", obtainedId);

                                    //get current timestamp
                                    Calendar currDate = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - hh:mm aa", Locale.US);
                                    String inDatePosted = sdf.format(currDate.getTime());

                                    //save current timestamp to hashmap
                                    newActivity.put("Date", inDatePosted);

                                    //add new activity into database
                                    fStore.collection("FeedActivities").add(newActivity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(AlbumProfileActivity.this, "Successfully updated your review!", Toast.LENGTH_SHORT).show();
                                                //refreshes activity to reflect changes
                                                finish();
                                                startActivity(getIntent());
                                            } else {
                                                Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            String reviewContent = reviewETAlbum.getText().toString().trim();
            String rating = String.valueOf(ratingViewAlbum.getRating());

            int newAccRatings = Math.round(ratingViewAlbum.getRating()) + albumDisplayed.getAccRatingScore();
            int newRatingsCount = albumDisplayed.getRatingsCount() + 1;

            //get new average rating thru dividing accumulated ratings by rating count
            double newAvgRating = (double) newAccRatings / newRatingsCount;

            Map<String, Object> updatedRating = new HashMap<>();
            updatedRating.put("AccRatings", newAccRatings);
            updatedRating.put("AvgRating", newAvgRating);
            updatedRating.put("RatingCount", newRatingsCount);

            Map<String, Object> reviewSubmitted = new HashMap<>();
            reviewSubmitted.put("UserID", mUserID);
            reviewSubmitted.put("AlbumID", obtainedId);
            reviewSubmitted.put("Rating", rating);
            reviewSubmitted.put("ReviewContent", reviewContent);
            reviewSubmitted.put("UserImageURL", "placeholder");
            fStore.collection("Review").add(reviewSubmitted).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        //update album's rating data after successfully submitting review
                        fStore.collection("Albums").document(obtainedId).update(updatedRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //create a new activity for feed
                                    Map<String, Object> newActivity = new HashMap<>();
                                    if(reviewContent.equals("")){
                                        newActivity.put("ActivityTitle", "rated " + albumDisplayed.getAlbumName()
                                                + " " + rating + " stars!");
                                        newActivity.put("ExtraContent", "");
                                    } else {
                                        newActivity.put("ActivityTitle", "wrote a review for " + albumDisplayed.getAlbumName()
                                                + "!");
                                        newActivity.put("ExtraContent", '\"' + reviewContent + '\"');
                                    }
                                    newActivity.put("UserID", mUserID);
                                    newActivity.put("IntentFor", "Album");
                                    newActivity.put("IntentID", obtainedId);

                                    //get current timestamp
                                    Calendar currDate = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - hh:mm aa", Locale.US);
                                    String inDatePosted = sdf.format(currDate.getTime());

                                    //save current timestamp to hashmap
                                    newActivity.put("Date", inDatePosted);

                                    //add new activity into database
                                    fStore.collection("FeedActivities").add(newActivity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(AlbumProfileActivity.this, "Successfully submitted your review!", Toast.LENGTH_SHORT).show();
                                                //refreshes activity to reflect changes
                                                finish();
                                                startActivity(getIntent());
                                            } else {
                                                Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //function to set edit text data if user has already reviewed album
    public void setEditReview(){
        if(checkIfReviewed()){
            float currRating = (float) reviewList.get(userReviewIndex).getRating();
            reviewETAlbum.setText(reviewList.get(userReviewIndex).getReviewContent());
            reviewETAlbum.setHint("Update your rating with a review...");
            ratingViewAlbum.setRating(currRating);
        }
    }

    public void initializeAddtoCollection(){
        arrayListCollection = new ArrayList<>();
        collIDs = new ArrayList<>();

        arrayListCollection.add("Choose a collection...");
        collIDs.add("placeholder"); //filler data, ensures both arrays have same size and indexes

        fStore.collection("AlbumCollection")
                .whereEqualTo("UserID", mUserID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String collID = documentSnapshot.getId();
                        String collTitle = documentSnapshot.getString("Title");

                        arrayListCollection.add(collTitle);
                        collIDs.add(collID);
                    }
                    initializeAddtoCollListener();
                } else {
                    Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //function to initialize add to collection button listener
    public void initializeAddtoCollListener(){
        btn_add_to_col.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AlbumProfileActivity.this);
                View view = getLayoutInflater().inflate(R.layout.activity_add_to_collection,null);

                Spinner spinner = view.findViewById(R.id.spinner_collection);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AlbumProfileActivity.this,
                        android.R.layout.simple_spinner_item,
                        arrayListCollection);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                btn_close = view.findViewById(R.id.btn_close_add_to_collection);
                btn_add = view.findViewById(R.id.btn_add_to_collection);

                builder.setView(view);
                AlertDialog myDialog = builder.create();
                myDialog.show();

                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!spinner.getSelectedItem().toString().equalsIgnoreCase("Choose a collection...")){
                            checkIfInCollection(spinner.getSelectedItem().toString());
                            myDialog.dismiss();
                        }
                    }
                });

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
            }
        });
    }

    //function to check if the user has already reviewed the album
    public boolean checkIfReviewed(){
        boolean reviewCheck = false;
        for(int i = 0; i < reviewList.size(); i++){
            String userName = reviewList.get(i).getUsername();
            if(userName.equals(mUserID)){
                userReviewIndex = i;
                reviewCheck = true;
            }
        }
        return reviewCheck;
    }

    public void addToCollectionDB(String collTitle){
        int titleIndex = arrayListCollection.indexOf(collTitle);
        String collId = collIDs.get(titleIndex);
        String albumId = albumDisplayed.getAlbumID();
        String imageURL = albumDisplayed.getAlbumArtURL();
        String albumTitle = albumDisplayed.getAlbumName();

        //save album id
        fStore.collection("AlbumCollection").document(collId).update("AlbumIDList",
                FieldValue.arrayUnion(albumId)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //save album image url
                    fStore.collection("AlbumCollection").document(collId).update("ImageURLList",
                            FieldValue.arrayUnion(imageURL)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                fStore.collection("AlbumCollection").document(collId).update("AlbumTitleList",
                                        FieldValue.arrayUnion(albumTitle)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            String successMSG = "Successfully added " + albumTitle + " to " + collTitle + "!";
                                            Toast.makeText(AlbumProfileActivity.this, successMSG,
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //function to update album's approval status to yes, making it visible for all users
    public void approveAlbum(){
        Map<String, Object> albumApproved = new HashMap<>();
        albumApproved.put("Approved", "Yes");

        fStore.collection("Albums").document(albumDisplayed.getAlbumID()).update(albumApproved).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AlbumProfileActivity.this, albumDisplayed.getAlbumName() + " has been approved!", Toast.LENGTH_SHORT).show();
                    //refreshes activity to reflect changes
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //deletes the unapproved album from the database
    public void deleteAlbum(){
        fStore.collection("Albums").document(albumDisplayed.getAlbumID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AlbumProfileActivity.this, albumDisplayed.getAlbumName() + " has been deleted.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AlbumProfileActivity.this, SearchCollectionActivity.class);
                    i.putExtra("FROM_ACTIVITY", "approval");
                    startActivity(i);
                } else {
                    Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //function to check if the album is already in a selected collection, if not then add to database
    //void not bool because of async nature of firebase
    public void checkIfInCollection(String collTitle){
        int titleIndex = arrayListCollection.indexOf(collTitle);
        String collId = collIDs.get(titleIndex);
        String albumId = albumDisplayed.getAlbumID();

        fStore.collection("AlbumCollection").document(collId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    ArrayList<String> albumIDList = (ArrayList<String>) snapshot.get("AlbumIDList");

                    isInColl = albumIDList.contains(albumId);

                    if(isInColl) {
                        String errorMsg = albumDisplayed.getAlbumName() + " is already in " + collTitle + "!";
                        Toast.makeText(AlbumProfileActivity.this,
                                errorMsg,
                                Toast.LENGTH_SHORT).show();
                    } else if (albumIDList.size() == 10) {
                        String errorMsg = collTitle + " already has 10 albums! Please remove to make more space.";
                        Toast.makeText(AlbumProfileActivity.this,
                                errorMsg,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        addToCollectionDB(collTitle);
                    }
                } else {
                    Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnBackPressed(){
        finish();
    }

}
