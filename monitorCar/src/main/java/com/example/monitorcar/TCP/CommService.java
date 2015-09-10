package com.example.monitorcar.TCP;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class CommService implements ICommService {
    // private static CommService mInstance = null;
    private String loginSvrIp = null;
    private Session loginSession = null;
    private InnerRspListener innerRspAdapter = null;// 通信模块内部的监听器
    private List<IResponseListener> asynListeners = new ArrayList<IResponseListener>();// 所有异步消息监听器
    private boolean hasInited = false;
    private Context mContext;

    // private static CommService instance = new CommService();
    private volatile static CommService instance = null;

    private CommService() {

    }

    /**
     * 初始化，连接接入服务器获取登陆服务器的ip
     */
    public static CommService getInstance() {
        if (instance == null) {
            synchronized (CommService.class) {
                if (instance == null) {
                    instance = new CommService();
                }
            }
        }
        return instance;
    }

    public boolean getHasInited() {
        return hasInited;
    }

    public synchronized void init(Context context) {
        if (hasInited) {
            return;
        }
        mContext = context;
        Socket sock = new Socket();
        try {
            // sock.connect(new InetSocketAddress("192.168.1.20", 8012),
            // 4000);//
            sock.connect(new InetSocketAddress("192.168.1.10", 8012), 4000);
            InnerRspListener asl = new InnerRspListener(Event.USER_ACCESS_RSP);
            AccessSession as = new AccessSession(sock, new MyMessage(), asl);
            as.start();

            Pdu pdu = new Pdu();
            pdu.msgId = Event.USER_ACCESS_REQ;
            pdu.sender = Event.CLIENT_SERVER;
            pdu.receiver = DataTransfer.IntToByteArray(Event.LOCAL_SERVER);
            pdu.sendProcess = Event.CLIENT_USER_PROCESS;
            pdu.recvRrocess = Event.ACCESS_MAN_PROCESS;
            pdu.prototype = Event.PROTO_TCP;
            pdu.routerType = Event.ROUTE_APPOINT;
            pdu.sericeType = Event.COMMON_SERVICE;
            as.sendMessage(pdu);

            while (!asl.isRspRecv) {
                Thread.sleep(50);
            }
            IMessage event = asl.getResponse();

            int result = DataTransfer.ByteArrayToInt(event
                    .getValue(Event.RSP_KEY));
            if (result == Event.RSP_OK) {
                String tmpString = new String(
                        event.getValue(Event.SERVERIP_KEY));
                loginSvrIp = tmpString;// .substring(0, tmpString.length()-1);
                hasInited = true;

            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void sendMessage(Pdu pdu) {
        try {
            if (loginSession == null) {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress(loginSvrIp, 8012), 4000);
                loginSession = new LoginSession(sock, new MyMessage(),
                        innerRspAdapter, asynListeners);
                loginSession.start();
            }
            loginSession.sendMessage(pdu);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息等待指定事件号的回应。
     *
     * @param pdu
     * @param resEventNo
     * @return
     */

    public IMessage sendMessage(Pdu pdu, int resEventNo) {
        try {
            if (loginSession == null) {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress(loginSvrIp, 8012), 4000);
                innerRspAdapter = new InnerRspListener(resEventNo);
                loginSession = new LoginSession(sock, new MyMessage(),
                        innerRspAdapter, asynListeners);
                loginSession.start();
            } else {
                innerRspAdapter.setEventNo(resEventNo);
                innerRspAdapter.isRspRecv = false;
            }
            loginSession.sendMessage(pdu);
            while (!innerRspAdapter.isRspRecv) {
                Thread.sleep(50);
            }
            return innerRspAdapter.getResponse();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addAsyncListener(IResponseListener adapter) {
        asynListeners.add(adapter);
    }
}
