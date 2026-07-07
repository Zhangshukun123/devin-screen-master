package com.diwen.liliao.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.diwen.liliao.utils.PemfPayloadBuilder;
import com.diwen.liliao.utils.UiTextUtils;
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
    private static final long AUTO_SAVE_DELAY_MS = 500L;
    private final Handler autoSaveHandler = new Handler(Looper.getMainLooper());
    private final Runnable autoSaveRunnable = new Runnable() {
        @Override
        public void run() {
            savePemf();
        }
    };
    private ButtonAirListAdapter intensityAdapter;
    private ArrayList<SettingItem> intensityList;
    private int pemfState = 1;
    private boolean pemfRunning;
    private boolean applyingDeviceValues;

    public static PemfFragment newInstance() {
        Bundle args = new Bundle();
        PemfFragment fragment = new PemfFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvFrequency.setText(label("频率", "Frequency"));
        binding.tvIntensity.setText(label("强度", "Intensity"));
        binding.tvTreatmentTime.setText(label("治疗时间", "Treatment Time"));
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
        setupAutoSave();
        queryPemfAttribute();

        binding.llPemfAuto.setOnClickListener(view -> {
            pemfState = pemfState == 1 ? 0 : 1;
            if (pemfState == 1) {
                pemfRunning = false;
            }
            updateAutoUi();
            updateManualUi();
            savePemfNow();
        });
        binding.btnPemfManual.setOnClickListener(view -> {
            if (pemfState == 1) {
                return;
            }
            pemfRunning = !pemfRunning;
            updateManualUi();
            sendPemfWorkState();
        });
        intensityAdapter.setOnItemClickListener((adapter, view, position) -> {
            selectIntensity(position + 1);
            savePemfNow();
        });

        setupHideKeyboardOnTouch();
    }

    @SuppressWarnings("ClickableViewAccessibility")
    private void setupHideKeyboardOnTouch() {
        binding.getRoot().setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboardIfOutside(event);
            }
            return false;
        });
    }

    private void hideKeyboardIfOutside(MotionEvent event) {
        if (binding == null) {
            return;
        }
        EditText current = getFocusedEditText();
        if (current == null) {
            return;
        }
        if (isTouchOutside(current, event)) {
            hideKeyboard(current);
            current.clearFocus();
        }
    }

    private EditText getFocusedEditText() {
        if (binding.evFrequency.hasFocus()) {
            return binding.evFrequency;
        }
        return null;
    }

    private boolean isTouchOutside(View view, MotionEvent event) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Rect rect = new Rect(location[0], location[1],
                location[0] + view.getWidth(), location[1] + view.getHeight());
        return !rect.contains((int) event.getRawX(), (int) event.getRawY());
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
            applyingDeviceValues = true;
            autoSaveHandler.removeCallbacks(autoSaveRunnable);
            Set<String> strings = map.keySet();
            if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
                Integer onLineState = getInt(map.get(PadSAttribute.onLineState.getAttribute()));
                if (onLineState != null && onLineState == 1) {
                    queryPemfAttribute();
                }
            }
            if (strings.contains(PadSAttribute.PemfFrequncy.getAttribute())) {
                setTextIfChanged(binding.evFrequency,
                        PemfPayloadBuilder.formatFrequencyForDisplay(
                                getIntValue(map.get(PadSAttribute.PemfFrequncy.getAttribute()))));
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
            if (strings.contains(PadSAttribute.PemfWorkState.getAttribute())) {
                pemfRunning = getIntValue(map.get(PadSAttribute.PemfWorkState.getAttribute())) == 1;
                updateManualUi();
            }
        } catch (Exception e) {
            ToastUtils.show(e.toString());
            e.printStackTrace();
        } finally {
            applyingDeviceValues = false;
        }
    }

    private void queryPemfAttribute() {
        JSONObject jsonObject = BTCodeUtils.getInstance().queryPemfAttribute();
        DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
    }

    private void savePemf() {
        try {
            JSONObject jsonObject = new JSONObject();
            Map<String, Integer> payload = PemfPayloadBuilder.build(
                    AtyUtils.getText(binding.evFrequency),
                    getSelectedIntensity(),
                    "",
                    pemfState == 1 ? 1 : 0
            );
            for (Map.Entry<String, Integer> entry : payload.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendPemfWorkState() {
        if (applyingDeviceValues) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.PemfWorkState.getAttribute(), pemfRunning ? 1 : 0);
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupAutoSave() {
        addAutoSaveTextWatcher(binding.evFrequency);
    }

    private void addAutoSaveTextWatcher(EditText source) {
        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                scheduleSavePemf();
            }
        });
    }

    private void scheduleSavePemf() {
        if (applyingDeviceValues) {
            return;
        }
        autoSaveHandler.removeCallbacks(autoSaveRunnable);
        autoSaveHandler.postDelayed(autoSaveRunnable, AUTO_SAVE_DELAY_MS);
    }

    private void savePemfNow() {
        if (applyingDeviceValues) {
            return;
        }
        autoSaveHandler.removeCallbacks(autoSaveRunnable);
        savePemf();
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
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) binding.tvPemfAutoState.getLayoutParams();
        layoutParams.gravity = (auto ? Gravity.END : Gravity.START) | Gravity.CENTER_VERTICAL;
        binding.tvPemfAutoState.setLayoutParams(layoutParams);
        binding.tvPemfAutoState.getShapeDrawableBuilder()
                .setSolidColor(Color.parseColor(auto ? "#10A5F9" : "#6A2ED8"))
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

    private String label(String key, String fallback) {
        return UiTextUtils.withoutTrailingColon(lang(key, fallback));
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

    @Override
    public void onDestroyView() {
        autoSaveHandler.removeCallbacks(autoSaveRunnable);
        super.onDestroyView();
    }
}
