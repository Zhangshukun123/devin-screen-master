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
import me.jessyan.autosize.AutoSizeConfig;


/**
 * Created By  tian on 2019/11/2
 * Describe:    应用的入口
 */
public class DemoApp extends Application {
    public Context mContext;
    private static DemoApp applicationUtils;
    private AppViewModel appViewModel;
    public boolean buildCompany = false;

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(getApplicationContext());
        appViewModel = new ViewModelProvider.AndroidViewModelFactory(this).create(AppViewModel.class);
        applicationUtils = this;
        Utils.init(this);
        ToastUtils.init(this);
        MMKV.initialize(this);
        AutoSize.checkAndInit(this);
        appViewModel.setLang();
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

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
    }

}