package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.adapter.SettingListAdapter;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutDevicesettingactivityBinding;
import com.diwen.liliao.fragment.BluetoothFragment;
import com.diwen.liliao.fragment.FansFragment;
import com.diwen.liliao.fragment.LanguageFragment;
import com.diwen.liliao.fragment.TimeFragment;
import com.diwen.liliao.fragment.VersionFragment;
import com.diwen.liliao.fragment.WifiFragment;
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
    private Fragment currentFragment;
    private BluetoothFragment bluetoothFragment;
    private WifiFragment wifiFragment;
    private TimeFragment timeFragment;
    private LanguageFragment languageFragment;
    private FansFragment fansFragment;
    private VersionFragment versionFragment;

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void setUiText() {
        binding.tvBluetooth.setText(DemoApp.getInstance().getAppViewModel().getLangText("设置蓝牙"));
        binding.tvWifi.setText(DemoApp.getInstance().getAppViewModel().getLangText("设置WiFi"));
        binding.tvTime.setText(DemoApp.getInstance().getAppViewModel().getLangText("设置时间"));
        binding.tvLanguage.setText(DemoApp.getInstance().getAppViewModel().getLangText("设置语言"));
        binding.tvFans.setText(DemoApp.getInstance().getAppViewModel().getLangText("设置风扇"));
        binding.tvVersion.setText(DemoApp.getInstance().getAppViewModel().getLangText("设置版本"));
        binding.llBlueTooth.setBackgroundResource(R.drawable.bg_radius_4_white10);
        binding.ivBluetooth.setImageResource(R.mipmap.ic_bluetooth_setting_check);
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
                    if (DemoApp.getInstance().buildCompany) {
                        ActivityUtils.startActivity(MaiChongSettingActivity.class);
                    } else {
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
        if (bluetoothFragment == null) {
            bluetoothFragment = BluetoothFragment.newInstance();
        }
        switchFragment(bluetoothFragment).commit();
    }

    @Override
    protected void config() {
        binding.tvName.setText(MyMMKV.getDeviceName());
    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this);
        binding.llBlueTooth.setOnClickListener(this);
        binding.llWifi.setOnClickListener(this);
        binding.llTime.setOnClickListener(this);
        binding.llLanguage.setOnClickListener(this);
        binding.llFans.setOnClickListener(this);
        binding.llVersion.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            BTCodeUtils.getInstance().finishTo(1);
            ActivityUtils.finishActivity(DeviceSettingActivity.class);
        }
        if (v == binding.llBlueTooth) {
            resetCheckView();
            binding.llBlueTooth.setBackgroundResource(R.drawable.bg_radius_4_white10);
            binding.ivBluetooth.setImageResource(R.mipmap.ic_bluetooth_setting_check);
            switchFragment(bluetoothFragment).commit();
        }
        if (v == binding.llWifi) {
            resetCheckView();
            binding.llWifi.setBackgroundResource(R.drawable.bg_radius_4_white10);
            binding.ivWifi.setImageResource(R.mipmap.ic_wifi_setting_check);
            if (wifiFragment == null) {
                wifiFragment = WifiFragment.newInstance();
            }
            switchFragment(wifiFragment).commit();
        }
        if (v == binding.llTime) {
            resetCheckView();
            binding.llTime.setBackgroundResource(R.drawable.bg_radius_4_white10);
            binding.ivTime.setImageResource(R.mipmap.ic_time_setting_check);
            if (timeFragment == null) {
                timeFragment = TimeFragment.newInstance();
            }
            switchFragment(timeFragment).commit();
        }
        if (v == binding.llLanguage) {
            resetCheckView();
            binding.llLanguage.setBackgroundResource(R.drawable.bg_radius_4_white10);
            binding.ivLanguage.setImageResource(R.mipmap.ic_language_setting_check);
            if (languageFragment == null) {
                languageFragment = LanguageFragment.newInstance();
            }
            switchFragment(languageFragment).commit();
        }
        if (v == binding.llFans) {
            resetCheckView();
            binding.llFans.setBackgroundResource(R.drawable.bg_radius_4_white10);
            binding.ivFans.setImageResource(R.mipmap.ic_fans_setting_check);
            if (fansFragment == null) {
                fansFragment = FansFragment.newInstance();
            }
            switchFragment(fansFragment).commit();
        }
        if (v == binding.llVersion) {
            resetCheckView();
            binding.llVersion.setBackgroundResource(R.drawable.bg_radius_4_white10);
            binding.ivVersion.setImageResource(R.mipmap.ic_version_setting_check);
            if (versionFragment == null) {
                versionFragment = VersionFragment.newInstance();
            }
            switchFragment(versionFragment).commit();
        }
    }

    private void resetCheckView() {
        binding.llBlueTooth.setBackgroundResource(R.drawable.bg_translate);
        binding.ivBluetooth.setImageResource(R.mipmap.ic_bluetooth_setting);
        binding.llWifi.setBackgroundResource(R.drawable.bg_translate);
        binding.ivWifi.setImageResource(R.mipmap.ic_wifi_setting);
        binding.llTime.setBackgroundResource(R.drawable.bg_translate);
        binding.ivTime.setImageResource(R.mipmap.ic_time_setting);
        binding.llLanguage.setBackgroundResource(R.drawable.bg_translate);
        binding.ivLanguage.setImageResource(R.mipmap.ic_language_setting);
        binding.llFans.setBackgroundResource(R.drawable.bg_translate);
        binding.ivFans.setImageResource(R.mipmap.ic_fans_setting);
        binding.llVersion.setBackgroundResource(R.drawable.bg_translate);
        binding.ivVersion.setImageResource(R.mipmap.ic_version_setting);
    }

    private FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.frameLayout, targetFragment, targetFragment.getClass().getName()).show(targetFragment);
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }
} 
