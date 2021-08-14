package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityMainBinding;
import com.mobdeve.s18.recordnest.model.Album;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    private int testing;
    private int testing2;

    private ActivityMainBinding binding;
    private AlbumAdapter albumAdapter;

    private Button more;
    //private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);



        binding.btnMore.setOnClickListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);

        //home
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
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


        albumAdapter = new AlbumAdapter(getApplicationContext(), initializeData());


        //binding.rvDatalist.setLayoutManager(new LinearLayoutManager(getApplicationContext()))';
        binding.rvDatalist.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));

        //binding.rvDatalist.setLayoutManager(new GridLayoutManager(getApplicationContext());
        binding.rvDatalist.setAdapter(albumAdapter);


    }

    public void openAlbumProfile(){
        Intent i = new Intent(this, AlbumProfileActivity.class);
        startActivity(i);
    }



    public ArrayList<Album> initializeData() {

        ArrayList<Album> data = new ArrayList<>();
        data.add(new Album(R.drawable.album1, "Juicebox","Mac Ayres"));
        data.add(new Album(R.drawable.album2, "Twentytwenty","Jake Scott"));
        data.add(new Album(R.drawable.album3, "Happier than ever","Billie Eilish"));
        data.add(new Album(R.drawable.album1, "Juicebox","Mac Ayres"));
        data.add(new Album(R.drawable.album2, "Twentytwenty","Jake Scott"));

        Collections.shuffle(data);

        return data;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_more:
                Intent i = new Intent(MainActivity.this, SearchByActivity.class);
                startActivity(i);
                break;
        }

    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();

    }
}