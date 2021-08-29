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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.adapter.ArtistAdapter;
import com.mobdeve.s18.recordnest.adapter.GenreAdapter;
import com.mobdeve.s18.recordnest.adapter.YearAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivitySearchCollectionBinding;

public class SearchCollectionActivity extends AppCompatActivity {

    private ActivitySearchCollectionBinding binding;
    TextView collectionName;

    private AlbumAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchCollectionBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        //initialize fStore
        //fStore = FirebaseFirestore.getInstance();

        this.collectionName = findViewById(R.id.search_collection_item);

        Intent i = getIntent();

        String previousActivity= i.getStringExtra("FROM_ACTIVITY");

        if (previousActivity.equals("genre")){
            String name= i.getStringExtra(GenreAdapter.KEY_GENRE_NAME);
            this.collectionName.setText(name);
        }

        else if (previousActivity.equals("artist")){
            String name= i.getStringExtra(ArtistAdapter.KEY_ARTIST_NAME);
            this.collectionName.setText(name);
        }

        else if (previousActivity.equals("year")){
            String name= i.getStringExtra(YearAdapter.KEY_YEAR_NAME);
            this.collectionName.setText(name);
        }



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

/*
        albumAdapter = new AlbumAdapter(getApplicationContext(), initializeData());

        //TextView albumName = findViewById(R.id.tv_album_name);
        //albumName.setVisibility(View.VISIBLE);

        binding.rvSearchcollectionalbum.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        //findViewById(R.id.tv_album_name).setVisibility(View.  VISIBLE);;
        binding.rvSearchcollectionalbum.setAdapter(albumAdapter);

 */


    }
/*
    public ArrayList<Album> initializeData() {
        // get data from database here?
        ArrayList<Album> data = new ArrayList<>();
        data.add(new Album(R.drawable.album1, "Juicebox","Mac Ayres"));
        data.add(new Album(R.drawable.album2, "Twentytwenty","Jake Scott"));
        data.add(new Album(R.drawable.album3, "Happier than ever","Billie Eilish"));
        data.add(new Album(R.drawable.album1, "Juicebox","Mac Ayres"));
        data.add(new Album(R.drawable.album2, "Twentytwenty","Jake Scott"));

        Collections.shuffle(data);



        return data;
    }

 */
}

