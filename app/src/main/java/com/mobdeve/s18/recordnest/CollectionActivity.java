package com.mobdeve.s18.recordnest;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.AlbumAdapter;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityCollectionBinding;
import com.mobdeve.s18.recordnest.model.Album;
import com.mobdeve.s18.recordnest.model.Collection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class CollectionActivity extends AppCompatActivity {

    private ActivityCollectionBinding binding;
    TextView collectionName;
    View v_share;
    View v_sort;
    Button btn_cancel_share, btn_close_sortby, btn_sort;

    public AlbumAdapter albumAdapter;
    MainActivity mainActivity;

    private FirebaseFirestore fStore;
    private FirebaseUser fUser;
    private DocumentReference collRef;
    private ArrayList<Album> retAlbums;
    private Collection retCollection;
    private String collIntentID, retSortMethod;
    private CallbackManager callbackManager;
    private ShareButton sbFBShare;
    private LoginButton lbFBlogin;
    private View view;

    Dialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCollectionBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        view = binding.getRoot();
        setContentView(view);

        //initialize fStore
        fStore = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        /*
        lbFBlogin = findViewById(R.id.lb_facebooklogin);
        sbFBShare = findViewById(R.id.sb_facebookshare);
        //initialize callback manager
        //this allows the app to communicate with facebook
        callbackManager = CallbackManager.Factory.create();
        */

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

        v_sort = view.findViewById(R.id.v_sort);

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

    //creates sharing dialog (rename na lang yung function)
    public void createAddCollectionDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View addCollPopup = getLayoutInflater().inflate(R.layout.share, null);

        btn_cancel_share = addCollPopup.findViewById(R.id.btn_cancel_share);
        lbFBlogin = addCollPopup.findViewById(R.id.lb_facebooklogin);

        //initialize callback manager
        //this allows the app to communicate with facebook
        callbackManager = CallbackManager.Factory.create();


        sbFBShare = addCollPopup.findViewById(R.id.sb_facebookshare);

        //task 1: save current activity as screenshot
        Bitmap viewSS = getScreenshot(view);
        //task 2: share to facebook
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(viewSS).build();
        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                .addPhoto(sharePhoto).build();
        sbFBShare.setShareContent(sharePhotoContent);

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

    public void createSortDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CollectionActivity.this);
        View view = getLayoutInflater().inflate(R.layout.sortby, null);

        ArrayList<String> sortList = new ArrayList<>();

        sortList.add("Title");
        sortList.add("Artist");
        sortList.add("Year");
        sortList.add("Rating");

        btn_close_sortby = view.findViewById(R.id.btn_close_sortby);
        btn_sort = view.findViewById(R.id.btn_sort);


        builder.setTitle("Spinner");
        Spinner spinner = view.findViewById((R.id.spinner_sortby));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(CollectionActivity.this,
                android.R.layout.simple_spinner_item, sortList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        if(retSortMethod.equals("AvgRating")){
            spinner.setSelection(adapter.getPosition("Rating"));
        } else {
            spinner.setSelection(adapter.getPosition(retSortMethod));
        }

        builder.setView(view);
        AlertDialog myDialog = builder.create();
        myDialog.show();

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItem().toString().equalsIgnoreCase("Rating")){
                    saveSortCollection("AvgRating");
                } else {
                    saveSortCollection(spinner.getSelectedItem().toString());
                }
            }
        });

        btn_close_sortby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
    }

    //initializes collection from the obtained collection id
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
                    String snapshotSort = snapshot.getString("SortMethod");
                    retSortMethod = snapshotSort;
                    retCollection = new Collection(snapshotCollTitle);
                    retCollection.setCollectionID(snapshotCollId);
                    retCollection.setUsername(snapshotUserID);
                    retCollection.setDescription(snapshotDesc);

                    //retAlbums = new ArrayList<>();

                    //initialize album data
                    /*
                    for(int i = 0; i < snapshotAlbums.size(); i++){

                        String retAlbumID = snapshotAlbums.get(i);
                        String retImgURL = snapshotImgs.get(i);
                        String retAlbumTitle = snapshotTitles.get(i);

                        retAlbums.add(new Album(R.drawable.album1, retAlbumTitle, "Artist"));
                        retAlbums.get(i).setAlbumID(retAlbumID);
                        retAlbums.get(i).setAlbumArtURL(retImgURL);
                    }
                    */
                    collectionName.setText(snapshotCollTitle);

                    //check if collection's owner is the same as the current user
                    //if not, then hide sort and share buttons
                    if(snapshotUserID.equals(fUser.getUid())){
                        v_share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createAddCollectionDialog();
                            }
                        });
                        v_sort.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createSortDialog();
                            }
                        });
                    } else {
                        v_share.setVisibility(View.INVISIBLE);
                        v_sort.setVisibility(View.INVISIBLE);
                    }

                    //calls a function to get list of albums from firebase
                    getAlbumsFromColl(snapshotTitles, snapshotSort);
                    //initializeAlbumAdapter();
                } else {
                    Toast.makeText(CollectionActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //used to get albums in a collection in a sorted format
    public void getAlbumsFromColl(ArrayList<String> albumIDList, String sortMethod){
        retAlbums = new ArrayList<>();
        Query collQuery;
        if(sortMethod.equals("Title")){
            collQuery = fStore.collection("Albums").whereIn("Title", albumIDList);
        } else {
            collQuery = fStore.collection("Albums").whereIn("Title", albumIDList)
                    .orderBy(sortMethod);
        }
        collQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        String retID = snapshot.getId();
                        String retTitle = snapshot.getString("Title");
                        String retImgURL = snapshot.getString("ImageURL");

                        retAlbums.add(new Album(retID, retTitle, retImgURL));
                    }
                    initializeAlbumAdapter();
                } else {
                    Toast.makeText(CollectionActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.d("FireBaseError", task.getException().getMessage());
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

    //saves the method of how the collection is sorted into the database
    public void saveSortCollection(String sortMethod){
        Map<String, Object> newSortMethod = new HashMap<>();
        newSortMethod.put("SortMethod", sortMethod);
        fStore.collection("AlbumCollection").document(collIntentID).update(newSortMethod).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CollectionActivity.this, "Collection is now sorted by " + sortMethod + ".",
                            Toast.LENGTH_SHORT).show();
                    //refreshes activity to reflect changes
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(CollectionActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //this is used for logging in to facebook and setting the share button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //task 1: save current activity as screenshot
        Bitmap viewSS = getScreenshot(view);
        //task 2: share to facebook
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(viewSS).build();
        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                .addPhoto(sharePhoto).build();
        sbFBShare.setShareContent(sharePhotoContent);
    }

    //function to get current activity into bitmap form
    public static Bitmap getScreenshot(View v){
        View rootView = v.getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return bitmap;
    }

}
