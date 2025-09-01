package com.diwen.liliao.fragment;

import android.os.Bundle;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.databinding.FragmentWifiBinding;
import com.diwen.liliao.utils.StringUtils;

public class WifiFragment extends BaseFragment<FragmentWifiBinding> {
    public static WifiFragment newInstance() {

        Bundle args = new Bundle();

        WifiFragment fragment = new WifiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvWifi.setText(StringUtils.getText("设置WiFi"));
        binding.tvTop.setText(StringUtils.getText("设置WiFi"));
        binding.tvTop1.setText(StringUtils.getText("设置WiFi"));
        binding.tvDisWifi.setText(StringUtils.getText("断开"));
        binding.tvSaveWifi.setText(StringUtils.getText("保存"));
        binding.tvBottom.setText(StringUtils.getText("本地配置"));
        binding.tvBottom1.setText(StringUtils.getText("本地配置"));
        binding.tvSaveLocal.setText(StringUtils.getText("保存"));
        binding.tvDisLocal.setText(StringUtils.getText("断开"));

    }

    @Override
    protected void initData() {

    }
}
