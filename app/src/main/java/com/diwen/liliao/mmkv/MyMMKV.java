package com.diwen.liliao.mmkv;

import com.tencent.mmkv.MMKV;

/**
 * Created By  tian on 2022/5/31
 * Describe:  登录页会全部清除掉
 */
public class MyMMKV {
    private static final String fileName = "liliao_word";
    public static final String hostIp = "hostIp";
    public static final String Language = "Language";
    public static final String SoftWareVer = "SoftWareVer";


    public static MMKV get() {

        return MMKV.mmkvWithID(fileName, MMKV.MULTI_PROCESS_MODE);
    }

    public static void putInteger(String key, int value) {
        get().encode(key, value);
    }

    public static int getInteger(String key) {
        return get().decodeInt(key, 0);
    }

    public static int getInteger(String key, int value) {
        return get().decodeInt(key, value);
    }

    public static boolean getBoolean(String key) {
        return get().decodeBool(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return get().decodeBool(key, defaultValue);
    }

    public static boolean putBoolean(String key, boolean value) {
        return get().encode(key, value);
    }

    public static void putString(String key, String value) {
        get().encode(key, value);
    }

    public static String getString(String key) {
        return get().decodeString(key, "");
    }



    public static String getDeviceName() {
        return get().decodeString("deviceName", "");
    }
    public static String getDeviceIp() {
        return get().decodeString("deviceIp", "");
    }
}
