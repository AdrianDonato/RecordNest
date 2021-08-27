package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s18.recordnest.databinding.ActivityEditProfileBinding;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private Uri newPicURL;
    private String userID;
    private String newUsername, newEmail, newPassword;
    private TextView etUsername, etEmail, etPassword;
    private FirebaseUser fUser;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private Bitmap bitmap;

    ImageView iv_profilepic;
    Button btn_editpic, btn_save;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        fStorage = FirebaseStorage.getInstance();

        iv_profilepic = findViewById(R.id.iv_profile_picture);
        btn_save = findViewById(R.id.btn_save_edit_profile);
        btn_editpic = findViewById(R.id.btn_edit_profile_picture);

        etUsername = findViewById(R.id.et_edit_username);
        etEmail = findViewById(R.id.et_edit_email);
        etPassword = findViewById(R.id.et_edit_password);

        readCurrUserData();

        //setBtnSaveListener();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUsername = etUsername.getText().toString().trim();
                newEmail = etEmail.getText().toString().trim();
                if(newUsername.equals("") || newEmail.equals("")){
                    Toast.makeText(EditProfileActivity.this, "Please enter Username / Email!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    updateUserProfile();
                }
            }
        });

        btn_editpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

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
    }

    private void selectImage() {
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage;
        selectedImage = data == null ? null : data.getData();
        newPicURL = selectedImage;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            bitmap = Bitmap.createScaledBitmap(bitmap,  600 ,600, true);
            iv_profilepic.setImageBitmap(bitmap); //trying bitmap
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readCurrUserData(){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        String currUsername = fUser.getDisplayName();
        String currEmail = fUser.getEmail();
        userID = fUser.getUid();


        etUsername.setText(currUsername);
        etEmail.setText(currEmail);

        fStore.collection("UserDetails").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String picURL = documentSnapshot.getString("ProfPicURL");
                if(!(picURL.equals("placeholder"))) {
                    Glide.with(getApplicationContext()).load(picURL).into(iv_profilepic);
                } else {
                    iv_profilepic.setImageResource(R.drawable.user);
                }
            }
        });
        /*
        if(fUser.getPhotoUrl() != null){
            Uri currPic = fUser.getPhotoUrl();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currPic);
                bitmap = Bitmap.createScaledBitmap(bitmap,  600 ,600, true);
                iv_profilepic.setImageBitmap(bitmap); //trying bitmap
            } catch (IOException e) {
                e.printStackTrace();
            }
        } */
    }

    public void updateUserProfile(){
        newUsername = etUsername.getText().toString().trim();
        newPassword = etPassword.getText().toString().trim();
        newEmail = etEmail.getText().toString().trim();

        String pathLoc = "profilepics/" + userID + ".jpg";

        StorageReference storeRef = fStorage.getReference().child(pathLoc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] profPicData = baos.toByteArray();

        UploadTask uploadTask = storeRef.putBytes(profPicData);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storeRef.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri picURL = task.getResult();
                    String picURLStr = picURL.toString();

                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newUsername).setPhotoUri(picURL)
                            .build();

                    Map<String, Object> userDetailsUpdates = new HashMap<>();
                    userDetailsUpdates.put("Username", newUsername);
                    userDetailsUpdates.put("ProfPicURL", picURLStr);

                    fUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                fUser.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        fStore.collection("UserDetails").document(userID).update(userDetailsUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if(!(newPassword.equals(""))){
                                                    fUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(EditProfileActivity.this, "Successfully edited profile!",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                                                            startActivity(i);
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(EditProfileActivity.this, "Successfully edited profile!",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                                                    startActivity(i);
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Error! " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(EditProfileActivity.this, "Error! " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setBtnSaveListener(){
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUsername = etUsername.getText().toString().trim();
                newEmail = etEmail.getText().toString().trim();
                if(newUsername.equals("") || newEmail.equals("")){
                    Toast.makeText(EditProfileActivity.this, "Please enter Username / Email!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    updateUserProfile();
                }
            }
        });
    }

}
