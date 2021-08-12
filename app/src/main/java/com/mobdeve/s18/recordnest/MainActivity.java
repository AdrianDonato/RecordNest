package com.mobdeve.s18.recordnest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityMainBinding;
import com.mobdeve.s18.recordnest.model.Album;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private int testing;
    private int testing2;

    private ActivityMainBinding binding;
    private AlbumAdapter albumAdapter;

    //private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);

        setContentView(binding.getRoot());

        albumAdapter = new AlbumAdapter(getApplicationContext(), initializeData());


        //binding.rvDatalist.setLayoutManager(new LinearLayoutManager(getApplicationContext()))';
        binding.rvDatalist.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));

        //binding.rvDatalist.setLayoutManager(new GridLayoutManager(getApplicationContext());
        binding.rvDatalist.setAdapter(albumAdapter);
    }



    public ArrayList<Album> initializeData() {

        ArrayList<Album> data = new ArrayList<>();
        data.add(new Album(R.drawable.vinyl));
        data.add(new Album(R.drawable.vinyl2));
        data.add(new Album(R.drawable.vinyl));
        data.add(new Album(R.drawable.vinyl2));
        data.add(new Album(R.drawable.vinyl));


        Collections.shuffle(data);

        return data;
    }
}