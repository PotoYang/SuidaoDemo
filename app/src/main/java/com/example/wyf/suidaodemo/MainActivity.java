package com.example.wyf.suidaodemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wyf.suidaodemo.managepicture.ShowPictureFolderByDayActivity;
import com.example.wyf.suidaodemo.service.GpsService;
import com.example.wyf.suidaodemo.showpicture.AmapActivity;
import com.example.wyf.suidaodemo.takepicture.MyTakePhotoActivity;
import com.example.wyf.suidaodemo.utils.PermissionUtils;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页界面
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // 这里是控件绑定
    // 就是与res -> layout -> activity_main.xml中的按钮呀等进行绑定
    // 用于处理界面上的点击操作，后面能看到的都是一样的原理
    @BindView(R.id.btn_take_photo)
    Button btn_take_photo;
    @BindView(R.id.btn_manage_photo)
    Button btn_manage_photo;
    @BindView(R.id.btn_search_photo)
    Button btn_search_photo;

    // android 6.0 之后需要在运行时动态获取权限
    // 在此一次性获取多个权限
    private final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE};

    private final int REQUEST_CODE_CAMERA = 1, REQUEST_CODE_PERMISSIONS = 2;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = this;

        // 自定义申请多个权限
        PermissionUtils.checkMorePermissions(this, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {

            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                showExplainDialog(permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.requestMorePermissions(context, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                    }
                });
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                PermissionUtils.requestMorePermissions(context, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        });

        // 跳转到拍照界面
        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyTakePhotoActivity.class);
                startActivity(intent);
            }
        });

        // 跳转到管理界面
        btn_manage_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowPictureFolderByDayActivity.class);
                startActivity(intent);
            }
        });

        // 跳转到检索界面
        btn_search_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AmapActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 解释权限的dialog
     */
    private void showExplainDialog(String[] permission, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle("申请权限")
                .setMessage("我们需要" + Arrays.toString(permission) + "权限")
                .setPositiveButton("确定", onClickListener)
                .show();
    }

    /**
     * 显示前往应用设置Dialog
     */
    private void showToAppSettingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("需要权限")
                .setMessage("我们需要相关权限，才能实现功能，点击前往，将转到应用的设置界面，请开启应用的相关权限。")
                .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.toAppSetting(context);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    /**
     * 获取权限的结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (PermissionUtils.isPermissionRequestSuccess(grantResults)) {
                    // 权限申请成功
                    return;
                } else {
                    Toast.makeText(context, "打开相机失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_PERMISSIONS:
                PermissionUtils.onRequestMorePermissionsResult(context, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        Toast.makeText(context, "我们需要" + Arrays.toString(permission) + "权限", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        Toast.makeText(context, "我们需要" + Arrays.toString(permission) + "权限", Toast.LENGTH_SHORT).show();
                        showToAppSettingDialog();
                    }
                });
        }
    }

    @Override
    protected void onResume() {
        // 检测gps
//        checkGps();
        super.onResume();
    }

    private void checkGps() {
        // 判断GPS是否可用
        if (!isGpsEnabled((LocationManager) Objects.requireNonNull(getSystemService(Context.LOCATION_SERVICE)))) {
            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("GSP当前已禁用，请在您的系统设置中启动.")
                    .setMessage("必须启动GPS,才能正常使用该App.")
                    .setPositiveButton("去启动GPS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else {
            // 启动GPS服务
            startService(new Intent(MainActivity.this, GpsService.class));
            Log.i(TAG, "MyGps 定位服务已启动");
        }
    }

    private boolean isGpsEnabled(LocationManager locationManager) {
        boolean isOpenGPS = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean isOpenNetwork = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        return isOpenGPS || isOpenNetwork;
    }
}
