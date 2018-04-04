package com.example.wyf.suidaodemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wyf.suidaodemo.database.dao.SuidaoInfoDao;
import com.example.wyf.suidaodemo.database.entity.SuidaoInfoEntity;
import com.example.wyf.suidaodemo.receiver.MyGpsReceiver;
import com.example.wyf.suidaodemo.service.GpsService;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TakePhotoTestActivity extends AppCompatActivity implements MyGpsReceiver.IBroadcastInteraction {

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

    private MyGpsReceiver receiver = null;
    EditText editText1, editText2;

    String location = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_test);

        ButterKnife.bind(this);

        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);

        //注册广播
        receiver = new MyGpsReceiver();
        receiver.setBroadcastInteraction(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.wyf.suidaodemo.service.GpsService");
        registerReceiver(receiver, filter);

//        initData();

        takeFromCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder contentBuilder = new StringBuilder();

                // 从数据库中取出全部信息
                List<SuidaoInfoEntity> entities = new SuidaoInfoDao(TakePhotoTestActivity.this).getAll();
                if (entities != null) {
                    for (SuidaoInfoEntity entity : entities) {
                        System.out.println(entity.toString());
                    }
                } else {
                    System.out.println("Data null");
                }

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

    //获取广播数据
//    private class MyReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle bundle = intent.getExtras();
//            String lon = bundle.getString("lon");
//            String lat = bundle.getString("lat");
//            if (lon != null && !"".equals(lon) && lat != null && !"".equals(lat)) {
//                editText.setText("目前经纬度:经度：" + lon + "纬度：" + lat);
//            } else {
//                editText.setText("目前经纬度:经度：" + lon + "纬度：" + lat);
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        //注销服务
        unregisterReceiver(receiver);
        //结束服务，如果想让服务一直运行就注销此句
//        stopService(new Intent(this, GpsService.class));
        super.onDestroy();
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

    @SuppressLint("SetTextI18n")
    @Override
    public void setLat(String lat) {
        editText1.setText("纬度:" + lat);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setLng(String lng) {
        editText2.setText("经度" + lng);
    }
}
