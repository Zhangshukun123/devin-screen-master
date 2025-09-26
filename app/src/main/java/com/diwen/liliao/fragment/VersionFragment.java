package com.diwen.liliao.fragment;

import android.os.Bundle;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.databinding.FragmentVersionBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.StringUtils;
import com.diwen.liliao.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class VersionFragment extends BaseFragment<FragmentVersionBinding> {
    public static VersionFragment newInstance() {

        Bundle args = new Bundle();

        VersionFragment fragment = new VersionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvVersion.setText(StringUtils.getUpperText("版本"));
        binding.tvName.setText(DemoApp.getInstance().getAppViewModel().getLangText("名称"));
        binding.tvUIVersion.setText(String.valueOf(Utils.getVersionName(getActivity())));
        binding.tvSwVersion.setText(MyMMKV.getString(MyMMKV.SoftWareVer));
    }

    @Override
    protected void initData() {
    }

}
