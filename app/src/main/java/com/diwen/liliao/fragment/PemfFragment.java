package com.diwen.liliao.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.adapter.ButtonAirListAdapter;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.databinding.FragmentPemfBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.AtyUtils;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@BindEventBus
public class PemfFragment extends BaseFragment<FragmentPemfBinding> {
    private ButtonAirListAdapter intensityAdapter;
    private ArrayList<SettingItem> intensityList;
    private int pemfState = 1;
    private boolean pemfRunning;

    public static PemfFragment newInstance() {
        Bundle args = new Bundle();
        PemfFragment fragment = new PemfFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvFrequency.setText(lang("频率", "Frequency"));
        binding.tvIntensity.setText(lang("强度", "Intensity"));
        binding.tvTreatmentTime.setText(lang("治疗时间", "Treatment Time"));
        binding.tvSave.setText(lang("保存", "Save"));
        binding.tvAutoLabel.setText(lang("自动", "AUTO"));
        updateAutoUi();
        updateManualUi();
    }

    @Override
    protected void initData() {
        intensityList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            intensityList.add(new SettingItem());
        }
        intensityAdapter = new ButtonAirListAdapter(intensityList);
        binding.rectangle.setAdapter(intensityAdapter);
        selectIntensity(1);
        queryPemfAttribute();

        binding.tvSave.setOnClickListener(view -> savePemf());
        binding.llPemfAuto.setOnClickListener(view -> {
            pemfState = pemfState == 1 ? 0 : 1;
            if (pemfState == 1) {
                pemfRunning = false;
            }
            updateAutoUi();
            updateManualUi();
        });
        binding.btnPemfManual.setOnClickListener(view -> {
            if (pemfState == 1) {
                return;
            }
            pemfRunning = !pemfRunning;
            updateManualUi();
        });
        intensityAdapter.setOnItemClickListener((adapter, view, position) -> selectIntensity(position + 1));
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
            if (map == null || !DeviceId.equals(MyMMKV.getDeviceName())) {
                return;
            }
            Set<String> strings = map.keySet();
            if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
                Integer onLineState = getInt(map.get(PadSAttribute.onLineState.getAttribute()));
                if (onLineState != null && onLineState == 1) {
                    queryPemfAttribute();
                }
            }
            if (strings.contains(PadSAttribute.PemfFrequncy.getAttribute())) {
                setTextIfChanged(binding.evFrequency, String.valueOf(getIntValue(map.get(PadSAttribute.PemfFrequncy.getAttribute()))));
            }
            if (strings.contains(PadSAttribute.PemfIntensity.getAttribute())) {
                selectIntensity(getIntValue(map.get(PadSAttribute.PemfIntensity.getAttribute())));
            }
            if (strings.contains(PadSAttribute.PemfTreatmentTime.getAttribute())) {
                setTextIfChanged(binding.evTreatmentTime, String.valueOf(getIntValue(map.get(PadSAttribute.PemfTreatmentTime.getAttribute()))));
            }
            if (strings.contains(PadSAttribute.PemfState.getAttribute())) {
                pemfState = getIntValue(map.get(PadSAttribute.PemfState.getAttribute()));
                if (pemfState == 1) {
                    pemfRunning = false;
                }
                updateAutoUi();
                updateManualUi();
            }
        } catch (Exception e) {
            ToastUtils.show(e.toString());
            e.printStackTrace();
        }
    }

    private void queryPemfAttribute() {
        JSONObject jsonObject = BTCodeUtils.getInstance().queryPemfAttribute();
        DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
    }

    private void savePemf() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.evFrequency))) {
                jsonObject.put(PadSAttribute.PemfFrequncy.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evFrequency)));
            }
            jsonObject.put(PadSAttribute.PemfIntensity.getAttribute(), getSelectedIntensity());
            if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.evTreatmentTime))) {
                jsonObject.put(PadSAttribute.PemfTreatmentTime.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evTreatmentTime)));
            }
            jsonObject.put(PadSAttribute.PemfState.getAttribute(), pemfState == 1 ? 1 : 0);
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void selectIntensity(int intensity) {
        if (intensityAdapter == null) {
            return;
        }
        int checked = Math.max(1, Math.min(5, intensity));
        for (int i = 0; i < intensityAdapter.getData().size(); i++) {
            intensityAdapter.getData().get(i).setChose(checked == i + 1);
        }
        intensityAdapter.notifyDataSetChanged();
    }

    private int getSelectedIntensity() {
        for (int i = 0; i < intensityAdapter.getData().size(); i++) {
            if (intensityAdapter.getData().get(i).isChose()) {
                return i + 1;
            }
        }
        return 1;
    }

    private void updateAutoUi() {
        boolean auto = pemfState == 1;
        binding.btnPemfManual.setVisibility(auto ? View.GONE : View.VISIBLE);
        binding.tvPemfAutoState.setText(auto ? "ON" : "OFF");
        binding.tvPemfAutoState.getShapeDrawableBuilder()
                .setSolidColor(Color.parseColor(auto ? "#10A5F9" : "#3A2E67"))
                .intoBackground();
    }

    private void updateManualUi() {
        binding.ivRunning.setImageResource(pemfRunning ? R.mipmap.ic_running_true : R.mipmap.ic_running_false);
        binding.ivPemfManual.setImageResource(pemfRunning ? R.mipmap.ic_pemf_stop : R.mipmap.ic_pemf_start);
        binding.tvPemfManual.setText(pemfRunning ? lang("停止", "STOP") : lang("启动", "START"));
    }

    private String lang(String key, String fallback) {
        String text = DemoApp.getInstance().getAppViewModel().getLangText(key);
        return AtyUtils.isStringEmpty(text) ? text : fallback;
    }

    private int getIntValue(Object value) {
        Integer result = getInt(value);
        return result == null ? 0 : result;
    }

    private Integer getInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String && AtyUtils.isStringEmpty((String) value)) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private void setTextIfChanged(TextView view, String value) {
        if (!value.equals(AtyUtils.getText(view))) {
            view.setText(value);
        }
    }
}
