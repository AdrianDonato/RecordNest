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
import com.mobdeve.s18.recordnest.adapter.GenreAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivitySearchGenreBinding;
import com.mobdeve.s18.recordnest.model.Genre;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SearchGenreActivity extends AppCompatActivity {

    private ActivitySearchGenreBinding binding;

    public GenreAdapter genreAdapter;

    public ArrayList<Genre> genreList;

    public FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchGenreBinding.inflate(getLayoutInflater());

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

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

        initGenreList();
    }

    //retrieves list of genres from firebase, then calls initGenreAdapter
    public void initGenreList(){
        genreList = new ArrayList<>();
        fStore.collection("AlbumTags").document("GenreTags").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    ArrayList<String> retGenre = (ArrayList<String>) snapshot.get("GenreList");
                    for(int i = 0; i < retGenre.size(); i++){
                        genreList.add(new Genre(retGenre.get(i)));
                    }
                    initGenreAdapter();
                } else {
                    Toast.makeText(SearchGenreActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //initializes recyclerview for genres
    public void initGenreAdapter(){
        genreAdapter = new GenreAdapter(getApplicationContext(), genreList);

        binding.rvGenre.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvGenre.setLayoutManager(lm);
        binding.rvGenre.setAdapter(genreAdapter);
    }
}