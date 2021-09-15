package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.adapter.UserListAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityOtherUserProfileBinding;
import com.mobdeve.s18.recordnest.model.Collection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OtherUserProfileActivity extends AppCompatActivity {

    private ActivityOtherUserProfileBinding binding;

    private TextView username, followercount, followingcount;
    private ImageView userimg;
    private ToggleButton tbFollow, tbModerator;
    private ArrayList<Collection> collArray;
    private FirebaseFirestore fStore;
    private FirebaseUser fUser;
    private String userID, otherUserName; //id and name of other user in profile
    private int ownFollowingCount, targFollowerCount;

    CollectionAdapter collectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOtherUserProfileBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        fStore = FirebaseFirestore.getInstance();

        this.username = findViewById(R.id.tv_other_username);
        this.userimg = findViewById(R.id.iv_other_userImage);
        this.followercount = findViewById(R.id.tv_ou_followers);
        this.followingcount = findViewById(R.id.tv_ou_following);
        this.tbFollow = findViewById(R.id.btn_follow);
        this.tbModerator = findViewById(R.id.btn_set_moderator);

        //set visibility of moderator button to gone
        this.tbModerator.setVisibility(View.GONE);

        Intent i = getIntent();

        String name= i.getStringExtra(UserListAdapter.KEY_OTHER_USERNAME);
        int cover = i.getIntExtra(UserListAdapter.KEY_OTHER_USERIMG, 0);

        this.username.setText(name);
        this.userimg.setImageResource(cover);

        this.userID = i.getStringExtra(UserListAdapter.KEY_OTHER_USERID);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(userID.equals(fUser.getUid())){
            startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        }

        initializeUserData(userID);
        checkOwnFollows();

        tbFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser(tbFollow.isChecked());
            }
        });

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

        initializeDataCollection();
    }

    //retrieves the data of the selected user from the database
    public void initializeUserData(String userID){
        fStore.collection("UserDetails").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    String retUsername = snapshot.getString("Username");
                    String retImgURL = snapshot.getString("ProfPicURL");
                    String retUserType = snapshot.getString("Type");
                    int retFollowers = snapshot.getLong("FollowerCount").intValue();
                    int retFollowing = snapshot.getLong("FollowingCount").intValue();
                    targFollowerCount = retFollowers;
                    otherUserName = retUsername;
                    username.setText(retUsername);
                    followercount.setText(Integer.toString(retFollowers));
                    followingcount.setText(Integer.toString(retFollowing));

                    if(!(retImgURL.equals("placeholder"))){
                        Glide.with(getApplicationContext()).load(retImgURL).into(userimg);
                    } else {
                        userimg.setImageResource(R.drawable.user);
                    }

                    //sets the moderator toggle button based on the user's role/type
                    if(retUserType.equals("Moderator")){
                        tbModerator.setChecked(true);
                    } else {
                        tbModerator.setChecked(false);
                    }

                } else {
                    Toast.makeText(OtherUserProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initializeDataCollection() {
        collArray = new ArrayList<>();
        fStore.collection("AlbumCollection")
                .whereEqualTo("UserID", userID).get()
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
                            Toast.makeText(OtherUserProfileActivity.this, "Error! " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //initializes collection adapter
    public void initCollAdapter(){
        collectionAdapter = new CollectionAdapter(getApplicationContext(), collArray);

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvOtherCollection.setLayoutManager(lm);

        binding.rvOtherCollection.setAdapter(collectionAdapter);
    }

    //function to check if current user follows this user, also gets curr user's following count
    //also gets current user's role, if moderator, then set moderator button is visible
    public void checkOwnFollows(){
        fStore.collection("UserDetails").document(fUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    String userType = snapshot.getString("Type");

                    //set visibility and functionality of 'set moderator' toggle button
                    if(userType.equals("Moderator")){
                        tbModerator.setVisibility(View.VISIBLE);

                        tbModerator.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateRoleDb(tbModerator.isChecked());
                            }
                        });
                    }

                    ArrayList<String> followList = (ArrayList<String>) snapshot.get("FollowingList");
                    ownFollowingCount = followList.size();
                    tbFollow.setChecked(followList.contains(userID));
                } else {
                    Toast.makeText(OtherUserProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateRoleDb (boolean modOrNot){
        Map<String, Object> roleUpdate = new HashMap<>();

        if(modOrNot){
            roleUpdate.put("Type", "Moderator");
        } else {
            roleUpdate.put("Type", "Regular");
        }

        fStore.collection("UserDetails").document(userID).update(roleUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (modOrNot){
                        Toast.makeText(OtherUserProfileActivity.this, otherUserName + " has been set to Moderator.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OtherUserProfileActivity.this, otherUserName + " is no longer Moderator.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OtherUserProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void followUser(boolean followOrNot){
        Map<String, Object> addedOwnFollowing = new HashMap<>();
        Map<String, Object> addedTargFollower = new HashMap<>();

        if(followOrNot) {
            //to be added in current logged in user's following list
            addedOwnFollowing.put("FollowingList", FieldValue.arrayUnion(userID));
            ownFollowingCount = ownFollowingCount + 1;

            //to be added in the target user's follower list
            addedTargFollower.put("FollowerList", FieldValue.arrayUnion(fUser.getUid()));
            targFollowerCount = targFollowerCount + 1;
        } else {
            //to be added in current logged in user's following list
            addedOwnFollowing.put("FollowingList", FieldValue.arrayRemove(userID));
            ownFollowingCount = ownFollowingCount - 1;

            //to be added in the target user's follower list
            addedTargFollower.put("FollowerList", FieldValue.arrayRemove(fUser.getUid()));
            targFollowerCount = targFollowerCount - 1;
        }
        addedOwnFollowing.put("FollowingCount", ownFollowingCount);
        addedTargFollower.put("FollowerCount", targFollowerCount);
        //update own following count first
        fStore.collection("UserDetails").document(fUser.getUid()).update(addedOwnFollowing).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    fStore.collection("UserDetails").document(userID).update(addedTargFollower).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                if(followOrNot) {
                                    Toast.makeText(OtherUserProfileActivity.this, "You're now following " + otherUserName + "!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OtherUserProfileActivity.this, "You've unfollowed " + otherUserName + ".",
                                            Toast.LENGTH_SHORT).show();
                                }
                                followercount.setText(Integer.toString(targFollowerCount));
                            } else {
                                Toast.makeText(OtherUserProfileActivity.this, "Error! " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(OtherUserProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}