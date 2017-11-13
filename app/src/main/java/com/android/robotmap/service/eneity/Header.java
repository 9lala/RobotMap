package com.android.robotmap.service.eneity;

public class Header {
	private int alonght;// 消息总长度
	// 1:数据包，0：发送完毕，关闭连接,2:心跳包，只有头没有数据,
	// 3:大文件传输，直接传送文件iobuffer过去，编码时，变读边写，解码时边写边读
	private int type;
	public int getAlonght() {
		return alonght;
	}
	public void setAlonght(int alonght) {
		this.alonght = alonght;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}
