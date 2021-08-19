package com.mobdeve.s18.recordnest;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityAddToCollectionBinding;
import com.mobdeve.s18.recordnest.model.Collection;

import java.util.ArrayList;

public class AddToCollectionActivity extends AppCompatActivity {

    private ActivityAddToCollectionBinding binding;

    CollectionAdapter collectionAdapter;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddToCollectionBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);




        //TextView albumName = findViewById(R.id.tv_album_name);
        //albumName.setVisibility(View.VISIBLE);



        //binding.rvCollectionalbum.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        //binding.rvCollectionalbum.setAdapter(albumAdapter);
/*
        spinner = findViewById(R.id.spinner_collection);

        collectionAdapter = new CollectionAdapter(getApplicationContext(), initializeDataCollection());

        spinner.setAdapter((SpinnerAdapter) collectionAdapter); */

        /*
        collectionAdapter = ArrayAdapter.createFromResource(getApplicationContext(), initializeDataCollection(), R.layout.)
        collectionAdapter = Adapter.createFromResource(getActivity(), R.array.accounts,R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        spinner.setAdapter(adapter);

         */
        //spinner.setPrompt("Select an account");

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