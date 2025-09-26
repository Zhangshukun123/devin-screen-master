package com.diwen.liliao.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkMonitor {

    private final ConnectivityManager connectivityManager;
    private final ConnectivityManager.NetworkCallback networkCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NetworkMonitor(Context context) {
        connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // 网络可用（Wi-Fi/移动数据连接成功）
                EventBus.getDefault().post(new MessageEvent(MQTTCons.NETWORK_CONNECTED));
                Log.d("NetworkMonitor", "网络已连接");
            }

            @Override
            public void onLost(Network network) {
                // 网络丢失（断网）
                EventBus.getDefault().post(new MessageEvent(MQTTCons.NETWORK_ERROR));
                Log.d("NetworkMonitor", "网络已断开");
            }

            @Override
            public void onCapabilitiesChanged(Network network,
                                              NetworkCapabilities caps) {
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d("NetworkMonitor", "当前使用 Wi-Fi");
                } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d("NetworkMonitor", "当前使用移动数据");
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void start() {
        NetworkRequest request = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stop() {
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}
