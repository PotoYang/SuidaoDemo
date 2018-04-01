package com.example.wyf.suidaodemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import net.sourceforge.jheader.App1Header;
import net.sourceforge.jheader.JpegFormatException;
import net.sourceforge.jheader.JpegHeaders;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TakePhotoTestActivity extends TakePhotoActivity {

    private static final int IMAGE_REQUEST_CODE = 100;
    //UIs
    @BindView(R.id.btn_turn_to_another_activity)
    Button turnBtn;  //跳转AnotherActivity Button
    @BindView(R.id.btn_take_from_camera)
    Button takeFromCameraBtn;  //拍照以及从相册中选取Button
    @BindView(R.id.iv_test)
    ImageView imageView;  //图片展示ImageView
    @BindView(R.id.btn_addexif)
    Button btn_addexif;

    //TakePhoto
    private TakePhoto takePhoto;
    private CropOptions cropOptions = null;  //裁剪参数
    private CompressConfig compressConfig;  //压缩参数
    private Uri imageUri;  //图片保存路径
    String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_test);

        ButterKnife.bind(this);

        initData();

        takeFromCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = getImageCropUri();
                //拍照并裁剪
//                takePhoto.onPickFromCaptureWithCrop(imageUri, cropOptions);
                //仅仅拍照不裁剪
                takePhoto.onPickFromCapture(imageUri);
            }
        });

        btn_addexif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        turnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {
                    changeExif2(path);
                } else {
                    Toast.makeText(TakePhotoTestActivity.this, "Path invalidate", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeExif2(String path) {
        try {
//            Rational rational = new Rational();
            ExifInterface exifInterface = new ExifInterface(path);
//            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "22/1");
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "114/1,11/1,54111328/1000000");
//            exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, "100/1");
            exifInterface.saveAttributes();
//            String lo = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
//            System.out.println(lo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void initData() {
        ////获取TakePhoto实例
        takePhoto = getTakePhoto();
        //设置裁剪参数
//        cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();
        //设置压缩参数
        compressConfig = new CompressConfig.Builder().setMaxSize(50 * 1024).setMaxPixel(800).create();
        takePhoto.onEnableCompress(compressConfig, true);  //设置为需要压缩
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        String iconPath = result.getImage().getOriginalPath();
        //Toast显示图片路径
        Toast.makeText(this, "imagePath:" + iconPath, Toast.LENGTH_SHORT).show();
        //Google Glide库 用于加载图片资源
        Glide.with(this).load(iconPath).into(imageView);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Toast.makeText(TakePhotoTestActivity.this, "Error:" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    //获得照片的输出保存Uri
    private Uri getImageCropUri() {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        return Uri.fromFile(file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case IMAGE_REQUEST_CODE://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex); //获取照片路径
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
