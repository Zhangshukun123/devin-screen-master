package com.diwen.liliao.adapter;


import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.diwen.liliao.R;
import com.diwen.liliao.model.SettingItem;
import com.hjq.shape.view.ShapeTextView;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class ButtonAirListAdapter extends BaseQuickAdapter<SettingItem, BaseViewHolder> {
    public ButtonAirListAdapter(@Nullable List<SettingItem> data) {
        super(R.layout.item_buttonairsettings, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SettingItem item) {
        ShapeTextView tvButton=helper.getView(R.id.tvButton);
        if (item.isChose()){
            tvButton.getShapeDrawableBuilder().setSolidColor(Color.parseColor("#FFF1FF")).intoBackground();
            tvButton.setTextColor(ContextCompat.getColor(mContext,R.color.black));
        }else {
            tvButton.getShapeDrawableBuilder().setSolidColor(Color.parseColor("#40BCD4F6")).intoBackground();
            tvButton.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        }
        tvButton.setText(String.valueOf(helper.getLayoutPosition()+1));
        
    }
}
