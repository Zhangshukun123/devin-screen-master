package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.adapter.LanguageListAdapter;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutLanguagesettingactivityBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created By  tian on 2024/7/16
 * Describe:  语言设置
 */
@BindEventBus
public class LanguageSettingActivity extends MqttBaseActivity<LayoutLanguagesettingactivityBinding> {
    private ArrayList<SettingItem> settingItems;
    private LanguageListAdapter languageListAdapter;

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void setUiText() {
        binding.tvSave.setText(DemoApp.getInstance().getAppViewModel().getLangText("保存"));
    }

    @Override
    protected void config() {
        binding.tvName.setText(MyMMKV.getDeviceName());
        settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(R.mipmap.icon_language1, "英语"));
        settingItems.add(new SettingItem(R.mipmap.icon_language2, "德语"));
        settingItems.add(new SettingItem(R.mipmap.icon_language5, "法语"));
        settingItems.add(new SettingItem(R.mipmap.icon_language3, "意大利语"));
        settingItems.add(new SettingItem(R.mipmap.icon_language4, "西班牙语"));
        languageListAdapter = new LanguageListAdapter(settingItems);
        binding.languageList.setAdapter(languageListAdapter);
        getAllAttributes();
    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this);
        binding.tvSave.setOnClickListener(this);
        languageListAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (SettingItem datum : languageListAdapter.getData()) {
                datum.setChose(false);
            }
            languageListAdapter.getItem(position).setChose(true);
            languageListAdapter.notifyDataSetChanged();

            switch (position) {
                case 0:
                    binding.tvSave.setText("Save");
                    break;
                case 1:
                    binding.tvSave.setText("Speichern");
                    break;
                case 2:
                    binding.tvSave.setText("Enregistrer");
                    break;
                case 3:
                    binding.tvSave.setText("Salva");
                    break;
                case 4:
                    binding.tvSave.setText("Guardar");
                    break;
            }

        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            BTCodeUtils.getInstance().finishTo(2);

            ActivityUtils.finishActivity(LanguageSettingActivity.class);
        }
        if (v == binding.tvSave) {
            try {
                for (int i = 0; i < languageListAdapter.getData().size(); i++) {
                    if (languageListAdapter.getData().get(i).isChose()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(PadSAttribute.Language.getAttribute(), i + 1);
                        DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
                        MyMMKV.putInteger(MyMMKV.Language, i + 1);
                        DemoApp.getInstance().getAppViewModel().setLang();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(MQTTCons.ACTION_DATA_AVAILABLE)) {
            MqttParseOverModel model = event.getMqttParseOverModel();
            MqttMessage(model.getDeviceId(), model.getMap());
        }
    }

    @Override
    protected void MqttMessage(String DeviceId, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        if (!DeviceId.equals(MyMMKV.getDeviceName())) {
            return;
        }
        closeLoading();
        Set<String> strings = map.keySet();
        if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
            int onLineState = (int) map.get(PadSAttribute.onLineState.getAttribute());
            if (onLineState == 1) {
                getAllAttributes();
            }
        }
        if (strings.contains(PadSAttribute.Language.getAttribute())) {
            int Language = (int) map.get(PadSAttribute.Language.getAttribute());
            for (int i = 0; i < languageListAdapter.getData().size(); i++) {
                languageListAdapter.getData().get(i).setChose(false);
            }
            languageListAdapter.getData().get(Language - 1).setChose(true);
            languageListAdapter.notifyDataSetChanged();
            MyMMKV.putInteger(MyMMKV.Language, Language);
        }

    }

    private void getAllAttributes() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.Language.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

} 
