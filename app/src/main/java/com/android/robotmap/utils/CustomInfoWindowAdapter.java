package com.android.robotmap.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.android.robotmap.R;

/**
 * Created by Administrator on 2017/7/7.
 */

public class CustomInfoWindowAdapter implements AMap.InfoWindowAdapter{

    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.map_info_window, null);
        setViewContent(marker,view);
        return view;
    }
    //这个方法根据自己的实体信息来进行相应控件的赋值
    private void setViewContent(Marker marker,View view) {
        //实例：
        TextView tvName = (TextView) view.findViewById(R.id.inforwindow_text);
        tvName.setText(marker.getTitle());
    }

    //提供了一个给默认信息窗口定制内容的方法。如果用自定义的布局，不用管这个方法。
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}