package com.diwen.liliao.netty;

import android.content.Intent;
import android.util.Log;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.model.MqttModel;
import com.diwen.liliao.utils.JsonUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Author:ZJC
 * Date:2020/4/11  11:31
 * Description:NettyManager
 */
public class NettyManager implements NettyListener {

    private String TAG = "tian";
    private NettyClient nettyClient = null;
    private String name;

    public NettyManager(String inetHost, String name) {
        nettyClient = new NettyClient(inetHost);
        this.name = name;
    }

    public void setNewIp(String inetHost) {
        if (nettyClient != null) {
            nettyClient.setNewIp(inetHost);
        }

    }

    public void connectNetty() {
        new Thread(() -> {
            Log.e(TAG, "客户端启动自动连接...");
            if (!nettyClient.getConnectStatus()) {
                nettyClient.setListener(NettyManager.this);
                nettyClient.connect();
            }
        }).start();
    }


    public void isConnect() {
        if (!nettyClient.getConnectStatus()) {
            nettyClient.disconnect();
            nettyClient.connect();
        }
    }

    public void sendData(String data) {
        LogUtil.eTian(data);
        if (!nettyClient.getConnectStatus()) {
            nettyClient.disconnect();
            nettyClient.connect();
        }
        Log.e(TAG, "sendData: " + data);
        nettyClient.sendMsgToServer(data, future -> {
            if (future.isSuccess()) {
                Log.e(TAG, "发送成功");
            } else {
                Log.e(TAG, "发送失败");
            }
        });
    }

    public void sendDataByte(String data) {
        nettyClient.sendMsgToServer(data, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Log.e(TAG, "发送成功");
                } else {
                    Log.e(TAG, "发送失败");
                }
            }
        });
    }

    @Override
    public void onMessageResponse(Object msg) {
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        result.release();
        String results = null;
        try {
            results = new String(result1, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        broadcastUpdate(MQTTCons.ACTION_DATA_AVAILABLE, results);

    }

    @Override
    public void onServiceStatusConnectChanged(int statusCode) {
        if (statusCode == NettyListener.STATUS_CONNECT_SUCCESS) {
            Log.e(TAG, "STATUS_CONNECT_SUCCESS:");
            if (nettyClient.getConnectStatus()) {
                upOnlie();
                Log.e(TAG, "连接成功");
            }
        } else {
            Log.e(TAG, "onServiceStatusConnectChanged:" + statusCode);
            if (!nettyClient.getConnectStatus()) {
                disOnlie();
                Log.e(TAG, "网路不好，正在重连");
            }
        }
    }

    public void sendInquiryMQTT(JSONObject jsonObject) {
        MqttModel sCommServer = new MqttModel(jsonObject);
        sCommServer.setDeviceName(name);
        sCommServer.setIntentName(LampIntentName.查询指定属性.getIntentNameValues());
        sendData(JsonUtils.createJson(sCommServer).toString());

    }

    public void setMQTT(JSONObject jsonObject) {
        MqttModel sCommServer = new MqttModel(jsonObject);
        sCommServer.setDeviceName(name);
        sCommServer.setIntentName(LampIntentName.设置指定属性.getIntentNameValues());
        sendData(JsonUtils.createJson(sCommServer).toString());

    }

    public void close() {
        if (nettyClient != null) {
            nettyClient.disconnect();
        }
    }

    public void upOnlie() {
        String online = "{\"deviceName\":\"%s\",\"IntentName\":\"设备在线\",\"Params\":{\"onlinestate\":1}}";
        broadcastUpdate(MQTTCons.ACTION_DATA_AVAILABLE, String.format(online, name));
    }

    public void disOnlie() {
        String online = "{\"deviceName\":\"%s\",\"IntentName\":\"设备在线\",\"Params\":{\"onlinestate\":0}}";
        broadcastUpdate(MQTTCons.ACTION_DATA_AVAILABLE, String.format(online, name));
    }

    void broadcastUpdate(String action, String message) {
        LogUtil.eTian("收到消息：" + message);
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(MQTTCons.ACTION_DATA_AVAILABLE, message);
        LocalBroadcastManager.getInstance(DemoApp.getInstance().mContext).sendBroadcast(intent);
    }
}