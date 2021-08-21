package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.recordnest.adapter.CollectionAdapter;
import com.mobdeve.s18.recordnest.databinding.ActivityUserProfileBinding;
import com.mobdeve.s18.recordnest.model.Collection;
import com.mobdeve.s18.recordnest.model.Review;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;

    private CollectionAdapter collectionAdapter;

    private FirebaseUser mUser;
    private FirebaseFirestore fStore;
    private ArrayList<Collection> collArray;
    private String profileID;
    private TextView profileUsername;

    Button btn_edit;

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

        btn_edit = findViewById(R.id.btn_edit_profile);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                startActivity(i);
            }
        });

        //sets username of profile page (not finished yet, only works on logged in user's profile)
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        String mUserID = mUser.getUid();
        String mUsername = mUser.getDisplayName();
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

    public void initializeDataCollection() {
        collArray = new ArrayList<>();
        fStore.collection("AlbumCollection")
                .whereEqualTo("Username", mUser.getDisplayName()).get()
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

    public void initCollAdapter(){
        collectionAdapter = new CollectionAdapter(getApplicationContext(), collArray);

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvCollection.setLayoutManager(lm);

        binding.rvCollection.setAdapter(collectionAdapter);
    }
}
