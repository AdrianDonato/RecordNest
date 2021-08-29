package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.SearchCollectionActivity;
import com.mobdeve.s18.recordnest.model.Genre;

import java.util.ArrayList;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder>{

    private ArrayList<Genre> genreArrayList;
    private Context context;

    public static final String KEY_GENRE_NAME = "KEY_GENRE_NAME";

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

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SearchCollectionActivity.class);

                i.putExtra("FROM_ACTIVITY", "genre");
                i.putExtra(KEY_GENRE_NAME, genreArrayList.get(viewHolder.getBindingAdapterPosition()).getGenre());
                v.getContext().startActivity(i);
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(GenreAdapter.GenreViewHolder holder, int position) {

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
