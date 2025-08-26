package com.diwen.liliao.utils;


import com.alibaba.fastjson.JSON;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.model.MqttReceiveMsgBean;
import com.diwen.liliao.netty.LogUtil;
import com.diwen.liliao.netty.MQTTCons;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;


/**
 * Created By  tian on 2019/7/3
 * Describe:  子线程解析数据
 */
public class MqttWorkerThread implements Runnable {
    private String message;
 
    public MqttWorkerThread(String intent) {
      this.message=intent;
      
        
    }

    @Override
    public void run() {
        try {
           
                MqttReceiveMsgBean mqttReceiveMsgBean = JsonUtils.parseObject(message.trim(), MqttReceiveMsgBean.class);
                Map<String, Object> map = (Map) JSON.parse(mqttReceiveMsgBean.getParams());
                MqttParseOverModel mqttParseOverModel = new MqttParseOverModel();
                mqttParseOverModel.setIntentName(mqttReceiveMsgBean.getIntentName());
                mqttParseOverModel.setDeviceId(mqttReceiveMsgBean.getDeviceName());
                mqttParseOverModel.setMap(map);//mqtt设备使用
                mqttParseOverModel.setParamsString(mqttReceiveMsgBean.getParams());//原始数据
                EventBus.getDefault().post(new MessageEvent(MQTTCons.ACTION_DATA_AVAILABLE, mqttParseOverModel));
           
        } catch (Exception e) {
            LogUtil.i("mqtt解析异常：" + e.toString());

        }
    }


 
}
