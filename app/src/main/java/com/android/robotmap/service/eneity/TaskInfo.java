package com.android.robotmap.service.eneity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/1.
 */

public class TaskInfo implements Serializable {
    private Double lat;
    private Double lng;
    private String apps = "";
    private long startTime;
    private long endTime;
    private int id;
    private int runStatus;
    private int status;
    private int subTaskId;
    private String runinterval;
    private String point1;
    private String point2;
    private String point3;

    public int getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(int subTaskId) {
        this.subTaskId = subTaskId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getApps() {
        return apps;
    }

    public void setApps(String apps) {
        this.apps = apps;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(int runStatus) {
        this.runStatus = runStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPoint1() {
        return point1;
    }

    public void setPoint1(String point1) {
        this.point1 = point1;
    }

    public String getPoint3() {
        return point3;
    }

    public void setPoint3(String point3) {
        this.point3 = point3;
    }

    public String getPoint2() {
        return point2;
    }

    public void setPoint2(String point2) {
        this.point2 = point2;
    }

    public String getRuninterval() {
        return runinterval;
    }

    public void setRuninterval(String runinterval) {
        this.runinterval = runinterval;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", apps='" + apps + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", id=" + id +
                ", runStatus=" + runStatus +
                ", status=" + status +
                ", subTaskId=" + subTaskId +
                ", runinterval='" + runinterval + '\'' +
                ", point1='" + point1 + '\'' +
                ", point2='" + point2 + '\'' +
                ", point3='" + point3 + '\'' +
                '}';
    }
}
