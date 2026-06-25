package com.diwen.liliao.netty;

import com.diwen.liliao.DemoApp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created By  tian on 2024/7/19
 * Describe:
 */
public class BTCodeUtils {
    private static volatile BTCodeUtils socketSendUtils;

    private BTCodeUtils() {
    }

    public static synchronized BTCodeUtils getInstance() {
        if (socketSendUtils == null) {
            synchronized (BTCodeUtils.class) {
                if (socketSendUtils == null) {
                    socketSendUtils = new BTCodeUtils();
                }
            }
        }
        return socketSendUtils;
    }

    public JSONObject finishTo(int BackKey) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.BackKey.getAttribute(), BackKey);
//            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
        return new JSONObject();
     
    }

    public JSONObject queryDeviceTime() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.Year.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Month.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Day.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Hour.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Minute.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Second.getAttribute(), 1);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    //  查询风扇指定属性
    public JSONObject queryAirAttribute() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.AirT2Temp.getAttribute(), 1);
            jsonObject.put(PadSAttribute.AirT1Temp.getAttribute(), 1);
            jsonObject.put(PadSAttribute.AirAlarm.getAttribute(), 1);
            jsonObject.put(PadSAttribute.AirTemp.getAttribute(), 1);
            jsonObject.put(PadSAttribute.AirTime.getAttribute(), 1);
            jsonObject.put(PadSAttribute.AirBlowerStop.getAttribute(), 1);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public JSONObject queryPemfAttribute() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.PemfFrequncy.getAttribute(), 1);
            jsonObject.put(PadSAttribute.PemfIntensity.getAttribute(), 1);
            jsonObject.put(PadSAttribute.PemfTreatmentTime.getAttribute(), 1);
            jsonObject.put(PadSAttribute.PemfState.getAttribute(), 1);
            jsonObject.put(PadSAttribute.PemfWorkState.getAttribute(), 1);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    //  查询设备是否具有 PEMF 功能
    public JSONObject queryPemfEnable() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.PemfEnable.getAttribute(), 1);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

} 
