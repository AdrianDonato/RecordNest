package com.mobdeve.s18.recordnest;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import org.jetbrains.annotations.NotNull;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgViewAlbum, imgAlbum;
    private TextView nameAlbum, artistAlbum, nameViewAlbum, artistViewAlbum;

    public ViewHolder(@NonNull  View itemView) {
        super(itemView);

        this.imgViewAlbum =itemView.findViewById(R.id.iv_view_album);
        this.imgAlbum = itemView.findViewById(R.id.iv_view_album);

        this.nameViewAlbum = itemView.findViewById(R.id.tv_album_name);
        this.nameAlbum = itemView.findViewById(R.id.tv_album_name);

        this.artistViewAlbum = itemView.findViewById(R.id.tv_album_artist);
        this.artistAlbum = itemView.findViewById(R.id.tv_album_artist);

        //this.imgAlbum = itemView.findViewById(R.id.iv_vinyl);

    }

    public void setImgViewAlbum(int imgViewAlbum) {
        this.imgViewAlbum.setImageResource(imgViewAlbum);
    }

    public void setImgAlbum(int imgAlbum) {
        this.imgAlbum.setImageResource(imgAlbum);
    }

    public void setNameViewAlbum(String nameViewAlbum){
        this.nameViewAlbum.setText(nameViewAlbum);
    }

    public void setNameAlbum(String nameAlbum){
        this.nameAlbum.setText(nameAlbum);
    }


    public void setArtistViewAlbum(String artistViewAlbum){
        this.artistViewAlbum.setText(artistViewAlbum);
    }

    public void setArtistAlbum(String artistAlbum){
        this.artistAlbum.setText(artistAlbum);
    }


}
