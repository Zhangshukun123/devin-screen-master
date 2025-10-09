package com.diwen.liliao.adapter;


import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

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
        evhz.setMaxInputValue(10000);
        CustomEditText evKong = helper.getView(R.id.evKong);
        evKong.setMaxInputValue(100);
        evhz.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(evhz.getWindowToken(), 0);
                    return true;    // 消费该事件
                }
                return false;
            }
        });
        evKong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(evhz.getWindowToken(), 0);
                    return true;    // 消费该事件
                }
                return false;
            }
        });
    }
}
