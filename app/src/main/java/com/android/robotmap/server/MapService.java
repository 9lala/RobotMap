package com.android.robotmap.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


import com.android.robotmap.handler.MinaServiceHander;
import com.android.robotmap.service.oper.ProtocalFactory;
import com.android.robotmap.utils.ThreadPoolUtils;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2016/10/17.
 */

public class MapService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startMinaServer();
    }

    private void startMinaServer() {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                NioSocketAcceptor acceptor = new NioSocketAcceptor();
                acceptor.getFilterChain().addLast("code", new ProtocolCodecFilter(new ProtocalFactory("utf-8")));
                acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
                acceptor.setHandler(new MinaServiceHander());
                try {
                    acceptor.bind(new InetSocketAddress(6001));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
