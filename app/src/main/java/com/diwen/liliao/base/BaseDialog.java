package com.diwen.liliao.base;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.diwen.liliao.R;

import static me.jessyan.autosize.utils.ScreenUtils.getStatusBarHeight;


public abstract class BaseDialog {

    private int mDuration = 500;
    /**
     * 上下文
     */
    public Context context;
    /**
     * 弹窗
     */
    public Dialog dialog;

    public BaseDialog(Context context) {
        this.context = context;
        dialog = initDialog(context);
    }

    /**
     * 显示弹窗
     */
    public void showDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 隐藏弹窗
     */
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }


    /**
     * 初始化弹窗
     */
    public Dialog initMatchDialogOutside(View view, Context context, int Gravity, boolean Outside) {
        Dialog dialog = new Dialog(context, R.style.dialog_tran);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity;// Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        dialog.setCanceledOnTouchOutside(Outside);
        return dialog;
    }

    /**
     * 初始化弹窗
     */
    public Dialog initMatchDialog(View view, Context context, int Gravity) {
        Dialog dialog = new Dialog(context, R.style.dialog_tran);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity;// Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    /**
     * 初始化弹窗
     */
    public Dialog iniChoseHome(View view, Context context, int Gravity) {
        Dialog dialog = new Dialog(context, R.style.dialog_tran);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity;// Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = getScreenHeight() + getStatusBarHeight();
        window.setAttributes(params);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setCanceledOnTouchOutside(true);
        view.setOnClickListener(v -> dismissDialog());
        return dialog;
    }

    /**
     * 初始化弹窗
     */
    public Dialog initallMatchDialog(View view, Context context, int Gravity) {
        Dialog dialog = new Dialog(context, R.style.dialog_tran);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity;// Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    public Dialog initallSliderightMatchDialog(View view, Context context, int Gravity) {
        Dialog dialog = new Dialog(context, R.style.dialog_tran);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity;// Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = getScreenHeight() + getStatusBarHeight();
        window.setAttributes(params);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    /**
     * 初始化弹窗
     */
    public abstract Dialog initDialog(Context context);

    /**
     * 获取屏幕高度
     *
     * @return 屏幕高度
     */
    public int getScreenHeight() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }







}
