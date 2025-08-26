package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.adapter.SettingListAdapter;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutDevicesettingactivityBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.utils.ActivityUtils;

import java.util.ArrayList;

/**
 * Created By  tian on 2024/7/16
 * Describe:  设备设置
 */
public class DeviceSettingActivity extends MqttBaseActivity<LayoutDevicesettingactivityBinding> {
    private ArrayList<SettingItem> settingItems;
    private SettingListAdapter settingListAdapter;

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void setUiText() {
        settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(R.mipmap.icon_languge, "语言设置"));
        settingItems.add(new SettingItem(R.mipmap.icon_air, "风扇设置"));
        settingItems.add(new SettingItem(R.mipmap.icon_maichong, "脉冲设置"));
        settingItems.add(new SettingItem(R.mipmap.icon_time, "时间设置"));
        settingItems.add(new SettingItem(R.mipmap.icon_jilu, "记录查询"));
        settingItems.add(new SettingItem(R.mipmap.icon_liliao, "添加设备"));
        settingListAdapter = new SettingListAdapter(settingItems);
        binding.setTingList.setAdapter(settingListAdapter);
        settingListAdapter.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                case 0:
                    ActivityUtils.startActivity(LanguageSettingActivity.class);
                    break;
                case 1:
                    ActivityUtils.startActivity(AirSettingActivity.class);
                    break;
                case 2:
                    if (DemoApp.getInstance().buildCompany){
                        ActivityUtils.startActivity(MaiChongSettingActivity.class);
                    }else {
                        ActivityUtils.startActivity(DeviceModelActivity.class);
                    }
                    break;
                case 3:
                    ActivityUtils.startActivity(TimeSettingsActivity.class);
                    break;
                case 4:
                    ActivityUtils.startActivity(UseHitorActivity.class);
                    break;
                case 5:
                  ActivityUtils.startActivity(DeviceListActivity.class);
                    break;
            }

        });
    }

    @Override
    protected void config() {
        binding.tvName.setText(MyMMKV.getDeviceName());
    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            BTCodeUtils.getInstance().finishTo(1);
            ActivityUtils.finishActivity(DeviceSettingActivity.class);
        }
    }
} 
