package com.example.wyf.suidaodemo.managepicture;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wyf.suidaodemo.R;

import java.util.ArrayList;
import java.util.List;

public class ShowPicFolderByDayRecyclerAdapter extends RecyclerView.Adapter<ShowPicFolderByDayRecyclerAdapter.GridViewHolder> {

    private Context context;
    private ArrayList<String> folderNames = new ArrayList<>();

    ShowPicFolderByDayRecyclerAdapter(Context context, List<String> folderNames) {
        this.context = context;
        this.folderNames.addAll(folderNames);
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_picture_folder_item, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, final int position) {
        holder.tv.setText(folderNames.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowPictureFolderByConstrctionActivity.class);







////////////////////////////////////////////////////////////////////////////




                intent.putExtra("time", folderNames.get(position));
                context.startActivity(intent);
            }
        });

//        Glide.with(context).load(urls.get(position))
//                .asBitmap().placeholder(R.mipmap.ic_launcher)
//                .error(R.mipmap.ic_launcher)
//                .into(holder.iv);
//
//        holder.iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String[] result = urls.get(position).split("thumbnail");
//                Intent intent = new Intent(context, ShowOnePictureActivity.class);
//                intent.putExtra("url", result[0] + result[2].substring(1, result[2].length()));
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return folderNames.size();
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