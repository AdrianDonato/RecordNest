package com.mobdeve.s18.recordnest;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgViewAlbum, imgAlbum;
    private TextView nameAlbum, artistAlbum, nameViewAlbum, artistViewAlbum;
    private StorageReference albumCoverStorage;

    public ViewHolder(@NonNull  View itemView) {
        super(itemView);

        this.imgViewAlbum =itemView.findViewById(R.id.iv_view_album);
        this.imgAlbum = itemView.findViewById(R.id.iv_view_album);

        this.nameViewAlbum = itemView.findViewById(R.id.tv_album_name);
        this.nameAlbum = itemView.findViewById(R.id.tv_album_name);

        this.artistViewAlbum = itemView.findViewById(R.id.tv_album_artist);
        this.artistAlbum = itemView.findViewById(R.id.tv_album_artist);
    }

    public void setImgViewAlbumFirebase(String imageURL){
        albumCoverStorage = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
        Glide.with(imgViewAlbum.getContext()).load(imageURL).into(imgViewAlbum);
    }

    public void setNameAlbum(String nameAlbum){
        this.nameAlbum.setText(nameAlbum);
    }


    public void setArtistAlbum(String artistAlbum){
        this.artistAlbum.setText(artistAlbum);
    }

}
