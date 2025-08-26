package com.diwen.liliao.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.viewbinding.ViewBinding;


/**
 * Created By  tian on 2019/11/2
 * Describe:   界面的基类
 */
public abstract class MqttBaseActivity<T extends ViewBinding> extends BaseMqttActivity implements TitleBarAction {
    protected T binding;

    /**
     * 标题栏对象
     */
    private TitleBar mTitleBar;
    /**
     * 状态栏沉浸
     */
    private ImmersionBar mImmersionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        act = this;
        if (null != getIntent())
            handleIntent(getIntent());
        try {
            Type superclass = getClass().getGenericSuperclass();
            Class<T> aClass = (Class<T>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            binding = (T) method.invoke(null, getLayoutInflater());
            beForeInitView(savedInstanceState);
            setContentView(binding.getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //判断是否需要注册
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBus.getDefault().register(this);
        }
        getStatusBarConfig().init();
        // 设置标题栏沉浸
        if (getTitleBar() != null) {
            ImmersionBar.setTitleBar(this, getTitleBar());
        }


        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .transparentBar()
                .init();
        config();
        transparentNavBar(this);
        setUiText();
        setListener();
        if (DemoApp.getInstance().getAppViewModel() != null) {
            DemoApp.getInstance().getAppViewModel().langData.observe(this, stringStringHashMap -> {
                setUiText();
            });
        }
    }

    /*获取布局下的view*/
    protected void beForeInitView(Bundle savedInstanceState) {

    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    @NonNull
    public ImmersionBar getStatusBarConfig() {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig();
        }
        return mImmersionBar;
    }

    /**
     * 初始化沉浸式状态栏
     */
    @NonNull
    protected ImmersionBar createStatusBarConfig() {
        return ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
                .autoDarkModeEnable(true, 0.2f);
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(@StringRes int id) {
        setTitle(getString(id));
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (getTitleBar() != null) {
            getTitleBar().setTitle(title);
        }
    }

    @Override
    @Nullable
    public TitleBar getTitleBar() {
        if (mTitleBar == null) {
            mTitleBar = obtainTitleBar(findViewById(Window.ID_ANDROID_CONTENT));
        }
        return mTitleBar;
    }

    @Override
    public void onLeftClick(TitleBar titleBar) {
        onBackPressed();
    }

    public void transparentNavBar(@NonNull final Activity activity) {
        transparentNavBar(activity.getWindow());
    }

    public void transparentNavBar(@NonNull final Window window) {
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ((window.getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) == 0) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
        View decorView = window.getDecorView();
        int vis = decorView.getSystemUiVisibility();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(vis | option);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

