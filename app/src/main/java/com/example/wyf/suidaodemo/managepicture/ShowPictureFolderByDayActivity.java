package com.example.wyf.suidaodemo.managepicture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.wyf.suidaodemo.R;
import com.example.wyf.suidaodemo.database.dao.SuidaoInfoDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowPictureFolderByDayActivity extends AppCompatActivity {

    private static final String TAG = ShowPictureFolderByDayActivity.class.getSimpleName();

    @BindView(R.id.rv_pic_folder_by_day)
    RecyclerView rv_pic_folder_by_day;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture_folder_by_day);
        ButterKnife.bind(this);

        List<String> folderNames = new SuidaoInfoDao(this).getFolderNameByDay();
        if (folderNames.size() != 0) {
            initRecyclerView(folderNames);
        } else {
            Toast.makeText(this, "没有照片", Toast.LENGTH_SHORT).show();
        }
    }

    private void initRecyclerView(List<String> folderNames) {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rv_pic_folder_by_day.setLayoutManager(manager);
        rv_pic_folder_by_day.setHasFixedSize(true);
        ShowPicFolderByDayRecyclerAdapter adapter = new ShowPicFolderByDayRecyclerAdapter(this, folderNames);
        rv_pic_folder_by_day.setAdapter(adapter);
    }
}
