package com.mobdeve.s18.recordnest;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //task 1: save current activity as screenshot
        //for now, use a random pic
        //Bitmap testbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vinyl);
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
