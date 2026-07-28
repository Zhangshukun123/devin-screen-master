package com.diwen.liliao.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.databinding.FragmentBluetoothBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.AtyUtils;
import com.diwen.liliao.utils.StringUtils;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

@BindEventBus
public class BluetoothFragment extends BaseFragment<FragmentBluetoothBinding> implements View.OnClickListener {
    public static BluetoothFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothFragment fragment = new BluetoothFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvBluetooth.setText(StringUtils.getText("蓝牙"));
        binding.tvRename.setText(StringUtils.getText("重命名"));
        binding.tvSave.setText(StringUtils.getText("保存"));
        binding.tvBtLeftName.setText(StringUtils.getText("设备名称"));
        binding.tvBtLeftPwd.setText(StringUtils.getText("密码"));

    }

    @Override
    protected void initData() {
        binding.tvMusicPlay.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (TextUtils.isEmpty(AtyUtils.getText(binding.tvMusicPlay))) {
                        ToastUtils.show("蓝牙名称不能为空");
                    } else {
                        resetName();

                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(binding.tvMusicPlay.getWindowToken(), 0);
                    }
                    return true;
                }

                return false;
            }
        });
        binding.tvSave.setOnClickListener(this);
        getAllAttributes();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(MQTTCons.ACTION_DATA_AVAILABLE)) {
            MqttParseOverModel model = event.getMqttParseOverModel();
            MqttMessage(model.getDeviceId(), model.getMap());
        }
    }

    private void MqttMessage(String DeviceId, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        if (!DeviceId.equals(MyMMKV.getDeviceName())) {
            return;
        }
        Set<String> strings = map.keySet();
        if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
            getAllAttributes();
        }
        if (strings.contains(PadSAttribute.BtName.getAttribute())) {
            String BtState = (String) map.get(PadSAttribute.BtName.getAttribute());
            binding.tvMusicPlay.setText(BtState);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.tvMusicPlay) {
//            binding.llReset.setVisibility(View.VISIBLE);
        } else if (view == binding.tvSave) {
            binding.llReset.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PadSAttribute.BtName.getAttribute(), AtyUtils.getText(binding.tvMusicPlay));
                DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetName() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.BtName.getAttribute(), AtyUtils.getText(binding.tvMusicPlay));
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAllAttributes() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.BtName.getAttribute(), 1);
            jsonObject.put(PadSAttribute.PemfEnable.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
