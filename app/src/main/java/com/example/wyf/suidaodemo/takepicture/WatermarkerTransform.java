package com.example.wyf.suidaodemo.takepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.IOException;
import java.io.InputStream;

public class WatermarkerTransform extends BitmapTransformation {

    Context context;

    public WatermarkerTransform(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return watermarkCrop(toTransform);
    }

    private Bitmap watermarkCrop(Bitmap source) {
        if (source == null) return null;

        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open("couple.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap waterMark = BitmapFactory.decodeStream(inputStream);

        int w = source.getWidth();
        int h = source.getHeight();
        int ww = waterMark.getWidth();
        int wh = waterMark.getHeight();

        Canvas canvas = new Canvas(source);
        canvas.drawBitmap(source, 0, 0, null);
        canvas.drawBitmap(waterMark, w - ww - 5, h - wh - 5, null);

        return source;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}