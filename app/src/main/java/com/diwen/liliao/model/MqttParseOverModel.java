package com.diwen.liliao.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * Created By  tian on 2022/8/13
 * Describe:
 */
@Data
public class MqttParseOverModel implements Serializable {
    public String DeviceId;
    public String msgId;
    public String intentName;
    public String paramsString;
    public Map<String, Object> map;//mqtt使用

 
}

              
