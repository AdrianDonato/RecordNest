package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityMainBinding;
import com.mobdeve.s18.recordnest.model.Album;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    private String loggedName;
    private String loggedUID;


    private ActivityMainBinding binding;
    public AlbumAdapter albumAdapter;

    public FirebaseAuth mAuth;

    RecyclerView rvData;

    Animation topAnim;

    private FirebaseFirestore fStore;
    private CollectionReference newReleases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        //firestore initialization
        fStore = FirebaseFirestore.getInstance();

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);

        rvData = findViewById(R.id.rv_datalist);

        rvData.setAnimation(topAnim);

        binding.btnMore.setOnClickListener(this);
        binding.btnSubmitAlbum.setOnClickListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser loggedUser = mAuth.getCurrentUser();
        if(loggedUser != null){
            loggedName = loggedUser.getDisplayName(); //sets user id for profile
            loggedUID = loggedUser.getUid();
        }

        //home
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.profile:
                        Intent toProfile = new Intent(getApplicationContext(),UserProfileActivity.class);
                        toProfile.putExtra("profileUID", loggedUID);
                        startActivity(toProfile);
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
        initializeData();
    }

    //initializes 6 of the most recent albums
    public void initializeData() {
        ArrayList<Album> data = new ArrayList<>();
        newReleases = fStore.collection("Albums");
        newReleases.whereEqualTo("Approved", "Yes")
                .orderBy("Year", Query.Direction.DESCENDING).limit(6).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        data.add(new Album(R.drawable.album1, documentSnapshot.getString("Title"),
                                documentSnapshot.getString("Artist")));
                        data.get(data.size()-1).setAlbumID(documentSnapshot.getId());
                        data.get(data.size()-1).setAlbumArtURL(documentSnapshot.getString("ImageURL"));
                    }
                    initializeAlbumAdapter(data);
                } else {
                    Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.d("FirestoreIndexError", task.getException().getMessage());
                }
            }
        });
    }

    public void initializeAlbumAdapter(ArrayList<Album> albums){
        albumAdapter = new AlbumAdapter(getApplicationContext(), albums);
        binding.rvDatalist.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        binding.rvDatalist.setAdapter(albumAdapter);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_more:
                Intent i = new Intent(MainActivity.this, SearchByActivity.class);
                startActivity(i);
                break;

            case R.id.btn_submit_album:
                i = new Intent(MainActivity.this, SubmitAlbumActivity.class);
                startActivity(i);
                break;
        }

    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();

    }

    @Override
    public void onResume(){
        //super.onBackPressed();

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);

        rvData = findViewById(R.id.rv_datalist);

        rvData.setAnimation(topAnim);

        super.onResume();

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}