package com.android.robotmap.service.eneity;

import java.util.List;

/**
 * Created by Administrator on 2016/12/30.
 */
public class HeartInfo extends TaskProtocol {
    private List<UserInfo> userInfo;

    public List<UserInfo> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<UserInfo> userInfo) {
        this.userInfo = userInfo;
    }
}