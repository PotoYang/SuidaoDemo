package com.example.wyf.suidaodemo.managepicture;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wyf.suidaodemo.R;
import com.example.wyf.suidaodemo.database.dao.SuidaoInfoDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagePicActivity extends AppCompatActivity {
    private static final String TAG = ManagePicActivity.class.getSimpleName();

    @BindView(R.id.btn_store_pic_manage)
    Button btn_store_pic_manage;
    @BindView(R.id.tv_pic_manage)
    TextView tv_pic_manage;
    @BindView(R.id.rv_pic_manage)
    RecyclerView rv_pic_manage;

    private ArrayList<String> urls = new ArrayList<>();
    PicManageRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_manage_pic);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("constructionid");
        String name = intent.getStringExtra("tunnelname");

        tv_pic_manage.setText(name);

        String path = new SuidaoInfoDao(this).getImagePathById(id);

        String[] imagesPath = path.split(",");
        Collections.addAll(urls, imagesPath);
        initRecyclerView();

        btn_store_pic_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> results = adapter.getResults();
                StringBuilder newImagesPath = new StringBuilder("");
                for (String url : results) {
                    newImagesPath.append(url).append(",");
                }
                Log.d(TAG, newImagesPath.toString());
                int result = new SuidaoInfoDao(v.getContext()).updateImagesPathById(newImagesPath.toString(), id);
                if (result == 0) {
                    new AlertDialog.Builder(ManagePicActivity.this)
                            .setTitle("保存更改失败")
                            .setMessage("请重新保存")
                            .show();
                } else {
                    new AlertDialog.Builder(ManagePicActivity.this)
                            .setTitle("保存更改成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void initRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rv_pic_manage.setLayoutManager(manager);
        rv_pic_manage.setHasFixedSize(true);
        adapter = new PicManageRecyclerAdapter(this, urls);
        rv_pic_manage.setAdapter(adapter);
    }
}
