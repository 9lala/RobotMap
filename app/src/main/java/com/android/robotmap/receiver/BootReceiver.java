package com.android.robotmap.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.robotmap.server.MapService;


/**
 * Created by Administrator on 2016/10/17.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //改成Socket不需要Http了。
//        if (!TextUtils.isEmpty(Utils.getServerIp())) {
//            Constants.URL_SERVER_HEARD = Utils.getServerIp();
//            rebootSendMsg();
//        }

        //开机开启服务
        intent.setClass(context, MapService.class);
        context.startService(intent);

        System.out.println("接收到开机广播");

    }
}
