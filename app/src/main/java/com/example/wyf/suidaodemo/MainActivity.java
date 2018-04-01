package com.example.wyf.suidaodemo;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.wyf.suidaodemo.picture.MyTakePhotoActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private Button btn_take_photo, btn_manage_photo, btn_search_photo;

    private LocationManager locationManager;
    private String locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyTakePhotoActivity.class);
                startActivity(intent);
            }
        });

        btn_manage_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakePhotoTestActivity.class);
                startActivity(intent);
            }
        });

    }

    private void init() {
        btn_take_photo = findViewById(R.id.btn_take_photo);
        btn_manage_photo = findViewById(R.id.btn_manage_photo);
        btn_search_photo = findViewById(R.id.btn_search_photo);
    }

}
