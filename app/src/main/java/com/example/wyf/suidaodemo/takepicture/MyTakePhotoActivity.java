package com.example.wyf.suidaodemo.takepicture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import com.example.wyf.suidaodemo.R;

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

public class MyTakePhotoActivity extends AppCompatActivity {
    public static final String TAG = MyTakePhotoActivity.class.getSimpleName();

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
    @BindView(R.id.rv_pictures)
    RecyclerView rv_pictures;

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
    private static Map<String, String> oldExifInfos;

    //    List<String> tempData = new ArrayList<>();
    List<PicItemEntity> picItemEntities = new ArrayList<>();
    PicRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        ButterKnife.bind(this);

//        getData();
        initData();

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

        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btn_save_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.deleteItem(0);
            }
        });
    }

//    private void getData() {
//        PicItemEntity picItemEntity = new PicItemEntity("First", "123");
//        picItemEntities.add(picItemEntity);
//    }

    private void initData() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_pictures.setLayoutManager(manager);
        adapter = new PicRecyclerAdapter(this, picItemEntities);
        rv_pictures.setAdapter(adapter);
        rv_pictures.setItemAnimator(new DefaultItemAnimator());
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

    private File createImageThumbnailFile() throws IOException {
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

            // 向图片添加水印
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

            // 插入exif信息
            ExifInfoOperation.insertExif(this, oldExifInfos, photoFile.getAbsolutePath());

            // 重新加载图片
//            Glide.with(this).load(photoFile).override(180, 200).into(iv_pic);

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

//            System.gc();
            // 保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(photoFile);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

//            File file = new File(photoFile.getParent() + "/thumbnail/thumbnail_" + photoFile.getName());

            System.out.println(photoFile.getParent() + "/thumbnail/thumbnail_" + photoFile.getName());

//            try {
//                Bitmap future =
//                        Glide.with(this).load(photoFile).asBitmap().thumbnail(0.01f).into(100, 100).get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }

            PicItemEntity entity = null;

            entity = new PicItemEntity("Thumb" + picItemEntities.size(),
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

    @OnClick(R.id.btn_save_pic)
    public void btn_save_pic_click() {
        // 保存缩略图
//        Glide.with(this).load(photoFile).asBitmap().into(
//                new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        try {
//                            File file = createImageThumbnailFile();
//                            OutputStream os = new FileOutputStream(file);
//                            resource = small(resource);
//                            resource.compress(Bitmap.CompressFormat.JPEG, 100, os);
//                            os.close();
//                            // 向缩略图插入exif信息
//                            ExifInfoOperation.insertExif(MyTakePhotoActivity.this, oldExifInfos, file.getAbsolutePath());
//                            Toast.makeText(MyTakePhotoActivity.this, "保存Thumbnail成功", Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                            Log.e("TAG", "", e);
//                        }
//                    }
//                });
    }

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.1f, 0.1f); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
