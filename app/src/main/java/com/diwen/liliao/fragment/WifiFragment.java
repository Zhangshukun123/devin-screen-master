package com.diwen.liliao.fragment;

import android.os.Bundle;

import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.databinding.FragmentWifiBinding;

public class WifiFragment extends BaseFragment<FragmentWifiBinding> {
    public static WifiFragment newInstance() {

        Bundle args = new Bundle();

        WifiFragment fragment = new WifiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
