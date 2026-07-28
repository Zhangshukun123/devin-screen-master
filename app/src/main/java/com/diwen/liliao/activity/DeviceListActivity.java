package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.adapter.DeviceListAdapter;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.ActivityMainBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.DeviceModel;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class DeviceListActivity extends MqttBaseActivity<ActivityMainBinding> {
    private DeviceListAdapter deviceListAdapter;

    @Override
    protected void handleIntent(Intent intent) {
    }

    @Override
    protected void config() {
        deviceListAdapter = new DeviceListAdapter(DemoApp.getInstance().getAppViewModel().device.getValue());
        binding.deviceList.setAdapter(deviceListAdapter);
    }

    @Override
    protected void setUiText() {
        binding.tvTitle.setText(DemoApp.getInstance().getAppViewModel().getLangText("设备列表"));
        binding.tvName1.setText(DemoApp.getInstance().getAppViewModel().getLangText("设备列表"));
    }

    @Override
    protected void setListener() {
        DemoApp.getInstance().getAppViewModel().device.observe(this, deviceModels -> {
            deviceListAdapter.setNewData(deviceModels);
        });
        deviceListAdapter.setOnItemClickListener((adapter, view, position) -> {
            DeviceModel device = deviceListAdapter.getItem(position);
            if (device.isConnectTcp()) {
                if (!DeviceLaunchStateController.isKnown(device.getLaunch())) {
                    queryDeviceState(device.getDeviceName());
                    ToastUtils.show("Loading device status");
                    return;
                }
                DemoApp.getInstance().getAppViewModel().connectNetty(device.getDeviceName(), device.getDeviceIp());
                boolean deviceChanged = !device.getDeviceName().equals(MyMMKV.getDeviceName());
                MyMMKV.get().putString("deviceName", device.getDeviceName());//点击的设备   判读设备是否在线
                MyMMKV.get().putString("deviceIp", device.getDeviceIp());
                if (deviceChanged) {
                    EventBus.getDefault().post(new MessageEvent(MQTTCons.ACTION_DEVICE_CHANGE));
                }
                if (DemoApp.getInstance().buildCompany) {
                    ActivityUtils.finishToActivity(DeviceLauncherActivity.class, false);
                } else if (DeviceLaunchStateController.opensModeSelection(device.getLaunch())) {
                    ActivityUtils.startActivity(new Intent(mContext, DeviceModelActivity.class).putExtra("name", device.getDeviceName()));
                } else {
                    ActivityUtils.finishToActivity(DeviceLauncherActivity.class, false);
                }
            } else {
                ToastUtils.show("No networking");
            }
        });
        binding.llTitle.setVisibility(View.GONE);
        binding.reluserSee.setVisibility(View.VISIBLE);
        binding.ivFinish.setOnClickListener(v -> {
            BTCodeUtils.getInstance().finishTo(2);
            finish();
        });
    }

    private void queryDeviceState(String deviceName) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.Launch.getAttribute(), 1);
            jsonObject.put(PadSAttribute.PluseMode.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject, deviceName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
