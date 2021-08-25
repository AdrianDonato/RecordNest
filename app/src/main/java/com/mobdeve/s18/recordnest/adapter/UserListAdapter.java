package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.OtherUserProfileActivity;
import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.UserList;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    private ArrayList<UserList> userArrayList;
    private Context context;

    public static final String KEY_OTHER_USERNAME = "KEY_OTHER_USERNAME";
    public static final String KEY_OTHER_USERIMG = "KEY_OTHER_USERIMG";

    public UserListAdapter(Context context, ArrayList<UserList> userArrayList) {
        this.userArrayList = userArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public void filterList(ArrayList<UserList> filteredList){
        userArrayList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public UserListAdapter.UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userlist, parent, false);

        UserListAdapter.UserListViewHolder viewHolder = new UserListAdapter.UserListViewHolder(view);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), OtherUserProfileActivity.class);

                i.putExtra(KEY_OTHER_USERNAME, userArrayList.get(viewHolder.getBindingAdapterPosition()).getUserName());
                i.putExtra(KEY_OTHER_USERIMG, userArrayList.get(viewHolder.getBindingAdapterPosition()).getUserImage());
                v.getContext().startActivity(i);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserListAdapter.UserListViewHolder holder, int position) {

        holder.search_userImage.setImageResource(userArrayList.get(position).getUserImage());
        holder.search_userName.setText(userArrayList.get(position).getUserName());
    }

    protected class UserListViewHolder extends RecyclerView.ViewHolder{
        ImageView search_userImage;
        TextView search_userName;

        public UserListViewHolder(View view){
            super(view);
            search_userImage = view.findViewById(R.id.iv_search_userImage);
            search_userName = view.findViewById(R.id.tv_search_username);
        }
    }
}
