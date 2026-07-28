package com.diwen.liliao.fragment;

import android.os.Bundle;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.databinding.FragmentWifiBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

@BindEventBus
public class WifiFragment extends BaseFragment<FragmentWifiBinding> {
    public static WifiFragment newInstance() {

        Bundle args = new Bundle();

        WifiFragment fragment = new WifiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvWifi.setText(StringUtils.getText("Wi-Fi"));
        binding.tvTop.setText(StringUtils.getText("Wi-Fi"));
        binding.tvTop1.setText(StringUtils.getText("Wi-Fi"));
        binding.tvDisWifi.setText(StringUtils.getText("断开"));
        binding.tvSaveWifi.setText(StringUtils.getText("保存"));
        binding.tvBottom.setText(StringUtils.getText("本地设置"));
        binding.tvBottom1.setText(StringUtils.getText("本地设置"));
        binding.tvSaveLocal.setText(StringUtils.getText("保存"));
        binding.tvDisLocal.setText(StringUtils.getText("断开"));

    }

    @Override
    protected void initData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(MQTTCons.ACTION_DATA_AVAILABLE)) {
            MqttParseOverModel model = event.getMqttParseOverModel();
            MqttMessage(model.getDeviceId(), model.getMap());
        }
    }

    private void MqttMessage(String DeviceId, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        if (!DeviceId.equals(MyMMKV.getDeviceName())) {
            return;
        }
        Set<String> strings = map.keySet();
        if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
            getAllAttributes();
        }
        if (strings.contains(PadSAttribute.BtName.getAttribute())) {
            String BtState = (String) map.get(PadSAttribute.BtName.getAttribute());
            binding.tvWifiName.setText(BtState);
            binding.tvWifiPwd.setText(BtState);
            binding.tvLocalName.setText(BtState);
            binding.tvLocalPort.setText(BtState);
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
