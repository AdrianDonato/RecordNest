package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Activity;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>{

    private ArrayList<Activity> activityArrayList;
    private Context context;

    public ActivityAdapter(Context context, ArrayList<Activity> activityArrayList) {
        this.activityArrayList = activityArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return activityArrayList.size();
    }

    @Override
    public ActivityAdapter.ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recentactivity, parent, false);

        ActivityAdapter.ActivityViewHolder viewHolder = new ActivityAdapter.ActivityViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ActivityAdapter.ActivityViewHolder holder, int position) {

        holder.activity_username.setText(activityArrayList.get(position).getUsername());
        holder.activity_title.setText(activityArrayList.get(position).getTitle());
        holder.activity_content.setText(activityArrayList.get(position).getContent());
        holder.activity_date.setText(activityArrayList.get(position).getDate());
        holder.activity_icon.setImageResource(activityArrayList.get(position).getIcon());
    }

    protected class ActivityViewHolder extends RecyclerView.ViewHolder{
        TextView activity_username;
        TextView activity_title;
        TextView activity_content;
        TextView activity_date;
        ImageView activity_icon;

        public ActivityViewHolder(View view){
            super(view);
            activity_username = view.findViewById(R.id.tv_activity_username);
            activity_title = view.findViewById(R.id.tv_activity_title);
            activity_content = view.findViewById(R.id.tv_activity_content);
            activity_date = view.findViewById(R.id.tv_activity_date);
            activity_icon = view.findViewById(R.id.iv_activity_icon);
        }
    }
}
