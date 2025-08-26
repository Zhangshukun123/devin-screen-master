package com.diwen.liliao.netty;

import android.content.Context;


import com.diwen.liliao.DemoApp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created By  tian on 2019/12/9
 * Describe:  发送控制的UDP
 */
public class MyUDPHarwder {
    public static final int FEEDBACK_DATA = 0; /*UDP远程返回的内容*/

    private DatagramSocket mSocket = null;
    private DatagramPacket sPacket;
    private static final ExecutorService mEService = Executors.newCachedThreadPool();
    private static final byte[] mB_receiverBuf = new byte[1024];
    private int mI_port;
    private String mS_targetIP;
    private boolean mB_UDPLife = true;
    private boolean mB_isClose = false; //是否关闭
    private Listener mListener;
    private static volatile MyUDPHarwder mSocketUDP;
    /**
     * 设置UDP配置 IP 端口 监听器
     *
     * @param s_targetIP IP
     * @param s_port     端口
     * @param listener   监听器
     */
    public void setUDPIPAndPort(String s_targetIP, String s_port, Listener listener) {
        mB_isClose = false;
        mS_targetIP = s_targetIP;
        mI_port = Integer.parseInt(s_port);
        mListener = listener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setTargetIP(String s_targetIP) {
        mS_targetIP = s_targetIP;
    }

    public void setIpAndPort(String s_targetIP, int i_port) {
        mS_targetIP = s_targetIP;
        mI_port = i_port;
    }

    /**
     * 返回udp生命线程是否存活
     *
     * @return 是否
     */
    public boolean getB_UDPLife() {
        return mB_UDPLife;
    }

    /**
     * 是否接收UDP
     *
     * @param b 是否
     */
    public void setB_UDPLife(boolean b) {
        mB_UDPLife = b;
    }

    /**
     * 关闭UDP
     */
    public void close() {
        if (null != mSocket) {
            mB_isClose = true;
            mSocket.close();
            mSocket = null;
        }
    }


    public boolean sendContent(final byte[] sS_content) {
        if (mS_targetIP == null || mS_targetIP.length() == 0) {
            Context sContext = DemoApp.getInstance().getContext();
            WIFIUtils mWIFIUtils = WIFIUtils.getInstance();
            mWIFIUtils.setWifi(sContext);
            startScanDevice(sS_content, mWIFIUtils.getIPAddress());
            return true;
        } else {
            return sendContent(mS_targetIP, mI_port, sS_content);
        }
    }


    /**
     * 开始扫描设备
     *
     * @param s_content 扫描设备需要发送的内容
     * @param i_localIP 本机IP
     */
    public void startScanDevice(final byte[] s_content, final int i_localIP) {
        mEService.execute(() -> {
            String sS_prefixIP = "192.168.1.";
            if (i_localIP != 0) {
                String sS_localIP = WIFIUtils.intIpToStringIp(i_localIP);
                sS_prefixIP = sS_localIP.substring(0, sS_localIP.lastIndexOf(".") + 1);
            }

            for (int sI_suffixIP = 1; sI_suffixIP < 256; sI_suffixIP++) {
                if (!mB_isClose) {
                    String sS_IP = sS_prefixIP + sI_suffixIP;
                    sendContent(sS_IP, Constans.TCP_PORT, s_content);
                }
            }
        });
    }

    /**
     * 开始扫描设备
     *
     * @param s_content 扫描设备需要发送的内容
     */
    public void startScanDevice255(final byte[] s_content) {
        mEService.execute(() -> {
            sendContent("255.255.255.255", Constans.TCP_PORT, s_content);
        });
    }

    /**
     * 发送内容
     *
     * @param msgSend 需要发送的内容
     * @return
     */
    public boolean sendContent(String s_targetIP, int i_port, byte[] msgSend) {
        if (mSocket == null) {
            return false;
        }
        InetAddress sTargetAddress = null;
        try {
            sTargetAddress = InetAddress.getByName(s_targetIP);
        } catch (UnknownHostException e) {
            return false;
        }
        DatagramPacket sD_sendPacket = new DatagramPacket(
                msgSend, msgSend.length, sTargetAddress, i_port);
        try {
            if (mSocket != null) {
                if (!mB_isClose || !mSocket.isClosed()) {
                    mSocket.send(sD_sendPacket);
                }
            }
        } catch (IOException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * 开始接收UDP
     */
    public void startReceiverUDP() {
        if (mSocket != null) {
            close();
        }
        mEService.execute(() -> {
            try {
                mSocket = new DatagramSocket();
                mSocket.setSoTimeout(2000); /*设置超时的时间*/
                if (mListener != null) {
                    mListener.initOk();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            while (mB_UDPLife) {
                try {
                    sPacket = new DatagramPacket(mB_receiverBuf, mB_receiverBuf.length);
                    mSocket.receive(sPacket);
                    byte[] realData = new byte[sPacket.getLength()];
                    System.arraycopy(mB_receiverBuf, 0, realData, 0, realData.length);
                    String sS_ip = sPacket.getAddress().toString();
                    sS_ip = sS_ip.substring(1);
                    if (mListener != null) {
                        mListener.returnData(FEEDBACK_DATA, realData, sS_ip);
                    }
                } catch (SocketTimeoutException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public interface Listener {
        void returnData(int type, Object data, String udpOfIp);

        void initOk();
    }

}
