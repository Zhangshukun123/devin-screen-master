package com.diwen.liliao.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.diwen.liliao.R;


/**
 *
 */

public class DialogLoadCircle {

    private static Dialog createDialog(Activity activity) {
        View sView = LayoutInflater.from(activity).inflate(R.layout.dialog_load_circle, null);

//        获取整个布局
            LinearLayout sLayout = sView.findViewById(R.id.dialog_layout_bg);
//        创建自定义样式
        Dialog _dialog = new Dialog(activity, R.style.mydialog);
//        设置返回键无效
//        _dialog.setCancelable(false);
        DisplayMetrics dm = new DisplayMetrics();
        LottieAnimationView iv_lottie=sView.findViewById(R.id.iv_lottie);
        iv_lottie.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                closeDialog();
            }
        });
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        ViewGroup.LayoutParams sLayoutParams = new ViewGroup.LayoutParams(width, height);
        _dialog.setContentView(sLayout, sLayoutParams);
        return _dialog;
    }

    private static Dialog mDialog;

    //    打开
    public static void showDialog(Activity activity) {
        if (mDialog == null) {
            mDialog = DialogLoadCircle.createDialog(activity);
            mDialog.show();
        } else {
            closeDialog();
            showDialog(activity);
        }
    }

    //    打开
    public static void showDialogs(Activity activity) {
        if (mDialog == null) {
            mDialog = DialogLoadCircle.createDialog(activity);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        } else {
            closeDialog();
        }
    }

    //    关闭
    public static void closeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
