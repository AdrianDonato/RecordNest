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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s18.recordnest.databinding.ActivityEditProfileBinding;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;

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

        iv_profilepic = findViewById(R.id.iv_profile_picture);
        btn_save = findViewById(R.id.btn_save_edit_profile);
        btn_editpic = findViewById(R.id.btn_edit_profile_picture);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                startActivity(i);
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
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            bitmap = Bitmap.createScaledBitmap(bitmap,  600 ,600, true);
            iv_profilepic.setImageBitmap(bitmap); //trying bitmap
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
