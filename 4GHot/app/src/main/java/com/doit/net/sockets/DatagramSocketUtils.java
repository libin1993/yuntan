package com.doit.net.sockets;

import com.doit.net.utils.LogUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * Author：Libin on 2020/5/26 15:11
 * Email：1993911441@qq.com
 * Describe：udp发送ip
 */
public class DatagramSocketUtils {
    private static DatagramSocketUtils mInstance;
    private boolean isRunning = true; //未收到tcp连接请求就循环发送
    private DatagramSocket mSocket;
    public static String REMOTE_LTE_IP = "192.168.4.100";
    public final static int UDP_LOCAL_PORT = 7003;     //本机udp端口
    public final static int UDP_REMOTE_PORT = 6070; //设备udp端口
    public final static String SEND_LOCAL_IP = "MSG_SET_XA_IP"; //手机发送ip
    public final static String SEND_LOCAL_IP_ACK = "MSG_SET_XA_IP_ACK"; //手机发送ip设备回复

    private DatagramSocketUtils() {

    }

    public static DatagramSocketUtils getInstance() {
        if (mInstance == null) {
            synchronized (DatagramSocketUtils.class) {
                if (mInstance == null) {
                    mInstance = new DatagramSocketUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 开启udp socket
     */
    public void init() {
        if (mSocket == null || mSocket.isClosed()) {
            try {
                mSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
                LogUtils.log("UDP异常: " + e.toString());
            }

            new ReceiveThread().start();
        }

    }

    public class ReceiveThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (isRunning) {
                    byte[] buf = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(buf, buf.length);
                    mSocket.receive(dp);
                    String remoteIP = dp.getAddress().getHostAddress();
                    int remotePort = dp.getPort();
                    String receiveData = new String(dp.getData(), 0, dp.getLength());
//                    JSONObject jsonReceive = new JSONObject(receiveData);
//                    String id = jsonReceive.getString("id");
//                        if (NetConfig.BOALINK_LTE_IP.equals(remoteIP) && remotePort == UDP_PORT
//                                && SEND_LOCAL_IP_ACK.equals(id)) {
//
//                            isSend = false;
//                            socket.close();
//                        }

                    //tcp有设备连接,关闭socket
                    LogUtils.log("UDP接收数据：ip：" + remoteIP + ";端口：" + remotePort + ";内容：" + receiveData);
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.log("UDP接收失败: " + e.toString());
            }


        }
    }


    /**
     * 发送数据
     */
    public void sendData(String data) {
        init();
        new SendThread(data).start();
    }



    public void closeSocket() {
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.close();
        }
    }


    /**
     *
     */
    public class SendThread extends Thread {

        private String data;

        public SendThread(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            try {

                InetAddress inetAddress = InetAddress.getByName(REMOTE_LTE_IP);
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length,
                        inetAddress, UDP_REMOTE_PORT); //创建要发送的数据包，然后用套接字发送
                mSocket.send(packet); //用套接字发送数据包
                LogUtils.log("UDP发送：" + data);
            } catch (Exception e) {
                LogUtils.log("UDP发送失败： " + e.toString());
            }
        }
    }
}

