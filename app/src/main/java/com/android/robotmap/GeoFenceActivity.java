package com.android.robotmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
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
import com.android.robotmap.utils.PointLocation;
import com.android.robotmap.utils.ThreadPoolUtils;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */

public class GeoFenceActivity extends FragmentActivity implements
        View.OnClickListener,
        GeoFenceListener,
        AMap.OnMapClickListener,
        LocationSource,
        AMapLocationListener,
        CompoundButton.OnCheckedChangeListener {
    private TextView tvGuide;
    private TextView tvResult;
    private EditText etCustomId;
    private CheckBox cbAlertIn;
    private CheckBox cbAlertOut;
    private CheckBox cbAldertStated;
    private Button btAddFence, btnRemoveFence;
    private boolean isAddGeoFece = false;
    private UiSettings mUiSettings;
    private GeocodeSearch geocoderSearch;
    private String addressName = "深圳市";
    private String latStr = "22.5471260000", lonStr = "114.0642840000";
    public static final int PORT = 7000;//监听的端口号
    private double clickLat, clickLon;
    private Marker marker;

    /**
     * 用于显示当前的位置
     * <p>
     * 示例中是为了显示当前的位置，在实际使用中，单独的地理围栏可以不使用定位接口
     * </p>
     */
    private AMapLocationClient mlocationClient;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private MapView mMapView;
    private AMap mAMap;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_new);
        // 初始化地理围栏
        fenceClient = new GeoFenceClient(getApplicationContext());

        btAddFence = (Button) findViewById(R.id.bt_addFence);
        btnRemoveFence = (Button) findViewById(R.id.bt_removeFence);
        tvGuide = (TextView) findViewById(R.id.tv_guide);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvResult.setVisibility(View.GONE);
        etCustomId = (EditText) findViewById(R.id.et_customId);

        cbAlertIn = (CheckBox) findViewById(R.id.cb_alertIn);
        cbAlertOut = (CheckBox) findViewById(R.id.cb_alertOut);
        cbAldertStated = (CheckBox) findViewById(R.id.cb_alertStated);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        bitmap = BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED);
        markerOption = new MarkerOptions().icon(bitmap).draggable(true);
        geocoderSearch = new GeocodeSearch(this);
        init();
        initListener();
        initSocket();
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
                        new GeoFenceActivity.HandlerThread(client);
                    }
                } catch (Exception e) {
                    System.out.println("服务器异常: " + e.getMessage());
                }
            }
        });

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
                if (!TextUtils.isEmpty(clientInputStr)) {
                    GeocodeQuery query = new GeocodeQuery(clientInputStr, "");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                    geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
                }
                Thread.sleep(700);

                // 向客户端回复信息
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                // 发送键盘输入的一行

//                String location = lonStr + "," + latStr + ","
//                        + addressName;

                if (isAddGeoFece == true && null != polygonPoints && polygonPoints.size() > 0) {
                    PointLocation addr;
                    List<PointLocation> pointList = new LinkedList<>();
                    for (int i = 0; i < polygonPoints.size(); i++) {
                        addr = new PointLocation();
                        addr.lng = polygonPoints.get(i).longitude;
                        addr.lat = polygonPoints.get(i).latitude;
                        addr.location = pointAddr.get(i);
                        pointList.add(addr);
                    }
                    Gson gson = new Gson();
                    out.writeUTF(gson.toJson(pointList));
                    Toast.makeText(GeoFenceActivity.this, "发送数据：" + gson.toJson(pointList), Toast.LENGTH_LONG).show();
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

    private void initListener() {
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null) {
                        addressName = result.getRegeocodeAddress().getFormatAddress()
                                + "附近";
                        pointAddr.add(addressName);
//                        MarkerOptions markerOptions = new MarkerOptions();
//                        markerOptions.position(new LatLng(clickLat, clickLon));
//                        markerOptions.title(addressName);
//                        markerOptions.visible(true);
//                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mark1));
//                        markerOptions.icon(bitmapDescriptor);
//                        marker = mAMap.addMarker(markerOptions);
//                        marker.showInfoWindow();
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
                        mAMap.clear();
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
                        marker = mAMap.addMarker(markerOptions);
                        marker.showInfoWindow();
                        LatLng ll = new LatLng(address.getLatLonPoint().getLatitude(),
                                address.getLatLonPoint().getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
                        // 移动到某经纬度
                        mAMap.animateCamera(update);
                    } else {
                        Toast.makeText(GeoFenceActivity.this, "未找到", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(GeoFenceActivity.this, "未找到", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(13));
            mAMap.setOnMapClickListener(this);
        }
        //显示比例尺
        mUiSettings = mAMap.getUiSettings();
        mUiSettings.setScaleControlsEnabled(true);
        resetView_polygon();

        btAddFence.setOnClickListener(this);
        btnRemoveFence.setOnClickListener(this);
        cbAlertIn.setOnCheckedChangeListener(this);
        cbAlertOut.setOnCheckedChangeListener(this);
        cbAldertStated.setOnCheckedChangeListener(this);

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
        initMap();
    }

    private void initMap() {
        LatLng ll = new LatLng(22.5471260000,
                114.0642840000);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        // 移动到某经纬度
        mAMap.animateCamera(update);
        addressName = "深圳市";
        latStr = "22.5471260000";
        lonStr = "114.0642840000";
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        try {
            unregisterReceiver(mGeoFenceReceiver);
        } catch (Throwable e) {
        }

        if (null != fenceClient) {
            fenceClient.removeGeoFence();
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_addFence:
                addFence();
                break;
            case R.id.bt_removeFence:
                removeFence();
                break;
            default:
                break;
        }
    }

    private void removeFence() {
        if (isAddGeoFece) {
            mAMap.clear();
            removeMarkers();
            addALLMarkers(false);
            isAddGeoFece = false;
        } else {
            if (null == polygonPoints || polygonPoints.size() == 0) {
                return;
            }
            polygonPoints.remove(polygonPoints.size() - 1);
            pointAddr.remove(pointAddr.size() - 1);
            removeMarkers();
            addALLMarkers(false);
        }
    }

    private void addALLMarkers(boolean bool) {
        for (LatLng latLng : polygonPoints) {
            addPolygonMarker(latLng, bool);
        }
    }

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
        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
//        polygonPoints.clear();
        removeMarkers();
    }

    private void drawCircle(GeoFence fence) {
        LatLng center = new LatLng(fence.getCenter().getLatitude(),
                fence.getCenter().getLongitude());
        // 绘制一个圆形
        mAMap.addCircle(new CircleOptions().center(center)
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
            mAMap.addPolygon(polygonOption);
        }
    }


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
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.append(statusStr + "\n");
                    break;
                default:
                    break;
            }
        }
    };

    List<GeoFence> fenceList = new ArrayList<GeoFence>();

    @Override
    public void onGeoFenceCreateFinished(final List<GeoFence> geoFenceList,
                                         int errorCode, String customId) {
        Message msg = Message.obtain();
        if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
            fenceList = geoFenceList;
            msg.obj = customId;
            msg.what = 0;
        } else {
            msg.arg1 = errorCode;
            msg.what = 1;
        }
        handler.sendMessage(msg);
    }

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

    @Override
    public void onMapClick(LatLng latLng) {
        if (isAddGeoFece) {
            mAMap.clear();
            polygonPoints.clear();
            fenceClient.removeGeoFence();
        }
        isAddGeoFece = false;
        if (null == polygonPoints) {
            polygonPoints = new LinkedList<>();
        }
        polygonPoints.add(latLng);
        addPolygonMarker(latLng, true);
        tvGuide.setBackgroundColor(getResources().getColor(R.color.gary));
        tvGuide.setText("已选择" + polygonPoints.size() + "个点");
        if (polygonPoints.size() >= 3) {
            btAddFence.setEnabled(true);
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                tvResult.setVisibility(View.GONE);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText(errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 只是为了获取当前位置，所以设置为单次定位
            mLocationOption.setOnceLocation(true);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    // 添加多边形的边界点marker
    private void addPolygonMarker(LatLng latlng, boolean bool) {
        markerOption.position(latlng);
        Marker marker = mAMap.addMarker(markerOption);
        markerList.add(marker);
        //仅手动新增打点的时候，去回调取地址
        if (bool) {
            LatLonPoint latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                    GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
        }
    }

    private void removeMarkers() {
        if (null != markerList && markerList.size() > 0) {
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_alertIn:
                if (isChecked) {
                    activatesAction |= GeoFenceClient.GEOFENCE_IN;
                } else {
                    activatesAction = activatesAction
                            & (GeoFenceClient.GEOFENCE_OUT
                            | GeoFenceClient.GEOFENCE_STAYED);
                }
                break;
            case R.id.cb_alertOut:
                if (isChecked) {
                    activatesAction |= GeoFenceClient.GEOFENCE_OUT;
                } else {
                    activatesAction = activatesAction
                            & (GeoFenceClient.GEOFENCE_IN
                            | GeoFenceClient.GEOFENCE_STAYED);
                }
                break;
            case R.id.cb_alertStated:
                if (isChecked) {
                    activatesAction |= GeoFenceClient.GEOFENCE_STAYED;
                } else {
                    activatesAction = activatesAction
                            & (GeoFenceClient.GEOFENCE_IN
                            | GeoFenceClient.GEOFENCE_OUT);
                }
                break;
            default:
                break;
        }
        if (null != fenceClient) {
            fenceClient.setActivateAction(activatesAction);
        }
    }

    private void resetView_polygon() {
        tvGuide.setBackgroundColor(getResources().getColor(R.color.red));
        tvGuide.setText("请点击地图选择围栏的边界点,至少3个点");
        tvGuide.setVisibility(View.VISIBLE);
        tvGuide.setVisibility(View.VISIBLE);
        polygonPoints = new LinkedList<>();
        btAddFence.setEnabled(false);
    }

    /**
     * 添加围栏
     *
     * @author hongming.wang
     * @since 3.2.0
     */
    private void addFence() {
        removeMarkers();
        addPolygonFence();
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
                .strokeColor(Color.argb(50, 1, 1, 1)) // 边框颜色
                .fillColor(Color.argb(15, 9, 9, 9));   // 多边形的填充色
        mAMap.addPolygon(polygonOptions);
    }


    /**
     * 添加多边形围栏
     *
     * @author hongming.wang
     * @since 3.2.0
     */
    private void addPolygonFence() {
        String customId = etCustomId.getText().toString();
        if (null == polygonPoints || polygonPoints.size() < 3) {
            Toast.makeText(getApplicationContext(), "参数不全，至少需要三个点", Toast.LENGTH_SHORT)
                    .show();
            btAddFence.setEnabled(true);
            return;
        }
        List<DPoint> pointList = new ArrayList<DPoint>();
        for (LatLng latLng : polygonPoints) {
            pointList.add(new DPoint(latLng.latitude, latLng.longitude));
        }
        fenceClient.addGeoFence(pointList, customId);
        isAddGeoFece = true;
        Toast.makeText(GeoFenceActivity.this, "记录下点击了围栏", Toast.LENGTH_LONG).show();
    }

}
