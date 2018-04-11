package com.example.wyf.suidaodemo.showpicture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.wyf.suidaodemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowOnePictureActivity extends AppCompatActivity {

    private static final String TAG = ShowOnePictureActivity.class.getSimpleName();

    @BindView(R.id.iv_one_picture)
    SubsamplingScaleImageView iv_one_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_one_picture);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String path = intent.getStringExtra("url");

        Glide.with(this).load(path)
                .asBitmap()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv_one_picture.setImage(ImageSource.bitmap(resource));
                    }
                });
    }
}
