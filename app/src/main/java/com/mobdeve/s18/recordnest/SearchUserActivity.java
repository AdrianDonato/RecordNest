package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s18.recordnest.adapter.UserListAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivitySearchUserBinding;
import com.mobdeve.s18.recordnest.model.UserList;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity {

    private ActivitySearchUserBinding binding;

    private UserListAdapter userListAdapter;

    ArrayAdapter<String> adapter;
    ArrayList<UserList> userListArrayList;

    EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchUserBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

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
        binding.rvUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        binding.rvUser.setAdapter(userListAdapter);

        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });




    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();

    }

    private void filter(String text){
        ArrayList<UserList> filteredList = new ArrayList<>();

        for(UserList item : initializeData()){
            if(item.getUserName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        userListAdapter.filterList(filteredList);

    }

    public ArrayList<UserList> initializeData() {
        // get data from database here?
        ArrayList<UserList> data = new ArrayList<>();
        data.add(new UserList(R.drawable.album1, "ina"));
        data.add(new UserList(R.drawable.album2, "pat"));
        data.add(new UserList(R.drawable.album3, "eva"));

        return data;
    }
}