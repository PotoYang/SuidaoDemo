package com.example.wyf.suidaodemo.takepicture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 用于提供GPS信息
 */
public class GPSInfoProvider {
    private LocationManager manager;
    private static GPSInfoProvider mGPSInfoProvider;
    private static Context context;
    private static MyLoactionListener listener;

    /**
     * 使用单例模式实现
     */
    private GPSInfoProvider() {
    }

    public static synchronized GPSInfoProvider getInstance(Context context) {
        if (mGPSInfoProvider == null) {
            synchronized (GPSInfoProvider.class) {
                if (mGPSInfoProvider == null) {
                    mGPSInfoProvider = new GPSInfoProvider();
                    GPSInfoProvider.context = context;
                }
            }
        }
        return mGPSInfoProvider;
    }


    /**
     * 获取GPS信息
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getLocation() {
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 获取当前手机最好的位置提供者
        String provider = getProvider(manager);
        // 注册位置的监听器、60000每隔一分钟获取当前位置(最大频率)、位置每改变50米重新获取位置信息
        manager.requestLocationUpdates(provider, 60000, 50, getListener());
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String location = sp.getString("location", "");
        return location;
    }

    /**
     * 停止GPS监听
     */
    public void stopGPSListener() {
        manager.removeUpdates(getListener());
    }

    private synchronized MyLoactionListener getListener() {
        if (listener == null) {
            synchronized (GPSInfoProvider.class) {
                if (listener == null) {
                    listener = new MyLoactionListener();
                }
            }

        }
        return listener;
    }

    private class MyLoactionListener implements LocationListener {
        /**
         * 当手机位置发生改变的时候 调用的方法
         */
        public void onLocationChanged(Location location) {
            //最后一次获取到的位置信息 存放到sharedpreference里面
            SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("location", location.getLatitude() + "-" + location.getLongitude() +
                    "-" + location.getAltitude());
            editor.commit();
        }

        /**
         * 某一个设备的状态发生改变的时候 调用
         * 可用->不可用
         * 不可用->可用
         * status 当前状态
         * extras 额外消息
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        /**
         * 某个设备被打开
         */
        public void onProviderEnabled(String provider) {

        }

        /**
         * 某个设备被禁用
         */
        public void onProviderDisabled(String provider) {

        }

    }

    /**
     * @param manager 位置管理服务
     * @return 最好的位置提供者
     */
    private String getProvider(LocationManager manager) {
        //设置查询条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        return manager.getBestProvider(criteria, true);
    }
}
