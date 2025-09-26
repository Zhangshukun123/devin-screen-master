package com.diwen.liliao.netty;

/**
 * Created By  tian on 2022/4/7
 * Describe:
 */
public class MQTTCons {

    //region MQTT相关
    public final static String ACTION_MQTT_CONNECTED = "com.tian.mqtt.ACTION_MQTT_CONNECTED";//连接成功
    public final static String ACTION_MQTT_ERROR= "com.tian.mqtt.ACTION_MQTT_ERROR";//连接错误
    public final static String ACTION_MQTT_DISCONNECTED = "com.tian.mqtt.ACTION_MQTT_DISCONNECTED";//断开连接
    public final static String ACTION_DATA_AVAILABLE = "com.tian.mqtt.ACTION_DATA_AVAILABLE";//收到的信息
    public final static String ACTION_DATA_AVAILABLE_BLUE = "com.tian.mqtt.ACTION_DATA_AVAILABLE_BLUE";//收到蓝牙的信息
    public final static String ACTION_DATA_TOPIC = "com.tian.mqtt.ACTION_DATA_TOPIC";//收到的主题
    public final static String EXTRA_ERROR_CODE = "com.tian.mqtt.EXTRA_ERROR_CODE";//错误提示code
    public final static String EXTRA_ERROR_MESSAGE = "com.tian.mqtt.EXTRA_ERROR_MESSAGE";//错误信息  发送超时等 

    public static final String NETWORK_CONNECTED = "com.tian.mqtt.NETWORK_CONNECTED";
    public static final String NETWORK_ERROR = "com.tian.mqtt.NETWORK_ERROR";
    /**  服务器订阅
     * sys/event/cg/model/id/set receive    /#
     */
    public  static  String ReceiveMqttTopicPost="/app/att/%s/%s/%s/post";
    public  static  String ReceiveMqttTopicLinePost="/app/att/%s/%s/%s/post/+";
    public  static  String AppEventPost="/app/event/%s/%s/%s/post";

    /**
     * 手机端发布订阅
     * app/att/cg/model/id/set
     */
    public  static  String AppEventSet="/app/event/%s/%s/%s/set";
    public  static  String SendMqttTopic="/app/att/%s/%s/%s/set";
    public  static  String propertySet="thing.event.property.set";
    //mqtts://192.168.0.201:8883
    
    //public static final String Broker = "ssl://192.168.0.65:8883";
    public static final String Broker = "ssl://";


} 
