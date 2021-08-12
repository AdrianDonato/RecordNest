package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Album;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private ArrayList<Album> albumArrayList;
    private Context context;

    public AlbumAdapter(Context context, ArrayList<Album> albumArrayList) {
        this.albumArrayList = albumArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }

    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_list, parent, false);

        AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);
        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.AlbumViewHolder holder, int position) {
        holder.iv_vinyl.setImageResource(albumArrayList.get(position).getImageId());
    }

    protected class AlbumViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_vinyl;

        public AlbumViewHolder(View view) {
            super(view);

            iv_vinyl = view.findViewById(R.id.iv_vinyl);

        }
    }

}

