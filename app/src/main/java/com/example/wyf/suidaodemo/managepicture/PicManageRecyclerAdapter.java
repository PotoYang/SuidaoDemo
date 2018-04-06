package com.example.wyf.suidaodemo.managepicture;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wyf.suidaodemo.R;

import java.util.ArrayList;
import java.util.List;

public class PicManageRecyclerAdapter extends RecyclerView.Adapter<PicManageRecyclerAdapter.GridViewHolder> {

    private Context context;
    private ArrayList<String> urls = new ArrayList<>();

    PicManageRecyclerAdapter(Context context, List<String> strings) {
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

        holder.iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("是否删除这张照片")
                        .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteItem(position);
                            }
                        })
                        .setNegativeButton("不删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
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

    private void deleteItem(int position) {
        if (urls.size() > 0) {
            urls.remove(position);
            notifyItemRemoved(position);
            if (position != urls.size()) {
                notifyItemRangeChanged(position, urls.size() - position);
            }
        }
    }

    public ArrayList<String> getResults() {
        return urls;
    }
}