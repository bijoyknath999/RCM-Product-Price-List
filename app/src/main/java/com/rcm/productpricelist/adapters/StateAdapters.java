package com.rcm.productpricelist.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.activity.Home;
import com.rcm.productpricelist.models.State;
import com.rcm.productpricelist.utils.Tools;

import java.util.List;

public class StateAdapters extends RecyclerView.Adapter<StateAdapters.ViewHolder> {

    private Context context;
    private List<State> stateList;

    public StateAdapters(Context context, List<State> stateList) {
        this.context = context;
        this.stateList = stateList;
    }

    @NonNull
    @Override
    public StateAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_state,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StateAdapters.ViewHolder holder, int position) {
        State state = stateList.get(position);
        if (state!=null)
        {
            Glide.with(context.getApplicationContext())
                    .load(state.getIcon())
                    .into(holder.imageView);

            holder.textView.setText(state.getName());
            holder.itemView.setOnClickListener(v -> {
                Tools.saveString(context,"state",state.getName());
                Tools.saveString(context,"state_url",state.getUrl());
                Home.checkState(context);
            });
        }
    }

    @Override
    public int getItemCount() {
        return stateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_state_img);
            textView = itemView.findViewById(R.id.item_state_text);
        }
    }
}
