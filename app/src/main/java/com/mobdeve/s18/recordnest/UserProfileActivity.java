package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityUserProfileBinding;
import com.mobdeve.s18.recordnest.model.Collection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;

    private CollectionAdapter collectionAdapter;

    private FirebaseUser mUser;
    private FirebaseFirestore fStore;
    private ArrayList<Collection> collArray;
    private String profileID;
    private String mUsername;
    private String mUserID;
    private TextView profileUsername;

    Button btn_edit, btn_addcol, btn_close, btn_save, btn_logout;
    EditText et_col;
    TextView tv_username, tv_followers, tv_following;
    ImageView ivProfilePic;
    LinearLayout following, followers;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        mUserID = mUser.getUid();
        mUsername = mUser.getDisplayName();

        btn_edit = findViewById(R.id.btn_edit_profile);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                startActivity(i);
            }
        });

        following = findViewById(R.id.ll_following);

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(UserProfileActivity.this, FollowingActivity.class);
                i.putExtra("USER_ID", mUserID);
                startActivity(i);
            }
        });

        followers = findViewById(R.id.ll_followers);

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(UserProfileActivity.this, FollowersActivity.class);
                startActivity(i);
            }
        });

        tv_followers = findViewById(R.id.tv_own_followers);
        tv_following = findViewById(R.id.tv_own_following);

        btn_addcol = view.findViewById(R.id.btn_add_collection);

        btn_addcol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddCollectionDialog();
            }
        });



        tv_username = view.findViewById(R.id.profile_username);
        ivProfilePic = view.findViewById(R.id.profile_userImage);
        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLogoutDialog();
            }
        });

        //sets username of profile page (not finished yet, only works on logged in user's profile)
        setProfileDataFStore();

        Intent prevPage = getIntent();
        profileID = prevPage.getStringExtra("profileUID");
        profileUsername = (TextView)findViewById(R.id.profile_username);
        profileUsername.setText(mUsername);


        if(profileID == mUserID){
            profileUsername.setText(mUser.getDisplayName());
        }

        initializeDataCollection();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        //home
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:

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

    @Override
    public void onBackPressed(){
        //super.onBackPressed();

    }

    public void createAddCollectionDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View addCollPopup = getLayoutInflater().inflate(R.layout.activity_add_collection, null);

        btn_close = addCollPopup.findViewById(R.id.btn_close_add_collection);
        btn_save = addCollPopup.findViewById(R.id.btn_save_collection);
        et_col = addCollPopup.findViewById(R.id.et_addcollection);

        dialogBuilder.setView(addCollPopup);
        dialog = dialogBuilder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_col.getText().toString().equalsIgnoreCase("")){
                   /* Toast.makeText(UserProfileActivity.this,
                            et_col.getText().toString(),
                            Toast.LENGTH_SHORT).show(); */
                    createNewCollection(et_col.getText().toString(), "placeholder");
                    dialog.dismiss();
                }

                else{
                    Toast.makeText(UserProfileActivity.this,
                            "Please enter collection name",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void createLogoutDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View addLogoutPopup = getLayoutInflater().inflate(R.layout.activity_logout, null);

        btn_close = addLogoutPopup.findViewById(R.id.btn_close_logout);
        btn_logout = addLogoutPopup.findViewById(R.id.btn_logout);

        dialogBuilder.setView(addLogoutPopup);
        dialog = dialogBuilder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.50);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);

        dialog.getWindow().setLayout(width,height);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(UserProfileActivity.this,
                        "Logged Out",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void initializeDataCollection() {
        collArray = new ArrayList<>();
        fStore.collection("AlbumCollection")
                .whereEqualTo("UserID", mUserID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                String collID = documentSnapshot.getId();
                                String collTitle = documentSnapshot.getString("Title");

                                collArray.add(new Collection(collTitle));
                                collArray.get(collArray.size()-1).setCollectionID(collID);
                            }
                            initCollAdapter();
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Error! " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //initializes collection adapter
    public void initCollAdapter(){
        collectionAdapter = new CollectionAdapter(getApplicationContext(), collArray);

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvCollection.setLayoutManager(lm);

        binding.rvCollection.setAdapter(collectionAdapter);
    }


    public void createNewCollection(String collTitle, String collDesc){
        Map<String, Object> newColl = new HashMap<>();
        ArrayList<String> newCollAlbumID = new ArrayList<>();
        ArrayList<String> newCollImgURL = new ArrayList<>();

        newColl.put("Title", collTitle);
        newColl.put("Description", collDesc);
        newColl.put("UserID", mUserID);
        newColl.put("AlbumIDList", newCollAlbumID);
        newColl.put("ImageURLList", newCollImgURL);

        fStore.collection("AlbumCollection").add(newColl).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UserProfileActivity.this, "Successfully added " + collTitle + "!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void setProfileDataFStore(){
        fStore.collection("UserDetails").document(mUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String profURL = documentSnapshot.getString("ProfPicURL");
                int followercount = documentSnapshot.getLong("FollowerCount").intValue();
                int followingcount= documentSnapshot.getLong("FollowingCount").intValue();

                if(!(profURL.equals("placeholder"))){
                    Glide.with(getApplicationContext()).load(profURL).into(ivProfilePic);
                }

                tv_followers.setText(Integer.toString(followercount));
                tv_following.setText(Integer.toString(followingcount));
            }
        });
    }
}
