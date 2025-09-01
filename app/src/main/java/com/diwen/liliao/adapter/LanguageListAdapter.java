package com.diwen.liliao.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.model.SettingItem;

import java.util.List;

import androidx.annotation.Nullable;

public class LanguageListAdapter extends BaseQuickAdapter<SettingItem, BaseViewHolder> {
    public LanguageListAdapter(@Nullable List<SettingItem> data) {
        super(R.layout.item_language, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SettingItem item) {
        helper.setVisible(R.id.ivChose, item.isChose())
                .setVisible(R.id.viewDivider, helper.getAdapterPosition() != 0)
                .setImageResource(R.id.icLogo, item.getIcon_src())
                .setText(R.id.tvTile, DemoApp.getInstance().getAppViewModel().getLangText(item.getTitle()));
    }
}
