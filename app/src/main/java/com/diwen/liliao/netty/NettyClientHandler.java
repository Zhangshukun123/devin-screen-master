package com.diwen.liliao.netty;

import android.text.TextUtils;
import android.util.Log;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.DeviceModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final String TAG = NettyClientHandler.class.getSimpleName();
    private NettyListener listener;

    public NettyClientHandler(NettyListener listener) {
        this.listener = listener;
    }

    //每次给服务器发送的东西， 让服务器知道我们在连接中哎
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("读超时，认为连接断开，关闭通道并重连");
                ctx.close();  // 关闭后触发 channelInactive
            } else if (event.state() == IdleState.WRITER_IDLE) {
                sendHeartData();
//                    ctx.writeAndFlush(Unpooled.copiedBuffer(heart, CharsetUtil.UTF_8));
            }
        }
    }

    private void sendHeartData() {
        ArrayList<DeviceModel> value = DemoApp.getInstance().getAppViewModel().device.getValue();
        for (DeviceModel model : value) {
            if (model.isConnectTcp()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("heart", "5");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject, model.getDeviceName());
                System.out.println("客户端发送心跳" + model.getDeviceName());
            }
        }
    }

    /**
     * 连接成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_SUCCESS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.e(TAG, "channelInactive");
    }

    //接收消息的地方， 接口调用返回到activity了
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        listener.onMessageResponse(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当引发异常时关闭连接。
        Log.e(TAG, "引发异常,关闭连接:" + cause.toString());
        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR);
        cause.printStackTrace();
        ctx.close();
    }
}