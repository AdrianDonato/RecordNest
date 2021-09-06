package com.mobdeve.s18.recordnest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s18.recordnest.databinding.ActivitySubmitAlbumBinding;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubmitAlbumActivity extends AppCompatActivity{
    private ActivitySubmitAlbumBinding binding;

    public static final String KEY_User_Document1 = "doc1";


    ImageView iv_cover;
    Button btn_upload, btn_submit, btn_tracklist;
    LinearLayout ll_tracklist;

    private int hint=0;
    private ArrayList<EditText> etTracklists;
    private EditText etAlbTitle, etAlbArtist, etAlbGenre, etAlbYear;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private Bitmap bitmap;
    private String newId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySubmitAlbumBinding.inflate(getLayoutInflater());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        View view = binding.getRoot();
        setContentView(view);

        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        etAlbTitle = findViewById(R.id.et_album_name);
        etAlbArtist = findViewById(R.id.et_album_artist);
        etAlbGenre = findViewById(R.id.et_album_genre);
        etAlbYear = findViewById(R.id.et_album_date);

        iv_cover= findViewById(R.id.iv_cover);
        btn_upload= findViewById(R.id.btn_upload_cover);
        btn_submit = findViewById(R.id.btn_submit_album);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAlbum();
                //Intent i = new Intent(SubmitAlbumActivity.this, MainActivity.class);
                //startActivity(i);
            }
        });

        btn_tracklist = findViewById(R.id.btn_addtracklist);
        ll_tracklist = findViewById(R.id.ll_tracklist);
        etTracklists = new ArrayList<>();

        btn_tracklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEditTextView();
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

    protected void createEditTextView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.setMargins(0,10,0,10);
        //create new arraylist with this
        etTracklists.add(new EditText(this));
        //edittTxt = findViewById(R.id.et_tracklist);
        int maxLength = 100;
        hint++;
        etTracklists.get(etTracklists.size()-1).setHint("Track "+hint);
        etTracklists.get(etTracklists.size()-1).setLayoutParams(params);
        // etTracklists.get(etTracklists.size()-1).setBackgroundColor(Color.WHITE);
        etTracklists.get(etTracklists.size()-1).setInputType(InputType.TYPE_CLASS_TEXT);
        etTracklists.get(etTracklists.size()-1).setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        etTracklists.get(etTracklists.size()-1).setId(hint);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        etTracklists.get(etTracklists.size()-1).setFilters(fArray);
        ll_tracklist.addView(etTracklists.get(etTracklists.size()-1));
    }

    public void submitAlbum(){

        String subTitle = etAlbTitle.getText().toString().trim();
        String subArtist = etAlbArtist.getText().toString().trim();
        String subGenre = etAlbGenre.getText().toString().trim();
        int subYear = Integer.parseInt(etAlbYear.getText().toString().trim());
        ArrayList<String> subTracklist = new ArrayList<>();

        for(int i = 0; i < etTracklists.size(); i++){
            subTracklist.add(etTracklists.get(i).getText().toString().trim());
        }

        Map<String, Object> newAlbum = new HashMap<>();
        newAlbum.put("Title", subTitle);
        newAlbum.put("Artist", subArtist);
        newAlbum.put("Genre", subGenre);
        newAlbum.put("Year", subYear);
        newAlbum.put("AccRatings", 0);
        newAlbum.put("RatingCount", 0);
        newAlbum.put("AvgRating", 0);
        newAlbum.put("Tracklist", subTracklist);
        newAlbum.put("ImageURL", "placeholder");

        fStore.collection("Albums").add(newAlbum).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                newId = documentReference.getId();

                String pathLoc = "albumcovers/" + newId + ".jpg";
                StorageReference storeRef = fStorage.getReference().child(pathLoc);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] albumCoverData = baos.toByteArray();

                UploadTask uploadTask = storeRef.putBytes(albumCoverData);

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
                            Map<String, Object> newImgURL = new HashMap<>();
                            newImgURL.put("ImageURL", picURLStr);

                            fStore.collection("Albums").document(newId).update(newImgURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Map<String, Object> addGenre = new HashMap<>();
                                    addGenre.put("GenreList", FieldValue.arrayUnion(subGenre));
                                    fStore.collection("AlbumTags").document("GenreTags").update(addGenre).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Map<String, Object> addArtistAlphabet = new HashMap<>();
                                            char newArtistAlph = subArtist.charAt(0);
                                            addArtistAlphabet.put("ArtistAlphabetList", FieldValue.arrayUnion(String.valueOf(newArtistAlph)));
                                            fStore.collection("AlbumTags").document("ArtistAlphabetTags").update(addArtistAlphabet).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SubmitAlbumActivity.this, "Successfully added " + etAlbTitle.getText().toString().trim() +"!",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(SubmitAlbumActivity.this, AlbumProfileActivity.class);
                                                    i.putExtra("KEY_ID", newId);
                                                    startActivity(i);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
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
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            bitmap = Bitmap.createScaledBitmap(bitmap,  600 ,600, true);
            iv_cover.setImageBitmap(bitmap); //trying bitmap
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
