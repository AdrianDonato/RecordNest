package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s18.recordnest.adapter.UserListAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityFollowingBinding;
import com.mobdeve.s18.recordnest.model.UserList;

import java.util.ArrayList;

public class FollowingActivity extends AppCompatActivity {

    private ActivityFollowingBinding binding;

    private UserListAdapter userListAdapter;

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

        userListAdapter = new UserListAdapter(getApplicationContext(), initializeData());

        //TextView albumName = findViewById(R.id.tv_album_name);
        //albumName.setVisibility(View.VISIBLE);

        binding.rvFollowinglist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        binding.rvFollowinglist.setAdapter(userListAdapter);

    }

    public ArrayList<UserList> initializeData() {
        // get data from database here?
        ArrayList<UserList> data = new ArrayList<>();
        data.add(new UserList(R.drawable.album1, "ina", "qbvxJbLdBXWq0CYqEeoSqV91ALs2"));
        data.add(new UserList(R.drawable.album2, "pat", "sGMkuJr9S3TiTRfAg4inkCO5DnH2"));
        data.add(new UserList(R.drawable.album3, "eva", "4F41MMsEjSWDxzkVHcTDvlj8BLD3"));

        return data;
    }
/*
    public void onBackPressed(){
        //super.onBackPressed();

    }

 */
}