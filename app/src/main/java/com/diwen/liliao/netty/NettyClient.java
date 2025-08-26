package com.diwen.liliao.netty;

import android.os.SystemClock;
import android.util.Log;

import com.diwen.liliao.activity.AESUtils;
import com.diwen.liliao.utils.DataUtils;


import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public NettyClient(String inetHost) {
        this.inetHost = inetHost;
    }
    public void setNewIp(String inetHost) {
        this.inetHost = inetHost;

    }
    private static final String TAG = "tian";
    private String inetHost;
    private EventLoopGroup group;//Bootstrap参数

    private NettyListener listener;//写的接口用来接收服务端返回的值

    private Channel channel;//通过对象发送数据到服务端

    private boolean isConnect = false;//判断是否连接了

    private static int reconnectNum = Integer.MAX_VALUE;//定义的重连到时候用

    private boolean isNeedReconnect = true;//是否需要重连

    private boolean isConnecting = false;//是否正在连接

    private long reconnectIntervalTime = 5000;//重连的时间

    public void connect() {
        if (isConnecting) {
            return;
        }
        //起个线程
        Thread clientThread = new Thread("client-Netty") {
            @Override
            public void run() {
                super.run();
                isNeedReconnect = true;
                reconnectNum = Integer.MAX_VALUE;
                connectServer();
            }
        };
        clientThread.start();
    }

    //连接时的具体参数设置
    private void connectServer() {
        synchronized (NettyClient.this) {
            ChannelFuture channelFuture = null;//连接管理对象
            if (!isConnect) {
                isConnecting = true;
                group = new NioEventLoopGroup();//设置的连接group
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)//设置的一系列连接参数操作等
                        .channel(NioSocketChannel.class)
                        .handler(new NettyClientHandler(listener));
                try {
                    //连接监听
                    channelFuture = bootstrap.connect(inetHost, Constans.TCP_PORT).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                isConnect = true;
                                channel = channelFuture.channel();
                            } else {
                                Log.e(TAG, "连接失败");
                                isConnect = false;
                            }
                            isConnecting = false;
                        }
                    }).sync();
                    // 等待连接关闭
                    channelFuture.channel().closeFuture().sync();
                    Log.e(TAG, " 断开连接");
                } catch (Exception e) {
                    Log.e(TAG, "e:" + e.toString());
                    e.printStackTrace();
                } finally {
                    isConnect = false;
                    listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_CLOSED);//STATUS_CONNECT_CLOSED 这我自己定义的 接口标识
                    if (null != channelFuture) {
                        if (channelFuture.channel() != null && channelFuture.channel().isOpen()) {
                            channelFuture.channel().close();
                        }
                    }
                    group.shutdownGracefully();
                    reconnect();//重新连接
                }
            }
        }
    }

    //断开连接
    public void disconnect() {
        Log.e(TAG, "断开连接");
        isNeedReconnect = false;
        group.shutdownGracefully();
    }

    //重新连接
    public void reconnect() {
        Log.e(TAG, "reconnect");
        if (isNeedReconnect && reconnectNum > 0 && !isConnect) {
            reconnectNum--;
            SystemClock.sleep(reconnectIntervalTime);
            if (isNeedReconnect && reconnectNum > 0 && !isConnect) {
                Log.e(TAG, "重新连接");
                connectServer();
            }
        }
    }

    //发送消息到服务端。 Bootstrap设置的时候我没有设置解码，这边才转的
    public boolean sendMsgToServer(String data, ChannelFutureListener listener) {
        boolean flag = channel != null && isConnect;
        if (flag) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(data, //2
                    Charset.forName(DataUtils.GB2312));
            channel.writeAndFlush(byteBuf).addListener(listener);
        }
        return flag;
    }

    public boolean sendMsgByteToServer(String data, ChannelFutureListener listener) {
        boolean flag = channel != null && isConnect;
        if (flag) {
            byte[] encrypt = AESUtils.encrypt(data);

            ByteBuf byteBuf = Unpooled.copiedBuffer(encrypt);
            Log.i("tian", encrypt.length + "");
            channel.writeAndFlush(byteBuf).addListener(listener);
        }
        return flag;
    }

    //重连时间
    public void setReconnectNum(int reconnectNum) {
        this.reconnectNum = reconnectNum;
    }

    public void setReconnectIntervalTime(long reconnectIntervalTime) {
        this.reconnectIntervalTime = reconnectIntervalTime;
    }

    //现在连接的状态
    public boolean getConnectStatus() {
        return isConnect;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnectStatus(boolean status) {
        this.isConnect = status;
    }

    public void setListener(NettyListener listener) {
        this.listener = listener;
    }

}