package com.android.robotmap.service.oper;



import com.android.robotmap.service.eneity.MessageProtocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ProtocalDecoder extends CumulativeProtocolDecoder {

    private final String charset;
    public ProtocalDecoder(String charset) {
        this.charset = charset;
    }

    @Override
    /**
     *解码器，对传入的iobuffer 进行解码工作，注意顺序是先进先出原则。
     */
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        CharsetDecoder decoder = Charset.forName(charset).newDecoder();
        int smsLength = 0;
        int pos = in.position();
        int remaining = in.remaining();
        try {

            // 判断接收到的长度是否够解析总长度，不够等待一个数据包一起解析
            if (remaining < 4) {
                in.position(pos);
                // in.limit(limit);
                return false;
            }

            // 够获取总长度时，由于总长度采用int类型保存，直接获取总长度存入smsLength
            smsLength = in.getInt();

            // 未读数据是否够一个完整数据解析，如果不够就缓存起来等待下一个数据包再一起从新判断，直到够解析为止，方继续下一步解析具体内容
            if (remaining < smsLength || smsLength < 0) {
                in.position(pos);
                return false;
            }

            MessageProtocol mes = new MessageProtocol();
            mes.setAlonght(in.getInt(0));
            int type = in.getInt();
            mes.setType(type);

            if (type != 6) {
                int machineTagLongth = in.getInt();
                mes.setMachineTagLongth(machineTagLongth);
                String machineTag = in.getString(machineTagLongth,decoder);
                mes.setMachineTag(machineTag);
                long jsonLongth = in.getLong();
                mes.setJsonLongth(jsonLongth);
                mes.setJson(in.getString(decoder));
            }else {
                int namelongth = in.getInt();
                mes.setImagelongth(namelongth);
                String name = in.getString(namelongth, decoder);
                mes.setImagename(name);
                long imagelongth = in.getLong();
                mes.setImagelongth(imagelongth);
                byte[] image = new byte[(int) imagelongth];
                in.get(image);
                mes.setImage(image);
            }
            out.write(mes);
        } catch (Exception e) {
            in.position(pos);
            // in.limit(limit);
            return false;
        }
        return true;
    }

}
