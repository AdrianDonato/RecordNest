package com.mobdeve.s18.recordnest;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.adapter.UserListAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityOtherUserProfileBinding;
import com.mobdeve.s18.recordnest.model.Collection;
import com.mobdeve.s18.recordnest.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OtherUserProfileActivity extends AppCompatActivity {

    private ActivityOtherUserProfileBinding binding;

    private TextView username, followercount, followingcount;
    private ImageView userimg;
    private ArrayList<Collection> collArray;
    private FirebaseFirestore fStore;
    private FirebaseUser fUser;
    private String userID;

    CollectionAdapter collectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOtherUserProfileBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        View view = binding.getRoot();
        setContentView(view);

        fStore = FirebaseFirestore.getInstance();

        this.username = findViewById(R.id.tv_other_username);
        this.userimg = findViewById(R.id.iv_other_userImage);
        this.followercount = findViewById(R.id.tv_ou_followers);
        this.followingcount = findViewById(R.id.tv_ou_following);

        Intent i = getIntent();

        String name= i.getStringExtra(UserListAdapter.KEY_OTHER_USERNAME);
        int cover = i.getIntExtra(UserListAdapter.KEY_OTHER_USERIMG, 0);

        this.username.setText(name);
        this.userimg.setImageResource(cover);

        this.userID = i.getStringExtra(UserListAdapter.KEY_OTHER_USERID);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(userID.equals(fUser.getUid())){
            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        }

        initializeUserData(userID);


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

        initializeDataCollection();
    }

    public void initializeUserData(String userID){
        fStore.collection("UserDetails").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    String retUsername = snapshot.getString("Username");
                    String retImgURL = snapshot.getString("ProfPicURL");
                    int retFollowers = snapshot.getLong("FollowerCount").intValue();
                    int retFollowing = snapshot.getLong("FollowingCount").intValue();

                    username.setText(retUsername);
                    followercount.setText(Integer.toString(retFollowers));
                    followingcount.setText(Integer.toString(retFollowing));

                    if(!(retImgURL.equals("placeholder"))){
                        Glide.with(getApplicationContext()).load(retImgURL).into(userimg);
                    }

                } else {
                    Toast.makeText(OtherUserProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initializeDataCollection() {
        collArray = new ArrayList<>();
        fStore.collection("AlbumCollection")
                .whereEqualTo("UserID", userID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                String collID = documentSnapshot.getId();
                                String collTitle = documentSnapshot.getString("Title");

                                collArray.add(new Collection(collTitle));
                                collArray.get(collArray.size()-1).setCollectionID(collID);
                            }
                            initCollAdapter();
                        } else {
                            Toast.makeText(OtherUserProfileActivity.this, "Error! " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //initializes collection adapter
    public void initCollAdapter(){
        collectionAdapter = new CollectionAdapter(getApplicationContext(), collArray);

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvOtherCollection.setLayoutManager(lm);

        binding.rvOtherCollection.setAdapter(collectionAdapter);
    }

    public ArrayList<Collection> initializeData() {
        // get data from database here?
        ArrayList<Collection> data = new ArrayList<>();
        data.add(new Collection("Collection 1"));
        data.add(new Collection("Collection 2"));
        data.add(new Collection("Collection 3"));

        return data;
    }

    public void followUser(){

    }
}