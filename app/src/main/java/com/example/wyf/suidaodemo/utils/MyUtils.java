package com.example.wyf.suidaodemo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.WindowManager;

public class MyUtils {

    public static void get(Context context) {
        Resources resources = context.getResources();

        int resIdStatusbarHeight = resources.getIdentifier("status_bar_height", "dimen", "android");

        int statusBarHeight = 0;
        if (resIdStatusbarHeight > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resIdStatusbarHeight);//状态栏高度
        }

        int resIdShow = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean hasNavigationBar = false;
        if (resIdShow > 0) {
            hasNavigationBar = resources.getBoolean(resIdShow);//是否显示底部navigationBar
        }
        if (hasNavigationBar) {
            int resIdNavigationBar = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            int navigationbarHeight = 0;
            if (resIdNavigationBar > 0) {
                navigationbarHeight = resources.getDimensionPixelSize(resIdNavigationBar);//navigationBar高度
            }
        }
    }

}
