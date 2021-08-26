package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.UserListAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityFollowingBinding;
import com.mobdeve.s18.recordnest.model.UserList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowingActivity extends AppCompatActivity {

    private ActivityFollowingBinding binding;

    private UserListAdapter userListAdapter;
    private ArrayList<UserList> followingUsers;
    private ImageView ownImage;
    private TextView ownUsername, ownFollowers, ownFollowing;

    private FirebaseFirestore fStore;
    private String ownUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFollowingBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        fStore = FirebaseFirestore.getInstance();
        Intent i = getIntent();
        ownUserID = i.getStringExtra("USER_ID");

        BottomNavigationView bottomNavigationViewFollowing = findViewById(R.id.nav_following);
        //home
        bottomNavigationViewFollowing.setSelectedItemId(R.id.following);

        bottomNavigationViewFollowing.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.following:
                        return true;
                    case R.id.activity:
                        startActivity(new Intent(getApplicationContext(), FollowingActActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
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
        initOwnProfile(ownUserID);
        initializeUserData(ownUserID);
    }

    //initialize own profile (username, profile picture, etc)
    public void initOwnProfile(String userID){
        this.ownImage = findViewById(R.id.following_own_image);
        this.ownUsername = findViewById(R.id.profile_username);
        this.ownFollowers = findViewById(R.id.following_own_follower);
        this.ownFollowing = findViewById(R.id.following_own_following);

        fStore.collection("UserDetails").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    String retImg = snapshot.getString("ProfPicURL");
                    String retUsername = snapshot.getString("Username");
                    int fercount = snapshot.getLong("FollowerCount").intValue();
                    int fingcount= snapshot.getLong("FollowingCount").intValue();

                    ownUsername.setText(retUsername);
                    ownFollowers.setText(Integer.toString(fercount));
                    ownFollowing.setText(Integer.toString(fingcount));

                    if(!(retImg.equals("placeholder"))){
                        Glide.with(getApplicationContext())
                                .load(retImg).into(ownImage);
                    }
                } else {

                }
            }
        });
    }

    //generate followed users from database with this function
    public void initializeUserData(String userID){
        followingUsers = new ArrayList<>();
        fStore.collection("UserDetails").whereArrayContains("FollowerList", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String retUID = documentSnapshot.getId();
                        String retUsername = documentSnapshot.getString("Username");
                        String retImgURL = documentSnapshot.getString("ProfPicURL");

                        //set userlist instance with image url string
                        followingUsers.add(new UserList(retImgURL, retUsername, retUID));
                    }
                    initUsersAdapter();
                } else {
                    Toast.makeText(FollowingActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //initialize user adapter after getting users from db
    public void initUsersAdapter(){
        userListAdapter = new UserListAdapter(getApplicationContext(), followingUsers);

        //TextView albumName = findViewById(R.id.tv_album_name);
        //albumName.setVisibility(View.VISIBLE);

        binding.rvFollowinglist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        binding.rvFollowinglist.setAdapter(userListAdapter);
    }
/*
    public void onBackPressed(){
        //super.onBackPressed();

    }

 */
}