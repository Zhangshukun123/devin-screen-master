package com.diwen.liliao.fragment;

import android.os.Bundle;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.databinding.FragmentVersionBinding;

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
        binding.tvUIVersionLeft.setText("UI Ver：");
        binding.tvSwVersionLeft.setText("SW Ver：");
        binding.tvUIVersion.setText("1.0.0.0");
        binding.tvSwVersion.setText("1.0.0.0");
    }

    @Override
    protected void initData() {

    }
}
