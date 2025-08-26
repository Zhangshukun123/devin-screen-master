package com.diwen.liliao.netty;

public interface NettyListener {

    byte STATUS_CONNECT_SUCCESS = 1;//连接成功

    byte STATUS_CONNECT_CLOSED = 0;//关闭连接

    byte STATUS_CONNECT_ERROR = 0;//连接失败

    /**
     * 当接收到系统消息
     */
    void onMessageResponse(Object msg);

    /**
     * 当连接状态发生变化时调用
     */
    void onServiceStatusConnectChanged(int statusCode);
}