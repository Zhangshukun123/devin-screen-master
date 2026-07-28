package com.diwen.liliao.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.adapter.ButtonAirListAdapter;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.databinding.FragmentFansBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.AtyUtils;
import com.diwen.liliao.utils.DeviceValueUtils;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@BindEventBus
public class FansFragment extends BaseFragment<FragmentFansBinding> {
    private ButtonAirListAdapter buttonAirListAdapter;
    private ArrayList<SettingItem> buttonList;
    private boolean syncingTemperature;

    public static FansFragment newInstance() {
        Bundle args = new Bundle();
        FansFragment fragment = new FansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvSave.setText(DemoApp.getInstance().getAppViewModel().getLangText("保存"));
        binding.tv1.setText(DemoApp.getInstance().getAppViewModel().getLangText("人体循环风机"));
        binding.tv3.setText(DemoApp.getInstance().getAppViewModel().getLangText("设备风机设置"));
        binding.tvSet1.setText(DemoApp.getInstance().getAppViewModel().getLangText("启动温度"));
        binding.tvSet3.setText(DemoApp.getInstance().getAppViewModel().getLangText("报警温度"));
        binding.tvChiLun.setText(DemoApp.getInstance().getAppViewModel().getLangText("等级"));
        binding.tvMineD.setText(DemoApp.getInstance().getAppViewModel().getLangText("分钟"));
        binding.tv2.setText(DemoApp.getInstance().getAppViewModel().getLangText("延迟关闭时间"));

    }

    @Override
    protected void initData() {
        buttonList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            buttonList.add(new SettingItem());
        }
        buttonAirListAdapter = new ButtonAirListAdapter(buttonList);
        binding.rectangle.setAdapter(buttonAirListAdapter);
        queryAirAttribute();
        setupTemperatureSync();

        binding.tvSave.setOnClickListener(view -> setAirBlower());
        buttonAirListAdapter.setOnItemClickListener((adapter, view, position) -> {
            selectFanGear(position + 1);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(MQTTCons.ACTION_DATA_AVAILABLE)) {
            MqttParseOverModel model = event.getMqttParseOverModel();
            MqttMessage(model.getDeviceId(), model.getMap());
        }
    }

    private void MqttMessage(String DeviceId, Map<String, Object> map) {
        try {
            if (map == null) {
                return;
            }
            if (DeviceId.equals(MyMMKV.getDeviceName())) {
                Set<String> strings = map.keySet();
                if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
                    int onLineState = (int) map.get(PadSAttribute.onLineState.getAttribute());
                    if (onLineState == 1) {
                        queryAirAttribute();
                    }
                }
                if (strings.contains(PadSAttribute.AirBlowerStop.getAttribute())) {
                    int AirBlower = (int) map.get(PadSAttribute.AirBlowerStop.getAttribute());
                    selectFanGear(AirBlower);
                }
                if (strings.contains(PadSAttribute.AirTime.getAttribute())) {
                    int AirTime = (int) map.get(PadSAttribute.AirTime.getAttribute());
                    binding.evMineClose.setText(getPointTwo(AirTime));
                }

                if (strings.contains(PadSAttribute.AirTemp.getAttribute())) {
                    int AirTemp = (int) map.get(PadSAttribute.AirTemp.getAttribute());
                    setEditableTemperature(binding.evStarTemp, binding.evStarTempF, AirTemp);
                }
                if (strings.contains(PadSAttribute.AirAlarm.getAttribute())) {
                    int AirAlarm = (int) map.get(PadSAttribute.AirAlarm.getAttribute());
                    setEditableTemperature(binding.evCreTemp, binding.evCreTempF, AirAlarm);
                }
                if (strings.contains(PadSAttribute.AirT1Temp.getAttribute())) {
                    int AirT1Temp = (int) map.get(PadSAttribute.AirT1Temp.getAttribute());
                    binding.evStarTempT1.setText(getPointTwo(AirT1Temp));
                    binding.evStarTempT1F.setText(String.valueOf(DeviceValueUtils.toFahrenheit(AirT1Temp)));
                }
                if (strings.contains(PadSAttribute.AirT2Temp.getAttribute())) {
                    int AirT2Temp = (int) map.get(PadSAttribute.AirT2Temp.getAttribute());
                    binding.evCreTempT2.setText(getPointTwo(AirT2Temp));
                    binding.evStarTempT2F.setText(String.valueOf(DeviceValueUtils.toFahrenheit(AirT2Temp)));
                }
            }
        } catch (Exception e) {
            ToastUtils.show(e.toString());
            e.printStackTrace();
        }
    }

    public String getPointTwo(int a) {
        if (a < 10) {
            return "0" + String.valueOf(a);
        }
        return String.valueOf(a);
    }

    public void queryAirAttribute() {
        JSONObject jsonObject = BTCodeUtils.getInstance().queryAirAttribute();
        DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
    }

    public void setAirBlower() {
        try {
            JSONObject jsonObject = new JSONObject();
            int selectedFanGear = getSelectedFanGear();
            if (selectedFanGear > 0) {
                jsonObject.put(PadSAttribute.AirBlowerStop.getAttribute(), selectedFanGear);
            }
            if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.evMineClose))) {
                jsonObject.put(PadSAttribute.AirTime.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evMineClose)));
            }
            if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.evStarTemp))) {
                jsonObject.put(PadSAttribute.AirTemp.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evStarTemp)));
            }
            if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.evCreTemp))) {
                jsonObject.put(PadSAttribute.AirAlarm.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evCreTemp)));
            }

            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void selectFanGear(int gear) {
        if (buttonAirListAdapter == null) {
            return;
        }
        for (int i = 0; i < buttonAirListAdapter.getData().size(); i++) {
            buttonAirListAdapter.getData().get(i).setChose(gear == i + 1);
        }
        buttonAirListAdapter.notifyDataSetChanged();
    }

    private int getSelectedFanGear() {
        for (int i = 0; i < buttonAirListAdapter.getData().size(); i++) {
            if (buttonAirListAdapter.getData().get(i).isChose()) {
                return i + 1;
            }
        }
        return 0;
    }

    private void setupTemperatureSync() {
        bindTemperatureWatcher(binding.evStarTemp, binding.evStarTempF, true);
        bindTemperatureWatcher(binding.evStarTempF, binding.evStarTemp, false);
        bindTemperatureWatcher(binding.evCreTemp, binding.evCreTempF, true);
        bindTemperatureWatcher(binding.evCreTempF, binding.evCreTemp, false);
    }

    private void bindTemperatureWatcher(EditText source, EditText target, boolean sourceIsCelsius) {
        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (syncingTemperature) {
                    return;
                }
                Integer value = parseInteger(s.toString());
                syncingTemperature = true;
                if (value == null) {
                    setTextIfChanged(target, "");
                } else {
                    int converted = sourceIsCelsius
                            ? DeviceValueUtils.toFahrenheit(value)
                            : DeviceValueUtils.toCelsius(value);
                    setTextIfChanged(target, String.valueOf(converted));
                }
                syncingTemperature = false;
            }
        });
    }

    private void setEditableTemperature(EditText celsiusView, EditText fahrenheitView, int celsius) {
        syncingTemperature = true;
        setTextIfChanged(celsiusView, getPointTwo(celsius));
        setTextIfChanged(fahrenheitView, String.valueOf(DeviceValueUtils.toFahrenheit(celsius)));
        syncingTemperature = false;
    }

    private Integer parseInteger(String value) {
        try {
            if (!AtyUtils.isStringEmpty(value)) {
                return null;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setTextIfChanged(TextView view, String value) {
        if (!value.equals(AtyUtils.getText(view))) {
            view.setText(value);
        }
    }
}
