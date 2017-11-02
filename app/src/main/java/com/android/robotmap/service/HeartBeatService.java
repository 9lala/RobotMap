package com.android.robotmap.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * Created by Administrator on 2016/11/7.
 */

public class HeartBeatService extends Service {



    @Override
    public void onCreate() {
        //开启单独的线程，因为Service是位于主线程的，为了避免主线程被阻塞

//        ThreadPoolUtils.execute(new Runnable() {
//            @Override
//            public void run() {
//                NioSocketAcceptor acceptor = new NioSocketAcceptor();
//                acceptor.getFilterChain().addLast("code",
//                        new ProtocolCodecFilter(new ProtocalFactory("utf-8")));
//                acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 2);
//                acceptor.setHandler(new MinaServiceHander());
//                try {
//                    acceptor.bind(new InetSocketAddress(6000));
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
