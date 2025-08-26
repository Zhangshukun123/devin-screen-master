package com.diwen.liliao.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;


/**
 * Created By  tian on 2022/8/12
 * Describe:
 */
public abstract class BaseMqttActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    protected Context mContext;
    protected Activity act;
    private GestureDetector mGestureDetector;//手势

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    //2.让手势识别器 工作起来
    //当activity被触摸的时候调用的方法.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

   
    @Override
    public void onResume() {
      
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeLoading();
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBus.getDefault().unregister(this);
        }

    }

    /*处理intent,第一个被调用,不要做关于view的操作@param intent 从上个页面传过来的intent,不必非空判断*/
    protected abstract void handleIntent(Intent intent);

    /* 注册监听，对象声明，初始化等*/
    protected abstract void setUiText();
    protected abstract void config();

    /*设置view的监听器*/
    protected abstract void setListener();

    /*返回true 拦截点击，false不拦截点击*/
    @Override
    public boolean onLongClick(View v) {
        return false;
    }


    /* 添加fragment */
    protected void addFragment(Fragment fragment, int id) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(id, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    protected void setSoftInputMode(int mode) {
        this.getWindow().setSoftInputMode(mode);
    }


    /* showToast  status 吐司的提示信息 */
    protected void showToast(String status) {
    }



    @Override
    public void onClick(View v) {
       
    }



    /*打开加载动画*/
    public void showLoading() {
       // DialogLoadCircle.showDialogs((Activity) mContext);
    }


    /*关闭加载动画*/
    public void closeLoading() {
        //DialogLoadCircle.closeDialog();
    }





    protected void MqttMessage(String DeviceId, Map<String, Object> map) {

    }
}
