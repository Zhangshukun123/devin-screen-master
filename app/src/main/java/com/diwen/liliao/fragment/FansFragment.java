package com.diwen.liliao.fragment;

import android.os.Bundle;

import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.databinding.FragmentFansBinding;

public class FansFragment extends BaseFragment<FragmentFansBinding> {
    public static FansFragment newInstance() {

        Bundle args = new Bundle();

        FansFragment fragment = new FansFragment();
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
