package com.diwen.liliao.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.model.SettingItem;

import java.util.List;

import androidx.annotation.Nullable;

public class SettingListAdapter extends BaseQuickAdapter<SettingItem, BaseViewHolder> {
    public SettingListAdapter(@Nullable List<SettingItem> data) {
        super(R.layout.item_settinglist, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SettingItem item) {
       helper.setText(R.id.settingTitle,DemoApp.getInstance().getAppViewModel().getLangText(item.getTitle()));
       helper.setImageResource(R.id.ivSettingBg,item.getIcon_src());
    }
}
