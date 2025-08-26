package com.diwen.liliao.model;


/**
 * Created By  tian on 2018/11/29
 * Describe:
 */
public class MessageEvent {
    /**
    * 时间开始  结束
    */
    
    public String StartTime;
    public String EndTime;

    private String case_message;
    private String message;
    private int type;
    private MqttParseOverModel mqttParseOverModel;

    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getCase_message() {
        return case_message;
    }

    public void setCase_message(String case_messagez) {
        this.case_message = case_messagez;
    }

 

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public MessageEvent(String message) {
        this.message = message;
    }

    public MessageEvent(String message, Object object) {
        this.message = message;
        this.object = object;
    }
    public MessageEvent(String message, MqttParseOverModel mqttParseOverModel) {
        this.message = message;
        this.mqttParseOverModel = mqttParseOverModel;
    }
    public MessageEvent(String message, String case_message) {
        this.message = message;
        this.case_message = case_message;
    }
    public MessageEvent() {
    }

    public MessageEvent(int type) {
        this.type = type;
    }

    public MessageEvent(String message, int type) {
        this.type = type;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public MqttParseOverModel getMqttParseOverModel() {
        return mqttParseOverModel;
    }

    public void setMqttParseOverModel(MqttParseOverModel mqttParseOverModel) {
        this.mqttParseOverModel = mqttParseOverModel;
    }

}
