package com.android.robotmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.robotmap.server.MapService;
import com.android.robotmap.utils.AMapUtil;
import com.android.robotmap.utils.CustomInfoWindowAdapter;
import com.android.robotmap.utils.PointLocation;
import com.android.robotmap.utils.ThreadPoolUtils;
import com.android.robotmap.utils.Utils;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends FragmentActivity implements
        GeoFenceListener {

    MapView mMapView = null;
    private AMap aMap;
    private GeocodeSearch geocoderSearch;
    private String addressName = "深圳市";
    private Marker marker;
    private UiSettings mUiSettings;
    private String latStr = "22.5471260000", lonStr = "114.0642840000";
    public static final int PORT = 7000;//监听的端口号
    private EditText etSearch;
    private double clickLat, clickLon;
    private RadioGroup radioGroup;
    private RadioButton fixed, patrol;
    private LinearLayout llPatrol;
    private boolean isAddGeoFece = false;
    // 多边形围栏的边界点
    private List<LatLng> polygonPoints = new LinkedList<>();

    private List<Marker> markerList = new ArrayList<Marker>();

    // 当前的坐标点集合，主要用于进行地图的可视区域的缩放
    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

    private BitmapDescriptor bitmap = null;
    private MarkerOptions markerOption = null;

    // 地理围栏客户端
    private GeoFenceClient fenceClient = null;

    // 触发地理围栏的行为，默认为进入提醒
    private int activatesAction = GeoFenceClient.GEOFENCE_IN;
    // 地理围栏的广播action
    private static final String GEOFENCE_BROADCAST_ACTION = "com.example.geofence.polygon";

    // 记录已经添加成功的围栏
    private HashMap<String, GeoFence> fenceMap = new HashMap<String, GeoFence>();

    // 记录打点位置的地址信息
    private List<String> pointAddr = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        addressName = "广东省深圳市";

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        geocoderSearch = new GeocodeSearch(this);
        bitmap = BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED);
        markerOption = new MarkerOptions().icon(bitmap).draggable(true);
        init();
        initView();
        initData();
        initListener();
        initSocket();
    }

    private void init() {
        // 初始化地理围栏
        fenceClient = new GeoFenceClient(getApplication());
        IntentFilter fliter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        fliter.addAction(GEOFENCE_BROADCAST_ACTION);
        registerReceiver(mGeoFenceReceiver, fliter);
        /**
         * 创建pendingIntent
         */
        fenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
        fenceClient.setGeoFenceListener(this);
        /**
         * 设置地理围栏的触发行为,默认为进入
         */
        fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN);
    }

    private void initView() {
        aMap = mMapView.getMap();

        //显示比例尺
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setScaleControlsEnabled(true);

        etSearch = (EditText) findViewById(R.id.et_address);
        radioGroup = (RadioGroup) findViewById(R.id.rg_select);
        fixed = (RadioButton) findViewById(R.id.rb_fixed);
        patrol = (RadioButton) findViewById(R.id.rb_patrol);
        llPatrol = (LinearLayout) findViewById(R.id.ll_patrol);

        aMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

        initMap();
    }

    private void initMap() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(22.5471260000, 114.0642840000));
        markerOptions.title("深圳市");
        markerOptions.visible(true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mark1));
        markerOptions.icon(bitmapDescriptor);
        marker = aMap.addMarker(markerOptions);
        marker.showInfoWindow();
        LatLng ll = new LatLng(22.5471260000,
                114.0642840000);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        // 移动到某经纬度
        aMap.animateCamera(update);
        addressName = "深圳市";
        latStr = "22.5471260000";
        lonStr = "114.0642840000";
    }


    private void initData() {
        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        llPatrol.setVisibility(View.GONE);
    }

    private void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == fixed.getId()) {
                    llPatrol.setVisibility(View.GONE);
                } else if (checkedId == patrol.getId()) {
                    llPatrol.setVisibility(View.VISIBLE);
                }
                aMap.clear();
                polygonPoints.clear();
                pointAddr.clear();
            }
        });

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LatLng baidu = Utils.trLatLng(latLng);
                if (fixed.isChecked()) {
                    aMap.clear();
                    //获取经纬度
                    final double latitude = latLng.latitude;
                    final double longitude = latLng.longitude;
                    clickLat = latLng.latitude;
                    clickLon = latLng.longitude;
                    latStr = String.valueOf(latLng.latitude);
                    lonStr = String.valueOf(latLng.longitude);
                    LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
                    RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                            GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                    geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
                } else {
                    if (isAddGeoFece) {
                        aMap.clear();
                        polygonPoints.clear();
                        pointAddr.clear();
                        fenceClient.removeGeoFence();
                    }
                    isAddGeoFece = false;
                    if (null == polygonPoints) {
                        polygonPoints = new LinkedList<>();
                    }
                    polygonPoints.add(latLng);
                    addPolygonMarker(latLng, true);
                }
            }
        });

        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null) {
                        addressName = result.getRegeocodeAddress().getFormatAddress()
                                + "附近";
                        if (fixed.isChecked()) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(clickLat, clickLon));
                            markerOptions.title(addressName);
                            markerOptions.visible(true);
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mark1));
                            markerOptions.icon(bitmapDescriptor);
                            marker = aMap.addMarker(markerOptions);
                            marker.showInfoWindow();
                        } else {
                            pointAddr.add(addressName);
                        }
                    }
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult result, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result != null && result.getGeocodeAddressList() != null
                            && result.getGeocodeAddressList().size() > 0) {
                        GeocodeAddress address = result.getGeocodeAddressList().get(0);
//                                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
//                                        + address.getFormatAddress();
                        aMap.clear();
                        addressName = address.getFormatAddress();
                        latStr = String.valueOf(address.getLatLonPoint().getLatitude());
                        lonStr = String.valueOf(address.getLatLonPoint().getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(address.getLatLonPoint().getLatitude(),
                                address.getLatLonPoint().getLongitude()));
                        markerOptions.title(address.getFormatAddress());
                        markerOptions.visible(true);
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mark1));
                        markerOptions.icon(bitmapDescriptor);
                        marker = aMap.addMarker(markerOptions);
                        marker.showInfoWindow();
                        LatLng ll = new LatLng(address.getLatLonPoint().getLatitude(),
                                address.getLatLonPoint().getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
                        // 移动到某经纬度
                        aMap.animateCamera(update);
                    } else {
                        Toast.makeText(MainActivity.this, "未找到", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "未找到", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void setSize() {
        float zoomLevel = Float.parseFloat("13");
        CameraUpdate u = CameraUpdateFactory
                .zoomTo(zoomLevel);
        aMap.animateCamera(u);
    }
    private void initSocket() {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(PORT);
                    while (true) {
                        // 一旦有堵塞, 则表示服务器与客户端获得了连接
                        Socket client = serverSocket.accept();
                        // 处理这次连接
                        new HandlerThread(client);
                    }
                } catch (Exception e) {
                    System.out.println("服务器异常: " + e.getMessage());
                }
            }
        });

    }

    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
    }

    private class HandlerThread implements Runnable {
        private Socket socket;



        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {
            try {
                // 读取客户端数据
                DataInputStream input = new DataInputStream(socket.getInputStream());
                String clientInputStr = input.readUTF();//这里要注意和客户端输出流的写方法对应,否则会抛 EOFException
                // 处理客户端数据
                System.out.println("客户端发过来的内容:" + clientInputStr);
                final String log = "客户端发过来的内容:" + clientInputStr;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(App.ctx, log, Toast.LENGTH_LONG).show();
//                    }
//                });
                if (!TextUtils.isEmpty(clientInputStr)) {
                    GeocodeQuery query = new GeocodeQuery(clientInputStr, "");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                    geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
                }
                Thread.sleep(700);

                // 向客户端回复信息
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                // 发送键盘输入的一行

                if (fixed.isChecked()) {
                    List<PointLocation> pointList = new LinkedList<>();
                    PointLocation point = new PointLocation();
                    LatLng gaode = new LatLng(Double.valueOf(latStr), Double.valueOf(lonStr));
                    final LatLng latLng = Utils.trLatLng(gaode);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "高德坐标：lng:" + lonStr + ",lat:" + latStr + "\n 返回的百度坐标：lng：" +
//                                    latLng.longitude + ",lat:" + latLng.latitude, Toast.LENGTH_LONG).show();
//
//                        }
//                    });
                    point.lng = latLng.longitude;
                    point.lat = latLng.latitude;
                    point.location = addressName;
                    pointList.add(point);
                    Gson gson = new Gson();

                    out.writeUTF(gson.toJson(pointList));
                    final String str1 = gson.toJson(pointList);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(App.ctx, "返回的json:" + str1, Toast.LENGTH_LONG).show();
//                        }
//                    });
                } else {
                    if (isAddGeoFece == true && null != polygonPoints && polygonPoints.size() > 0) {
                        PointLocation addr;
                        List<PointLocation> pointList = new LinkedList<>();
                        for (int i = 0; i < polygonPoints.size(); i++) {
                            addr = new PointLocation();
                            LatLng latLng = Utils.trLatLng(polygonPoints.get(i));
                            addr.lng = latLng.longitude;
                            addr.lat = latLng.latitude;
                            addr.location = pointAddr.get(i);
                            pointList.add(addr);
                        }
                        Gson gson = new Gson();
                        out.writeUTF(gson.toJson(pointList));
                        final String str2 = gson.toJson(pointList);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(App.ctx, "返回的json:" + str2, Toast.LENGTH_LONG).show();
//
//                  }
//                        });
                    }
                }

                out.close();
                input.close();
            } catch (Exception e) {
                System.out.println("服务器 run 异常: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
    }

    public void search(View view) {
        if (TextUtils.isEmpty(etSearch.getText().toString())) {
            Toast.makeText(this, "搜索地址不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        GeocodeQuery query = new GeocodeQuery(etSearch.getText().toString(), "");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
//        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
//            @Override
//            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
//                    if (result != null && result.getRegeocodeAddress() != null
//                            && result.getRegeocodeAddress().getFormatAddress() != null) {
//                        addressName = result.getRegeocodeAddress().getFormatAddress()
//                                + "附近";
//                        MarkerOptions markerOptions = new MarkerOptions();
//                        markerOptions.position(new LatLng(clickLat, clickLon));
//                        markerOptions.title(addressName);
//                        markerOptions.visible(true);
//                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mark1));
//                        markerOptions.icon(bitmapDescriptor);
//                        marker = aMap.addMarker(markerOptions);
//                        marker.showInfoWindow();
//                    }
//                }
//            }
//
//            @Override
//            public void onGeocodeSearched(GeocodeResult result, int rCode) {
//                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
//                    if (result != null && result.getGeocodeAddressList() != null
//                            && result.getGeocodeAddressList().size() > 0) {
//                        GeocodeAddress address = result.getGeocodeAddressList().get(0);
////                                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
////                                        + address.getFormatAddress();
//                        aMap.clear();
//                        MarkerOptions markerOptions = new MarkerOptions();
//                        markerOptions.position(new LatLng(address.getLatLonPoint().getLatitude(),
//                                address.getLatLonPoint().getLongitude()));
//                        markerOptions.title(address.getFormatAddress());
//                        markerOptions.visible(true);
//                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mark1));
//                        markerOptions.icon(bitmapDescriptor);
//                        marker = aMap.addMarker(markerOptions);
//                        marker.showInfoWindow();
//                        LatLng ll = new LatLng(address.getLatLonPoint().getLatitude(),
//                                address.getLatLonPoint().getLongitude());
//                        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
//                        // 移动到某经纬度
//                        aMap.animateCamera(update);
//                    } else {
//                        Toast.makeText(MainActivity.this, "未找到", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "未找到", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        try {
            unregisterReceiver(mGeoFenceReceiver);
        } catch (Throwable e) {
        }

        if (null != fenceClient) {
            fenceClient.removeGeoFence();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        fixed.setChecked(true);
        addressName = "广东省深圳市";
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fixed.setChecked(true);
        addressName = "广东省深圳市";
    }

    /********************************************************************************/
    public void addFence(View view) {
//        removeMarkers();
        addPolygonFence();
    }

    public void removeFence(View view) {
        if (isAddGeoFece) {
            aMap.clear();
            removeMarkers();
            addALLMarkers(false);
            isAddGeoFece = false;
        } else {
            if (null == polygonPoints || polygonPoints.size() == 0) {
                return;
            }
            if (polygonPoints.size() > 0) {
                polygonPoints.remove(polygonPoints.size() - 1);
                if (pointAddr.size() > 0) {
                    pointAddr.remove(pointAddr.size() - 1);
                }
            }
            removeMarkers();
            addALLMarkers(false);
        }
    }

    private void addALLMarkers(boolean bool) {
        for (LatLng latLng : polygonPoints) {
            addPolygonMarker(latLng, bool);
        }
    }

    // 添加多边形的边界点marker
    private void addPolygonMarker(LatLng latlng, boolean bool) {
        markerOption.position(latlng);
        Marker marker = aMap.addMarker(markerOption);
        markerList.add(marker);
        //仅手动新增打点的时候，去回调取地址
        if (bool) {
            LatLonPoint latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                    GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
        }
    }

    /**
     * 添加多边形围栏
     *
     * @author hongming.wang
     * @since 3.2.0
     */
    private void addPolygonFence() {
        if (null == polygonPoints || polygonPoints.size() < 3) {
            Toast.makeText(getApplicationContext(), "参数不全，至少需要三个点", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
//        List<DPoint> pointList = new ArrayList<DPoint>();
//        for (LatLng latLng : polygonPoints) {
//            pointList.add(new DPoint(latLng.latitude, latLng.longitude));
//        }
//        fenceClient.addGeoFence(pointList, "");
        addPolygon();
        isAddGeoFece = true;
        LatLngBounds bounds = boundsBuilder.build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
    }

    /**
     * 加添多边形的方法，显示效果和添加围栏一样
     */
    private void addPolygon() {
        // 声明 多边形参数对象
        PolygonOptions polygonOptions = new PolygonOptions();
        for (LatLng lat : polygonPoints) {
            polygonOptions.add(lat);
        }
// 添加 多边形的每个顶点（顺序添加）
        polygonOptions.strokeWidth(10) // 多边形的边框
                .strokeColor(getResources().getColor(R.color.blue_stroke)) // 边框颜色
                .fillColor(getResources().getColor(R.color.blue_fill));   // 多边形的填充色
        aMap.addPolygon(polygonOptions);
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    StringBuffer sb = new StringBuffer();
                    sb.append("添加围栏成功");
                    String customId = (String) msg.obj;
                    if (!TextUtils.isEmpty(customId)) {
                        sb.append("customId: ").append(customId);
                    }
                    Toast.makeText(getApplicationContext(), sb.toString(),
                            Toast.LENGTH_SHORT).show();
                    drawFence2Map();
                    break;
                case 1:
                    int errorCode = msg.arg1;
                    Toast.makeText(getApplicationContext(),
                            "添加围栏失败 " + errorCode, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    String statusStr = (String) msg.obj;
                    break;
                default:
                    break;
            }
        }
    };

    Object lock = new Object();

    void drawFence2Map() {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (null == fenceList || fenceList.isEmpty()) {
                            return;
                        }
                        for (GeoFence fence : fenceList) {
                            if (fenceMap.containsKey(fence.getFenceId())) {
                                continue;
                            }
                            drawFence(fence);
                            fenceMap.put(fence.getFenceId(), fence);
                        }
                    }
                } catch (Throwable e) {

                }
            }
        }.start();
    }

    List<GeoFence> fenceList = new ArrayList<GeoFence>();

    /**
     * 接收触发围栏后的广播,当添加围栏成功之后，会立即对所有围栏状态进行一次侦测，如果当前状态与用户设置的触发行为相符将会立即触发一次围栏广播；
     * 只有当触发围栏之后才会收到广播,对于同一触发行为只会发送一次广播不会重复发送，除非位置和围栏的关系再次发生了改变。
     */
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
                String customId = bundle
                        .getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
                //status标识的是当前的围栏状态，不是围栏行为
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                StringBuffer sb = new StringBuffer();
                switch (status) {
                    case GeoFence.STATUS_LOCFAIL:
                        sb.append("定位失败");
                        break;
                    case GeoFence.STATUS_IN:
                        sb.append("进入围栏 ");
                        break;
                    case GeoFence.STATUS_OUT:
                        sb.append("离开围栏 ");
                        break;
                    case GeoFence.STATUS_STAYED:
                        sb.append("停留在围栏内 ");
                        break;
                    default:
                        break;
                }
                if (status != GeoFence.STATUS_LOCFAIL) {
                    if (!TextUtils.isEmpty(customId)) {
                        sb.append(" customId: " + customId);
                    }
                    sb.append(" fenceId: " + fenceId);
                }
                String str = sb.toString();
                Message msg = Message.obtain();
                msg.obj = str;
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }
    };

    private void drawFence(GeoFence fence) {
        switch (fence.getType()) {
            case GeoFence.TYPE_ROUND:
            case GeoFence.TYPE_AMAPPOI:
                drawCircle(fence);
                break;
            case GeoFence.TYPE_POLYGON:
            case GeoFence.TYPE_DISTRICT:
                drawPolygon(fence);
                break;
            default:
                break;
        }

        // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = boundsBuilder.build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
//        polygonPoints.clear();
        removeMarkers();
    }

    private void removeMarkers() {
        if (null != markerList && markerList.size() > 0) {
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }
    }

    private void drawCircle(GeoFence fence) {
        LatLng center = new LatLng(fence.getCenter().getLatitude(),
                fence.getCenter().getLongitude());
        // 绘制一个圆形
        aMap.addCircle(new CircleOptions().center(center)
                .radius(fence.getRadius()).strokeColor(Const.STROKE_COLOR)
                .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH));
        boundsBuilder.include(center);
    }

    private void drawPolygon(GeoFence fence) {
        final List<List<DPoint>> pointList = fence.getPointList();
        if (null == pointList || pointList.isEmpty()) {
            return;
        }
        for (List<DPoint> subList : pointList) {
            List<LatLng> lst = new ArrayList<LatLng>();

            PolygonOptions polygonOption = new PolygonOptions();
            for (DPoint point : subList) {
                lst.add(new LatLng(point.getLatitude(), point.getLongitude()));
                boundsBuilder.include(
                        new LatLng(point.getLatitude(), point.getLongitude()));
            }
            polygonOption.addAll(lst);

            polygonOption.strokeColor(Const.STROKE_COLOR)
                    .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH);
            aMap.addPolygon(polygonOption);
        }
    }
}
