package com.diwen.liliao.adapter;


import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.diwen.liliao.R;
import com.diwen.liliao.activity.DeviceLaunchStateController;
import com.diwen.liliao.model.DeviceModel;
import com.hjq.shape.layout.ShapeLinearLayout;
import com.hjq.shape.layout.ShapeRelativeLayout;

import java.util.List;

import androidx.annotation.Nullable;

public class DeviceListAdapter extends BaseQuickAdapter<DeviceModel, BaseViewHolder> {
    public DeviceListAdapter(@Nullable List<DeviceModel> data) {
        super(R.layout.item_devicelist, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceModel item) {
        ShapeRelativeLayout llBg = helper.getView(R.id.llBg);
        if (helper.getLayoutPosition() == 2) {
            helper.setGone(R.id.llMengceng, false);
            llBg.getShapeDrawableBuilder().setSolidColor(Color.parseColor("#4DF2F6FF")).intoBackground();
        } else {
            helper.setGone(R.id.llMengceng, true);
            llBg.getShapeDrawableBuilder().setSolidColor(Color.parseColor("#0DF2F6FF")).intoBackground();
        }
        if (item.isConnectWifi()) {
            if (item.isConnectTcp()) {
                helper.setImageResource(R.id.wifiState, R.mipmap.icon_phoneline);
            } else {
                if (item.isConnectUdp()) {
                    helper.setImageResource(R.id.wifiState, R.mipmap.icon_wificonnect);

                } else {
                    helper.setImageResource(R.id.wifiState, R.mipmap.icon_diswifi);
                }
            }
        } else {
            helper.setImageResource(R.id.wifiState, R.mipmap.icon_wificonnectdis);
        }
        helper.setText(R.id.tvName, item.getDeviceName());
        if (!item.isConnectTcp() || !DeviceLaunchStateController.isKnown(item.getLaunch())) {
            helper.setGone(R.id.launchState, false);
        } else {
            helper.setGone(R.id.launchState, true);
            if (item.getLaunch() == DeviceLaunchStateController.LAUNCH_STOPPED) {
                helper.setImageResource(R.id.launchState, R.mipmap.ic_device_state_stopped);
            } else if (item.getLaunch() == DeviceLaunchStateController.LAUNCH_PAUSED) {
                helper.setImageResource(R.id.launchState, R.mipmap.ic_device_state_paused);
            } else {
                helper.setImageResource(R.id.launchState, R.mipmap.ic_device_state_active);
            }
        }
    }
}
