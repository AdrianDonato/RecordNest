package com.mobdeve.s18.recordnest;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityCollectionBinding;
import com.mobdeve.s18.recordnest.model.Album;
import com.mobdeve.s18.recordnest.model.Collection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class CollectionActivity extends AppCompatActivity {

    private ActivityCollectionBinding binding;
    TextView collectionName;
    View v_share;
    Button btn_cancel_share;

    public AlbumAdapter albumAdapter;
    MainActivity mainActivity;

    private FirebaseFirestore fStore;
    private FirebaseUser fUser;
    private DocumentReference collRef;
    private ArrayList<Album> retAlbums;
    private Collection retCollection;
    private String collIntentID;

    Dialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCollectionBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        //initialize fStore
        fStore = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        this.collectionName = findViewById(R.id.collection_item);

        Intent i = getIntent();

        //String name= i.getStringExtra(CollectionAdapter.KEY_COLLECTION_NAME);

        //this.collectionName.setText(name);

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

        v_share = view.findViewById(R.id.v_share);

        v_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddCollectionDialog();
            }
        });

        /*
        albumAdapter = new AlbumAdapter(getApplicationContext(), initializeData());

        //TextView albumName = findViewById(R.id.tv_album_name);
        //albumName.setVisibility(View.VISIBLE);

        binding.rvCollectionalbum.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        //findViewById(R.id.tv_album_name).setVisibility(View.  VISIBLE);;
        binding.rvCollectionalbum.setAdapter(albumAdapter);
        */

        collIntentID = i.getStringExtra(CollectionAdapter.KEY_COLLECTION_ID);
        initializeCollection(collIntentID);
    }

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

    public void createAddCollectionDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View addCollPopup = getLayoutInflater().inflate(R.layout.share, null);

        btn_cancel_share = addCollPopup.findViewById(R.id.btn_cancel_share);
        dialogBuilder.setView(addCollPopup);
        dialog = dialogBuilder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_cancel_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void initializeCollection (String obtainedCollID){
        collRef = fStore.collection("AlbumCollection").document(obtainedCollID);
        collRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    String snapshotCollId = snapshot.getId();
                    String snapshotCollTitle = snapshot.getString("Title");
                    String snapshotUserID = snapshot.getString("UserID");
                    String snapshotDesc = snapshot.getString("Description");
                    ArrayList<String> snapshotAlbums = (ArrayList<String>) snapshot.get("AlbumIDList");
                    ArrayList<String> snapshotImgs = (ArrayList<String>) snapshot.get("ImageURLList");
                    ArrayList<String> snapshotTitles = (ArrayList<String>) snapshot.get("AlbumTitleList");


                    retCollection = new Collection(snapshotCollTitle);
                    retCollection.setCollectionID(snapshotCollId);
                    retCollection.setUsername(snapshotUserID);
                    retCollection.setDescription(snapshotDesc);

                    retAlbums = new ArrayList<>();

                    //initialize album data
                    for(int i = 0; i < snapshotAlbums.size(); i++){

                        String retAlbumID = snapshotAlbums.get(i);
                        String retImgURL = snapshotImgs.get(i);
                        String retAlbumTitle = snapshotTitles.get(i);

                        retAlbums.add(new Album(R.drawable.album1, retAlbumTitle, "Artist"));
                        retAlbums.get(i).setAlbumID(retAlbumID);
                        retAlbums.get(i).setAlbumArtURL(retImgURL);
                    }
                    collectionName.setText(snapshotCollTitle);
                    initializeAlbumAdapter();
                } else {
                    Toast.makeText(CollectionActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //function to initialize album adapter
    public void initializeAlbumAdapter(){
        albumAdapter = new AlbumAdapter(getApplicationContext(), retAlbums);
        albumAdapter.setCollectionID(collIntentID);
        albumAdapter.setOwnerID(retCollection.getUsername());
        albumAdapter.setViewerID(fUser.getUid());

        //TextView albumName = findViewById(R.id.tv_album_name);
        //albumName.setVisibility(View.VISIBLE);

        binding.rvCollectionalbum.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        //findViewById(R.id.tv_album_name).setVisibility(View.VISIBLE);;
        binding.rvCollectionalbum.setAdapter(albumAdapter);
    }



}
