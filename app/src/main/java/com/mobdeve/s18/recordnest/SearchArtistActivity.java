package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobdeve.s18.recordnest.adapter.ArtistAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivitySearchArtistBinding;
import com.mobdeve.s18.recordnest.model.Artist;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class SearchArtistActivity extends AppCompatActivity {

    private ActivitySearchArtistBinding binding;

    public ArtistAdapter artistAdapter;

    TextView artist;

    private FirebaseFirestore fStore;
    private ArrayList<Artist> retArtistAlphabet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchArtistBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        //firestore initialization
        fStore = FirebaseFirestore.getInstance();

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
        initArtistAlphabet();
    }

    //function to retrieve lists of starting letters from database
    public void initArtistAlphabet(){
        retArtistAlphabet = new ArrayList<>();
        fStore.collection("AlbumTags").document("ArtistAlphabetTags").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    ArrayList<String> retAlphabet = (ArrayList<String>) snapshot.get("ArtistAlphabetList");
                    //sort alphabet
                    Collections.sort(retAlphabet);
                    if(retAlphabet != null) {
                        for (int i = 0; i < retAlphabet.size(); i++) {
                            retArtistAlphabet.add(new Artist(retAlphabet.get(i)));
                        }
                        initArtistListAdapter();
                    }
                } else {
                    Toast.makeText(SearchArtistActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initArtistListAdapter(){
        artistAdapter = new ArtistAdapter(getApplicationContext(), retArtistAlphabet);

        binding.rvArtist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvArtist.setLayoutManager(lm);
        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        binding.rvArtist.setAdapter(artistAdapter);
    }
}