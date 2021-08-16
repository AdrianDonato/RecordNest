package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.adapter.ReviewAdapter;
import com.mobdeve.s18.recordnest.adapter.TracklistAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityAlbumProfileBinding;
import com.mobdeve.s18.recordnest.model.Album;
import com.mobdeve.s18.recordnest.model.Review;
import com.mobdeve.s18.recordnest.model.Tracklist;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumProfileActivity extends AppCompatActivity {

    private ActivityAlbumProfileBinding binding;

    private ImageView imgViewAlbum;

    private TextView nameViewAlbum, artistViewAlbum, trackListItem;

    private LinearLayout content, reviewInput;

    Animation topAnim, bottomAnim;

    //private RecyclerView rvTrackList;

    public TracklistAdapter tracklistAdapter;

    public ReviewAdapter reviewAdapter;

    private FirebaseFirestore fStore;
    private DocumentReference albumDocRef;

    private Album albumDisplayed;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlbumProfileBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        content = findViewById(R.id.content);
        reviewInput = findViewById(R.id.review_input_con);

        reviewInput.setAnimation(bottomAnim);
        content.setAnimation(topAnim);

        this.imgViewAlbum = findViewById(R.id.iv_view_album);
        this.nameViewAlbum = findViewById(R.id.tv_album_name);
        this.artistViewAlbum = findViewById(R.id.tv_album_artist);
        //this.trackListItem = findViewById(R.id.tracklist_item);
        //this.rvTrackList = findViewById(R.id.rv_tracklist);

        Intent i = getIntent();

        //Firestore Initialization
        fStore = FirebaseFirestore.getInstance();

        int cover = i.getIntExtra(AlbumAdapter.KEY_PICTURE, 0);
        String name = i.getStringExtra(AlbumAdapter.KEY_NAME);
        String artist = i.getStringExtra(AlbumAdapter.KEY_ARTIST);
        //String track = i.getStringExtra(TracklistAdapter.KEY_TRACK);

        /*
        this.imgViewAlbum.setImageResource(cover);
        this.nameViewAlbum.setText(name);
        this.artistViewAlbum.setText(artist);
        */
        //this.trackListItem.setText(track);


        //sets the data of the album (albumDisplayed) then sets data to the layout views
        setAlbumData("unjl1YJS8nP3cYZkv2Cv", cover);

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


        tracklistAdapter = new TracklistAdapter(getApplicationContext(), initializeDataTrack());
        binding.rvTracklist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvTracklist.setAdapter(tracklistAdapter);

        reviewAdapter = new ReviewAdapter(getApplicationContext(), initializeDataReview());
        binding.rvReview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvReview.setAdapter(reviewAdapter);



    }

    public ArrayList<Tracklist> initializeDataTrack() {

        ArrayList<Tracklist> data = new ArrayList<>();
        data.add(new Tracklist("ALUBUM TRACK 1"));
        data.add(new Tracklist("ALUBUM TRACK 2"));
        data.add(new Tracklist("ALUBUM TRACK 3"));
        data.add(new Tracklist("ALUBUM TRACK 4"));
        data.add(new Tracklist("ALUBUM TRACK 5"));
        data.add(new Tracklist("ALUBUM TRACK 6"));



        return data;
    }

    public ArrayList<Review> initializeDataReview() {

        ArrayList<Review> data = new ArrayList<>();
        data.add(new Review(4,R.drawable.album1, "ina", "wala ako maisip"));
        data.add(new Review(5,R.drawable.album2, "adrian", "ang tagal maglayout svsbe rgsjbhdvb fwjhbvvwhjhj"));
        data.add(new Review(5,R.drawable.album1, "ina", "wala ako maisip"));
        data.add(new Review(1,R.drawable.album1, "ina", "wala ako maisip"));
        data.add(new Review(4,R.drawable.album2, "adrian", "ang tagal maglayout svsbe rgsjbhdvb fwjhbvvwhjhj"));
        data.add(new Review(2,R.drawable.album1, "ina", "wala ako maisip"));

        return data;
    }

    //this function passes data from firebase to an Album class (albumDisplayed)
    public void setAlbumData (String albumID, int coverData){
        albumDocRef = fStore.collection("Albums").document(albumID);
        albumDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot value = task.getResult();
                    String retAlbumTitle = value.getString("Title");
                    String retArtistName = value.getString("Artist");
                    String retAlbumGenre = value.getString("Genre");
                    int retAlbumYear = value.getLong("Year").intValue();
                    float retAvgRating = value.getLong("AvgRating").floatValue();
                    int retRatingCount = value.getLong("RatingCount").intValue();

                    albumDisplayed = new Album(coverData, retAlbumTitle, retArtistName);
                    albumDisplayed.setGenre(retAlbumGenre);
                    albumDisplayed.setYear(retAlbumYear);
                    albumDisplayed.setAvgRating(retAvgRating);
                    albumDisplayed.setRatingsCount(retRatingCount);

                    setProfileViewData(coverData);
                } else {
                    Toast.makeText(AlbumProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //this function sets the data of the views on album profile
    public void setProfileViewData(int coverData){
        int cover = coverData;//albumDisplayed.getImageId();
        String name = albumDisplayed.getAlbumName();
        String artist = albumDisplayed.getArtist();

        this.imgViewAlbum.setImageResource(cover);
        this.nameViewAlbum.setText(name);
        this.artistViewAlbum.setText(artist);
    }




}
