package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.ActivityAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityFollowingActBinding;
import com.mobdeve.s18.recordnest.model.Activity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowingActActivity extends AppCompatActivity {

    private ActivityFollowingActBinding binding;

    private Button btn_edit;
    private LinearLayout followers;

    private ImageView ownImage;
    private TextView ownUsername, ownFollowers, ownFollowing;

    private FirebaseFirestore fStore;
    private String ownUserID;
    private ArrayList<String> followingIDs;
    private ArrayList<Activity> feedList;

    //private ReviewAdapter reviewAdapter;
    private ActivityAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFollowingActBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        fStore = FirebaseFirestore.getInstance();
        Intent i = getIntent();
        ownUserID = i.getStringExtra("USER_ID");

        BottomNavigationView bottomNavigationViewFollowing = findViewById(R.id.nav_following);
        //home
        bottomNavigationViewFollowing.setSelectedItemId(R.id.activity);

        bottomNavigationViewFollowing.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.following:
                        Intent i = new Intent(getApplicationContext(), FollowingActivity.class);
                        i.putExtra("USER_ID", ownUserID);
                        startActivity(i);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.activity:
                        return true;
                }
                return false;
            }
        });

        initOwnProfile(ownUserID);

        followers  = findViewById(R.id.ll_followers);

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FollowingActActivity.this, FollowersActivity.class);
                i.putExtra("USER_ID", ownUserID);
                startActivity(i);
            }
        });

        btn_edit = findViewById(R.id.btn_edit_profile);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(FollowingActActivity.this, EditProfileActivity.class);
                i.putExtra("USER_ID", ownUserID);
                startActivity(i);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);

        //home
        bottomNavigationView.setSelectedItemId(R.id.search);

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

                        return true;
                }
                return false;
            }
        });
        /*
        activityAdapter = new ActivityAdapter(getApplicationContext(), initializeData());

        //TextView albumName = findViewById(R.id.tv_album_name);
        //albumName.setVisibility(View.VISIBLE);

        binding.rvRecentactivity.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        binding.rvRecentactivity.setAdapter(activityAdapter);

         */
    }

    //initialize own profile (username, profile picture, etc) as well as user's feed
    public void initOwnProfile(String userID){
        this.ownImage = findViewById(R.id.following_own_image);
        this.ownUsername = findViewById(R.id.profile_username);
        this.ownFollowers = findViewById(R.id.followingact_own_follower);
        this.ownFollowing = findViewById(R.id.followingact_own_following);

        fStore.collection("UserDetails").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    String retImg = snapshot.getString("ProfPicURL");
                    String retUsername = snapshot.getString("Username");
                    int fercount = snapshot.getLong("FollowerCount").intValue();
                    int fingcount= snapshot.getLong("FollowingCount").intValue();
                    followingIDs = (ArrayList<String>) snapshot.get("FollowingList");

                    ownUsername.setText(retUsername);
                    ownFollowers.setText(Integer.toString(fercount));
                    ownFollowing.setText(Integer.toString(fingcount));

                    if(!(retImg.equals("placeholder"))){
                        Glide.with(getApplicationContext())
                                .load(retImg).into(ownImage);
                    }

                    initFeed();
                } else {
                    Toast.makeText(FollowingActActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public ArrayList<Activity> initializeData() {
        // get data from database here?
        ArrayList<Activity> data = new ArrayList<>();
        data.add(new Activity("Username","Title","Content", "Date", R.drawable.vinyl));
        data.add(new Activity("Username","Title","Content", "Date", R.drawable.vinyl));
        return data;
    }

    //retrieves recent activity of users followed from firestore db
    public void initFeed(){
        feedList = new ArrayList<>();
        fStore.collection("FeedActivities").whereIn("UserID", followingIDs).orderBy("Date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String retUserID = documentSnapshot.getString("UserID");
                        String retTitle = documentSnapshot.getString("ActivityTitle");
                        String retContent = documentSnapshot.getString("ExtraContent");
                        String retDate = documentSnapshot.getString("Date");
                        String retIntentFor = documentSnapshot.getString("IntentFor");
                        String retIntentID = documentSnapshot.getString("IntentID");
                        feedList.add(new Activity(retUserID, retTitle, retContent, retDate));
                        feedList.get(feedList.size()-1).setIntentFor(retIntentFor);
                        feedList.get(feedList.size()-1).setIntentID(retIntentID);
                    }
                    initFeedAdapter();
                } else {
                    Toast.makeText(FollowingActActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.d("firebaserror", task.getException().getMessage());
                }
            }
        });
    }

    //initializes adapter of recent activities, executed after retrieving activities from db on initFeed()
    public void initFeedAdapter(){
        activityAdapter = new ActivityAdapter(getApplicationContext(), feedList);

        //TextView albumName = findViewById(R.id.tv_album_name);
        //albumName.setVisibility(View.VISIBLE);

        binding.rvRecentactivity.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        binding.rvRecentactivity.setAdapter(activityAdapter);
    }
}