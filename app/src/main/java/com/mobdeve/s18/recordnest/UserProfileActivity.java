package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityUserProfileBinding;
import com.mobdeve.s18.recordnest.model.Collection;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;

    private CollectionAdapter collectionAdapter;

    private FirebaseUser mUser;
    private String profileID;
    private TextView profileUsername;

    Button btn_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        btn_edit = findViewById(R.id.btn_edit_profile);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                startActivity(i);
            }
        });


        //sets username of profile page (not finished yet, only works on logged in user's profile)
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUserID = mUser.getUid();
        String mUsername = mUser.getDisplayName();
        Intent prevPage = getIntent();
        profileID = prevPage.getStringExtra("profileUID");
        profileUsername = (TextView)findViewById(R.id.profile_username);
        profileUsername.setText(mUsername);


        if(profileID == mUserID){
            profileUsername.setText(mUser.getDisplayName());
        }




        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        //home
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:

                        return true;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(),SearchUserActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        collectionAdapter = new CollectionAdapter(getApplicationContext(), initializeDataCollection());

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvCollection.setLayoutManager(lm);

        binding.rvCollection.setAdapter(collectionAdapter);

    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();

    }

    public ArrayList<Collection> initializeDataCollection() {

        ArrayList<Collection> data = new ArrayList<>();
        data.add(new Collection("Collection"));
        data.add(new Collection("Collection 2"));
        data.add(new Collection("Collection 3"));
        data.add(new Collection("Collection 4"));
        data.add(new Collection("Collection 5"));
        data.add(new Collection("Collection 6"));



        return data;
    }
}
