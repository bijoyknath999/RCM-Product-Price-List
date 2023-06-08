package com.rcm.productpricelist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.activity.RecentSingleProduct;
import com.rcm.productpricelist.activity.SingleProduct;
import com.rcm.productpricelist.models.Product;
import com.rcm.productpricelist.models.Recent;
import com.rcm.productpricelist.sqlite.FavProductDBController;

import java.util.ArrayList;
import java.util.List;

public class RecentProductAdapters extends RecyclerView.Adapter<RecentProductAdapters.ViewHolder> {

    private Context context;
    private List<Recent> recentList;
    private String url;

    public RecentProductAdapters(Context context, List<Recent> recentList, String url) {
        this.context = context;
        this.recentList = recentList;
        this.url = url;
    }

    public void filterList(ArrayList<Recent> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        recentList = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecentProductAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentProductAdapters.ViewHolder holder, int position) {
        Recent recent = recentList.get(position);
        if (recent!=null)
        {

            holder.Title.setText(""+recent.getProductName());
            holder.NewMrp.setText("New MRP : ₹"+recent.getNewMrp());
            holder.NewSp.setText("SP : ₹"+recent.getNewSP());
            holder.NewPV.setText("PV: "+recent.getNewPV());
            holder.OldMrp.setText("Old MRP : ₹"+recent.getOldMrp());
            holder.OldSp.setText("SP : ₹"+recent.getOldSP());
            holder.OldPV.setText("PV: "+recent.getOldPV());
            Glide.with(context.getApplicationContext())
                    .load(recent.getImageUrl())
                    .into(holder.imageView);

            if (recent.getNewSP()>recent.getOldSP())
            {
                float increase = recent.getNewSP()-recent.getOldSP();
                holder.PriceIncrease.setText("Price Increase : ₹"+increase);
            }
            else
            {
                float increase = recent.getOldSP()-recent.getNewSP();
                holder.PriceIncrease.setText("Price Decrease : ₹"+increase);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecentSingleProduct.class);
                intent.putExtra("title", ""+recent.getProductName());
                intent.putExtra("url", ""+recent.getImageUrl());
                intent.putExtra("newmrp", ""+recent.getNewMrp());
                intent.putExtra("newsp", ""+recent.getNewSP());
                intent.putExtra("newpv", ""+recent.getNewPV());
                intent.putExtra("oldmrp", ""+recent.getOldMrp());
                intent.putExtra("oldsp", ""+recent.getOldSP());
                intent.putExtra("oldpv", ""+recent.getOldPV());
                intent.putExtra("id", ""+recent.getSrNo());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView Title, NewMrp, OldMrp, NewSp, OldSp, NewPV, OldPV, PriceIncrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.item_recent_product_title);
            imageView = itemView.findViewById(R.id.item_recent_product_img);
            NewMrp = itemView.findViewById(R.id.item_recent_product_new_mrp);
            OldMrp = itemView.findViewById(R.id.item_recent_product_old_mrp);
            NewSp = itemView.findViewById(R.id.item_recent_product_new_sp);
            OldSp = itemView.findViewById(R.id.item_recent_product_old_sp);
            NewPV = itemView.findViewById(R.id.item_recent_product_new_pv);
            OldPV = itemView.findViewById(R.id.item_recent_product_old_pv);
            PriceIncrease = itemView.findViewById(R.id.item_recent_product_price_increase);
        }
    }
}
