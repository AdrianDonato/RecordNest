package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityEditAlbumBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditAlbumActivity extends AppCompatActivity {

    ActivityEditAlbumBinding binding;

    Button btn_save, btn_delete;
    private FirebaseFirestore fStore;
    private DocumentReference albumDocRef;
    private FirebaseUser mUser;

    private String mUserID;
    private String collID;
    private String albumID, albImgURL, albTitle;
    private ArrayList<String> otherCollIDs, otherCollTitles;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_album);

        binding = ActivityEditAlbumBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        fStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserID = mUser.getUid();
        setAlbumData();

        spinner = view.findViewById(R.id.spinner_move_to_collection);
        initMoveToColl();

        btn_delete = findViewById(R.id.btn_delete_album);
        btn_save = findViewById(R.id.btn_save_edit_album);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromColl();
            }
        });

        setTransferListener();

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

    public void setAlbumData(){
        Intent i = getIntent();
        albumID = i.getStringExtra(AlbumAdapter.KEY_ID);
        collID = i.getStringExtra(AlbumAdapter.KEY_COLLECTION);
        fStore.collection("Albums").document(albumID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot value = task.getResult();
                    String retAlbumTitle = value.getString("Title");
                    String retArtistName = value.getString("Artist");
                    String retAlbumGenre = value.getString("Genre");
                    String retAlbumImg = value.getString("ImageURL");
                    int retAlbumYear = value.getLong("Year").intValue();
                    int retRatingCount = value.getLong("RatingCount").intValue();
                    double retAvgRating = value.getDouble("AvgRating");

                    albTitle = retAlbumTitle;
                    albImgURL = retAlbumImg;

                    //use binding
                    binding.tvEditAlbumName.setText(retAlbumTitle);
                    binding.tvEditAlbumArtist.setText(retArtistName);
                    binding.tvEditAlbumGenre.setText(retAlbumGenre);
                    binding.tvEditAlbumYear.setText(Integer.toString(retAlbumYear));
                    binding.tvEditAlbumAvgrating.setText(String.format("%.2f", retAvgRating));

                    //don't display average rating if no review has been made yet
                    if(retRatingCount > 0) {
                        //format average to 2 decimal places only
                        binding.tvEditAlbumAvgrating.setText(String.format("%.2f", retAvgRating));
                    } else {
                        binding.tvEditAlbumAvgrating.setText("No Ratings Yet");
                    }

                    Glide.with(getApplicationContext()).load(retAlbumImg).into(binding.ivEditAlbumImg);
                } else {
                    Toast.makeText(EditAlbumActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeFromColl(){
        Map<String, Object> removeColl = new HashMap<>();

        removeColl.put("AlbumIDList", FieldValue.arrayRemove(albumID));
        removeColl.put("AlbumTitleList", FieldValue.arrayRemove(albTitle));
        removeColl.put("ImageURLList", FieldValue.arrayRemove(albImgURL));

        fStore.collection("AlbumCollection").document(collID).update(removeColl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(EditAlbumActivity.this, "Removed " + albTitle + ".",
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), CollectionActivity.class);
                    i.putExtra(CollectionAdapter.KEY_COLLECTION_NAME, collID);
                    startActivity(i);
                } else {
                    Toast.makeText(EditAlbumActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void transferColls(String targCollTitle){
        Map<String, Object> removeColl = new HashMap<>();
        removeColl.put("AlbumIDList", FieldValue.arrayRemove(albumID));
        removeColl.put("AlbumTitleList", FieldValue.arrayRemove(albTitle));
        removeColl.put("ImageURLList", FieldValue.arrayRemove(albImgURL));

        fStore.collection("AlbumCollection").document(collID).update(removeColl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()) {
                    int titleIndex = otherCollTitles.indexOf(targCollTitle);
                    String targCollID = otherCollIDs.get(titleIndex);

                    Map<String, Object> collTransfer = new HashMap<>();
                    collTransfer.put("AlbumIDList", FieldValue.arrayUnion(albumID));
                    collTransfer.put("AlbumTitleList", FieldValue.arrayUnion(albTitle));
                    collTransfer.put("ImageURLList", FieldValue.arrayUnion(albImgURL));

                    fStore.collection("AlbumCollection").document(targCollID).update(collTransfer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(EditAlbumActivity.this, "Transferred " + albTitle + " to " + targCollTitle + "!",
                                        Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), CollectionActivity.class);
                                i.putExtra(CollectionAdapter.KEY_COLLECTION_NAME, collID);
                                startActivity(i);
                            } else {
                                Toast.makeText(EditAlbumActivity.this, "Error! " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(EditAlbumActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initMoveToColl(){
        otherCollIDs = new ArrayList<>();
        otherCollTitles = new ArrayList<>();

        otherCollTitles.add("Choose a collection...");
        otherCollIDs.add("placeholder"); //filler data, ensures both arrays have same size and indexes

        fStore.collection("AlbumCollection")
                .whereEqualTo("UserID", mUserID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        if (!(collID.equals(documentSnapshot.getId()))) {
                            String retCollID = documentSnapshot.getId();
                            String retCollTitle = documentSnapshot.getString("Title");

                            otherCollTitles.add(retCollTitle);
                            otherCollIDs.add(retCollID);
                        }
                    }
                    //initialize collection listener
                    initCollSpinner();
                } else {
                    Toast.makeText(EditAlbumActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initCollSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                otherCollTitles);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void setTransferListener(){
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spinner.getSelectedItem().toString().equalsIgnoreCase("Choose a collection...")){
                    transferColls(spinner.getSelectedItem().toString());
                } else {
                    Toast.makeText(EditAlbumActivity.this, "Please choose a collection!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}