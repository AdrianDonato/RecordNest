package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.CollectionActivity;
import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Collection;

import java.util.ArrayList;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {
    private ArrayList<Collection> collectionArrayList;
    private Context context;

    public static final String KEY_COLLECTION_NAME = "KEY_COLLECTION_NAME";

    public CollectionAdapter(Context context, ArrayList<Collection> collectionArrayList) {
        this.collectionArrayList = collectionArrayList;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return collectionArrayList.size();
    }

    @Override
    public CollectionAdapter.CollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection, parent, false);

        CollectionAdapter.CollectionViewHolder viewHolder = new CollectionAdapter.CollectionViewHolder(view);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), CollectionActivity.class);

                i.putExtra(KEY_COLLECTION_NAME, collectionArrayList.get(viewHolder.getBindingAdapterPosition()).getCollectionTitle());

                v.getContext().startActivity(i);
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CollectionAdapter.CollectionViewHolder holder, int position) {

        //holder.setImgAlbum(this.albumArrayList.get(position).getImageId());
        //holder.setNameAlbum(this.albumArrayList.get(position).getAlbumName());
        //holder.setArtistAlbum(this.albumArrayList.get(position).getArtist());
        //holder.setCollectionItem(this.collectionArrayList.get(position).getTrackTitle());

        holder.collection_item.setText(collectionArrayList.get(position).getCollectionTitle());
    }

    protected class CollectionViewHolder extends RecyclerView.ViewHolder{
        TextView collection_item;

        public CollectionViewHolder(View view){
            super(view);
            collection_item = view.findViewById(R.id.collection_item);
        }
    }
}
