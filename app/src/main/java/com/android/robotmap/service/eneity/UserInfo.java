package com.android.robotmap.service.eneity;

public class UserInfo {
    private String taskId = "";
    private String status = "";
    private String appId = "";
    private String nickname = "";
    private String feel = "";
    private String age = "";
    private String sex = "";
    private String addr = "";
    private String signature = "";
    private String subTaskId = "";
    private String lng = "";
    private String lat = "";

    /**
     * 对应的头像文件名字。
     */
//    private String imageName = "";

    /**
     * 进过计算的精确位置，用于存储从app抓取出的用户精确的地理位置以lng,lat格式赋值
     */
    private String calculatedLocation = "";

    /**
     * 多久之前出现过
     */
    private String beforeTime = "";

    /**
     * 头像网络地址
     */
    private String headBigImageUrl = "";

    /**
     * 生活圈背景图
     */
    private String snsBgUrl = "";

    /**
     * 用户唯一标示
     */
    private String user = "";

    /**
     * 微博昵称
     */
    private String weiboNick = "";

    public UserInfo() {

    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFeel() {
        return feel;
    }

    public void setFeel(String feel) {
        this.feel = feel;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBeforeTime() {
        return beforeTime;
    }

    public void setBeforeTime(String beforeTime) {
        this.beforeTime = beforeTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHeadBigImageUrl() {
        return headBigImageUrl;
    }

    public void setHeadBigImageUrl(String headBigImageUrl) {
        this.headBigImageUrl = headBigImageUrl;
    }

    public String getWeiboNick() {
        return weiboNick;
    }

    public void setWeiboNick(String weiboNick) {
        this.weiboNick = weiboNick;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSnsBgUrl() {
        return snsBgUrl;
    }

    public void setSnsBgUrl(String snsBgUrl) {
        this.snsBgUrl = snsBgUrl;
    }

    public String getCalculatedLocation() {
        return calculatedLocation;
    }

    public void setCalculatedLocation(String calculatedLocation) {
        this.calculatedLocation = calculatedLocation;
    }
}