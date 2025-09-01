package com.diwen.liliao.fragment;

import android.os.Bundle;

import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.databinding.FragmentBluetoothBinding;

public class BluetoothFragment extends BaseFragment<FragmentBluetoothBinding> {
    public static BluetoothFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothFragment fragment = new BluetoothFragment();
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
