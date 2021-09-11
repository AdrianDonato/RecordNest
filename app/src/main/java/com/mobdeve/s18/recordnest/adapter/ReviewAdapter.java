package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobdeve.s18.recordnest.AlbumProfileActivity;
import com.mobdeve.s18.recordnest.OtherUserProfileActivity;
import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Album;
import com.mobdeve.s18.recordnest.model.Review;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private ArrayList<Review> reviewArrayList;
    private DocumentReference usernameDocRef;
    private Context context;
    private boolean hideDelete; //if true then hide delete button
    private Album albumReviewed;

    public ReviewAdapter(Context context, ArrayList<Review> reviewArrayList) {
        this.reviewArrayList = reviewArrayList;
        this.context = context;
    }

    public void setHideDelete(boolean hideDelete){this.hideDelete = hideDelete;}

    public void setAlbumReviewed(Album albumReviewed){this.albumReviewed = albumReviewed;}

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

        Review review = reviewArrayList.get(position);
        if(hideDelete){
            holder.delete_review.setVisibility(View.GONE);
        } else {
            holder.delete_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    //final View addLogoutPopup = context.getLayoutInflater().inflate(R.layout.activity_logout, null);
                    //final View deleteDialog = View.inflate(context,R.layout.delete,null);

                    //delete_review = deleteDialog.findViewById(R.id.v_delete);
                    //btn_logout = addLogoutPopup.findViewById(R.id.btn_logout);

            /*
            dialogBuilder.setView(deleteDialog);
            myDialog = dialogBuilder.create();
            myDialog.show();

            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

             */
                    dialogBuilder.setTitle("DELETE REVIEW?");
                    dialogBuilder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialogBuilder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseFirestore.getInstance().collection("Review").document(review.getReviewIDString())
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //when a review is deleted, we need to recalculate the album's average rating
                                        Map<String, Object> albumUpdate = new HashMap<>();

                                        int newRatingCount = albumReviewed.getRatingsCount() - 1;
                                        int newAccRating = albumReviewed.getAccRatingScore() - review.getRating();
                                        double newAvgRating;
                                        if(newRatingCount > 0) {
                                            newAvgRating = (double) newAccRating / newRatingCount;
                                        } else {
                                            newAvgRating = 0; //this is to prevent a situation where 0 is divided by 0
                                        }
                                        albumUpdate.put("AccRatings", newAccRating);
                                        albumUpdate.put("AvgRating", newAvgRating);
                                        albumUpdate.put("RatingCount", newRatingCount);

                                        FirebaseFirestore.getInstance().collection("Albums")
                                                .document(albumReviewed.getAlbumID()).update(albumUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(context.getApplicationContext(), "Deleted review.",
                                                            Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                } else {
                                                    Toast.makeText(context.getApplicationContext(), "Error!" + task.getException().getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context.getApplicationContext(), "Error!" + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                    //dialogBuilder.setView(deleteDialog);
                    AlertDialog myDialog = dialogBuilder.create();
                    myDialog.show();

                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                }
            });
        }


        FirebaseFirestore.getInstance().collection("UserDetails").document(
                reviewArrayList.get(position).getUsername()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    String retUserID = snapshot.getId();
                    String retUsername = snapshot.getString("Username");
                    String retImgLink = snapshot.getString("ProfPicURL");

                    if(!(retImgLink.equals("placeholder"))){
                        Glide.with(holder.review_userImage.getContext()).load(retImgLink).into(holder.review_userImage);
                    } else {
                        holder.review_userImage.setImageResource(reviewArrayList.get(position).getUserImageId());
                    }

                    holder.review.setText(reviewArrayList.get(position).getReviewContent());
                    holder.review_username.setText(retUsername);
                    holder.rating.setText(String.valueOf(reviewArrayList.get(position).getRating()));

                    holder.review_userImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(v.getContext(), OtherUserProfileActivity.class);
                            i.putExtra("KEY_OTHER_USERID", retUserID);
                            v.getContext().startActivity(i);
                        }
                    });

                    holder.review_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(v.getContext(), OtherUserProfileActivity.class);
                            i.putExtra("KEY_OTHER_USERID", retUserID);
                            v.getContext().startActivity(i);
                        }
                    });
                }
            }
        });


    }

    protected class ReviewViewHolder extends RecyclerView.ViewHolder{
        ImageView review_userImage;
        TextView review, review_username, rating;
        View delete_review;

        public ReviewViewHolder(View view){
            super(view);
            review_userImage = view.findViewById(R.id.review_userImage);
            review = view.findViewById(R.id.review);
            review_username = view.findViewById(R.id.review_username);
            rating = view.findViewById(R.id.rating);
            delete_review = view.findViewById(R.id.v_delete);
        }

        private void showDialogDelete(View view){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context.getApplicationContext());
            //final View addLogoutPopup = context.getLayoutInflater().inflate(R.layout.activity_logout, null);
            final View deleteDialog = View.inflate(context,R.layout.delete,null);

            delete_review = deleteDialog.findViewById(R.id.v_delete);
            //btn_logout = addLogoutPopup.findViewById(R.id.btn_logout);

            /*
            dialogBuilder.setView(deleteDialog);
            myDialog = dialogBuilder.create();
            myDialog.show();

            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

             */
            dialogBuilder.setView(deleteDialog);
            AlertDialog myDialog = dialogBuilder.create();
            myDialog.show();

            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }




    }


}
