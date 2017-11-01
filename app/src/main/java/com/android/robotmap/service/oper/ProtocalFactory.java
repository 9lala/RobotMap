package com.android.robotmap.service.oper;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 此工厂只是用于小文件（几十mb吧，不然会抛内存溢出错误，对于大文件传输，必须边读边写）
 *
 */
public class ProtocalFactory implements ProtocolCodecFactory {



        private final ProtocalEncoder encoder;  //编码
        private final ProtocalDecoder decoder;  //解码
        public ProtocalFactory(String charset) {
            encoder=new ProtocalEncoder(charset);  
            decoder=new ProtocalDecoder(charset);  
        }  
           
        public ProtocolEncoder getEncoder(IoSession session) {
            return encoder;  
        }  
        public ProtocolDecoder getDecoder(IoSession session) {
            return decoder;  
        }  
          
} 