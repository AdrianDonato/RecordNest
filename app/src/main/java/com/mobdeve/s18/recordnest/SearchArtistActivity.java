package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s18.recordnest.adapter.ArtistAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivitySearchArtistBinding;
import com.mobdeve.s18.recordnest.model.Artist;

import java.util.ArrayList;

public class SearchArtistActivity extends AppCompatActivity {

    private ActivitySearchArtistBinding binding;

    public ArtistAdapter artistAdapter;

    TextView artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchArtistBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

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
        artistAdapter = new ArtistAdapter(getApplicationContext(), initializeData());

        binding.rvArtist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvArtist.setLayoutManager(lm);
        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        binding.rvArtist.setAdapter(artistAdapter);
    }

    public ArrayList<Artist> initializeData() {
        // get data from database here?
        ArrayList<Artist> data = new ArrayList<>();
        data.add(new Artist("0-9"));
        data.add(new Artist("A-Z"));
        data.add(new Artist("Symbols/Special Characters"));
        data.add(new Artist("Others"));
        return data;
    }
}