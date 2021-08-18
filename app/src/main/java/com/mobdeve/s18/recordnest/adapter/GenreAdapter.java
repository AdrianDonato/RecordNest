package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Genre;

import java.util.ArrayList;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder>{

    private ArrayList<Genre> genreArrayList;
    private Context context;

    public static final String KEY_GENRE = "KEY_GENRE";

    public GenreAdapter(Context context, ArrayList<Genre> genreArrayList) {
            this.genreArrayList = genreArrayList;
            this.context = context;
    }

    @Override
    public int getItemCount() {
        return genreArrayList.size();
    }

    @Override
    public GenreAdapter.GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category, parent, false);

        GenreAdapter.GenreViewHolder viewHolder = new GenreAdapter.GenreViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(GenreAdapter.GenreViewHolder holder, int position) {

        //holder.setImgAlbum(this.albumArrayList.get(position).getImageId());
        //holder.setNameAlbum(this.albumArrayList.get(position).getAlbumName());
        //holder.setArtistAlbum(this.albumArrayList.get(position).getArtist());
        //holder.setGenreItem(this.genreArrayList.get(position).getTrackTitle());

        holder.genre_item.setText(genreArrayList.get(position).getGenre());
    }

    protected class GenreViewHolder extends RecyclerView.ViewHolder{
        TextView genre_item;

        public GenreViewHolder(View view){
            super(view);
            genre_item = view.findViewById(R.id.tv_category);
        }
    }
}
