package com.example.wyf.suidaodemo;

import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void zuobiaoConvert() {
//        double x_pi = 3.14159265358979324 * 3000.0/100.0;
//        double x =
    }

    @Test
    public void test02() {
        String gpsLocation = "22.569035026833962";
        double location = Double.parseDouble(gpsLocation);
        double degrees = Math.floor(location);
        double minutes = Math.floor((location - degrees) * 60);
        double seconds = (location - degrees) * 3600 - minutes * 60;

        System.out.println(degrees + "/1," + minutes + "/60," + seconds + "/3600");
    }

    private double test03(String rationalStr) {
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

    @Test
    public void outoo() {

    }

    @Test
    public void aMapConverter() {
        String longitude = "114.0/1,9.0/60,57.23392402671607/3600";
        String latitude = "22.0/1,34.0/60,8.526096602263806/3600";
        LatLng sourceLatLng = new LatLng(test03(latitude), test03(longitude));
        CoordinateConverter converter = new CoordinateConverter();
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 LatLng类型
        converter.coord(sourceLatLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();

        System.out.println(desLatLng.latitude + "  " + desLatLng.longitude);
    }
}