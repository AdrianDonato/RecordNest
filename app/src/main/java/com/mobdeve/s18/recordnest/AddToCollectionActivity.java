package com.mobdeve.s18.recordnest;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityAddToCollectionBinding;
import com.mobdeve.s18.recordnest.model.Collection;

import java.util.ArrayList;

public class AddToCollectionActivity extends AppCompatActivity{

    private ActivityAddToCollectionBinding binding;

    CollectionAdapter collectionAdapter;
    Spinner spinner;

    TextInputLayout til_col;
    AutoCompleteTextView act_col;

    ArrayAdapter<String> arrayAdapterCollection;
    ArrayList<String> arrayListCollection;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)

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
/*
        til_col = findViewById(R.id.til_collection);
        act_col = findViewById(R.id.act_collection);

        arrayListCollection = new ArrayList<>();
        arrayListCollection.add("Collection 1");
        arrayListCollection.add("Collection 2");
        arrayListCollection.add("Collection 3");

        String []option = {"col 1", "col 2"};

        arrayAdapterCollection = new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,option);

        act_col.setAdapter(arrayAdapterCollection);

 */


        //act_col.setThreshold(3);


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
/*
        Spinner spinner = findViewById(R.id.spinner_collection);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddToCollectionActivity.this, R.array.numbers, android.R.layout.simple_spinner_item);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddToCollectionActivity.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.numbers));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

 */




        //spinner.setOnItemSelectedListener(this);

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