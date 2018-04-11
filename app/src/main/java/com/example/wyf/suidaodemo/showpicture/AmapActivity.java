package com.example.wyf.suidaodemo.showpicture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MultiPointItem;
import com.amap.api.maps.model.MultiPointOverlay;
import com.amap.api.maps.model.MultiPointOverlayOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.wyf.suidaodemo.R;
import com.example.wyf.suidaodemo.database.dao.SuidaoInfoDao;
import com.example.wyf.suidaodemo.database.entity.SuidaoInfoEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 高德地图显示界面
 */
public class AmapActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {

    private static final String TAG = AmapActivity.class.getSimpleName();
    private static final int IMAGE_REQUEST_CODE = 100;

    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;
    MyLocationStyle myLocationStyle;
    OnLocationChangedListener mListener;
    private ProgressDialog progDialog = null;
    boolean isFirstLoc = true;
    AMap aMap;
    String path;

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.btn_showexif)
    Button btn_showexif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);

        ButterKnife.bind(this);

        // 获取地图控件引用
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
            myLocationStyle = new MyLocationStyle();
            try {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeStream(this.getResources().getAssets().open("couple.png")));
                myLocationStyle.myLocationIcon(bitmapDescriptor);
            } catch (IOException e) {
                e.printStackTrace();
            }
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);//设置了定位的监听,这里要实现LocationSource接口
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            locationSettings();
        }

        btn_showexif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示项目在地图上的位置
                showAllPoint();
            }
        });
    }

    private void showAllPoint() {
        showProgressDialog();
        List<SuidaoInfoEntity> entities = new SuidaoInfoDao(AmapActivity.this).getAll();
        Log.i(TAG, "Get points");
        MultiPointOverlayOptions overlayOptions = new MultiPointOverlayOptions();
        overlayOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.point));//设置图标
        overlayOptions.anchor(0.5f, 0.5f); //设置锚点
        MultiPointOverlay multiPointOverlay = aMap.addMultiPointOverlay(overlayOptions);
        List<MultiPointItem> list = new ArrayList<>();
        // 生成坐标点
        for (SuidaoInfoEntity entity : entities) {
            double latitude = changeFromgRationalToGPS(entity.getPiclatitude());
            double longitude = changeFromgRationalToGPS(entity.getPiclongitude());
            Log.d(TAG, latitude + "  " + longitude);
            MultiPointItem multiPointItem = new MultiPointItem(coordinateConverter(latitude, longitude));
            multiPointItem.setCustomerId(String.valueOf(entity.getId()));
            list.add(multiPointItem);
        }
        // 将规范化的点集交给海量点管理对象设置，待加载完毕即可看到海量点信息
        if (multiPointOverlay != null) {
            multiPointOverlay.setItems(list);
            multiPointOverlay.setEnable(true);
        }
        dissmissProgressDialog();

        // 定义海量点点击事件
        AMap.OnMultiPointClickListener multiPointClickListener = new AMap.OnMultiPointClickListener() {
            // 海量点中某一点被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onPointClick(MultiPointItem pointItem) {
                Intent intent = new Intent(AmapActivity.this, ShowConstructionDetailActivity.class);
                intent.putExtra("pointId", pointItem.getCustomerId());
                startActivity(intent);
                return true;
            }
        };
        // 绑定海量点点击事件
        aMap.setOnMultiPointClickListener(multiPointClickListener);
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在解析添加海量点中，请稍后...");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void pictureMark() {
        if (path == null) {
            return;
        }
        LatLng latLng = decodeImage(path);

        if (latLng == null) {
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title("XXX市").snippet("XX隧道123km+123m处");

        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeFile(path)));
        Marker marker = aMap.addMarker(markerOption);
    }

    private LatLng decodeImage(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String lat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lon = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            double latitude = changeFromgRationalToGPS(lat);
            double longitude = changeFromgRationalToGPS(lon);
            Log.d(TAG, latitude + "  " + longitude);
            return coordinateConverter(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double changeFromgRationalToGPS(String rationalStr) {
        if (rationalStr == null) {
            return 0.0;
        }
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

    private LatLng coordinateConverter(double latitude, double longitude) {
        LatLng sourceLatLng = new LatLng(latitude, longitude);
        CoordinateConverter converter = new CoordinateConverter(this);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 LatLng类型
        converter.coord(sourceLatLng);
        return converter.convert();
    }

    private void locationSettings() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);

        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
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
                        pictureMark();
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
