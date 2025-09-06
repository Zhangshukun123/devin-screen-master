package com.diwen.liliao.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.adapter.DeviceListAdapter;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.ActivityMainBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.DeviceModel;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.netty.Constans;
import com.diwen.liliao.netty.LogUtil;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.MyUDPHarwder;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;
import com.diwen.liliao.utils.DataUtils;
import com.diwen.liliao.utils.DoubleClickExitDetector;
import com.diwen.liliao.utils.JsonUtils;
import com.diwen.liliao.utils.MqttWorkerThread;
import com.diwen.liliao.utils.ThreadPoolUtils;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

@BindEventBus
public class MainActivity extends MqttBaseActivity<ActivityMainBinding> {
    private DeviceListAdapter deviceListAdapter;
    private DoubleClickExitDetector exitDetector;

    @Override
    protected void handleIntent(Intent intent) {
    }

    @Override
    protected void config() {
        deviceListAdapter = new DeviceListAdapter(DemoApp.getInstance().getAppViewModel().device.getValue());
        binding.deviceList.setAdapter(deviceListAdapter);
        register();
    }

    @Override
    protected void setUiText() {
        binding.tvTitle.setText(DemoApp.getInstance().getAppViewModel().getLangText("设备列表"));
    }

    @Override
    public void onBackPressed() {
        if (exitDetector == null) {
            exitDetector = new DoubleClickExitDetector(this);
        }
        if (exitDetector.click()) {
            super.onBackPressed();
        }

    }

    @Override
    protected void setListener() {
        handler.sendEmptyMessage(31);
        deviceListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (deviceListAdapter.getItem(position).isConnectTcp()) {
                MyMMKV.get().putString("deviceName", deviceListAdapter.getItem(position).getDeviceName());//点击的设备   判读设备是否在线
                DemoApp.getInstance().getAppViewModel().connectNetty(deviceListAdapter.getItem(position).getDeviceName(), deviceListAdapter.getItem(position).getDeviceIp());
                if (DemoApp.getInstance().buildCompany) {
                    ActivityUtils.startActivity(new Intent(mContext, DeviceLauncherActivity.class));
                    return;
                }
                ActivityUtils.startActivity(new Intent(mContext, DeviceModelActivity.class));
            } else {
                ToastUtils.show("No networking");
                ActivityUtils.startActivity(new Intent(mContext, DeviceLauncherActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private MyUDPHarwder myUDPHarwder;

    public void getDeviceList() {
        try {
            if (myUDPHarwder == null) {
                myUDPHarwder = new MyUDPHarwder();
                myUDPHarwder.setListener(new MyUDPHarwder.Listener() {
                    @Override
                    public void returnData(int type, Object data, String udpOfIp) {
                        try {
                            String parseOkOfData = DataUtils.bytesToString((byte[]) data, DataUtils.GB2312);
                            Bundle bundle = new Bundle();
                            Message message = Message.obtain();
                            message.what = 30;
                            bundle.putString("json", parseOkOfData);
                            bundle.putString("ip", udpOfIp);
                            message.setData(bundle);
                            handler.sendMessage(message);
                            LogUtil.i("UDP:" + udpOfIp + "---" + parseOkOfData);
                        } catch (Exception e) {
                            LogUtil.i(e.toString());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void initOk() {
                        try {
                            myUDPHarwder.startScanDevice255(Constans.sid.getBytes("utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            myUDPHarwder = null;
                        }
                    }
                });
                myUDPHarwder.startReceiverUDP();
            } else {
                try {
                    myUDPHarwder.startScanDevice255(Constans.sid.getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                    myUDPHarwder = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            myUDPHarwder = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(30);
            handler.removeMessages(31);
        }
    }

    //{"deviceName":"B02","IntentName":"设备在线","Params":{"Launch":2}}
    //{"deviceName":"B03"}
    SpecialMessageReceiver mServerMessageReceiver;

    /*注册广播接收器*/
    private void register() {
        mServerMessageReceiver = new SpecialMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(MQTTCons.ACTION_MQTT_DISCONNECTED);
        filter.addAction(MQTTCons.ACTION_MQTT_CONNECTED);
        filter.addAction(MQTTCons.ACTION_MQTT_ERROR);
        filter.addAction(MQTTCons.ACTION_DATA_AVAILABLE);//收到mqtt消息
        filter.addAction(MQTTCons.EXTRA_ERROR_MESSAGE);//收到mqtt错误消息
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mServerMessageReceiver, filter);
    }

    private StringBuffer stringBuffer;

    private class SpecialMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                String message = intent.getStringExtra(MQTTCons.ACTION_DATA_AVAILABLE);
                EventBus.getDefault().post(new MessageEvent(MQTTCons.ACTION_DATA_AVAILABLE_BLUE, message));
                if (MQTTCons.ACTION_DATA_AVAILABLE.equals(action)) {
                    if (stringBuffer == null) {
                        stringBuffer = new StringBuffer();
                    }
                    if (message.startsWith("{") && message.endsWith("}")) {
                        stringBuffer.setLength(0);
                    }
                    stringBuffer.append(message);
                    if (isJson()) {
                        AnalysisMqtt(stringBuffer.toString());
                        stringBuffer.setLength(0);
                    }
                } else if (MQTTCons.EXTRA_ERROR_MESSAGE.equals(action)) {

                } else if (MQTTCons.ACTION_MQTT_CONNECTED.equals(action)) {
                    //连接成功  查询设备的  属性 
                } else if (MQTTCons.ACTION_MQTT_ERROR.equals(action)) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isJson() {
        try {
            new JSONObject(stringBuffer.toString());
            //    LogUtil.i("allGet-----------" + stringBuffer.toString());
            return true;
        } catch (JSONException e) {
            // 字符串不为JSON格式
            //   LogUtil.i("allGet++++++++++++++++" + stringBuffer.toString());
            return false;
        }
    }

    public void AnalysisMqtt(String intent) {
        MqttWorkerThread worker = new MqttWorkerThread(intent);
        ThreadPoolUtils.getInstance().execute(worker);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(MQTTCons.ACTION_DATA_AVAILABLE)) {
            MqttParseOverModel model = event.getMqttParseOverModel();
            MqttMessage(model.getDeviceId(), model.getMap());
        }
    }

    protected void MqttMessage(String DeviceId, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        closeLoading();
        Set<String> strings = map.keySet();
        if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
            int onLineState = (int) map.get(PadSAttribute.onLineState.getAttribute());
//            for (DeviceModel model : DemoApp.getInstance().getAppViewModel().device.getValue()) {
//                if (model.getDeviceName().equals(DeviceId)) {
//                    model.setConnectTcp(onLineState == 1);
//                    if (onLineState == 1) {
//                        //   getAllAttributes();
//                    }
//                }
//            }
//            deviceListAdapter.notifyDataSetChanged();
//            DemoApp.getInstance().getAppViewModel().device.postValue(DemoApp.getInstance().getAppViewModel().device.getValue());
            DeviceModel deviceModel = new DeviceModel();
            deviceModel.setDeviceName(DeviceId);
            deviceModel.setConnectTcp(onLineState == 1);
            DemoApp.getInstance().getAppViewModel().device.getValue().add(deviceModel);
            deviceListAdapter.notifyDataSetChanged();

        }
        if (strings.contains(PadSAttribute.Language.getAttribute())) {
            int Language = (int) map.get(PadSAttribute.Language.getAttribute());
            MyMMKV.putInteger(MyMMKV.Language, Language);
            DemoApp.getInstance().getAppViewModel().setLang();
        }
        if (strings.contains(PadSAttribute.Launch.getAttribute())) {
            int Launch = (int) map.get(PadSAttribute.Launch.getAttribute());
            if (Launch == 2) {
                //选中当前停机的设备
                if (MyMMKV.getDeviceName().equals(DeviceId)) {
                    return;
                }
                MyMMKV.get().putString("deviceName", DeviceId);//点击的设备   判读设备是否在线
                ActivityUtils.finishToActivity(MainActivity.class, false);
                ActivityUtils.startActivity(new Intent(mContext, DeviceLauncherActivity.class));
            }
        }
    }

    Handler handler = new Handler(Looper.myLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 30) {
                try {
                    Bundle bundle = msg.getData();
                    String parseOkOfData = bundle.getString("json");
                    String ip = bundle.getString("ip");
                    DeviceModel deviceModel = JsonUtils.parseObject(parseOkOfData, DeviceModel.class);
                    for (DeviceModel model : DemoApp.getInstance().getAppViewModel().device.getValue()) {
                        if (model.getDeviceName().equals(deviceModel.getDeviceName())) {
                            model.setConnectUdp(true);
                            model.setDeviceIp(ip);
                            DemoApp.getInstance().getAppViewModel().connectNetty(deviceModel.getDeviceName(), ip);
                            DemoApp.getInstance().getAppViewModel().upNettyIp(deviceModel.getDeviceName(), ip);
                        }
                    }
                    deviceListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (msg.what == 31) {
                for (DeviceModel model : DemoApp.getInstance().getAppViewModel().device.getValue()) {
                    model.setConnectUdp(false);
                }
                deviceListAdapter.notifyDataSetChanged();
                getDeviceList();
                handler.sendEmptyMessageDelayed(31, 10 * 1000);
            }
        }
    };

    public void getAllAttributes() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.BtState.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Language.getAttribute(), 1);
            jsonObject.put(PadSAttribute.AirBlowerStop.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}