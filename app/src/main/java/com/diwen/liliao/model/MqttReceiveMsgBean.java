package com.diwen.liliao.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created By  tian on 2022/8/12
 * Describe:
 */
public class MqttReceiveMsgBean {

    @SerializedName("IntentName")
    private String intentName;
    @SerializedName("Params")
    private String params;
    @MyKey(majorKey = "deviceName")
    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }


    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

} 
