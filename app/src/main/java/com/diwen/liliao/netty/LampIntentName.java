package com.diwen.liliao.netty;

/**
 * Created By  tian on 2022/4/14
 * Describe:   
 */
public enum LampIntentName {
    上报事件("上报事件"),//上报事件
    设置指定属性("setDeviceStats"),//设置指定属性
    查询指定属性("getDeviceStats"),//查询指定属性
    语音播报("语音播报"),// 语音播报
    设备在线("设备在线"),// 设备在线
    搜索设备("搜索设备"),// 搜索设备
    停止搜索("停止搜索"),// 停止搜索
    查询当前状态("查询当前状态"),// 查询当前状态   蓝牙设备使用
    查询所有属性("查询所有属性");//查询所有属性

    // 成员变量  
    private String IntentNameValues;

    private LampIntentName(String IntentNameValues) {
        this.IntentNameValues = IntentNameValues;
    }

    public String getIntentNameValues() {
        return IntentNameValues;
    }

    public void setIntentNameValues(String intentNameValues) {
        IntentNameValues = intentNameValues;
    }
} 
