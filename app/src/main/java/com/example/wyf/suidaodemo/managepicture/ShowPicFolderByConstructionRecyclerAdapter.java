package com.example.wyf.suidaodemo.managepicture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wyf.suidaodemo.R;

import java.util.List;

public class ShowPicFolderByConstructionRecyclerAdapter extends RecyclerView.Adapter<ShowPicFolderByConstructionRecyclerAdapter.GridViewHolder> {

    private Context context;
    private List<String> ids;
    private List<String> names;

    ShowPicFolderByConstructionRecyclerAdapter(Context context, List<String> ids, List<String> names) {
        this.context = context;
        this.names = names;
        this.ids = ids;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_picture_folder_item, parent, false);
        return new GridViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(GridViewHolder holder, final int position) {
        holder.tv.setText(names.get(position) + "+" + ids.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManagePicActivity.class);
                intent.putExtra("constructionid", ids.get(position));
                intent.putExtra("tunnelname", names.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        private View view;

        private GridViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv = itemView.findViewById(R.id.tv_pic_folder_decription);
        }
    }
}