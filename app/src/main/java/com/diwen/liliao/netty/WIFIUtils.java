package com.diwen.liliao.netty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.List;


import androidx.annotation.Nullable;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Administrator on 2017/11/24 0024.
 */

public class WIFIUtils {
    private static final int SECURITY_NONE = 0;
    private static final int SECURITY_WEP = 1;
    private static final int SECURITY_PSK = 2;

    public static final int SCAN_ALL_TYPE = 0;
    public static final int SCAN_SCAN_RESULTS = 1;
    public static final int SCAN_CONFIGURED_NETWORKS = 2;
//    private static final int SECURITY_EAP = 3;

    // 定义WifiManager对象
    public WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiManager.WifiLock mWifiLock;

    private WIFIListener mWifiListener;
    private WiFiReceiver mWiFiReceiver;
    private Context mContext;

    private static class WIFIUtilsHolder {
        @SuppressLint("StaticFieldLeak")
        private static final WIFIUtils INSTANCE = new WIFIUtils();
    }

    private WIFIUtils() {
    }

    public static WIFIUtils getInstance() {
        return WIFIUtilsHolder.INSTANCE;
    }

    public void setWifi(Context context, @Nullable WIFIListener wifiListener) {
        startInit(context);
        if (wifiListener != null) {
            mWifiListener = wifiListener;
            mWiFiReceiver = WiFiReceiver.getInstance();
        }
    }

    public void setWifi(Context context) {
        startInit(context);
    }

    private void startInit(Context context) {
        mContext = context.getApplicationContext();
        // 取得WifiManager对象
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
        // 取得WifiInfo对象
        if (mWifiManager != null) {
            mWifiInfo = mWifiManager.getConnectionInfo();
        }
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 开启wifi广播监听器
     */
    public void startReceiverListener() {
        if (mWiFiReceiver != null) {
            mWiFiReceiver.observeWifiSwitch(mContext, mWifiListener);
        }
    }

    /**
     * 判断是否打开wifi
     *
     * @param context context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = null;
        if (connectivityManager != null) {
            activeNetInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }

        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    /**
     * wifi扫描
     */
    public void startScan(int i_type) {
        try {
            mWifiManager.startScan();
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (i_type) {
            case WIFIUtils.SCAN_ALL_TYPE:
                mWifiList = mWifiManager.getScanResults();
                mWifiConfiguration = mWifiManager.getConfiguredNetworks();
                break;
            case WIFIUtils.SCAN_SCAN_RESULTS:
                // 得到扫描结果
                mWifiList = mWifiManager.getScanResults();
                break;
            case WIFIUtils.SCAN_CONFIGURED_NETWORKS:
                // 得到配置好的网络连接
                mWifiConfiguration = mWifiManager.getConfiguredNetworks();
                break;
        }
    }

    /**
     * 得到密码加密方式
     */
    public int getSecurity(WifiConfiguration config) {

        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
//            return SECURITY_EAP;
//        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    /**
     * 得到已经保存的wifi配置
     *
     * @return WifiConfiguration
     */
    public WifiConfiguration getWifiConfiguration(String s_SSID, WifiInfo wifiInfo) {
        WifiConfiguration sWifiConfiguration = null;
        for (WifiConfiguration wifiConfiguration : mWifiConfiguration) {
            //比较networkId，防止配置网络保存相同的SSID
            String id = wifiConfiguration.SSID.replace("\"", "");
            int net = wifiConfiguration.networkId;
            if (s_SSID.equals(id) && wifiInfo.getNetworkId() == net) {
                sWifiConfiguration = wifiConfiguration;
            }
        }
        return sWifiConfiguration;
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_").append(Integer.toString(i + 1)).append(":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }


    public String getSSID() {
        {
            String ssid = "unknown id";
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT == 28) {
                WifiManager mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
                assert mWifiManager != null;
                WifiInfo info = mWifiManager.getConnectionInfo();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    return info.getSSID();
                } else {
                    return info.getSSID().replace("\"", "");
                }
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
                ConnectivityManager connManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                assert connManager != null;
                NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                if (networkInfo.isConnected()) {
                    if (networkInfo.getExtraInfo() != null) {
                        return networkInfo.getExtraInfo().replace("\"", "");
                    }
                }
            } else {
                WifiManager wifiManager = ((WifiManager)mContext.getApplicationContext().getSystemService(WIFI_SERVICE));
                assert wifiManager != null;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String SSID = wifiInfo.getSSID();
                int networkId = wifiInfo.getNetworkId();
                @SuppressLint("MissingPermission") List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                    if (wifiConfiguration.networkId == networkId) {
                        SSID = wifiConfiguration.SSID;
                    }
                }
                return SSID.replace("\"", "");
            }
            return ssid;
        }
    }

    /**
     * 判断是不是5G
     */
    public boolean isWifi5G(Context context) {
        int freq = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            freq = wifiInfo.getFrequency();
        } else {
            String ssid = wifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }
      
        return freq > 4000;
    }


    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    /**
     * ip 地址转为字符串
     *
     * @param i_IpAddress ip 地址
     * @return 字符串
     */
    public static String intIpToStringIp(int i_IpAddress) {
        return (i_IpAddress & 0xFF) + "." + ((i_IpAddress >> 8) & 0xFF) + "." +
                ((i_IpAddress >> 16) & 0xFF) + "." + (i_IpAddress >> 24 & 0xFF);
    }

    public int addWifiItem(String ssid, String password) {
        int networkId = mWifiManager.addNetwork(createWifiInfo(ssid, password, password.length() == 0 ? 0 : 2));
        boolean sB_save = mWifiManager.saveConfiguration();
        return networkId;
    }

    public boolean connectToSpecSsid(int netId) {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
        return wifiManager.enableNetwork(netId, true);
    }

    /**
     * 连接wifi
     *
     * @param wcg wifi配置
     * @return 连接结果
     */
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean sB_enable = mWifiManager.enableNetwork(wcgID, true);
        boolean sB_save = mWifiManager.saveConfiguration();
//        boolean sB_reconnect = mWifiManager.reconnect();
    }

    // 断开指定ID的网络
    public void disconnectWifi(boolean b, int netId) {
        if (mWifiManager != null) {
            if (b) {
                mWifiManager.disableNetwork(netId);
            }
            mWifiManager.disconnect();
        }
    }

    /**
     * 释放资源
     */
    public void onClose() {
        try {
            if (mWiFiReceiver != null) {
                mWiFiReceiver.onClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mWifiManager != null) {
            mWifiManager = null;
            mWifiInfo = null;
        }
        if (mContext != null) {
            mContext = null;
        }
    }

    /**
     * 是否配置过
     *
     * @param SSID 账号
     * @return 配置
     */
    private WifiConfiguration isExist(String SSID) {
        try {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    //然后是一个实际应用方法，只验证过没有密码的情况：

    public WifiConfiguration createWifiInfo(String SSID, String password, int type) {

//        Log.v(TAG, "SSID = " + SSID + "## Password = " + password + "## Type = " + type);

        WifiConfiguration tempConfig = this.isExist(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + SSID + "\"";

        // 分为三种情况：1没有密码2用wep加密3用wpa加密
        if (type == 0) {// WIFICIPHER_NOPASS
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.wepKeys[0] = "\"".concat(password).concat("\"");
            config.wepTxKeyIndex = 0;
        } else if (type == 2) {   // WIFICIPHER_WPA
            config.hiddenSSID = true;
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.preSharedKey = "\"".concat(password).concat("\"");

        }

        return config;
    }

    /**
     * 获取SSID
     *
     * @param activity 上下文
     * @return WIFI 的SSID
     */
    public String getWIFISSID(Activity activity) {
        String ssid = "unknown id";

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT == 28) {

            WifiManager mWifiManager = (WifiManager)mContext.getApplicationContext().getSystemService(WIFI_SERVICE);

            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {

            ConnectivityManager connManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    return networkInfo.getExtraInfo().replace("\"", "");
                }
            }
        }
        return ssid;
    }

}
