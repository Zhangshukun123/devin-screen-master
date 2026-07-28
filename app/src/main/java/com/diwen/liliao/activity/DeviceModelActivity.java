package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.adapter.ModelListAdapter;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutDevicemodelactivityBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;
import com.diwen.liliao.utils.AtyUtils;
import com.diwen.liliao.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created By  tian on 2024/7/16
 * Describe:
 */
@BindEventBus
public class DeviceModelActivity extends MqttBaseActivity<LayoutDevicemodelactivityBinding> {
    private ArrayList<SettingItem> settingItems;
    private ModelListAdapter modelListAdapter;
    private String deviceName = "";

    @Override
    protected void handleIntent(Intent intent) {
        deviceName = intent.getStringExtra("name");
    }

    @Override
    protected void setUiText() {
        binding.tvTitle.setText(StringUtils.getUpperText("工作模式"));
        settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(R.mipmap.icon_model1, "肌肉恢复"));
        settingItems.add(new SettingItem(R.mipmap.icon_model2, "缓解疼痛"));
        settingItems.add(new SettingItem(R.mipmap.icon_model3, "减重"));
        settingItems.add(new SettingItem(R.mipmap.icon_model4, "促进胶原蛋白"));
        settingItems.add(new SettingItem(R.mipmap.icon_model6, "智能模式"));
        settingItems.add(new SettingItem(R.mipmap.icon_model5, "手动模式"));
        modelListAdapter = new ModelListAdapter(settingItems);
        binding.setTingList.setAdapter(modelListAdapter);
        modelListAdapter.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                case 0:
                    sendModel(1);
                    break;
                case 1:
                    sendModel(2);
                    break;
                case 2:
                    sendModel(3);
                    break;
                case 3:
                    sendModel(4);
                    break;
                case 4:
                    sendModel(6);
                    break;
                case 5:
                    ActivityUtils.startActivity(new Intent(mContext, MaiChongSettingActivity.class).putExtra("IntelligentMode", 0)
                            .putExtra("name", deviceName)
                    );
                    break;
            }
        });
    }

    public void sendModel(int PluseMode) {
        sendModel(PluseMode, true);
    }

    public void sendModel(int PluseMode, boolean finish) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.PluseMode.getAttribute(), PluseMode);
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
            if (!finish) {
                return;
            }
            showLoading();
            startPrepareLauncher();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startPrepareLauncher() {
        if (ActivityUtils.isActivityExistsInStack(DeviceLauncherActivity.class)) {
            ActivityUtils.finishActivity(DeviceLauncherActivity.class);
        }
        Intent intent = new Intent(mContext, DeviceLauncherActivity.class);
        ActivityUtils.startActivity(intent);
        ActivityUtils.finishActivity(DeviceModelActivity.class);
    }

    @Override
    protected void config() {
        if (AtyUtils.isStringEmpty(deviceName)) {
            binding.tvName.setText(deviceName);
        } else {
            binding.tvName.setText(MyMMKV.getDeviceName());
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.PulseSet.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this);
    }


    public void finishThis() {
        BTCodeUtils.getInstance().finishTo(1);
        if (ActivityUtils.isActivityExistsInStack(DeviceLauncherActivity.class)) {
            ActivityUtils.finishToActivity(DeviceLauncherActivity.class, false);
        } else if (ActivityUtils.isActivityExistsInStack(DeviceListActivity.class)) {
            ActivityUtils.finishToActivity(DeviceLauncherActivity.class, false);
        } else {
            ActivityUtils.finishActivity(DeviceModelActivity.class);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            finishThis();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(MQTTCons.ACTION_DATA_AVAILABLE)) {
            MqttParseOverModel model = event.getMqttParseOverModel();
            if (!model.getDeviceId().equals(MyMMKV.getDeviceName())) {
                return;
            }
            if (model.getMap() == null) {
                return;
            }
            closeLoading();
            Set<String> strings = model.getMap().keySet();
            if (strings.contains(PadSAttribute.Fitness.getAttribute())
                    || strings.contains(PadSAttribute.PainRelief.getAttribute())
                    || strings.contains(PadSAttribute.BobySim.getAttribute())
                    || strings.contains(PadSAttribute.BoostCollagen.getAttribute())
            ) {
                finish();
            }
        }
    }
} 
