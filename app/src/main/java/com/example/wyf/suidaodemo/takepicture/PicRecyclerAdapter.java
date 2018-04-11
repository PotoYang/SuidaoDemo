package com.example.wyf.suidaodemo.takepicture;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wyf.suidaodemo.R;
import com.example.wyf.suidaodemo.database.entity.PicItemEntity;

import java.util.List;

/**
 * 用于动态加载预览图片区域
 */
public class PicRecyclerAdapter extends RecyclerView.Adapter<PicRecyclerAdapter.ViewHolder> {
    private List<PicItemEntity> picItemEntities;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        TextView tv_pic;
        AppCompatImageButton btn_delete_item;
        View itemView;

        ViewHolder(View itemView) {
            super(itemView);
            iv_pic = itemView.findViewById(R.id.rv_iv_pic);
            tv_pic = itemView.findViewById(R.id.rv_tv_pic);
            btn_delete_item = itemView.findViewById(R.id.btn_delete_item);
            this.itemView = itemView;
        }
    }


    PicRecyclerAdapter(Context context, List<PicItemEntity> picItemEntities) {
        this.context = context;
        this.picItemEntities = picItemEntities;
    }

    /**
     * 用于创建ViewHolder实例的，并把加载出来的布局传入到构造函数当中，最后将viewholder的实例返回
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.take_picture_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 用于对RecyclerView子项的数据进行赋值的，会在每个子项被滚动到屏幕内的时候执行
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PicItemEntity picItemEntity = picItemEntities.get(position);
        String msg = picItemEntity.getMsg() + position;
        String imagePath = picItemEntity.getImagePath();
        // 使用Glide的这种方法类似于在异步加载图片，所以有时候需要过一会才能加载出来
        Glide.with(context).load(imagePath).asBitmap().into(holder.iv_pic);
        holder.tv_pic.setText(msg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Toast.makeText(v.getContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        // 点击删除按钮删除拍摄的图片
        holder.btn_delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return picItemEntities.size();
    }

    /**
     * 添加预览的图片
     *
     * @param position
     * @param data
     */
    public void addItem(int position, PicItemEntity data) {
        picItemEntities.add(data);
        notifyItemChanged(position);
        if (position != picItemEntities.size()) {
            notifyItemRangeChanged(position, picItemEntities.size() - position);
        }
    }

    /**
     * 删除已经拍摄的图片
     *
     * @param position
     */
    public void deleteItem(int position) {
        if (picItemEntities.size() > 0) {
            picItemEntities.remove(position);
            notifyItemRemoved(position);
            if (position != picItemEntities.size()) {
                notifyItemRangeChanged(position, picItemEntities.size() - position);
            }
        }
    }
}
