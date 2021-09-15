package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.adapter.ArtistAdapter;
import com.mobdeve.s18.recordnest.adapter.GenreAdapter;
import com.mobdeve.s18.recordnest.adapter.YearAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivitySearchCollectionBinding;
import com.mobdeve.s18.recordnest.model.Album;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SearchCollectionActivity extends AppCompatActivity {

    private ActivitySearchCollectionBinding binding;
    private TextView collectionName;

    private FirebaseFirestore fStore;

    private AlbumAdapter albumAdapter;
    private ArrayList<Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchCollectionBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        //initialize fStore
        fStore = FirebaseFirestore.getInstance();

        this.collectionName = findViewById(R.id.search_collection_item);

        //initialize albums from database
        initPageAlbData();

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
    }

    public void initPageAlbData(){
        Intent i = getIntent();
        String previousActivity= i.getStringExtra("FROM_ACTIVITY");
        if(previousActivity.equals("genre")){
            String pageGenre = i.getStringExtra(GenreAdapter.KEY_GENRE_NAME);
            collectionName.setText(pageGenre + " Albums");
            initCollGenre(pageGenre);
        } else if(previousActivity.equals("year")){
            String pageYear = i.getStringExtra(YearAdapter.KEY_YEAR_NAME);
            String pageYearSbstr = pageYear.substring(0, 4);
            collectionName.setText("Albums from the " + pageYear);
            initCollYear(Integer.parseInt(pageYearSbstr));
        } else if(previousActivity.equals("artist")){
            String pageArtist= i.getStringExtra(ArtistAdapter.KEY_ARTIST_NAME);
            this.collectionName.setText("Albums from Artists - " + pageArtist);
            initCollArtist(pageArtist);
        } else if (previousActivity.equals("approval")){
            this.collectionName.setText("Albums for Approval");
            initApprovalList();
        }
    }

    //initializes list of albums using a genre
    public void initCollGenre(String albGenre){
        albumList = new ArrayList<>();
        fStore.collection("Albums").whereEqualTo("Genre", albGenre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot : task.getResult()){
                        albumList.add(new Album(R.drawable.album1, snapshot.getString("Title"),
                                snapshot.getString("Artist")));
                        albumList.get(albumList.size()-1).setAlbumID(snapshot.getId());
                        albumList.get(albumList.size()-1).setAlbumArtURL(snapshot.getString("ImageURL"));
                    }
                    initializeAlbumAdapter();
                } else {
                    Toast.makeText(SearchCollectionActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //initializes list of albums using a year
    public void initCollYear(int albYear){
        albumList = new ArrayList<>();
        fStore.collection("Albums").whereGreaterThanOrEqualTo("Year", albYear).whereLessThan("Year", (albYear+10))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot : task.getResult()){
                        albumList.add(new Album(R.drawable.album1, snapshot.getString("Title"),
                                snapshot.getString("Artist")));
                        albumList.get(albumList.size()-1).setAlbumID(snapshot.getId());
                        albumList.get(albumList.size()-1).setAlbumArtURL(snapshot.getString("ImageURL"));
                    }
                    initializeAlbumAdapter();
                } else {
                    Toast.makeText(SearchCollectionActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //initializes list of albums using an artist
    public void initCollArtist(String albArtist){
        albumList = new ArrayList<>();

        fStore.collection("Albums").whereGreaterThanOrEqualTo("Artist", albArtist)
                .whereLessThanOrEqualTo("Artist", albArtist+'\uf8ff')
                .orderBy("Artist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot : task.getResult()){
                        albumList.add(new Album(R.drawable.album1, snapshot.getString("Title"),
                                snapshot.getString("Artist")));
                        albumList.get(albumList.size()-1).setAlbumID(snapshot.getId());
                        albumList.get(albumList.size()-1).setAlbumArtURL(snapshot.getString("ImageURL"));
                    }
                    initializeAlbumAdapter();
                } else {
                    Toast.makeText(SearchCollectionActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //initializes list of albums that are not yet approved - this is for the use of moderators
    public void initApprovalList(){
        albumList = new ArrayList<>();
        fStore.collection("Albums").whereEqualTo("Approved", "No").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot : task.getResult()){
                        albumList.add(new Album(R.drawable.album1, snapshot.getString("Title"),
                                snapshot.getString("Artist")));
                        albumList.get(albumList.size()-1).setAlbumID(snapshot.getId());
                        albumList.get(albumList.size()-1).setAlbumArtURL(snapshot.getString("ImageURL"));
                    }
                    initializeAlbumAdapter();
                } else {
                    Toast.makeText(SearchCollectionActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //function to initialize album adapter
    public void initializeAlbumAdapter(){
        albumAdapter = new AlbumAdapter(getApplicationContext(), albumList);
        binding.rvSearchcollectionalbum.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        binding.rvSearchcollectionalbum.setAdapter(albumAdapter);
    }
}

