package com.diwen.liliao.fragment;

import android.os.Bundle;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.databinding.FragmentVersionBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

@BindEventBus
public class VersionFragment extends BaseFragment<FragmentVersionBinding> {
    public static VersionFragment newInstance() {

        Bundle args = new Bundle();

        VersionFragment fragment = new VersionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvVersion.setText(DemoApp.getInstance().getAppViewModel().getLangText("版本"));
        binding.tvName.setText(DemoApp.getInstance().getAppViewModel().getLangText("名称"));
//        binding.tvUIVersionLeft.setText("UI Ver：");
//        binding.tvSwVersionLeft.setText("SW Ver：");
        binding.tvUIVersion.setText(String.valueOf(Utils.getVersionName(getActivity())));
        binding.tvSwVersion.setText(MyMMKV.getString(MyMMKV.SoftWareVer));
    }

    @Override
    protected void initData() {
        getVersionAttributes();
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
            getVersionAttributes();
        }
        if (strings.contains(PadSAttribute.SoftWareVer.getAttribute())) {
            String BtState = (String) map.get(PadSAttribute.SoftWareVer.getAttribute());
            binding.tvUIVersion.setText(BtState);
            binding.tvSwVersion.setText(BtState);
        }
    }

    public void getVersionAttributes() {
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put(PadSAttribute.SoftWareVer.getAttribute(), 1);
//            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}
