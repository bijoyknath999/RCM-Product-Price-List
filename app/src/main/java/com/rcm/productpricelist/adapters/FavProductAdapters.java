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
import com.rcm.productpricelist.activity.SingleProduct;
import com.rcm.productpricelist.models.Product;
import com.rcm.productpricelist.sqlite.FavProductDBController;

import java.util.ArrayList;
import java.util.List;

public class FavProductAdapters extends RecyclerView.Adapter<FavProductAdapters.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private String url;
    private String page;
    private ListItemClickListener itemClickListener;

    public FavProductAdapters(Context context, List<Product> productList, String url, String page) {
        this.context = context;
        this.productList = productList;
        this.url = url;
        this.page = page;
    }

    @NonNull
    @Override
    public FavProductAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new ViewHolder(view,itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavProductAdapters.ViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product!=null)
        {
            holder.Title.setText(product.getName());
            holder.Price.setText("MRP : ₹"+product.getMrp());
            holder.SalePrice.setText("SP : ₹"+product.getSalePrice());
            holder.PV.setText("PV: "+product.getPv());
            Glide.with(context.getApplicationContext())
                    .load(product.getImageUrl())
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SingleProduct.class);
                intent.putExtra("title", product.getName());
                intent.putExtra("url", product.getImageUrl());
                intent.putExtra("mrp", ""+product.getMrp());
                intent.putExtra("sp", ""+product.getSalePrice());
                intent.putExtra("pv", ""+product.getPv());
                intent.putExtra("id", ""+product.getSrNo());
                intent.putExtra("site_url", ""+url);
                intent.putExtra("page", ""+page);
                context.startActivity(intent);
            });


            FavProductDBController favProductDBController = new FavProductDBController(context);

            if (favProductDBController.checkFav(product.getSrNo().toString()))
            {
                Glide.with(context.getApplicationContext())
                        .load(R.drawable.ic_favourite)
                        .into(holder.FavBtn);
            }
            else
            {
                Glide.with(context.getApplicationContext())
                        .load(R.drawable.ic_unfavourite)
                        .into(holder.FavBtn);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageView, FavBtn;
        private TextView Title, Price, SalePrice, PV;

        public ViewHolder(@NonNull View itemView, ListItemClickListener itemClickListener) {
            super(itemView);
            Title = itemView.findViewById(R.id.item_product_title);
            Price = itemView.findViewById(R.id.item_product_price);
            imageView = itemView.findViewById(R.id.item_product_img);
            SalePrice = itemView.findViewById(R.id.item_product_salePrice);
            PV = itemView.findViewById(R.id.item_product_pv);
            FavBtn = itemView.findViewById(R.id.item_product_fav_btn);

            FavBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    public void setItemClickListener(ListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ListItemClickListener {
        void onItemClick(int position, View view);
    }
}
