package com.example.wyf.suidaodemo.picture;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wyf.suidaodemo.R;
import com.example.wyf.suidaodemo.utils.PermissionUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.io.File.createTempFile;

public class MyTakePhotoActivity extends AppCompatActivity {
    public static final String TAG = "MyTakePhotoActivity";
//
//    private static final int REQUEST_CAMERA = 0;
//    private static final int REQUEST_STORAGE = 1;

    private Context mContext;
    // 相机权限、多个权限
    private final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.INTERNET};

    // 打开相机请求Code，多个权限请求Code
    private final int REQUEST_CODE_CAMERA = 1, REQUEST_CODE_PERMISSIONS = 2;


    @BindView(R.id.sp_section_1)
    Spinner sp_section_1;
    @BindView(R.id.sp_section_2)
    Spinner sp_section_2;
    @BindView(R.id.sp_tunnel)
    Spinner sp_tunnel;
    @BindView(R.id.et_kilo)
    EditText et_kilo;
    @BindView(R.id.et_meter)
    EditText et_meter;
    @BindView(R.id.iv_pic)
    ImageView iv_pic;
    @BindView(R.id.btn_save_pic)
    Button btn_save_pic;
    @BindView(R.id.ll_take_photo)
    View layout;

//    View layout = null;
//    layout = findViewById(R.id.ll_take_photo);

    public static final int REQUEST_TAKE_PHOTO = 0;
    File photoFile = null;

    //使用三维数组实现列表的三级联动
    private String section_1[] = new String[]{"京昆线", "厦蓉线"};
    private String section_2[][] =
            {
                    {"北京段", "河北段", "河南段", "陕西段", "四川段", "云南段"},
                    {"四川段", "重庆段", "湖北段", "安徽段", "浙江段", "上海段"}
            };
    private String tunnel[][][] =
            {
                    {
                            {"八达岭隧道", "香山隧道"}, {"李家山隧道", "邯郸坡隧道"}, {"李隧道", "邯郸坡隧道"},
                            {"山隧道", "邯坡隧道"}, {"李山隧道", "郸坡隧道"}, {"李家隧道", "坡隧道"}
                    },
                    {
                            {"山隧道", "邯坡隧道"}, {"李山隧道", "郸坡隧道"}, {"李家隧道", "坡隧道"},
                            {"八达岭隧道", "香山隧道"}, {"李家山隧道", "邯郸坡隧道"}, {"李隧道", "邯郸坡隧道"}
                    }
            };

    ArrayAdapter<String> adapter01, adapter02, adapter03;
    private int section_1_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        mContext = this;

        ButterKnife.bind(this);

        adapter01 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, section_1);
        sp_section_1.setAdapter(adapter01);

        adapter02 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, section_2[0]);
        sp_section_2.setAdapter(adapter02);

        adapter03 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tunnel[0][0]);
        sp_tunnel.setAdapter(adapter03);

        sp_section_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                section_1_index = position;
                adapter02 = new ArrayAdapter<>(MyTakePhotoActivity.this, android.R.layout.simple_list_item_1, section_2[position]);
                sp_section_2.setAdapter(adapter02);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_section_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter03 = new ArrayAdapter<>(MyTakePhotoActivity.this, android.R.layout.simple_list_item_1, tunnel[section_1_index][position]);
                sp_tunnel.setAdapter(adapter03);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.iv_pic)
    public void image_view() {
        // 自定义申请多个权限
        PermissionUtils.checkMorePermissions(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                openCamera();
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                showExplainDialog(permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.requestMorePermissions(mContext, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                    }
                });
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                PermissionUtils.requestMorePermissions(mContext, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        });
    }

    /**
     * 解释权限的dialog
     */
    private void showExplainDialog(String[] permission, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(mContext)
                .setTitle("申请权限")
                .setMessage("我们需要" + Arrays.toString(permission) + "权限")
                .setPositiveButton("确定", onClickListener)
                .show();
    }

    /**
     * 显示前往应用设置Dialog
     */
    private void showToAppSettingDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("需要权限")
                .setMessage("我们需要相关权限，才能实现功能，点击前往，将转到应用的设置界面，请开启应用的相关权限。")
                .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.toAppSetting(mContext);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                photoFile = createImageFile();

                //  不同版本获取URI的方式不一样
                Uri photoURI = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    photoURI = FileProvider.getUriForFile(getBaseContext().getApplicationContext(),
                            "com.example.wyf.suidaodemo.fileprovider", photoFile);
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory(), "/suidao/");
        if (!storageDir.exists()) storageDir.mkdirs();
        return createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //  向照片的exif添加GPS信息
//            ExifInfoOperation.changeExifInfo(this, photoFile.getAbsolutePath());
            Map<String, String> oldExifInfos = ExifInfoOperation.getExif(photoFile.getAbsolutePath());

//            System.gc();
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            bitmap = addWatermark(bitmap);
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(photoFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ExifInfoOperation.insertExif(this, oldExifInfos, photoFile.getAbsolutePath());

            //  重新加载图片
//            Glide.with(this).load(photoFile).override(300, 300).transform(new WatermarkerTransform(this)).into(iv_pic);
            Glide.with(this).load(photoFile).override(300, 300).into(iv_pic);

//            Glide.with(this).load(photoFile).asBitmap().into(
//                    new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(
//                                Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            resource = addWatermark(resource);
//                        }
//                    });


            //  保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(photoFile);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } else {
            photoFile.delete();
        }
    }

    private Bitmap addWatermark(Bitmap oldBitmap) {
        Bitmap tarBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int w = tarBitmap.getWidth();
        int h = tarBitmap.getHeight();
        Canvas canvas = new Canvas(tarBitmap);
        //启用抗锯齿和使用设备的文本字距
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        //字体的相关设置
        textPaint.setTextSize(50.0f);//字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.GREEN);
        textPaint.setShadowLayer(3f, 1, 1, Color.GRAY);
        //图片上添加水印的位置，这里设置的是中下部3/4处
        canvas.drawText("Ai Fangfang Ai Sing", (float) (w * 0.05), (float) (h * 0.9), textPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return tarBitmap;
    }

//    private void requestCameraPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//            Snackbar.make(layout, R.string.permission_camera_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat.requestPermissions(MyTakePhotoActivity.this,
//                                    new String[]{Manifest.permission.CAMERA},
//                                    REQUEST_CAMERA);
//                        }
//                    })
//                    .show();
//        } else {
//            ActivityCompat.requestPermissions(MyTakePhotoActivity.this,
//                    new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA);
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (PermissionUtils.isPermissionRequestSuccess(grantResults)) {
                    // 权限申请成功
                    openCamera();
                } else {
                    Toast.makeText(mContext, "打开相机失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_PERMISSIONS:
                PermissionUtils.onRequestMorePermissionsResult(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        openCamera();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        Toast.makeText(mContext, "我们需要" + Arrays.toString(permission) + "权限", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        Toast.makeText(mContext, "我们需要" + Arrays.toString(permission) + "权限", Toast.LENGTH_SHORT).show();
                        showToAppSettingDialog();
                    }
                });
        }
    }


    @OnClick(R.id.btn_save_pic)
    public void btn_save_pic_click() {
//        StringBuilder info = new StringBuilder();
//        info = info.append(sp_section_1.getSelectedItem().toString())
//                .append(sp_section_2.getSelectedItem().toString())
//                .append(sp_tunnel.getSelectedItem().toString())
//                .append(et_kilo.getText().toString())
//                .append("km+")
//                .append(et_meter.getText().toString())
//                .append("m");
//        Toast.makeText(this, info.toString(), Toast.LENGTH_SHORT).show();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestStoragePermission();
//        } else {
//            storePic();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile().toString(); //获取路径
        String fileName = new Date().getTime() + ".jpg";//定义文件名
        File file = new File(path, fileName);
        if (!file.getParentFile().exists()) {//文件夹不存在
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 100);//takePhotoRequestCode是自己定义的一个请求码
    }
//    }

//    private void requestStoragePermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Snackbar.make(layout, R.string.permission_write_storage_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat.requestPermissions(MyTakePhotoActivity.this,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                    REQUEST_STORAGE);
//                        }
//                    })
//                    .show();
//        } else {
//            ActivityCompat.requestPermissions(MyTakePhotoActivity.this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_STORAGE);
//        }
//    }

}
