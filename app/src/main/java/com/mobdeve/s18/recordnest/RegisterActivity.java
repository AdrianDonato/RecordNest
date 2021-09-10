package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobdeve.s18.recordnest.databinding.ActivityRegisterBinding;
import com.mobdeve.s18.recordnest.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity{
    private ActivityRegisterBinding binding;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        //binding.btnLogin.setOnClickListener(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_register);



        Button btn = (Button)findViewById(R.id.btn_register2);
        EditText regUsername = (EditText)findViewById(R.id.et_register_username);
        EditText regEmail = (EditText)findViewById(R.id.et_register_email);
        EditText regPassword = (EditText)findViewById(R.id.et_register_password);
        //registerref = FirebaseDatabase.getInstance().getReference().child("User");
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String insertUser = regUsername.getText().toString().trim();
                String insertEmail = regEmail.getText().toString().trim();
                String insertPass = regPassword.getText().toString().trim();

                if(TextUtils.isEmpty(insertUser) || TextUtils.isEmpty(insertEmail) || TextUtils.isEmpty(insertPass)){
                    Toast.makeText(RegisterActivity.this, "One or more fields are empty!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(insertEmail, insertPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser newUser = mAuth.getCurrentUser();
                                String newUserID = mAuth.getUid();
                                UserProfileChangeRequest addUsername = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(insertUser).build();
                                newUser.updateProfile(addUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Map<String, Object> newUserDetails = new HashMap<>();
                                            ArrayList<String> emptyArray = new ArrayList<>();
                                            newUserDetails.put("Username", insertUser);
                                            newUserDetails.put("ProfPicURL", "");
                                            newUserDetails.put("FollowerCount", 0);
                                            newUserDetails.put("FollowingCount", 0);
                                            newUserDetails.put("FollowerList", emptyArray);
                                            newUserDetails.put("FollowingList", emptyArray);
                                            newUserDetails.put("Type", "Regular");

                                            fStore.collection("UserDetails").document(newUserID).set(newUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


    }

}
