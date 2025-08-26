package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutBlstateactivityBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;
import com.diwen.liliao.utils.AtyUtils;
import com.diwen.liliao.utils.CustomWatcherText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created By  tian on 2024/7/18
 * Describe: 蓝牙连接状态
 */
@BindEventBus
public class BlStateActivity extends MqttBaseActivity<LayoutBlstateactivityBinding> {
    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void setUiText() {
        binding.tvMusicPlay.setText(DemoApp.getInstance().getAppViewModel().getLangText("音乐播放"));
        binding.tvLanya.setText(DemoApp.getInstance().getAppViewModel().getLangText("保存"));
    }

    @Override
    protected void config() {
        binding.tvName.setText(MyMMKV.getDeviceName());
    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this);
        binding.tvLanya.setOnClickListener(this);
        binding.tvMusicPlay.addTextChangedListener(new CustomWatcherText(mContext, 32, binding.tvMusicPlay));
        getAllAttributes();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            BTCodeUtils.getInstance().finishTo(1);
            ActivityUtils.finishActivity(BlStateActivity.class);
        }
        if (v == binding.tvLanya) {
            try {
                showLoading();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PadSAttribute.BtName.getAttribute(), AtyUtils.getText(binding.tvMusicPlay));
                DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
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
        if (map == null) {
            return;
        }
        if (!DeviceId.equals(MyMMKV.getDeviceName())) {
            return;
        }
        closeLoading();
        Set<String> strings = map.keySet();
        if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
            getAllAttributes();
        }
        if (strings.contains(PadSAttribute.BtName.getAttribute())) {
            String BtState = (String) map.get(PadSAttribute.BtName.getAttribute());
            binding.tvMusicPlay.setText(BtState);
        }
    }

    public void getAllAttributes() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.BtName.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
} 
