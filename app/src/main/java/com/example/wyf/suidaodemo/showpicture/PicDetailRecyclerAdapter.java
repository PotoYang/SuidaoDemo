package com.example.wyf.suidaodemo.showpicture;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wyf.suidaodemo.R;

import java.util.ArrayList;
import java.util.List;

public class PicDetailRecyclerAdapter extends RecyclerView.Adapter<PicDetailRecyclerAdapter.GridViewHolder> {

    private Context context;
    private ArrayList<String> urls = new ArrayList<>();
    private LayoutInflater inflater;

    PicDetailRecyclerAdapter(Context context, List<String> strings) {
        this.context = context;
        this.urls.addAll(strings);
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail_picture_item, parent, false);

        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, final int position) {
        Glide.with(context).load(urls.get(position))
                .asBitmap().placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.iv);

        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] result = urls.get(position).split("thumbnail");
                Intent intent = new Intent(context, ShowOnePictureActivity.class);
                intent.putExtra("url", result[0] + result[2].substring(1, result[2].length()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv;

        private GridViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_item_detail);
        }
    }
}