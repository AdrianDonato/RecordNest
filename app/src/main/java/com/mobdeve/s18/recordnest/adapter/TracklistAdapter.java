package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Tracklist;

import java.util.ArrayList;

public class TracklistAdapter extends RecyclerView.Adapter<TracklistAdapter.TrackListViewHolder> {
    private ArrayList<Tracklist> tracklistArrayList;
    private Context context;

    public TracklistAdapter(Context context, ArrayList<Tracklist> tracklistArrayList) {
        this.tracklistArrayList = tracklistArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return tracklistArrayList.size();
    }

    @Override
    public TracklistAdapter.TrackListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracklist, parent, false);

        TrackListViewHolder viewHolder = new TrackListViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(TracklistAdapter.TrackListViewHolder holder, int position) {

        //holder.setImgAlbum(this.albumArrayList.get(position).getImageId());
        //holder.setNameAlbum(this.albumArrayList.get(position).getAlbumName());
        //holder.setArtistAlbum(this.albumArrayList.get(position).getArtist());
        //holder.setTracklistItem(this.tracklistArrayList.get(position).getTrackTitle());

        holder.tracklist_item.setText(tracklistArrayList.get(position).getTrackTitle());
    }

    protected class TrackListViewHolder extends RecyclerView.ViewHolder{
        TextView tracklist_item;

        public TrackListViewHolder(View view){
            super(view);
            tracklist_item = view.findViewById(R.id.tracklist_item);
        }
    }
}
