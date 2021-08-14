package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private ArrayList<Review> reviewArrayList;
    private Context context;

    public ReviewAdapter(Context context, ArrayList<Review> reviewArrayList) {
        this.reviewArrayList = reviewArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list, parent, false);

        ReviewAdapter.ReviewViewHolder viewHolder = new ReviewAdapter.ReviewViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {

        //holder.setImgAlbum(this.albumArrayList.get(position).getImageId());
        //holder.setNameAlbum(this.albumArrayList.get(position).getAlbumName());
        //holder.setArtistAlbum(this.albumArrayList.get(position).getArtist());
        //holder.setTracklistItem(this.tracklistArrayList.get(position).getTrackTitle());

        holder.review_userImage.setImageResource(reviewArrayList.get(position).getUserImageId());
        holder.review.setText(reviewArrayList.get(position).getReviewContent());
        holder.review_username.setText(reviewArrayList.get(position).getUsername());
        holder.rating.setText(String.valueOf(reviewArrayList.get(position).getRating()));
    }

    protected class ReviewViewHolder extends RecyclerView.ViewHolder{
        ImageView review_userImage;
        TextView review, review_username, rating;

        public ReviewViewHolder(View view){
            super(view);
            review_userImage = view.findViewById(R.id.review_userImage);
            review = view.findViewById(R.id.review);
            review_username = view.findViewById(R.id.review_username);
            rating = view.findViewById(R.id.rating);
        }
    }
}
