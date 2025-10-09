package com.diwen.liliao;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;


import com.diwen.liliao.model.DeviceModel;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.NetworkMonitor;
import com.diwen.liliao.utils.Utils;
import com.diwen.liliao.viewmodel.AppViewModel;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import me.jessyan.autosize.AutoSize;


/**
 * Created By  tian on 2019/11/2
 * Describe:    应用的入口
 */
public class DemoApp extends Application {
    private NetworkMonitor networkMonitor;
    public Context mContext;
    private static DemoApp applicationUtils;
    private AppViewModel appViewModel;
    public boolean buildCompany = false;

    private Handler handler;
    private Runnable task;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        setContext(getApplicationContext());
        networkMonitor = new NetworkMonitor(this);
        networkMonitor.start();
        appViewModel = new ViewModelProvider.AndroidViewModelFactory(this).create(AppViewModel.class);
        applicationUtils = this;
        Utils.init(this);
        ToastUtils.init(this);
        MMKV.initialize(this);
        AutoSize.checkAndInit(this);
        appViewModel.setLang();


        handler = new Handler(Looper.getMainLooper());
        task = new Runnable() {
            @Override
            public void run() {
                sendDataToServer();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(task, 5000);
    }

    private void sendDataToServer() {
        try {
            for (DeviceModel model : DemoApp.getInstance().getAppViewModel().device.getValue()) {
                if (model.isConnectTcp()) {

//                    DemoApp.getInstance().getAppViewModel().checkConnect(model.getDeviceName(), model.getDeviceIp());
//                    DemoApp.getInstance().getAppViewModel().upNettyIp(model.getDeviceName(), model.getDeviceIp());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(PadSAttribute.HeartTimer.getAttribute(), 1);
//                    DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject, model.getDeviceName());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public static DemoApp getInstance() {
        return applicationUtils;
    }

    public AppViewModel getAppViewModel() {
        return appViewModel;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        networkMonitor.stop();
        // 根据手机内存剩余情况清理图片内存缓存
    }

}