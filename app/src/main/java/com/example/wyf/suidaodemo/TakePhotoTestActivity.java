package com.example.wyf.suidaodemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TakePhotoTestActivity extends AppCompatActivity {

    private static final String TAG = TakePhotoTestActivity.class.getSimpleName();
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

    String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_test);

        ButterKnife.bind(this);

//        initData();

        takeFromCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                imageUri = getImageCropUri();
                //拍照并裁剪
//                takePhoto.onPickFromCaptureWithCrop(imageUri, cropOptions);
                //仅仅拍照不裁剪
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
//                    showExif(path);
                    decodeImage(path);
                } else {
                    Toast.makeText(TakePhotoTestActivity.this, "Path invalidate", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void decodeImage(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String lat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lon = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            double latitude = 0;
            if (lat != null) {
                latitude = changeFromgRationalToGPS(lat);
            }
            double longitude = 0;
            if (lon != null) {
                longitude = changeFromgRationalToGPS(lon);
            }

            Log.d(TAG, latitude + "  " + longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double changeFromgRationalToGPS(String rationalStr) {
        String[] parts = rationalStr.split(",");
        String[] pair;

        pair = parts[0].split("/");
        double degrees = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
        pair = parts[1].split("/");
        double minutes = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
        pair = parts[2].split("/");
        double seconds = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());

        return degrees + minutes + seconds;
    }

//    private void showExif(String path) {
//        Map<String, String> infomap = ExifInfoOperation.getExif(path);
//        String lat = infomap.get(ExifInterface.TAG_GPS_LATITUDE);
//        Toast.makeText(this, lat, Toast.LENGTH_SHORT).show();
//        System.out.println(lat);
//    }

//    private void changeExif2(String path) {
//        try {
////            Rational rational = new Rational();
//            ExifInterface exifInterface = new ExifInterface(path);
////            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "22/1");
//            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "114/1,11/1,54111328/1000000");
////            exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, "100/1");
//            exifInterface.saveAttributes();
////            String lo = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
////            System.out.println(lo);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


/*    private void initData() {
        ////获取TakePhoto实例
        takePhoto = getTakePhoto();
        //设置裁剪参数
//        cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();
        //设置压缩参数
        compressConfig = new CompressConfig.Builder().setMaxSize(50 * 1024).setMaxPixel(800).create();
        takePhoto.onEnableCompress(compressConfig, true);  //设置为需要压缩
    }*/

/*    @Override
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
    }*/

//    //获得照片的输出保存Uri
//    private Uri getImageCropUri() {
//        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
//        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
//        return Uri.fromFile(file);
//    }

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
