package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Year;

import java.util.ArrayList;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.YearViewHolder>{

    private ArrayList<Year> yearArrayList;
    private Context context;

    public YearAdapter(Context context, ArrayList<Year> yearArrayList) {
        this.yearArrayList = yearArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return yearArrayList.size();
    }

    @Override
    public YearAdapter.YearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category, parent, false);

        YearAdapter.YearViewHolder viewHolder = new YearAdapter.YearViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(YearAdapter.YearViewHolder holder, int position) {

        holder.year_item.setText(yearArrayList.get(position).getYear());
    }

    protected class YearViewHolder extends RecyclerView.ViewHolder{
        TextView year_item;

        public YearViewHolder(View view){
            super(view);
            year_item = view.findViewById(R.id.tv_category);
        }
    }
}