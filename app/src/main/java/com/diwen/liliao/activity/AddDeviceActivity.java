package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutAdddeviceBinding;
import com.diwen.liliao.utils.ActivityUtils;

/**
 * Created By  tian on 2024/7/18
 * Describe:  添加设备
 */
public class AddDeviceActivity extends MqttBaseActivity<LayoutAdddeviceBinding> {
    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void setUiText() {
        binding.tvSave.setText(DemoApp.getInstance().getAppViewModel().getLangText("保存"));
    }

    @Override
    protected void config() {

    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this);
        binding.tvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            ActivityUtils.finishActivity(AddDeviceActivity.class);
        }
        if (v == binding.tvSave) {
        }
    }
} 
