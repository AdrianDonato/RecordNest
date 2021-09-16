package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.UserListAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityFollowersBinding;
import com.mobdeve.s18.recordnest.model.UserList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowersActivity extends AppCompatActivity {

    private ActivityFollowersBinding binding;

    private UserListAdapter userListAdapter;

    private LinearLayout following;
    private Button btn_edit;

    private ArrayList<UserList> followerUsers;
    private ImageView ownImage;
    private TextView ownUsername, ownFollowers, ownFollowing;

    private FirebaseFirestore fStore;
    private String ownUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowersBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        fStore = FirebaseFirestore.getInstance();
        Intent i = getIntent();
        ownUserID = i.getStringExtra("USER_ID");

        btn_edit = findViewById(R.id.btn_edit_profile);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(FollowersActivity.this, EditProfileActivity.class);
                i.putExtra("USER_ID", ownUserID);
                startActivity(i);
            }
        });

        following = findViewById(R.id.ll_following);

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FollowersActivity.this, FollowingActivity.class);
                i.putExtra("USER_ID", ownUserID);
                startActivity(i);
            }
        });

        initOwnProfile(ownUserID);
        initializeUserData(ownUserID);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);

        //home
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

    //initialize own profile (username, profile picture, etc)
    public void initOwnProfile(String userID){
        this.ownImage = findViewById(R.id.follower_own_image);
        this.ownUsername = findViewById(R.id.profile_username);
        this.ownFollowers = findViewById(R.id.follower_own_follower);
        this.ownFollowing = findViewById(R.id.follower_own_following);

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
                    Toast.makeText(FollowersActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //generate followed users from database with this function
    public void initializeUserData(String userID){
        followerUsers = new ArrayList<>();
        fStore.collection("UserDetails").whereArrayContains("FollowingList", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String retUID = documentSnapshot.getId();
                        String retUsername = documentSnapshot.getString("Username");
                        String retImgURL = documentSnapshot.getString("ProfPicURL");

                        //set userlist instance with image url string
                        followerUsers.add(new UserList(retImgURL, retUsername, retUID));
                    }
                    initUsersAdapter();
                } else {
                    Toast.makeText(FollowersActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //initialize user adapter after getting users from db
    public void initUsersAdapter(){
        userListAdapter = new UserListAdapter(getApplicationContext(), followerUsers);

        binding.rvFollowerslist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvFollowerslist.setAdapter(userListAdapter);
    }


}