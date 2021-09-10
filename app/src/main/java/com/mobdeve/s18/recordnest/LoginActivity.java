package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.mobdeve.s18.recordnest.databinding.ActivityLoginBinding;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity{
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(R.layout.activity_login);

        Button btn_login = (Button)findViewById(R.id.btn_login);
        Button btn_reg = (Button)findViewById(R.id.btn_register);
        EditText logEmail = (EditText)findViewById(R.id.et_login_email);
        EditText logPassword = (EditText)findViewById(R.id.et_login_password);
        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authEmail = logEmail.getText().toString().trim();
                String authPass = logPassword.getText().toString().trim();

                if(TextUtils.isEmpty(authEmail) || TextUtils.isEmpty(authPass)){
                    Toast.makeText(LoginActivity.this, "One or more fields are empty!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(authEmail, authPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                 if(task.isSuccessful()){
                                     startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                 } else {
                                     Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(),
                                             Toast.LENGTH_SHORT).show();
                                 }
                        }
                    });
                }

            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }


}
