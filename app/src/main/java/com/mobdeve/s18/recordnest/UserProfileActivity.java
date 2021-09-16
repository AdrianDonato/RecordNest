package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
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

    Button btn_edit, btn_addcol, btn_close, btn_save, btn_logout, btn_mod_submission;
    EditText et_col, et_coldesc;
    TextView tv_username, tv_followers, tv_following, tv_nocoll;
    ImageView ivProfilePic;
    LinearLayout following, followers;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        mUserID = mUser.getUid();
        mUsername = mUser.getDisplayName();

        tv_nocoll = findViewById(R.id.tv_no_collection);
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
                i.putExtra("USER_ID", mUserID);
                startActivity(i);
            }
        });

        tv_followers = findViewById(R.id.tv_own_followers);
        tv_following = findViewById(R.id.tv_own_following);

        btn_addcol = view.findViewById(R.id.btn_add_collection);
        btn_mod_submission = view.findViewById(R.id.btn_check_submissions);
        btn_mod_submission.setVisibility(View.GONE);

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

        //sets the data of profile page
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

    //creates the dialog for adding a collection
    public void createAddCollectionDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View addCollPopup = getLayoutInflater().inflate(R.layout.activity_add_collection, null);

        btn_close = addCollPopup.findViewById(R.id.btn_close_add_collection);
        btn_save = addCollPopup.findViewById(R.id.btn_save_collection);
        et_col = addCollPopup.findViewById(R.id.et_addcollection);
        et_coldesc = addCollPopup.findViewById(R.id.et_addcollection_desc);

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
                    createNewCollection(et_col.getText().toString(), et_coldesc.getText().toString());
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

    //creates dialog for logging out
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

    //retrieves user's collection from firebase
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
                                String collDesc = documentSnapshot.getString("Description");

                                collArray.add(new Collection(collTitle));
                                collArray.get(collArray.size()-1).setCollectionID(collID);
                                collArray.get(collArray.size()-1).setDescription(collDesc);
                            }
                            if(collArray.size()>0){
                                tv_nocoll.setVisibility(View.INVISIBLE);
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

    //adds a new collection to firebase
    public void createNewCollection(String collTitle, String collDesc){
        Map<String, Object> newColl = new HashMap<>();
        ArrayList<String> newCollAlbumID = new ArrayList<>();
        ArrayList<String> newCollImgURL = new ArrayList<>();
        ArrayList<String> newCollTitle = new ArrayList<>();

        newColl.put("Title", collTitle);
        newColl.put("Description", collDesc);
        newColl.put("UserID", mUserID);
        newColl.put("AlbumIDList", newCollAlbumID);
        newColl.put("AlbumTitleList", newCollTitle);
        newColl.put("ImageURLList", newCollImgURL);
        newColl.put("SortMethod", "Title"); //default sorting method

        fStore.collection("AlbumCollection").add(newColl).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    DocumentReference docRef = task.getResult();
                    //create a new activity for feed
                    Map<String, Object> newActivity = new HashMap<>();
                    newActivity.put("ActivityTitle", "created a new collection!");
                    newActivity.put("UserID", mUserID);
                    newActivity.put("ExtraContent", "Check out "+collTitle+"!");
                    newActivity.put("IntentFor", "Collection");
                    newActivity.put("IntentID", docRef.getId());

                    //get current timestamp
                    Calendar currDate = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - hh:mm aa", Locale.US);
                    String inDatePosted = sdf.format(currDate.getTime());

                    //save current timestamp to hashmap
                    newActivity.put("Date", inDatePosted);

                    //add new activity into database
                    fStore.collection("FeedActivities").add(newActivity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
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
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //sets user's profile data from firestore
    public void setProfileDataFStore(){
        fStore.collection("UserDetails").document(mUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String profURL = documentSnapshot.getString("ProfPicURL");
                String userType = documentSnapshot.getString("Type");
                int followercount = documentSnapshot.getLong("FollowerCount").intValue();
                int followingcount= documentSnapshot.getLong("FollowingCount").intValue();

                if(!(profURL.equals("placeholder"))){
                    Glide.with(getApplicationContext()).load(profURL).into(ivProfilePic);
                } else {
                    ivProfilePic.setImageResource(R.drawable.user);
                }

                //set visibility and functionality of submissions button if moderator
                if(userType.equals("Moderator")){
                    btn_mod_submission.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(UserProfileActivity.this, SearchCollectionActivity.class);
                            i.putExtra("FROM_ACTIVITY", "approval");
                            startActivity(i);
                        }
                    });
                    btn_mod_submission.setVisibility(View.VISIBLE);
                }

                tv_followers.setText(Integer.toString(followercount));
                tv_following.setText(Integer.toString(followingcount));
            }
        });
    }
}
