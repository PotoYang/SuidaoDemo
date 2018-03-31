package com.example.wyf.suidaodemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.example.wyf.suidaodemo.utils.CornersTransform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyTakePhotoActivity extends AppCompatActivity {
    public static final String TAG = "MyTakePhotoActivity";

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;

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

    String mCurrentPhotoPath = "";
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

    View layout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        layout = findViewById(R.id.ll_take_photo);
        ButterKnife.bind(this);

        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent1();
            }
        });

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


    private File createImageFile() throws IOException {
        Log.d(TAG, "createImageFile");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile  image=" + image.getAbsolutePath());
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.d(TAG, "createImageFile  mCurrentPhotoPath=" + mCurrentPhotoPath);
        return image;
    }

    private void dispatchTakePictureIntent1() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                photoFile = createImageFile();

                if (photoFile != null) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
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
    }

    private void storePic() {
        Bitmap b = iv_pic.getDrawingCache();
        String dir = Environment.getExternalStorageDirectory() + "/suidao/pic/";
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Snackbar.make(layout, "外部存储未挂载", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String fileName = simpleDate.format(now.getTime());

        try {
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(dir, fileName + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(b.getHeight() + "  " + b.getWidth());
        iv_pic.setDrawingCacheEnabled(false);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(layout, R.string.permission_write_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MyTakePhotoActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(MyTakePhotoActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }
    }

    @OnClick(R.id.iv_pic)
    public void image_view() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            showCamera();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(layout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MyTakePhotoActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(MyTakePhotoActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    private void showCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(layout, R.string.permission_available_camera, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(layout, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
                }
            }
            break;
            case REQUEST_STORAGE: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(layout, R.string.permission_available_storage, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(layout, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    iv_pic.setImageBitmap(imageBitmap);
                } else {
                    Log.d(TAG, "no Bitmap return");
                }
            } else {
                Log.d(TAG, "data is null" + "  " + photoFile.getAbsolutePath());
                Glide.with(this).load(photoFile).override(300, 300)
                        .transform(new CornersTransform(this)).into(iv_pic);
            }
        }
    }
}
