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
import com.mobdeve.s18.recordnest.model.Year;

import java.util.ArrayList;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.YearViewHolder>{

    private ArrayList<Year> yearArrayList;
    private Context context;

    public static final String KEY_YEAR_NAME = "KEY_YEAR_NAME";

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

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SearchCollectionActivity.class);

                i.putExtra("FROM_ACTIVITY", "year");
                i.putExtra(KEY_YEAR_NAME, yearArrayList.get(viewHolder.getBindingAdapterPosition()).getYear());
                v.getContext().startActivity(i);
            }
        });

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