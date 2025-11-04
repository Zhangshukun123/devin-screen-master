package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.adapter.DeviceListAdapter;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.ActivityMainBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.utils.ActivityUtils;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

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
            if (deviceListAdapter.getItem(position).isConnectTcp()) {
                DemoApp.getInstance().getAppViewModel().connectNetty(deviceListAdapter.getItem(position).getDeviceName(), deviceListAdapter.getItem(position).getDeviceIp());
                if (!deviceListAdapter.getItem(position).getDeviceName().equals(MyMMKV.getDeviceName())) {
                    EventBus.getDefault().post(new MessageEvent(MQTTCons.ACTION_DEVICE_CHANGE));
                }
                MyMMKV.get().putString("deviceName", deviceListAdapter.getItem(position).getDeviceName());//点击的设备   判读设备是否在线
                if (DemoApp.getInstance().buildCompany) {
                    ActivityUtils.finishToActivity(DeviceLauncherActivity.class, false);
                } else {
                    ActivityUtils.startActivity(new Intent(mContext, DeviceModelActivity.class).putExtra("name", deviceListAdapter.getItem(position).getDeviceName()));
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}