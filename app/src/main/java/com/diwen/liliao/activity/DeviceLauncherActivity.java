package com.diwen.liliao.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.InputDialog;
import com.diwen.liliao.R;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.BlufiMessageItemBinding;
import com.diwen.liliao.databinding.LayoutDevicelauncheractivityBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.DeviceModel;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;
import com.diwen.liliao.utils.AtyUtils;
import com.diwen.liliao.utils.DeviceValueUtils;
import com.diwen.liliao.utils.ForbadClick;
import com.diwen.liliao.utils.NetworkMonitor;
import com.diwen.liliao.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created By  tian on 2024/7/16
 * Describe: 设备控制
 */
@BindEventBus
public class DeviceLauncherActivity extends MqttBaseActivity<LayoutDevicelauncheractivityBinding> {

    private long AnimatorTime = 1000;
    private int mine = 10;
    private int seconds = 0;
    private int MusicalState = 1;
    private int Launch = DeviceLaunchStateController.LAUNCH_UNKNOWN;//
    private int AirBlowerRun = 3;
    private int prepareSeconds = 10;
    private int prepareRemainingSeconds = 10;
    private ArrayList<SettingItem> settingItems;
    private int PluseMode = 0;
    private String deviceName;
    private List<String> mMsgList;
    private MsgAdapter mMsgAdapter;

    @Override
    protected void handleIntent(Intent intent) {
    }

    @Override
    protected void setUiText() {
        setLaunch();
        binding.tvRemainingTime.setText(StringUtils.getText("剩余时间"));
        binding.tvMusic.setText(StringUtils.getUpperText("音乐"));
        binding.tvPlayingNow.setText(StringUtils.getText("开始播放"));
        binding.tvPrepareTitle.setText(StringUtils.getUpperText("开始延时秒数"));
        binding.tvPrepareHint.setText(StringUtils.getText("现在上床"));
        binding.tvPrepareSeconds.setText(String.valueOf(prepareSeconds));
        if (DemoApp.getInstance().buildCompany) {

        } else {
            settingItems = new ArrayList<>();
            settingItems.add(new SettingItem(R.mipmap.icon_model1, "肌肉恢复", 1));
            settingItems.add(new SettingItem(R.mipmap.icon_model2, "疼痛缓解", 2));
            settingItems.add(new SettingItem(R.mipmap.icon_model3, "瘦身", 3));
            settingItems.add(new SettingItem(R.mipmap.icon_model4, "胶原蛋白增生", 4));
            settingItems.add(new SettingItem(R.mipmap.icon_model5, "手动调节", 5));
            settingItems.add(new SettingItem(R.mipmap.icon_model6, "自动调节", 6));
            if (PluseMode != 0) {
                for (SettingItem item : settingItems) {
                    if (item.getDeviceModel() == PluseMode) {
                        binding.modelName.setText(DemoApp.getInstance().getAppViewModel().getLangText(item.getTitle()));
                    }
                }
            }

            for (DeviceModel model : DemoApp.getInstance().getAppViewModel().device.getValue()) {
                if (model.isConnectWifi()) {
                    binding.ivWifi.setImageResource(R.mipmap.icon_wificonnect);
                } else {
                    binding.ivWifi.setImageResource(R.mipmap.icon_wificonnectdis);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void config() {
        getAllAttributes();
        mMsgList = new ArrayList<>();
        mMsgAdapter = new MsgAdapter();
        binding.mesreList.setAdapter(mMsgAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(826);
            handler.removeMessages(827);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        deviceName = MyMMKV.getDeviceName();
        if (AtyUtils.isStringEmpty(AtyUtils.getText(binding.tvDeviceName))) {
            if (!AtyUtils.getText(binding.tvDeviceName).equals(deviceName)) {
                if (handler != null) {
                    handler.removeMessages(826);
                }
                binding.tvDeviceName.setText(deviceName);
                getAllAttributes();

            }
        } else {
            binding.tvDeviceName.setText(MyMMKV.getDeviceName());
        }

    }

    @Override
    protected void setListener() {
        binding.ivSong1.setOnClickListener(this);
        binding.ivSong2.setOnClickListener(this);
        binding.ivSong3.setOnClickListener(this);
        binding.rlStartRight.setOnClickListener(this);
        binding.rlStartLeft.setOnClickListener(this);
        // 加号按钮
        setupButton(binding.rlStartRight, true);
        // 减号按钮
        setupButton(binding.rlStartLeft, false);
        binding.rlStart.setOnClickListener(this);
        binding.tvPrepareSeconds.setOnClickListener(this);
        binding.ivPrepareBack.setOnClickListener(this);
        binding.ivFinish.setOnClickListener(this);
        binding.ivSetting.setOnClickListener(this);
        binding.ivRecord.setOnClickListener(this);
        binding.ivMaiChong.setOnClickListener(this);
        binding.fenshanJian.setOnClickListener(this);
        binding.fenshanJia.setOnClickListener(this);
        binding.ivBl.setOnClickListener(this);
        binding.llDeviceName.setOnClickListener(this);
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(PadSAttribute.Volume.getAttribute(), seekBar.getProgress());
                    DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.rlStart.setOnLongClickListener(v -> {
            try {
                JSONObject jsonObject = new JSONObject();
                int nextLaunch = DeviceLaunchStateController.nextLaunchForLongPress(Launch);
                jsonObject.put(PadSAttribute.Launch.getAttribute(), nextLaunch);
                DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
                showLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });
    }


    // 用于控制长按自动加减
    private Handler longClickHandler = new Handler(Looper.getMainLooper());
    private boolean isLongPressing = false;

    // 控制连续加减的速度（越小越快，单位：毫秒）
    private static final int REPEAT_INTERVAL = 100;

    /**
     * 设置按钮点击与长按逻辑
     */
    private void setupButton(RelativeLayout button, boolean isAdd) {
        // 长按时开始快速变化
        button.setOnLongClickListener(v -> {
            isLongPressing = true;
            longClickHandler.post(new RepeatRunnable(isAdd));
            return true; // 返回 true 表示长按事件已消费
        });

        // 松手或离开按钮区域时停止
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                isLongPressing = false;
                setDeviceTimeMin();
            }
            return false; // 让点击事件继续生效
        });
    }

    /**
     * 连续执行加减的 Runnable
     */
    private class RepeatRunnable implements Runnable {
        private final boolean isAdd;

        RepeatRunnable(boolean isAdd) {
            this.isAdd = isAdd;
        }

        @Override
        public void run() {
            if (PluseMode != 5) {
                return;
            }
            if (Launch != 2) {
                return;
            }
            if (isLongPressing) {
                if (isAdd) {
                    mine++;
                    if (mine >= 30) {
                        mine = 30;
                    }
                } else {
                    mine--;
                    if (mine <= 0) {
                        mine = 0;
                    }
                }
                binding.tvMine.setText(getPointTwo(mine));
                longClickHandler.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    }

    public void finishThis() {
        BTCodeUtils.getInstance().finishTo(3);
        if (DemoApp.getInstance().buildCompany) {
            if (!ActivityUtils.isActivityExistsInStack(MaiChongSettingActivity.class)) {
                ActivityUtils.startActivity(MaiChongSettingActivity.class);
            }
        } else {
            if (!ActivityUtils.isActivityExistsInStack(DeviceModelActivity.class)) {
                ActivityUtils.startActivity(DeviceModelActivity.class);
            }
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivFinish) {
            finishThis();
        }
        if (v == binding.rlStartRight) {
            if (ForbadClick.isFastDoubleClick(1)) {
                return;
            }
            if (PluseMode != 5) {
                return;
            }
            if (Launch != 2) {
                return;
            }
            mine++;
            if (mine >= 30) {
                mine = 30;
            }
            binding.tvMine.setText(getPointTwo(mine));
            setDeviceTimeMin();
        }
        if (v == binding.rlStartLeft) {
            if (ForbadClick.isFastDoubleClick(1)) {
                return;
            }
            if (PluseMode != 5) {
                return;
            }
            if (Launch != 2) {
                return;
            }
            mine--;
            if (mine <= 0) {
                mine = 0;
            }
            binding.tvMine.setText(getPointTwo(mine));
            setDeviceTimeMin();
        }
        if (v == binding.rlStart) {
            try {
                JSONObject jsonObject = new JSONObject();
                int nextLaunch = DeviceLaunchStateController.nextLaunchForStartButton(Launch);
                if (nextLaunch == DeviceLaunchStateController.NO_LAUNCH_COMMAND) {
                    return;
                }
                jsonObject.put(PadSAttribute.Launch.getAttribute(), nextLaunch);
                if (nextLaunch == DeviceLaunchStateController.LAUNCH_PREPARING) {
                    startPrepareCountdown();
                }
                DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
                showLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (v == binding.ivSetting) {
            ActivityUtils.startActivity(new Intent(mContext, DeviceSettingActivity.class));
        }
        if (v == binding.ivRecord) {
            ActivityUtils.startActivity(UseHitorActivity.class);
        }
        if (v == binding.ivMaiChong) {
            ActivityUtils.startActivity(DeviceModelActivity.class);
        }
        if (v == binding.fenshanJia) {
            if (Launch == 1) {
                if (AirBlowerRun >= 5) {
                    return;
                }
                AirBlowerRun++;
                binding.seekbarFengshan.setProgress(AirBlowerRun);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(PadSAttribute.AirBlowerRun.getAttribute(), AirBlowerRun);
                    DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
                    showLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (v == binding.fenshanJian) {
            if (Launch == 1) {
                if (AirBlowerRun <= 1) {
                    return;
                }
                AirBlowerRun--;
                binding.seekbarFengshan.setProgress(AirBlowerRun);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(PadSAttribute.AirBlowerRun.getAttribute(), AirBlowerRun);
                    DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
                    showLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (v == binding.ivBl) {
//            ActivityUtils.startActivity(new Intent(mContext, BlStateActivity.class));
        }
        if (v == binding.tvPrepareSeconds) {
            showPrepareSecondsDialog();
        }
        if (v == binding.ivPrepareBack) {
            returnToModeSelection();
        }
        if (v == binding.llDeviceName) {
            ActivityUtils.startActivity(new Intent(mContext, DeviceListActivity.class));
        }
        if (v == binding.ivSong1) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PadSAttribute.CutSong.getAttribute(), 0);
                DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
                showLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (v == binding.ivSong3) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PadSAttribute.CutSong.getAttribute(), 1);
                showLoading();
                DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (v == binding.ivSong2) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PadSAttribute.MusicalState.getAttribute(), MusicalState == 1 ? 0 : 1);
                showLoading();
                DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
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
        } else if (MQTTCons.ACTION_DATA_AVAILABLE_BLUE.equals(event.getMessage())) {
//            mMsgList.add(event.getCase_message());
//            mMsgAdapter.notifyDataSetChanged();
        } else if (MQTTCons.NETWORK_CONNECTED.equals(event.getMessage())) {
            binding.ivWifi.setImageResource(R.mipmap.icon_wificonnect);
        } else if (MQTTCons.NETWORK_ERROR.equals(event.getMessage())) {
            binding.ivWifi.setImageResource(R.mipmap.icon_wificonnectdis);
        } else if (MQTTCons.ACTION_DEVICE_CHANGE.equals(event.getMessage())) {
            Launch = DeviceLaunchStateController.LAUNCH_UNKNOWN;
            setLaunch();
            getAllAttributes();
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
                binding.ivPhone.setImageResource(R.mipmap.icon_phoneline);
            } else {
                binding.ivPhone.setImageResource(R.mipmap.icon_phoneunline);
            }
        }
        if (strings.contains(PadSAttribute.SoftWareVer.getAttribute())) {
            MyMMKV.get().putString(MyMMKV.SoftWareVer, (String) map.get(PadSAttribute.SoftWareVer.getAttribute()));
        }
        if (strings.contains(PadSAttribute.Volume.getAttribute())) {
//            binding.ivPhone.setImageResource(R.mipmap.icon_phoneline);
            int Volume = (int) map.get(PadSAttribute.Volume.getAttribute());
            if (Volume > 0) {
                binding.seekbar.setProgress(Volume);
            }
        }
        if (strings.contains(PadSAttribute.Warning.getAttribute())) {
            int warning = (int) map.get(PadSAttribute.Warning.getAttribute());
            if (warning == 1) {
                binding.ivWarning.setVisibility(View.VISIBLE);
            } else {
                binding.ivWarning.setVisibility(View.GONE);
            }
        }
        if (strings.contains(PadSAttribute.MusicalState.getAttribute())) {
            MusicalState = (int) map.get(PadSAttribute.MusicalState.getAttribute());
            if (MusicalState == 1) {
                binding.ivSong2.setImageResource(R.mipmap.icon_song2);
            } else {
                binding.ivSong2.setImageResource(R.mipmap.icon_playing);
            }
        }
        if (strings.contains(PadSAttribute.DeviceTimeMin.getAttribute())) {
            mine = (int) map.get(PadSAttribute.DeviceTimeMin.getAttribute());
            binding.tvMine.setText(getPointTwo(mine));
            seconds = (int) map.get(PadSAttribute.DeviceTimeSecond.getAttribute());
            binding.tvSeconds.setText(getPointTwo(seconds));
        }
        if (strings.contains(PadSAttribute.GetReadySecond.getAttribute())) {
            prepareSeconds = DeviceValueUtils.coercePrepareSeconds((int) map.get(PadSAttribute.GetReadySecond.getAttribute()));
            if (binding.prepareOverlay.getVisibility() != View.VISIBLE) {
                binding.tvPrepareSeconds.setText(String.valueOf(prepareSeconds));
            }
        }
        if (strings.contains(PadSAttribute.Launch.getAttribute())) {
            int oLaunch = (int) map.get(PadSAttribute.Launch.getAttribute());
            if (Launch != oLaunch) {
                Launch = oLaunch;
                setLaunch();
                if (Launch == 1) {
                    allTime = mine * 60 + seconds;
                    handler.removeMessages(826);
                    handler.sendEmptyMessage(826);
                }
            }
        }
        if (strings.contains(PadSAttribute.AirBlowerRun.getAttribute())) {
            AirBlowerRun = Math.max(1, Math.min(5, (int) map.get(PadSAttribute.AirBlowerRun.getAttribute())));
            binding.seekbarFengshan.setProgress(AirBlowerRun);
        }
        if (strings.contains(PadSAttribute.AirBlowerStop.getAttribute())) {
            int AirBlowerStop = (int) map.get(PadSAttribute.AirBlowerStop.getAttribute());
            if (Launch == 2) {
                binding.seekbarFengshan.setProgress(AirBlowerRun);
            }
        }
        if (strings.contains(PadSAttribute.Language.getAttribute())) {
            int Language = (int) map.get(PadSAttribute.Language.getAttribute());
            int localLanguage = MyMMKV.getInteger(MyMMKV.Language);
            if (localLanguage != Language) {
                MyMMKV.putInteger(MyMMKV.Language, Language);
                DemoApp.getInstance().getAppViewModel().setLang();
            }
        }
        if (strings.contains(PadSAttribute.BtState.getAttribute())) {
            int BtState = (int) map.get(PadSAttribute.BtState.getAttribute());
            if (BtState == 1) {
                binding.ivBl.setImageResource(R.mipmap.icon_lanta);
            } else {
                binding.ivBl.setImageResource(R.mipmap.icon_lantadis);
            }
        }
        if (strings.contains(PadSAttribute.PluseMode.getAttribute())) {
            PluseMode = (int) map.get(PadSAttribute.PluseMode.getAttribute());
            if (DemoApp.getInstance().buildCompany) {

            } else {
                if (PluseMode != 0) {
                    for (SettingItem item : settingItems) {
                        if (item.getDeviceModel() == PluseMode) {
                            binding.modelName.setText(DemoApp.getInstance().getAppViewModel().getLangText(item.getTitle()));
                        }
                    }
                }
                if (PluseMode == 5 && Launch == 2) {
                    binding.ivmiue.setImageResource(R.mipmap.icon_jian);
                    binding.ivAdd.setImageResource(R.mipmap.icon_add);
                } else {
                    binding.ivmiue.setImageResource(R.mipmap.icon_jian_no);
                    binding.ivAdd.setImageResource(R.mipmap.icon_add_no);
                }
            }

        }
    }

    public void getAllAttributes() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.AirBlowerRun.getAttribute(), 1);
            jsonObject.put(PadSAttribute.MusicalState.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Volume.getAttribute(), 1);
            jsonObject.put(PadSAttribute.DeviceTimeMin.getAttribute(), 1);
            jsonObject.put(PadSAttribute.DeviceTimeSecond.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Launch.getAttribute(), 1);
            jsonObject.put(PadSAttribute.PluseMode.getAttribute(), 1);
            jsonObject.put(PadSAttribute.GetReadySecond.getAttribute(), 1);

            jsonObject.put(PadSAttribute.BtState.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Language.getAttribute(), 1);
            jsonObject.put(PadSAttribute.AirBlowerStop.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //设置时间
    public void setDeviceTimeMin() {
        try {
            showLoading();
            int M = Integer.parseInt(AtyUtils.getText(binding.tvMine));
            int S = Integer.parseInt(AtyUtils.getText(binding.tvSeconds));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.DeviceTimeMin.getAttribute(), M);
            jsonObject.put(PadSAttribute.DeviceTimeSecond.getAttribute(), +S);
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getPointTwo(int a) {
        if (a < 10) {
            return "0" + String.valueOf(a);
        }
        return String.valueOf(a);
    }

    private class MsgAdapter extends RecyclerView.Adapter<MsgHolder> {

        @NonNull
        @Override
        public MsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            BlufiMessageItemBinding binding = BlufiMessageItemBinding.inflate(
                    getLayoutInflater(),
                    parent,
                    false
            );
            return new MsgHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MsgHolder holder, int position) {
            String msg = mMsgList.get(position);
            holder.text1.setText(msg);
        }

        @Override
        public int getItemCount() {
            return mMsgList.size();
        }
    }

    private static class MsgHolder extends RecyclerView.ViewHolder {
        TextView text1;

        MsgHolder(BlufiMessageItemBinding binding) {
            super(binding.getRoot());

            text1 = binding.text1;
        }
    }

    private int allTime = 10 * 60;
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 826) {
                allTime--;
                if (allTime >= 0) {
                    handler.sendEmptyMessageDelayed(826, 1000);
                    setTime();
                } else {
                    handler.removeMessages(826);
                }
            }
            if (msg.what == 827) {
                prepareRemainingSeconds--;
                if (prepareRemainingSeconds > 0) {
                    binding.tvPrepareSeconds.setText(String.valueOf(prepareRemainingSeconds));
                    handler.sendEmptyMessageDelayed(827, 1000);
                } else {
                    finishPrepareCountdown();
                }
            }
        }
    };

    public void setTime() {
        int mine = allTime / 60;
        int seconds = allTime % 60;
        binding.tvMine.setText(getPointTwo(mine));
        binding.tvSeconds.setText(getPointTwo(seconds));
    }

    public void setLaunch() {
        // 0 暂停 1 启动 2 停止 3 准备
        if (Launch == DeviceLaunchStateController.LAUNCH_UNKNOWN) {
            binding.ivStart.setImageResource(R.mipmap.ic_start);
            binding.tvStatus.setText(StringUtils.getUpperText("设置"));
        }
        if (Launch == 0) {
            binding.ivStart.setImageResource(R.mipmap.ic_stop);
            binding.tvStatus.setText(StringUtils.getUpperText("运行"));
//            binding.tvStart.setText(DemoApp.getInstance().getAppViewModel().getLangText("暂停"));//Pause
        }
        if (Launch == 1) {
            binding.ivStart.setImageResource(R.mipmap.ic_run);
            binding.tvStatus.setText(StringUtils.getUpperText("运行"));
//            binding.tvStart.setText(DemoApp.getInstance().getAppViewModel().getLangText("已停止"));//Stop
        }
        if (Launch == 2) {
            binding.ivStart.setImageResource(R.mipmap.ic_start);
            binding.tvStatus.setText(StringUtils.getUpperText("设置"));
//            binding.tvStart.setText(DemoApp.getInstance().getAppViewModel().getLangText("开始"));//Start
        }
        if (Launch == 3) {
            // 准备状态：正在进行启动倒计时
            binding.ivStart.setImageResource(R.mipmap.ic_start);
            binding.tvStatus.setText(StringUtils.getUpperText("准备"));
        }
        if (Launch != 1) {
            handler.removeMessages(826);
        }
        if (Launch != 3) {
            cancelPrepareCountdown();
        }
    }

    private void startPrepareCountdown() {
        prepareSeconds = DeviceValueUtils.coercePrepareSeconds(prepareSeconds);
        prepareRemainingSeconds = prepareSeconds;
        binding.tvPrepareSeconds.setText(String.valueOf(prepareRemainingSeconds));
        binding.prepareOverlay.setVisibility(View.VISIBLE);
        handler.removeMessages(827);
        handler.sendEmptyMessageDelayed(827, 1000);
    }

    private void cancelPrepareCountdown() {
        handler.removeMessages(827);
        binding.prepareOverlay.setVisibility(View.GONE);
    }

    private void finishPrepareCountdown() {
        cancelPrepareCountdown();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.Launch.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
            showLoading();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void returnToModeSelection() {
        cancelPrepareCountdown();
        ActivityUtils.startActivity(new Intent(mContext, DeviceModelActivity.class)
                .putExtra("name", MyMMKV.getDeviceName()));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (binding.prepareOverlay.getVisibility() == View.VISIBLE) {
            returnToModeSelection();
            return;
        }
        super.onBackPressed();
    }

    private void showPrepareSecondsDialog() {
        InputDialog dialog = new InputDialog(mContext);
        dialog.setMaxInputValue(DeviceValueUtils.MAX_PREPARE_SECONDS);
        dialog.setshow(String.valueOf(prepareSeconds));
        dialog.setOnitemchildClicke((view, postion, obj) -> {
            try {
                prepareSeconds = DeviceValueUtils.coercePrepareSeconds(Integer.parseInt(String.valueOf(obj)));
                prepareRemainingSeconds = prepareSeconds;
                binding.tvPrepareSeconds.setText(String.valueOf(prepareSeconds));
                setPrepareSeconds();
                if (binding.prepareOverlay.getVisibility() == View.VISIBLE) {
                    handler.removeMessages(827);
                    handler.sendEmptyMessageDelayed(827, 1000);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        });
        dialog.showDialog();
    }

    private void setPrepareSeconds() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.GetReadySecond.getAttribute(), prepareSeconds);
            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
} 
