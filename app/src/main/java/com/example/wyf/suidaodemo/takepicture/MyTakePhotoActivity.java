package com.example.wyf.suidaodemo.takepicture;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.wyf.suidaodemo.MainActivity;
import com.example.wyf.suidaodemo.R;
import com.example.wyf.suidaodemo.database.dao.SuidaoInfoDao;
import com.example.wyf.suidaodemo.database.entity.PicItemEntity;
import com.example.wyf.suidaodemo.database.entity.SuidaoInfoEntity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.io.File.createTempFile;

/**
 * 拍摄照片界面
 */
public class MyTakePhotoActivity extends AppCompatActivity {
    public static final String TAG = MyTakePhotoActivity.class.getSimpleName();

    @BindView(R.id.sp_line)
    Spinner sp_line;
    @BindView(R.id.sp_section)
    Spinner sp_section;
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
    @BindView(R.id.rv_pictures)
    RecyclerView rv_pictures;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static Map<String, String> oldExifInfos;
    private int line_index;

    private ArrayAdapter<String> lineAdapter, sectionAdapter, tunnelAdapter;
    private List<PicItemEntity> picItemEntities = new ArrayList<>();

    private File photoFile = null;
    private PicRecyclerAdapter adapter;

    //使用三维数组实现列表的三级联动
    private String line[] = new String[]{"京昆线", "厦蓉线"};
    private String section[][] =
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        ButterKnife.bind(this);

        init();

        threeCascades();

        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btn_save_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder paths = new StringBuilder();
                if (picItemEntities.size() != 0) {
                    for (PicItemEntity entity : picItemEntities) {
                        paths.append(entity.getImagePath()).append(",");
                    }
                }
                System.out.println(paths);

                SuidaoInfoEntity infoEntity = new SuidaoInfoEntity();
                infoEntity.setLine(sp_line.getSelectedItem().toString());
                infoEntity.setSection(sp_section.getSelectedItem().toString());
                infoEntity.setTunnel(sp_tunnel.getSelectedItem().toString());
                infoEntity.setKilo(Integer.parseInt(et_kilo.getText().toString()));
                infoEntity.setMeter(Integer.parseInt(et_meter.getText().toString()));
                infoEntity.setPicpath(paths.toString());

                Log.d(TAG, oldExifInfos.get(ExifInterface.TAG_GPS_LATITUDE) + "  "
                        + oldExifInfos.get(ExifInterface.TAG_GPS_LONGITUDE));

                infoEntity.setPiclatitude(oldExifInfos.get(ExifInterface.TAG_GPS_LATITUDE));
                infoEntity.setPiclongitude(oldExifInfos.get(ExifInterface.TAG_GPS_LONGITUDE));
                @SuppressLint("SimpleDateFormat")
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                infoEntity.setCreatetime(timeStamp);

                new SuidaoInfoDao(MyTakePhotoActivity.this).insert(infoEntity);

                AlertDialog.Builder builder = new AlertDialog.Builder(MyTakePhotoActivity.this);
                builder.setTitle("保存成功");
                builder.setMessage("是否继续添加?");
                builder.setPositiveButton("继续添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_kilo.setText("");
                        et_meter.setText("");
                        while (picItemEntities.size() != 0) {
                            adapter.deleteItem(picItemEntities.size() - 1);
                        }
                        picItemEntities.clear();
                    }
                });
                builder.setNegativeButton("退出添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }
        });
    }

    /**
     * 页面初始化s
     */
    private void init() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_pictures.setLayoutManager(manager);
        adapter = new PicRecyclerAdapter(this, picItemEntities);
        rv_pictures.setAdapter(adapter);
        rv_pictures.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 三级联动的具体实现
     */
    private void threeCascades() {
        lineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, line);
        sp_line.setAdapter(lineAdapter);

        sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, section[0]);
        sp_section.setAdapter(sectionAdapter);

        tunnelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tunnel[0][0]);
        sp_tunnel.setAdapter(tunnelAdapter);

        sp_line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                line_index = position;
                sectionAdapter = new ArrayAdapter<>(MyTakePhotoActivity.this, android.R.layout.simple_list_item_1, section[position]);
                sp_section.setAdapter(sectionAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tunnelAdapter = new ArrayAdapter<>(MyTakePhotoActivity.this, android.R.layout.simple_list_item_1, tunnel[line_index][position]);
                sp_tunnel.setAdapter(tunnelAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // 创建照片文件
                photoFile = createImageFile();
                // 不同android版本获取URI的方式不一样
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
        // 使用日期进行照片的命名
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        // 照片存储位置是 根目录/suidao
        File storageDir = new File(Environment.getExternalStorageDirectory(), "/suidao/");
        if (!storageDir.exists()) storageDir.mkdirs();
        return createTempFile(imageFileName, ".jpg", storageDir);
    }

    private File createImageThumbnailFile() throws IOException {
        // 缩略图存放位置是 根目录/suidao/thumbnail
        File file = new File(photoFile.getParent(), "/thumbnail/");
        if (!file.exists()) file.mkdirs();
        file = new File(file.getAbsolutePath() + "/thumbnail_" + photoFile.getName());
        if (file.createNewFile()) {
            return file;
        } else {
            return null;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // 获取拍摄照片的exif信息，在使用bitmap转换过程中，exif信息会丢失
            oldExifInfos = ExifInfoOperation.getExif(photoFile.getAbsolutePath());

            // 向原图添加水印
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

            // 向原图插入exif信息
            oldExifInfos = ExifInfoOperation.insertExif(this, oldExifInfos, photoFile.getAbsolutePath());

            // 保存缩略图
            File finalFile = null;
            try {
                finalFile = createImageThumbnailFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final File finalFile1 = finalFile;
            Glide.with(this).load(photoFile).asBitmap().into(
                    new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            try {
                                OutputStream os = new FileOutputStream(finalFile1);
                                resource = small(resource);
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                os.close();
                                // 向缩略图插入exif信息
                                ExifInfoOperation.insertExif(MyTakePhotoActivity.this, oldExifInfos, finalFile1.getAbsolutePath());
                                Toast.makeText(MyTakePhotoActivity.this, "保存Thumbnail成功", Toast.LENGTH_SHORT).show();
                                // 保存缩略图后发送广播通知更新数据库
                                Uri uri = Uri.fromFile(finalFile1);
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                            } catch (Exception e) {
                                Log.e("TAG", "", e);
                            }
                        }
                    });

            // 保存原图后发送广播通知更新数据库
            Uri uri = Uri.fromFile(photoFile);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            System.out.println(photoFile.getParent() + "/thumbnail/thumbnail_" + photoFile.getName());


            PicItemEntity entity = null;

            // 将缩略图加载到预览页面上
            entity = new PicItemEntity("Thumb_",
                    photoFile.getParent() + "/thumbnail/thumbnail_" + photoFile.getName());
            adapter.addItem(picItemEntities.size() - 1, entity);
        } else {
            photoFile.delete();
        }
    }

    /**
     * 向原图添加水印
     *
     * @param oldBitmap
     * @return
     */
    private Bitmap addWatermark(Bitmap oldBitmap) {
        Bitmap tarBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int w = tarBitmap.getWidth();
        int h = tarBitmap.getHeight();
        Canvas canvas = new Canvas(tarBitmap);
        // 启用抗锯齿和使用设备的文本字距
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        // 字体的相关设置
        textPaint.setTextSize(50.0f);//字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.GREEN);
        textPaint.setShadowLayer(3f, 1, 1, Color.GRAY);
        // 图片上添加水印内容，水印的位置设置的是中下部3/4处
        canvas.drawText("Ai Fangfang Ai Sing", (float) (w * 0.05), (float) (h * 0.9), textPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return tarBitmap;
    }

    /**
     * 缩放图片
     *
     * @param bitmap
     * @return
     */
    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.1f, 0.1f); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
