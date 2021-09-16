package com.mobdeve.s18.recordnest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.recordnest.R;
import com.mobdeve.s18.recordnest.model.Collection;

import java.util.ArrayList;

public class SearchCollectionAdapter extends RecyclerView.Adapter<SearchCollectionAdapter.SearchCollectionViewHolder>{
    private ArrayList<Collection> searchCollectionArrayList;
    private Context context;

    public static final String KEY_S_COLLECTION_NAME = "KEY_COLLECTION_NAME";
    public static final String KEY_COLLECTION_ID = "KEY_COLLECTION_ID";

    public SearchCollectionAdapter(Context context, ArrayList<Collection> searchCollectionArrayList) {
        this.searchCollectionArrayList = searchCollectionArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return searchCollectionArrayList.size();
    }

    @Override
    public SearchCollectionAdapter.SearchCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection, parent, false);

        SearchCollectionAdapter.SearchCollectionViewHolder viewHolder = new SearchCollectionAdapter.SearchCollectionViewHolder(view);

        return viewHolder;
    }

    public void onBindViewHolder(SearchCollectionAdapter.SearchCollectionViewHolder holder, int position) {
        holder.collection_item.setText(searchCollectionArrayList.get(position).getCollectionTitle());
    }

    protected class SearchCollectionViewHolder extends RecyclerView.ViewHolder{
        TextView collection_item;

        public SearchCollectionViewHolder(View view){
            super(view);
            collection_item = view.findViewById(R.id.search_collection_item);
        }
    }
}
