package com.rcm.productpricelist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rcm.productpricelist.R;
import com.rcm.productpricelist.activity.NewProductActivity;
import com.rcm.productpricelist.activity.ProductActivity;
import com.rcm.productpricelist.activity.RecentActivity;
import com.rcm.productpricelist.sqlite.SearchModels;

import java.util.List;

public class SearchAdapters extends RecyclerView.Adapter<SearchAdapters.ViewHolder> {

    private List<SearchModels> searchModelsList;
    private Context context;
    private String page;

    public SearchAdapters(List<SearchModels> searchModelsList, Context context, String page) {
        this.searchModelsList = searchModelsList;
        this.context = context;
        this.page = page;
    }

    @NonNull
    @Override
    public SearchAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapters.ViewHolder holder, int position) {
        SearchModels searchModels = searchModelsList.get(position);
        if (searchModels!=null)
        {
            holder.SearchText.setText(searchModels.getSearch_text());
            holder.itemView.setOnClickListener(v -> {
                if (page.equals("product"))
                    ProductActivity.SelectSearch(searchModels.getSearch_text(),context);
                else if (page.equals("new"))
                    NewProductActivity.SelectSearch(searchModels.getSearch_text(),context);
                else if (page.equals("recent"))
                    RecentActivity.SelectSearch(searchModels.getSearch_text(),context);
            });
        }
    }

    @Override
    public int getItemCount() {
        return searchModelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView SearchText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SearchText = itemView.findViewById(R.id.item_search_history_text);
        }
    }
}
