package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.InputDialog;
import com.diwen.liliao.R;
import com.diwen.liliao.adapter.MaiChongAdapter;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutMaichongactivityBinding;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created By  tian on 2024/7/16
 * Describe:   脉冲设置
 */
@BindEventBus
public class MaiChongSettingActivity extends MqttBaseActivity<LayoutMaichongactivityBinding> {
    private MaiChongAdapter maiChongAdapter;
    private ArrayList<SettingItem> buttonList;
    private int IntelligentMode;
    private String[] splitset = new String[6];
    private boolean allChose = true;
    private InputDialog inputDialog;
    private String deviceName;

    @Override
    protected void handleIntent(Intent intent) {
        IntelligentMode = intent.getIntExtra("IntelligentMode", 0);
        deviceName = intent.getStringExtra("name");
    }

    @Override
    protected void setUiText() {
        binding.tvMaiChong.setText(StringUtils.getUpperText("脉冲"));
        binding.tvSave.setText(DemoApp.getInstance().getAppViewModel().getLangText("保存"));
    }

    @Override
    protected void config() {
        for (int i = 0; i < 6; i++) {
            splitset[i] = "0";
        }
        if (AtyUtils.isStringEmpty(deviceName)) {
            binding.tvName.setText(deviceName);
        } else {
            binding.tvName.setText(MyMMKV.getDeviceName());
        }
        buttonList = new ArrayList<>();
        binding.rlChose.setOnClickListener(v -> {
            if (allChose) {
                allChose = false;
                binding.ivChose.setVisibility(View.INVISIBLE);
            } else {
                allChose = true;
                binding.ivChose.setVisibility(View.VISIBLE);
            }
            for (SettingItem datum : maiChongAdapter.getData()) {
                datum.setChose(allChose);
            }
            maiChongAdapter.notifyDataSetChanged();
        });


        binding.evHz.setOnClickListener(v -> {
            inputDialog = new InputDialog(mContext);
            inputDialog.setshow(AtyUtils.getText(binding.evHz));
            inputDialog.setOnitemchildClicke((view, postion, obj) -> {
                binding.evHz.setText((String) obj);
                for (SettingItem o : buttonList) {
                    o.setPulseSetting((String) obj);
                }
                maiChongAdapter.notifyDataSetChanged();
            });
            inputDialog.setMaxInputValue(10000);
            inputDialog.showDialog();
        });
        binding.evKong.setOnClickListener(v -> {
            inputDialog = new InputDialog(mContext);
            inputDialog.setshow(AtyUtils.getText(binding.evKong));
            inputDialog.setOnitemchildClicke((view, postion, obj) -> {
                binding.evKong.setText((String) obj);
                for (SettingItem o : buttonList) {
                    o.setPulseDuty(AtyUtils.getText(binding.evKong));
                }
                maiChongAdapter.notifyDataSetChanged();
            });
            inputDialog.setMaxInputValue(100);
            inputDialog.showDialog();
        });
        binding.tv1.setText(StringUtils.getUpperText("全部的"));
        binding.tv2.setText(DemoApp.getInstance().getAppViewModel().getLangText("频率"));
        binding.tv3.setText(DemoApp.getInstance().getAppViewModel().getLangText("占空比"));


        buttonList.add(new SettingItem(R.mipmap.icon_lanta, "输出1"));
        buttonList.add(new SettingItem(R.mipmap.icon_lanta, "输出2"));
        buttonList.add(new SettingItem(R.mipmap.icon_lanta, "输出3"));
        buttonList.add(new SettingItem(R.mipmap.icon_lanta, "输出4"));
        buttonList.add(new SettingItem(R.mipmap.icon_lanta, "输出5"));
        for (SettingItem o : buttonList) {
            o.setChose(true);
        }
        maiChongAdapter = new MaiChongAdapter(buttonList);
        binding.rectangle.setAdapter(maiChongAdapter);
        queryMaiChongAttribute();
    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this::onClick);
        binding.tvSave.setOnClickListener(this);
        maiChongAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            maiChongAdapter.getItem(position).setChose(!maiChongAdapter.getItem(position).isChose());
            maiChongAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            if (ActivityUtils.isActivityExistsInStack(DeviceLauncherActivity.class)) {
                BTCodeUtils.getInstance().finishTo(1);
            }
            ActivityUtils.finishActivity(MaiChongSettingActivity.class);
            ActivityUtils.finishActivity(DeviceModelActivity.class);
            ActivityUtils.finishActivity(DeviceSettingActivity.class);
            ActivityUtils.finishActivity(DeviceListActivity.class);
            setMaiChonBlower();
        }
        if (v == binding.tvSave) {
            setMaiChonBlower();
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
                queryMaiChongAttribute();
            }
        }
        if (strings.contains(PadSAttribute.OutState.getAttribute())) {
            int OutState = (int) map.get(PadSAttribute.OutState.getAttribute());
            String binaryString = Integer.toBinaryString(OutState);
            String[] split = binaryString.split("");
            for (int i = 0; i < split.length; i++) {
                splitset[i] = split[i];
            }
            //从全部到5
            if (splitset[0].equals("0")) {
                binding.ivChose.setVisibility(View.INVISIBLE);
                allChose = false;
            } else {
                allChose = true;
                binding.ivChose.setVisibility(View.VISIBLE);
            }
            for (int i = 1; i < splitset.length; i++) {
                if (splitset[i].equals("0")) {
                    maiChongAdapter.getData().get(i - 1).setChose(false);
                } else {
                    maiChongAdapter.getData().get(i - 1).setChose(true);
                }
            }
            maiChongAdapter.notifyDataSetChanged();
        }

        if (strings.contains(PadSAttribute.PulseSetting0.getAttribute())) {
            int PulseSetting = (int) map.get(PadSAttribute.PulseSetting0.getAttribute());
            binding.evHz.setText(String.valueOf(PulseSetting));
            int PulseSetting1 = (int) map.get(PadSAttribute.PulseSetting1.getAttribute());
            int PulseSetting2 = (int) map.get(PadSAttribute.PulseSetting2.getAttribute());
            int PulseSetting3 = (int) map.get(PadSAttribute.PulseSetting3.getAttribute());
            int PulseSetting4 = (int) map.get(PadSAttribute.PulseSetting4.getAttribute());
            int PulseSetting5 = (int) map.get(PadSAttribute.PulseSetting5.getAttribute());


            maiChongAdapter.getData().get(0).setPulseSetting(PulseSetting1 + "");
            maiChongAdapter.getData().get(1).setPulseSetting(PulseSetting2 + "");
            maiChongAdapter.getData().get(2).setPulseSetting(PulseSetting3 + "");
            maiChongAdapter.getData().get(3).setPulseSetting(PulseSetting4 + "");
            maiChongAdapter.getData().get(4).setPulseSetting(PulseSetting5 + "");

        }

        if (strings.contains(PadSAttribute.PulseDuty0.getAttribute())) {
            int PulseDuty = (int) map.get(PadSAttribute.PulseDuty0.getAttribute());
            binding.evKong.setText(String.valueOf(PulseDuty));

            int PulseDuty1 = (int) map.get(PadSAttribute.PulseDuty1.getAttribute());
            int PulseDuty2 = (int) map.get(PadSAttribute.PulseDuty2.getAttribute());
            int PulseDuty3 = (int) map.get(PadSAttribute.PulseDuty3.getAttribute());
            int PulseDuty4 = (int) map.get(PadSAttribute.PulseDuty4.getAttribute());
            int PulseDuty5 = (int) map.get(PadSAttribute.PulseDuty5.getAttribute());


            maiChongAdapter.getData().get(0).setPulseDuty(PulseDuty1 + "");
            maiChongAdapter.getData().get(1).setPulseDuty(PulseDuty2 + "");
            maiChongAdapter.getData().get(2).setPulseDuty(PulseDuty3 + "");
            maiChongAdapter.getData().get(3).setPulseDuty(PulseDuty4 + "");
            maiChongAdapter.getData().get(4).setPulseDuty(PulseDuty5 + "");

        }

        maiChongAdapter.notifyDataSetChanged();
    }

    public void queryMaiChongAttribute() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (IntelligentMode == 0) {
                jsonObject.put(PadSAttribute.PluseMode.getAttribute(), 5);
            } else {
                jsonObject.put(PadSAttribute.PluseMode.getAttribute(), 6);
            }
            jsonObject.put(PadSAttribute.PulseDuty0.getAttribute(), 1000);
            jsonObject.put(PadSAttribute.PulseSetting0.getAttribute(), 100);
            StringBuffer binaryString = new StringBuffer();
            binaryString.append("1");
            for (int i = 0; i < maiChongAdapter.getData().size(); i++) {
                jsonObject.put(PadSAttribute.PulseDuty.getAttribute() + (i + 1), 1000);
                jsonObject.put(PadSAttribute.PulseSetting.getAttribute() + (i + 1), 100);
                binaryString.append("1");
            }
            if (AtyUtils.isStringEmpty(binaryString.toString())) {
                int decimal = Integer.parseInt(binaryString.toString(), 2);
                jsonObject.put(PadSAttribute.OutState.getAttribute(), decimal);
            }
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMaiChonBlower() {
        try {
            if (AtyUtils.isStringEmpty(deviceName)) {
                MyMMKV.get().putString("deviceName", deviceName);//点击的设备   判读设备是否在线
            }
            JSONObject jsonObject = new JSONObject();
            if (IntelligentMode == 0) {
                jsonObject.put(PadSAttribute.PluseMode.getAttribute(), 5);
            } else {
                jsonObject.put(PadSAttribute.PluseMode.getAttribute(), 6);
            }

            jsonObject.put(PadSAttribute.PulseSetting0.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evHz)));
            jsonObject.put(PadSAttribute.PulseDuty0.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evKong)));

            StringBuffer binaryString = new StringBuffer();
            if (allChose) {
                binaryString.append("1");
            } else {
                binaryString.append("0");
            }
            for (int i = 0; i < maiChongAdapter.getData().size(); i++) {
                jsonObject.put(PadSAttribute.PulseDuty.getAttribute() + (i + 1), Integer.parseInt(maiChongAdapter.getData().get(i).getPulseDuty()));
                jsonObject.put(PadSAttribute.PulseSetting.getAttribute() + (i + 1), Integer.parseInt(maiChongAdapter.getData().get(i).getPulseSetting()));
                if (maiChongAdapter.getData().get(i).isChose()) {
                    binaryString.append("1");
                } else {
                    binaryString.append("0");
                }
            }
            if (AtyUtils.isStringEmpty(binaryString.toString())) {
                int decimal = Integer.parseInt(binaryString.toString(), 2);
                jsonObject.put(PadSAttribute.OutState.getAttribute(), decimal);

            }
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
            if (ActivityUtils.isActivityExistsInStack(DeviceLauncherActivity.class)) {
                ActivityUtils.finishActivity(DeviceLauncherActivity.class);
            }
            ActivityUtils.startActivity(new Intent(mContext, DeviceLauncherActivity.class));
            ActivityUtils.finishActivity(MaiChongSettingActivity.class);
            ActivityUtils.finishActivity(DeviceModelActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
