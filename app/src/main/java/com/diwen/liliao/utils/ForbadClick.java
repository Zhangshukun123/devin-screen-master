package com.diwen.liliao.utils;

import android.view.View;

/**
 * 快速双击
 * Created by Administrator on 2018/1/16 0016.
 */

public class ForbadClick {

    private static long lastClickTime;
    private static long lastSendClickTime=0;
    private static View view;

    public static boolean isFastDoubleClick(View v) {
        long time = System.currentTimeMillis();
        if (view == v && time - lastClickTime < 1000) {
            return true;
        }
        view = v;
        lastClickTime = time;
        return false;
    }

    public static boolean isFastDoubleClick(View v, long last) {
        long time = System.currentTimeMillis();
        if (view == v && time - lastClickTime < last) {
            return true;
        }
        view = v;
        lastClickTime = time;
        return false;
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    public static boolean isFastDoubleClick(long last) {
        long time = System.currentTimeMillis();
        if (time - lastSendClickTime <300) {
            return true;
        }
        lastSendClickTime = time;
        return false;
    }
    
}
