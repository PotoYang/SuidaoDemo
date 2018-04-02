package com.example.wyf.suidaodemo.takepicture;

import android.content.Context;
import android.support.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ExifInfoOperation {

    private static final String TAG = ExifInterface.class.getSimpleName();

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

            String gpsLocation = GPSInfoProvider.getInstance(context).getLocation();
            Log.i(TAG, gpsLocation);
            if (gpsLocation != null && gpsLocation.length() != 0) {
                String[] coordinates = gpsLocation.split("-");
                String latitude = changeFromgGPSToRational(coordinates[0]);
                String longitude = changeFromgGPSToRational(coordinates[1]);
                String altitude = coordinates[2] + "/1";
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitude);
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitude);
                exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, altitude);
            }
            exifInterface.saveAttributes();
            Log.d(TAG, "Success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String changeFromgGPSToRational(String gpsLocation) {
        double location = Double.parseDouble(gpsLocation);
        double degrees = Math.floor(location);
        double minutes = Math.floor((location - degrees) * 60);
        double seconds = (location - degrees) * 3600 - minutes * 60;
//        System.out.println(degrees + "/1," + minutes + "/60," + seconds + "/3600");
        return (degrees + "/1," + minutes + "/60," + seconds + "/3600");
    }
}
