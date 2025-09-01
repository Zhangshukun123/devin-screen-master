package com.diwen.liliao.fragment;

import android.os.Bundle;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.R;
import com.diwen.liliao.activity.DeviceSettingActivity;
import com.diwen.liliao.activity.LanguageSettingActivity;
import com.diwen.liliao.adapter.LanguageListAdapter;
import com.diwen.liliao.base.BaseFragment;
import com.diwen.liliao.databinding.FragmentLanguageBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.SettingItem;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LanguageFragment extends BaseFragment<FragmentLanguageBinding> {
    private ArrayList<SettingItem> settingItems;
    private LanguageListAdapter languageListAdapter;

    public static LanguageFragment newInstance() {

        Bundle args = new Bundle();

        LanguageFragment fragment = new LanguageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        binding.tvLanguage.setText(DemoApp.getInstance().getAppViewModel().getLangText("语言"));
        settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(R.mipmap.icon_language1, "英语"));
        settingItems.add(new SettingItem(R.mipmap.icon_language2, "德语"));
        settingItems.add(new SettingItem(R.mipmap.icon_language5, "法语"));
        settingItems.add(new SettingItem(R.mipmap.icon_language3, "意大利语"));
        settingItems.add(new SettingItem(R.mipmap.icon_language4, "西班牙语"));

        int integer = MyMMKV.getInteger(MyMMKV.Language);
        if (integer > 0) {
            settingItems.get(integer - 1).setChose(true);
        }
        languageListAdapter = new LanguageListAdapter(settingItems);
        binding.recyclerView.setAdapter(languageListAdapter);
    }

    @Override
    protected void initData() {
        languageListAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (SettingItem datum : languageListAdapter.getData()) {
                datum.setChose(false);
            }
            languageListAdapter.getItem(position).setChose(true);
            languageListAdapter.notifyDataSetChanged();

            switch (position) {
                case 0:
                    binding.tvSave.setText("Save");
                    break;
                case 1:
                    binding.tvSave.setText("Speichern");
                    break;
                case 2:
                    binding.tvSave.setText("Enregistrer");
                    break;
                case 3:
                    binding.tvSave.setText("Salva");
                    break;
                case 4:
                    binding.tvSave.setText("Guardar");
                    break;
            }
        });

        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    for (int i = 0; i < languageListAdapter.getData().size(); i++) {
                        if (languageListAdapter.getData().get(i).isChose()) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(PadSAttribute.Language.getAttribute(), i + 1);
                            DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
                            MyMMKV.putInteger(MyMMKV.Language, i + 1);
                            DemoApp.getInstance().getAppViewModel().setLang();
                            ActivityUtils.finishActivity(DeviceSettingActivity.class);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
