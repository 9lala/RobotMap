package com.android.robotmap.service.oper;



import com.android.robotmap.service.eneity.MessageProtocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;


public class ProtocalEncoder extends ProtocolEncoderAdapter {
	private final String charset;

	public ProtocalEncoder(String charset) {
		this.charset = charset;
	}

	/* 编码器，对接收到的object进行编码工作，然后交由下一个过滤器处理 */
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		MessageProtocol mes = (MessageProtocol) message;
		CharsetEncoder encoder = Charset.forName(charset).newEncoder();

		IoBuffer io = IoBuffer.allocate(mes.getAlonght()).setAutoExpand(true);
		io.clear();
		io.position(0);// 清空缓存并重置

		/* 写入顺序需要和读取顺序一致，才能正确解析 */
		io.putInt(mes.getAlonght());
		io.putInt(mes.getType());
		if(mes.getType() != 6){
			io.putInt(mes.getMachineTagLongth());
			io.putString(mes.getMachineTag(), encoder);
			io.putLong(mes.getJsonLongth());
			io.putString(mes.getJson(), encoder);
		}else {
			try {
				io.putInt(mes.getImagenamelongth());
				io.putString(mes.getImagename(), encoder);
			} catch (CharacterCodingException e1) {
				e1.printStackTrace();
			}
			io.putLong(mes.getImagelongth());
			io.put(mes.getImage());
		}
		io.flip();

		out.write(io);
	}

	public void dispose() throws Exception {
	}
}
