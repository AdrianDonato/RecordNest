package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.adapter.UserListAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityOtherUserProfileBinding;
import com.mobdeve.s18.recordnest.model.Collection;

import java.util.ArrayList;

public class OtherUserProfileActivity extends AppCompatActivity {

    private ActivityOtherUserProfileBinding binding;

    TextView username;
    ImageView userimg;

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


        this.username = findViewById(R.id.tv_other_username);
        this.userimg = findViewById(R.id.iv_other_userImage);

        Intent i = getIntent();

        String name= i.getStringExtra(UserListAdapter.KEY_OTHER_USERNAME);
        int cover = i.getIntExtra(UserListAdapter.KEY_OTHER_USERIMG, 0);

        this.username.setText(name);
        this.userimg.setImageResource(cover);


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

        collectionAdapter = new CollectionAdapter(getApplicationContext(), initializeData());

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
}