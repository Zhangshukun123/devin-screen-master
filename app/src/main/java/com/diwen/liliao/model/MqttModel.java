package com.diwen.liliao.model;


import org.json.JSONObject;

/**
 * Created By  tian on 2022/4/8
 * Describe:消息最外层
 */
public class MqttModel {
    @MyKey(majorKey = "IntentName")
    private String IntentName;
    @MyKey(majorKey = "Params")
    private JSONObject Params;

    @MyKey(majorKey = "deviceName")
    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }



    public MqttModel(JSONObject json) {
        setParams(json);
    }

    public MqttModel() {

    }


    public JSONObject getParams() {
        return Params;
    }

    public void setParams(JSONObject params) {
        Params = params;
    }



    public String getIntentName() {
        return IntentName;
    }

    public void setIntentName(String intentName) {
        IntentName = intentName;
    }
} 
