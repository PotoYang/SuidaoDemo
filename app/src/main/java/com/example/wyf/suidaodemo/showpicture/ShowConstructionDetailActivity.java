package com.example.wyf.suidaodemo.showpicture;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.wyf.suidaodemo.R;
import com.example.wyf.suidaodemo.database.dao.SuidaoInfoDao;
import com.example.wyf.suidaodemo.database.entity.SuidaoInfoEntity;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 显示施工项目详情界面
 */
public class ShowConstructionDetailActivity extends AppCompatActivity {

    private static final String TAG = ShowConstructionDetailActivity.class.getSimpleName();

    @BindView(R.id.tv_detail_title)
    TextView tv_detail_title;
    @BindView(R.id.tv_detail_addr)
    TextView tv_detail_addr;
    @BindView(R.id.tv_detail_createtime)
    TextView tv_detail_createtime;
    @BindView(R.id.rv_detail_picture)
    RecyclerView rv_detail_picture;

    private ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_construction_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String pointId = intent.getStringExtra("pointId");

        SuidaoInfoEntity entity = new SuidaoInfoDao(this).queryById(Integer.parseInt(pointId));

        tv_detail_title.setText(entity.getTunnel());
        tv_detail_addr.setText(entity.getLine().concat(entity.getSection()));
        tv_detail_createtime.setText(entity.getCreatetime());

        String[] imagesPath = entity.getPicpath().split(",");
        Collections.addAll(urls, imagesPath);
        initRecyclerView();
    }

    private void initRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rv_detail_picture.setLayoutManager(manager);
        rv_detail_picture.setHasFixedSize(true);
        PicDetailRecyclerAdapter adapter = new PicDetailRecyclerAdapter(this, urls);
        rv_detail_picture.setAdapter(adapter);
    }
}
