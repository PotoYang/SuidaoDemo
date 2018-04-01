package com.example.wyf.suidaodemo.picture;

import android.content.Context;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ExifInfoOperation {

    public static Map<String, String> getExif(String path) {
        Map<String, String> exifInfos = new HashMap<>();
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            Class<ExifInterface> cls = ExifInterface.class;
            Field[] fields = cls.getFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (!TextUtils.isEmpty(fieldName) && fieldName.startsWith("TAG")) {
                    String fieldValue = field.get(cls).toString();
                    String attribute = exifInterface.getAttribute(fieldValue);
                    if (attribute != null) {
                        exifInfos.put(fieldValue, attribute);
                    }

                }
            }
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
        return exifInfos;
    }

    public static void insertExif(Context context, Map<String, String> exifInfos, String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            for (Object o : exifInfos.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                exifInterface.setAttribute((String) entry.getKey(), (String) entry.getValue());
            }

            String str = GPSInfoProvider.getInstance(context).getLocation();
            if (str != null && str.length() != 0) {
                String[] strings = str.split("-");
                System.out.println(str);
                String lat = changeToFormat(strings[0]);
                String lon = changeToFormat(strings[1]);
                String alt = strings[2] + "/1";
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat);
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon);
                exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, alt);
            }
            exifInterface.saveAttributes();
            Log.d("ExifInfoOperation", "Success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String changeToFormat(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] strings = string.split("\\.");
        stringBuilder.append(strings[0]).append("/1,");
        double fen = Double.parseDouble("0." + strings[1]) * 60;
        stringBuilder.append(Math.round(fen)).append("/1,");
        double miao = (fen - Math.round(fen)) * 60;
        stringBuilder.append(Math.round(miao)).append("/1");

        return stringBuilder.toString();
    }
}
