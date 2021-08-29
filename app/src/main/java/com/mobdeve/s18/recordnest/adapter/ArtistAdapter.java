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
import com.mobdeve.s18.recordnest.model.Artist;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>{

    private ArrayList<Artist> artistArrayList;
    private Context context;

    public static final String KEY_ARTIST_NAME = "KEY_ARTIST_NAME";

    public ArtistAdapter(Context context, ArrayList<Artist> artistArrayList) {
        this.artistArrayList = artistArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return artistArrayList.size();
    }

    @Override
    public ArtistAdapter.ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category, parent, false);

        ArtistAdapter.ArtistViewHolder viewHolder = new ArtistAdapter.ArtistViewHolder(view);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SearchCollectionActivity.class);

                i.putExtra("FROM_ACTIVITY", "artist");
                i.putExtra(KEY_ARTIST_NAME, artistArrayList.get(viewHolder.getBindingAdapterPosition()).getArtist());
                v.getContext().startActivity(i);
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ArtistAdapter.ArtistViewHolder holder, int position) {

        //holder.setImgAlbum(this.albumArrayList.get(position).getImageId());
        //holder.setNameAlbum(this.albumArrayList.get(position).getAlbumName());
        //holder.setArtistAlbum(this.albumArrayList.get(position).getArtist());
        //holder.setArtistItem(this.artistArrayList.get(position).getTrackTitle());

        holder.artist_item.setText(artistArrayList.get(position).getArtist());
    }

    protected class ArtistViewHolder extends RecyclerView.ViewHolder{
        TextView artist_item;

        public ArtistViewHolder(View view){
            super(view);
            artist_item = view.findViewById(R.id.tv_category);
        }
    }
}
