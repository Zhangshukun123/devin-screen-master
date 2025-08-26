package com.diwen.liliao.viewmodel;

import android.app.Application;
import android.content.res.AssetManager;

import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.DeviceModel;
import com.diwen.liliao.netty.NettyManager;
import com.diwen.liliao.utils.AtyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * Created By  tian on 2024/2/20
 * Describe:
 */
public class AppViewModel extends AndroidViewModel {
    public MutableLiveData<HashMap<String, String>> langData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<DeviceModel>> device = new MutableLiveData<>();


    public AppViewModel(@NonNull Application application) {
        super(application);
        device.setValue(new ArrayList<>());
        for (int i = 0; i < 6; i++) {
            DeviceModel deviceModel = new DeviceModel();
            deviceModel.setDeviceName("B0" + (i + 1));
            device.getValue().add(deviceModel);
        }

    }

    public String getLangText(String key) {
        if (langData.getValue() == null) {
            return "";
        }
        if (AtyUtils.isStringEmpty(langData.getValue().get(key))) {
            return langData.getValue().get(key);
        }
        return "";
    }

    // 语言类型 en/zh
    public String langType = "zh";

    // 读取 语言文件
    public void setLang() {
        int integer = MyMMKV.getInteger(MyMMKV.Language, 0);
        switch (integer) {
            case 0:
                langType = "zh";
                break;
            case 1:
                langType = "en";
                break;
            case 2:
                langType = "de";
                break;
            case 3:
                langType = "fr";
                break;
            case 4:
                langType = "it";
                break;
            case 5:
                langType = "es";
                break;
        }
        //读取asset
        AssetManager assets = getApplication().getAssets();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assets.open(langType + ".json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
            Gson gson = new Gson();
            HashMap<String, String> langMap = gson.fromJson(stringBuilder.toString(), new TypeToken<HashMap<String, String>>() {
            }.getType());
            langData.setValue(langMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private NettyManager nettyManagerB01;
    private NettyManager nettyManagerB02;
    private NettyManager nettyManagerB03;
    private NettyManager nettyManagerB04;
    private NettyManager nettyManagerB05;
    private NettyManager nettyManagerB06;

    public void upNettyIp(String name, String ip) {
        switch (name) {
            case "B01":
                if (nettyManagerB01 != null) {
                    nettyManagerB01.setNewIp(ip);
                }
                break;
            case "B02":
                if (nettyManagerB02 != null) {
                    nettyManagerB02.setNewIp(ip);
                }
                break;
            case "B03":
                if (nettyManagerB03 != null) {
                    nettyManagerB03.setNewIp(ip);
                }
                break;
            case "B04":
                if (nettyManagerB04 != null) {
                    nettyManagerB04.setNewIp(ip);
                }
                break;
            case "B05":
                if (nettyManagerB05 != null) {
                    nettyManagerB05.setNewIp(ip);
                }
                break;
            case "B06":
                if (nettyManagerB06 != null) {
                    nettyManagerB06.setNewIp(ip);
                }
                break;
        }
    }

    public void connectNetty(String name, String ip) {
        switch (name) {
            case "B01":
                if (nettyManagerB01 == null) {
                    nettyManagerB01 = new NettyManager(ip, name);
                    nettyManagerB01.connectNetty();
                } else {
                    nettyManagerB01.isConnect();
                }
                break;
            case "B02":
                if (nettyManagerB02 == null) {
                    nettyManagerB02 = new NettyManager(ip, name);
                    nettyManagerB02.connectNetty();
                } else {

                    nettyManagerB02.isConnect();
                }
                break;
            case "B03":
                if (nettyManagerB03 == null) {
                    nettyManagerB03 = new NettyManager(ip, name);
                    nettyManagerB03.connectNetty();
                } else {

                    nettyManagerB03.isConnect();
                }
                break;
            case "B04":
                if (nettyManagerB04 == null) {
                    nettyManagerB04 = new NettyManager(ip, name);
                    nettyManagerB04.connectNetty();
                } else {

                    nettyManagerB04.isConnect();
                }
                break;
            case "B05":
                if (nettyManagerB05 == null) {
                    nettyManagerB05 = new NettyManager(ip, name);
                    nettyManagerB05.connectNetty();
                } else {

                    nettyManagerB05.isConnect();
                }
                break;
            case "B06":
                if (nettyManagerB06 == null) {
                    nettyManagerB06 = new NettyManager(ip, name);
                    nettyManagerB06.connectNetty();
                } else {

                    nettyManagerB06.isConnect();
                }
                break;
        }
    }

    public void setMQTT(JSONObject jsonObject) {
        String name = MyMMKV.getDeviceName();
        switch (name) {
            case "B01":
                
                if (nettyManagerB01 != null) {
                    nettyManagerB01.setMQTT(jsonObject);
                }
        
                break;
            case "B02":
                if (nettyManagerB02 != null) {
                    nettyManagerB02.setMQTT(jsonObject);
                }
                break;
            case "B03":
                if (nettyManagerB03 != null) {
                    nettyManagerB03.setMQTT(jsonObject);
                }
                break;
            case "B04":
                if (nettyManagerB04 != null) {
                    nettyManagerB04.setMQTT(jsonObject);
                }
                break;
            case "B05":
                if (nettyManagerB05 != null) {
                    nettyManagerB05.setMQTT(jsonObject);
                }
                break;
            case "B06":
                if (nettyManagerB06 != null) {
                    nettyManagerB06.setMQTT(jsonObject);
                }
                break;
        }
    }

    public void sendInquiryMQTT(JSONObject jsonObject) {
        String name = MyMMKV.getDeviceName();
        switch (name) {
            case "B01":
                if (nettyManagerB01 != null) {
                    nettyManagerB01.sendInquiryMQTT(jsonObject);
                }
                break;
            case "B02":
                if (nettyManagerB02 != null) {
                    nettyManagerB02.sendInquiryMQTT(jsonObject);
                }
                break;
            case "B03":
                if (nettyManagerB03 != null) {
                    nettyManagerB03.sendInquiryMQTT(jsonObject);
                }
                break;
            case "B04":
                if (nettyManagerB04 != null) {
                    nettyManagerB04.sendInquiryMQTT(jsonObject);
                }
                break;
            case "B05":
                if (nettyManagerB05 != null) {
                    nettyManagerB05.sendInquiryMQTT(jsonObject);
                }
                break;
            case "B06":
                if (nettyManagerB06 != null) {
                    nettyManagerB06.sendInquiryMQTT(jsonObject);
                }
                break;
        }
    }

} 
