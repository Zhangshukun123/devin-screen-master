package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutUsehittorBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created By  tian on 2024/7/18
 * Describe:使用记录
 */
@BindEventBus
public class UseHitorActivity extends MqttBaseActivity<LayoutUsehittorBinding> {
    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void setUiText() {
        binding.tvAllTime.setText(DemoApp.getInstance().getAppViewModel().getLangText("累计工作时间"));
        binding.tvXuhao.setText(DemoApp.getInstance().getAppViewModel().getLangText("序号"));
        binding.tvStartTime.setText(DemoApp.getInstance().getAppViewModel().getLangText("开始时间"));
        binding.tvEndTime.setText(DemoApp.getInstance().getAppViewModel().getLangText("结束时间"));
        binding.tvDeviceTime.setText(DemoApp.getInstance().getAppViewModel().getLangText("使用时长"));
        binding.tvUp.setText(DemoApp.getInstance().getAppViewModel().getLangText("上一条"));
        binding.tvDown.setText(DemoApp.getInstance().getAppViewModel().getLangText("下一条"));

    }

    @Override
    protected void config() {
        binding.tvName.setText(MyMMKV.getDeviceName());
        PageNumber();
    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this);
        binding.tvUp.setOnClickListener(this);
        binding.tvDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            BTCodeUtils.getInstance().finishTo(2);
            ActivityUtils.finishActivity(UseHitorActivity.class);
        }
        if (v == binding.tvUp) {
            try {
                showLoading();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PadSAttribute.RecordQuery.getAttribute(), 0);
                DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (v == binding.tvDown) {
            try {
                showLoading();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PadSAttribute.RecordQuery.getAttribute(), 1);
                DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(MQTTCons.ACTION_DATA_AVAILABLE)) {
            MqttParseOverModel model = event.getMqttParseOverModel();
            MqttMessage(model.getDeviceId(), model.getMap());
        }
    }

    @Override
    protected void MqttMessage(String DeviceId, Map<String, Object> map) {
        super.MqttMessage(DeviceId, map);
        if (map == null) {
            return;
        }
        if (!DeviceId.equals(MyMMKV.getDeviceName())) {
            return;
        }

        Set<String> strings = map.keySet();
        if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
            int onLineState = (int) map.get(PadSAttribute.onLineState.getAttribute());
            if (onLineState == 1) {
                PageNumber();
            }
        }
        //TotalTime:00001,Number:119,StartTime:2024/08/06 12:45:09,EndTime:2024/08/06 12:45:09,UsageTime:10:05
        if (strings.contains(PadSAttribute.TotalTime.getAttribute())) {
            try {
                int  TotalTime = (int ) map.get(PadSAttribute.TotalTime.getAttribute());
                String s = String.valueOf(TotalTime);
                  while (s.length()<5){
                      s="0"+s;
                  }              
                binding.tvWorkAllTime.setText(s + " h");
             int Number = (int) map.get(PadSAttribute.Number.getAttribute());
             binding.tvNumber.setText(getPointTwo(Number));
             String StartTime = (String) map.get(PadSAttribute.StartTime.getAttribute());
             binding.evStartTime.setText(StartTime);

             String EndTime = (String) map.get(PadSAttribute.EndTime.getAttribute());
             binding.evEndTime.setText(EndTime);

             String UsageTime = (String) map.get(PadSAttribute.UsageTime.getAttribute());
             binding.evDeviceTime.setText(UsageTime);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }

    }
    public String getPointTwo(int a) {
        if (a < 10) {
            return "0" + String.valueOf(a);
        }
        return String.valueOf(a);
    }

    public void PageNumber() {
        try {
            showLoading();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.RecordQuery.getAttribute(), 2);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
} 
