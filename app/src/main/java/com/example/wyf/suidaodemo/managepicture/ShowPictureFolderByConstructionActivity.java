package com.example.wyf.suidaodemo.managepicture;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture_folder);
        ButterKnife.bind(this);

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
