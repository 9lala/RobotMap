/**
 *
 */
package com.android.robotmap.service.eneity;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luozhihua
 */
public class DataProtocol {

    private String taskId;
    private List<UserInfo> infoList = new ArrayList<UserInfo>();

    public DataProtocol() {

    }

    public DataProtocol(String taskId, List<UserInfo> infoList) {
        super();
        this.taskId = taskId;
        this.infoList = infoList;
    }

    public static DataProtocol getData(String json) {
        Gson gson = new Gson();
        DataProtocol dpl = gson.fromJson(json, DataProtocol.class);
        return dpl;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<UserInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<UserInfo> infoList) {
        this.infoList = infoList;
    }

    @Override
    public String toString() {
        return "DataProtocol [taskId=" + taskId + ", infoList=" + infoList + "]";
    }

}
