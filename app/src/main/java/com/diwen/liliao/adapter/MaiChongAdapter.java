package com.diwen.liliao.adapter;


import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.utils.CustomEditText;

import java.util.List;

import androidx.annotation.Nullable;

public class MaiChongAdapter extends BaseQuickAdapter<SettingItem, BaseViewHolder> {
    public MaiChongAdapter(@Nullable List<SettingItem> data) {
        super(R.layout.item_maichong, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SettingItem item) {
        helper.setText(R.id.tv1, DemoApp.getInstance().getAppViewModel().getLangText(item.getTitle()));
        helper.setText(R.id.tv2, DemoApp.getInstance().getAppViewModel().getLangText("频率"));
        helper.setText(R.id.tv3, DemoApp.getInstance().getAppViewModel().getLangText("占空比"));
        helper.setVisible(R.id.ivChose, item.isChose());

        helper.setText(R.id.evHz, item.getPulseSetting() + "");
        helper.setText(R.id.evKong, item.getPulseDuty() + "");
        helper.addOnClickListener(R.id.rlChose);

        CustomEditText evhz = helper.getView(R.id.evHz);
        evhz.setMaxInputValue(20000);
        CustomEditText evKong = helper.getView(R.id.evKong);
        evKong.setMaxInputValue(100);
    }
}
