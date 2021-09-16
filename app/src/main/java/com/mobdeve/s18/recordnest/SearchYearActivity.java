package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s18.recordnest.adapter.YearAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivitySearchYearBinding;
import com.mobdeve.s18.recordnest.model.Year;

import java.util.ArrayList;

public class SearchYearActivity extends AppCompatActivity {

    private ActivitySearchYearBinding binding;

    public YearAdapter yearAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchYearBinding.inflate(getLayoutInflater());

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
        yearAdapter = new YearAdapter(getApplicationContext(), initializeData());

        binding.rvYear.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvYear.setLayoutManager(lm);
        binding.rvYear.setAdapter(yearAdapter);
    }

    //initializes set year data
    public ArrayList<Year> initializeData() {
        ArrayList<Year> data = new ArrayList<>();
        data.add(new Year("2020s"));
        data.add(new Year("2010s"));
        data.add(new Year("2000s"));
        data.add(new Year("1990s"));
        data.add(new Year("1980s"));
        data.add(new Year("1970s"));
        data.add(new Year("1960s"));
        data.add(new Year("1950s"));

        return data;
    }
}