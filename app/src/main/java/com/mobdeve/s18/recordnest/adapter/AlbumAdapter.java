package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.mobdeve.s18.recordnest.AlbumProfileActivity;
import com.mobdeve.s18.recordnest.EditAlbumActivity;
import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.ViewHolder;
import com.mobdeve.s18.recordnest.model.Album;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<ViewHolder> {

    private StorageReference albumCoverStorage;
    private ArrayList<Album> albumArrayList;
    private Context context;

    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_PICTURE = "KEY_PICTURE";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_ARTIST = "KEY_ARTIST";
    public static final String KEY_COLLECTION = "KEY_COLLECTION";
    public String collectionID;

    public AlbumAdapter(Context context, ArrayList<Album> albumArrayList) {
        this.albumArrayList = albumArrayList;
        this.context = context;
    }

    public void setCollectionID(String collID){
        this.collectionID = collID;
    }

    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.album_list, parent, false);
        //view = inflater.inflate(R.layout.tracklist, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), AlbumProfileActivity.class);

                i.putExtra(KEY_PICTURE, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getImageId());
                i.putExtra(KEY_NAME, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getAlbumName());
                i.putExtra(KEY_ARTIST, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getArtist());
                i.putExtra(KEY_ID, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getAlbumID());
                //i.putExtra(KEY_TRACK, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getTracklist());

                v.getContext().startActivity(i);
            }
        });

        //only enable long press if collectionid is not null
        if(collectionID != null) {
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //Toast.makeText(context, "long clicked " + albumArrayList.get(viewHolder.getBindingAdapterPosition()).getAlbumName(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(v.getContext(), EditAlbumActivity.class);

                    i.putExtra(KEY_PICTURE, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getImageId());
                    i.putExtra(KEY_NAME, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getAlbumName());
                    i.putExtra(KEY_ARTIST, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getArtist());
                    i.putExtra(KEY_ID, albumArrayList.get(viewHolder.getBindingAdapterPosition()).getAlbumID());
                    i.putExtra(KEY_COLLECTION, collectionID);

                    v.getContext().startActivity(i);

                    return true;

                }
            });
        }


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        //holder.setImgAlbum(this.albumArrayList.get(position).getImageId());
        holder.setImgViewAlbumFirebase(albumArrayList.get(position).getAlbumArtURL());
        holder.setNameAlbum(this.albumArrayList.get(position).getAlbumName());
        holder.setArtistAlbum(this.albumArrayList.get(position).getArtist());
        //holder.setTracklistItem(this.albumArrayList.get(position).getTracklist());

    }
}

