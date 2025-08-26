package com.diwen.liliao.netty;

import android.util.Log;


import java.io.UnsupportedEncodingException;

/**
 * Created by shen on 2016/2/28.
 */
public class LogUtil {
    public static boolean showI = true;

    /**
     * 得到tag（所在类.方法（L:行））
     *
     * @return
     */
    private static String generateTag() {
        return "tian";
    }

    public static String tostring(String msg) {
        String str = "";
        try {
            str = new String(msg.getBytes("ISO-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void i(String msg) {
        if (showI) {
            String tag = generateTag();
            Log.i(tag, String.valueOf(msg));
        }
    }

    public static void w(String msg) {
        if (showI) {
            Log.w("retrofitBack", String.valueOf(msg));
        }
    }

    public static void i(String msg, Throwable tr) {
        if (showI) {
            String tag = generateTag();
            Log.i(tag, msg, tr);
        }
    }
    public static void eTian(String msg) {
        if (showI) {
            String tag = generateTag();
            Log.e("mqtt", msg);
        }
    }
    public static void e(String msg) {
        if (showI) {
            Log.e("retrofitBack", msg);
        }
    }
    public static void iMqtt(String msg) {
        if (showI) {
            String tag = generateTag();
            Log.i("mqtt", msg);
        }
    }

    public static void iTcp(String msg) {
        if (showI) {
            Log.i("TCP", msg);
        }
    }
    public static void eTcp(String msg) {
        if (showI) {
            Log.e("TCP", msg);
        }
    }
}
