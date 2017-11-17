package com.android.robotmap.handler;

import com.android.robotmap.service.eneity.HeartInfo;
import com.android.robotmap.service.eneity.MessageProtocol;
import com.android.robotmap.service.eneity.TaskProtocol;
import com.android.robotmap.utils.Utils;
import com.google.gson.Gson;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
public class MinaServiceHander implements IoHandler {
    public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
        arg1.printStackTrace();
    }
    public void messageReceived(IoSession session, Object message) throws Exception {
//        session.setAttribute("receivetime", System.currentTimeMillis());
        MessageProtocol msg = (MessageProtocol) message;
        String machineTag = msg.getMachineTag();
        String json = msg.getJson();
        System.out.println("服务端接收的json：" + json);
        //假数据
//        String json = "{\n" +
//                "    \"type\": \"2\",\n" +
//                "    \"task\": {\n" +
//                "        \"id\": \"12\",\n" +
//                "        \"apps\": \"1,2,3\",\n" +
//                "        \"startTime\": \"2016-11-01 15:15:29\",\n" +
//                "        \"endTime\": \"2016-11-01 15:15:39\",\n" +
//                "        \"lon\": \"116.4039039\",\n" +
//                "        \"lat\": \"39.9065783\",\n" +
//                "\"lac\": \"4286\",\n" +
//                "\"cid\": \"18709250\",\n" +
//                "        \"status\": \"1\",\n" +
//                "        \"runStatus\": \"0\"\n" +
//                "    }\n" +
//                "}\n";
//        msg.setJson(json);

        Gson gson = new Gson();
        TaskProtocol info = gson.fromJson(msg.getJson(), TaskProtocol.class);
    }


    public void messageSent(IoSession arg0, Object arg1) throws Exception {

    }

    public void sessionClosed(IoSession arg0) throws Exception {
    }


    public void sessionCreated(IoSession arg0) throws Exception {

    }

    /*当设置了idletime时，会定时调用该方法*/
    public void sessionIdle(IoSession session, IdleStatus arg1) throws Exception {
        Utils.setSession(session);
        //返回心跳
        Gson gson = new Gson();
        HeartInfo heartInfo = new HeartInfo();


        String json = gson.toJson(heartInfo);

        MessageProtocol send = new MessageProtocol();
        String mTag = Utils.getDeviceTag();
        send.setType(2);
        send.setMachineTag(mTag);
        send.setMachineTagLongth(mTag.getBytes("utf-8").length);
        send.setJson(json);
        send.setJsonLongth(json.getBytes("utf-8").length);
        send.setAlonght(send.getMachineTagLongth() + (int) send.getJsonLongth() + 4 + 4 + 4);
        session.write(send);
        System.out.println("服务端发送json:" + json);


//        /*超过指定时间未收到客户端相应认为客户端已下线，单位毫秒*/
//        Long lastrecesivetime = (Long) session.getAttribute("receivetime");
//        Long nowtime = System.currentTimeMillis();
//        /*在收到客户端数据包时记录最近收到时间*/
//        if (nowtime - lastrecesivetime > 100000) {
//            System.out.println("客户端已下线......");
//
//			/*表明客户端已下线，某些地方需要验证，所以记录下来*/
//            session.setAttribute("islive", false);
//            session.close(true);// 主动关闭连接
//        }
    }

    public void sessionOpened(final IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
//        session.setAttribute("receivetime", System.currentTimeMillis());
//        session.setAttribute("islive", true);
    }
}
