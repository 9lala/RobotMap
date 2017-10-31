package com.android.robotmap.utils;


import com.amap.api.maps.model.LatLng;
import com.android.robotmap.App;

import org.apache.mina.core.session.IoSession;

/**
 * Created by Administrator on 2017/4/7.
 */

public class Utils {


    public static IoSession session;

    public static IoSession getSession() {
        return session;
    }

    public static void setSession(IoSession se) {
        session = se;
    }

    public static String getDeviceTag() {
        return PreferenceUtils.getString(App.ctx, Constants.MACHINE_ID);
    }

    /**
     * @param lat1 第一个纬度
     * @param lng1 第一个经度
     * @param lat2 第二个纬度
     * @param lng2 第二个经度
     * @return 两个经纬度的距离
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;//地球半径
        s = Math.round(s * 10000) / 10000;
        return s;

    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

//    public static LatLng trLatLng(final LatLng latLng) {
//        String url = "http://api.map.baidu.com/geoconv/v1/?coords=" + latLng.longitude + "," + latLng.latitude + "&from=3&to=5&ak=B129dc8290926b3c5042d75d81476dc6";
//        try {
//            URL u = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
//            connection.setDoInput(true);
//            connection.setRequestMethod("POST");
//            connection.connect();
//            InputStream in = connection.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf8"));
//            final StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//            sb.toString();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static LatLng trLatLng(LatLng latLng) {
        double x_pi = Math.PI * 3000.0 / 180.0;
        double x = latLng.longitude, y = latLng.latitude; // x代表高德经度，y代表高德纬度
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double lng = z * Math.cos(theta) + 0.0065;
        double lat = z * Math.sin(theta) + 0.006;
        return new LatLng(lat, lng);
    }

}
