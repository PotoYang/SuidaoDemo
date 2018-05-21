package com.example.wyf.suidaodemo.managepicture;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.suidaodemo.R;
import com.example.wyf.suidaodemo.database.dao.SuidaoInfoDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowPictureFolderByConstructionActivity extends AppCompatActivity {
    private static final String TAG = ShowPictureFolderByConstructionActivity.class.getSimpleName();

    @BindView(R.id.rv_pic_folder)
    RecyclerView rv_pic_folder;
    @BindView(R.id.btn_sync_pic)
    Button btn_sync_pic;
    @BindView(R.id.btn_top_back)
    Button btn_top_back;
    @BindView(R.id.tv_top_title)
    TextView tv_top_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture_folder);
        ButterKnife.bind(this);

        btn_top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_top_title.setText("按工程显示");

        Intent intent = getIntent();
        String time = intent.getStringExtra("time");

        Map<Integer, String> folderNames = new SuidaoInfoDao(this)
                .getFolderNameByConstruction(time);
        List<String> ids;
        List<String> names;
        if (folderNames.size() != 0) {
            ids = new ArrayList<>();
            names = new ArrayList<>();
            for (TreeMap.Entry entry : folderNames.entrySet()) {
                ids.add(String.valueOf(entry.getKey()));
                names.add((String) entry.getValue());
            }
            initRecyclerView(ids, names);
        } else {
            Toast.makeText(this, "没有照片", Toast.LENGTH_SHORT).show();
            btn_sync_pic.setVisibility(View.INVISIBLE);
        }


    }

    private void initRecyclerView(List<String> ids, List<String> names) {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rv_pic_folder.setLayoutManager(manager);
        rv_pic_folder.setHasFixedSize(true);
        ShowPicFolderByConstructionRecyclerAdapter adapter = new ShowPicFolderByConstructionRecyclerAdapter(this, ids, names);
        rv_pic_folder.setAdapter(adapter);
    }
}
