package com.android.robotmap.service.eneity;

import java.io.Serializable;

public class MessageProtocol extends Header implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int machineTagLongth;
    private long jsonLongth;
    private String machineTag;
    private String json;// json数据

    // 图片数据
    private int imagenamelongth;// 图片名称长度
    private long imagelongth;// 图片长度（大小）
    private String imagename;// 图片名称（字符串，顺便测试json）
    private byte[] image;// 图片内容

    public MessageProtocol() {
        super();
    }

    public MessageProtocol(String machineTag, String json, String machineIp) {
        super();
        this.machineTag = machineTag;
        this.json = json;
    }

    public MessageProtocol(String machineTag, String json) {
        super();
        this.machineTag = machineTag;
        this.json = json;
    }

    public String getMachineTag() {
        return machineTag;
    }

    public void setMachineTag(String machineTag) {
        this.machineTag = machineTag;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getImagenamelongth() {
        return imagenamelongth;
    }

    public void setImagenamelongth(int imagenamelongth) {
        this.imagenamelongth = imagenamelongth;
    }

    public long getImagelongth() {
        return imagelongth;
    }

    public void setImagelongth(long imagelongth) {
        this.imagelongth = imagelongth;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getMachineTagLongth() {
        return machineTagLongth;
    }

    public void setMachineTagLongth(int machineTagLongth) {
        this.machineTagLongth = machineTagLongth;
    }

    public long getJsonLongth() {
        return jsonLongth;
    }

    public void setJsonLongth(long jsonLongth) {
        this.jsonLongth = jsonLongth;
    }

}
