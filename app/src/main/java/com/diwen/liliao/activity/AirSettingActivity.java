package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.adapter.ButtonAirListAdapter;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutAirsettingactivityBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;
import com.diwen.liliao.utils.AtyUtils;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created By  tian on 2024/7/16
 * Describe: 风扇设置
 */
@BindEventBus
public class AirSettingActivity extends MqttBaseActivity<LayoutAirsettingactivityBinding> {
    private ButtonAirListAdapter buttonAirListAdapter;
    private ArrayList buttonList;

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void setUiText() {
        binding.tvSave.setText(DemoApp.getInstance().getAppViewModel().getLangText("保存"));
        binding.tv1.setText(DemoApp.getInstance().getAppViewModel().getLangText("人体风机设置"));
        binding.tv3.setText(DemoApp.getInstance().getAppViewModel().getLangText("设备风机设置"));
        binding.tvSet1.setText(DemoApp.getInstance().getAppViewModel().getLangText("开始温度"));
        binding.tvSet3.setText(DemoApp.getInstance().getAppViewModel().getLangText("报警温度"));
        binding.tvChiLun.setText(DemoApp.getInstance().getAppViewModel().getLangText("档位"));
        binding.tvMineD.setText(DemoApp.getInstance().getAppViewModel().getLangText("分钟"));
        binding.tv2.setText(DemoApp.getInstance().getAppViewModel().getLangText("延迟关闭时间"));


    }

    @Override
    protected void config() {

        binding.tvName.setText(MyMMKV.getDeviceName());
        buttonList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            buttonList.add(new SettingItem());
        }
        buttonAirListAdapter = new ButtonAirListAdapter(buttonList);
        binding.rectangle.setAdapter(buttonAirListAdapter);
        queryAirAttribute();
    }

    @Override
    protected void setListener() {
        binding.tvSave.setOnClickListener(this);
        buttonAirListAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (SettingItem datum : buttonAirListAdapter.getData()) {
                datum.setChose(false);
            }
            buttonAirListAdapter.getItem(position).setChose(true);
            buttonAirListAdapter.notifyDataSetChanged();
        });
        binding.ivFinish.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            BTCodeUtils.getInstance().finishTo(2);
            ActivityUtils.finishActivity(AirSettingActivity.class);
        }
        if (v == binding.tvSave) {
            setAirBlower();
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
        try {
            if (map == null) {
                return;
            }
            if (DeviceId.equals(MyMMKV.getDeviceName())) {
                closeLoading();
                Set<String> strings = map.keySet();
                if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
                    int onLineState = (int) map.get(PadSAttribute.onLineState.getAttribute());
                    if (onLineState == 1) {
                        queryAirAttribute();
                    }
                }
                if (strings.contains(PadSAttribute.AirBlowerStop.getAttribute())) {
                    int AirBlower = (int) map.get(PadSAttribute.AirBlowerStop.getAttribute());
                    if (AirBlower > 0) {
                        buttonAirListAdapter.getData().get(AirBlower - 1).setChose(true);
                        buttonAirListAdapter.notifyDataSetChanged();
                    }
                }
                if (strings.contains(PadSAttribute.AirTime.getAttribute())) {
                    int AirTime = (int) map.get(PadSAttribute.AirTime.getAttribute());
                    binding.evMineClose.setText(getPointTwo(AirTime));
                }
    
                if (strings.contains(PadSAttribute.AirTemp.getAttribute())) {
                    int AirTemp = (int) map.get(PadSAttribute.AirTemp.getAttribute());
                    binding.evStarTemp.setText(getPointTwo(AirTemp));
                }
                if (strings.contains(PadSAttribute.AirAlarm.getAttribute())) {
                    int AirAlarm = (int) map.get(PadSAttribute.AirAlarm.getAttribute());
                    binding.evCreTemp.setText(getPointTwo(AirAlarm));
                }
                if (strings.contains(PadSAttribute.AirT1Temp.getAttribute())) {
                    int AirT1Temp = (int) map.get(PadSAttribute.AirT1Temp.getAttribute());
                    binding.evStarTempT1.setText(getPointTwo(AirT1Temp));
                }
                if (strings.contains(PadSAttribute.AirT2Temp.getAttribute())) {
                    int AirT2Temp = (int) map.get(PadSAttribute.AirT2Temp.getAttribute());
                    binding.evCreTempT2.setText(getPointTwo(AirT2Temp));
                }
            }
        } catch (Exception e) {
            ToastUtils.show(e.toString());
            e.printStackTrace();
        }
    }

    public String getPointTwo(int a) {
        if (a < 10) {
            return "0" + String.valueOf(a);
        }
        return String.valueOf(a);
    }

    public void queryAirAttribute() {
        JSONObject jsonObject = BTCodeUtils.getInstance().queryAirAttribute();
        DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
    }

    public void setAirBlower() {
        try {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < buttonAirListAdapter.getData().size(); i++) {
                if (buttonAirListAdapter.getData().get(i).isChose()) {
                    jsonObject.put(PadSAttribute.AirBlowerStop.getAttribute(), i + 1);
                }
            }
            if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.evMineClose))) {
                jsonObject.put(PadSAttribute.AirTime.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evMineClose)));
            }
            if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.evStarTemp))) {
                jsonObject.put(PadSAttribute.AirTemp.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evStarTemp)));
            }
            if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.evCreTemp))) {
                jsonObject.put(PadSAttribute.AirAlarm.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evCreTemp)));
            }

            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
} 
