package com.android.robotmap;

import android.app.Application;
import android.content.Context;

import com.android.robotmap.utils.Constants;
import com.android.robotmap.utils.PreferenceUtils;


/**
 * Created by Administrator on 2017/4/6.
 */

public class App extends Application {
    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        getDeviceId();
    }

    private void getDeviceId() {
        String m_szDevIDShort = android.os.Build.SERIAL;
        PreferenceUtils.putString(App.this, Constants.MACHINE_ID, m_szDevIDShort);
    }

}
