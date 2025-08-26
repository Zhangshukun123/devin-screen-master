package com.diwen.liliao.activity;

import android.content.Intent;
import android.view.View;

import com.diwen.liliao.DemoApp;
import com.diwen.liliao.adapter.ArrayWheelAdapter;
import com.diwen.liliao.base.BindEventBus;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.LayoutTimesettingsactivityBinding;
import com.diwen.liliao.mmkv.MyMMKV;
import com.diwen.liliao.model.MessageEvent;
import com.diwen.liliao.model.MqttParseOverModel;
import com.diwen.liliao.netty.BTCodeUtils;
import com.diwen.liliao.netty.MQTTCons;
import com.diwen.liliao.netty.PadSAttribute;
import com.diwen.liliao.utils.AtyUtils;
import com.diwen.liliao.utils.TimeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * Created By  tian on 2024/7/17
 * Describe: 时间设置
 */
@BindEventBus
public class TimeSettingsActivity extends MqttBaseActivity<LayoutTimesettingsactivityBinding> {
    private String dateFromat = "yyyy-MM-dd HH:mm:ss";
    private ArrayList<String> arry_years;
    private ArrayList<String> arry_month;
    private ArrayList<String> arry_day;
    private ArrayList<String> arry_time;
    private ArrayList<String> arry_timeM;
    private ArrayList<String> arry_times;

    @Override
    protected void handleIntent(Intent intent) {
    }

    @Override
    protected void setUiText() {
        binding.tvSave.setText(DemoApp.getInstance().getAppViewModel().getLangText("保存"));
        binding.tvYear.setText(DemoApp.getInstance().getAppViewModel().getLangText("年"));
        binding.tvMoth.setText(DemoApp.getInstance().getAppViewModel().getLangText("月"));
        binding.tvDay.setText(DemoApp.getInstance().getAppViewModel().getLangText("日"));
        binding.tvHour.setText(DemoApp.getInstance().getAppViewModel().getLangText("时"));
        binding.tvFen.setText(DemoApp.getInstance().getAppViewModel().getLangText("分"));
        binding.tvMiao.setText(DemoApp.getInstance().getAppViewModel().getLangText("秒"));
    }

    @Override
    protected void config() {
        binding.tvName.setText(MyMMKV.getDeviceName());
        arry_years = new ArrayList<>();
        arry_month = new ArrayList<>();
        arry_day = new ArrayList<>();
        arry_time = new ArrayList<>();
        arry_timeM = new ArrayList<>();
        arry_times = new ArrayList<>();
        initYears();
        getAllAttributes();
    }

    @Override
    protected void setListener() {
        binding.ivFinish.setOnClickListener(this);
        binding.tvSave.setOnClickListener(this);
        binding.evMoth.setOnClickListener(this);
        binding.evYear.setOnClickListener(this);
        binding.evDay.setOnClickListener(this);
        binding.evHour.setOnClickListener(this);
        binding.evFen.setOnClickListener(this);
        binding.evMiao.setOnClickListener(this);
        binding.ivDismis.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivDismis) {
            binding.llYangli.setVisibility(View.GONE);
        }
        if (v == binding.evYear
                || v == binding.evMoth
                || v == binding.evDay
                || v == binding.evHour
                || v == binding.evFen
                || v == binding.evMiao
        ) {
            binding.llYangli.setVisibility(View.VISIBLE);
        }
        if (v == binding.ivFinish) {
            BTCodeUtils.getInstance().finishTo(2);
            finish();
        }
        if (v == binding.tvSave) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PadSAttribute.Year.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evYear)));
                jsonObject.put(PadSAttribute.Month.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evMoth)));
                jsonObject.put(PadSAttribute.Day.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evDay)));
                jsonObject.put(PadSAttribute.Hour.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evHour)));
                jsonObject.put(PadSAttribute.Minute.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evFen)));
                jsonObject.put(PadSAttribute.Second.getAttribute(), Integer.parseInt(AtyUtils.getText(binding.evMiao)));
                DemoApp.getInstance().getAppViewModel().setMQTT(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getAllAttributes() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PadSAttribute.Year.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Month.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Day.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Hour.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Minute.getAttribute(), 1);
            jsonObject.put(PadSAttribute.Second.getAttribute(), 1);
            DemoApp.getInstance().getAppViewModel().sendInquiryMQTT(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(MQTTCons.ACTION_DATA_AVAILABLE)) {
            MqttParseOverModel model = event.getMqttParseOverModel();
            MqttMessage(model.getDeviceId(), model.getMap());
        }
    }

    @Override
    protected void MqttMessage(String DeviceId, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        if (!DeviceId.equals(MyMMKV.getDeviceName())) {
            return;
        }
        closeLoading();
        Set<String> strings = map.keySet();
        if (strings.contains(PadSAttribute.onLineState.getAttribute())) {
            int onLineState = (int) map.get(PadSAttribute.onLineState.getAttribute());
            if (onLineState == 1) {
                getAllAttributes();
            }
        }
        if (strings.contains(PadSAttribute.Second.getAttribute())) {
            String Year = (String) map.get(PadSAttribute.Year.getAttribute());
            String Month = (String) map.get(PadSAttribute.Month.getAttribute());
            String Day = (String) map.get(PadSAttribute.Day.getAttribute());
            String Hour = (String) map.get(PadSAttribute.Hour.getAttribute());
            String Minute = (String) map.get(PadSAttribute.Minute.getAttribute());
            String Second = (String) map.get(PadSAttribute.Second.getAttribute());
            String format = String.format("%s-%s-%s %s:%s:%s", "20" + Year,
                    Month,
                    Day,
                    Hour,
                    Minute,
                    Second
            );
            setTime(format);
            setEvText();
        }

    }

    int now_year = 2024;

    private void initYears() {
        Calendar nowCalendar = Calendar.getInstance();
        now_year = nowCalendar.get(Calendar.YEAR) - 2000;

        binding.evYear.setText(String.valueOf(now_year));
        binding.evMoth.setText(getPointTwo(nowCalendar.get(Calendar.MONTH) + 1));
        binding.evDay.setText(getPointTwo(nowCalendar.get(Calendar.DATE)));
        binding.evHour.setText(getPointTwo(nowCalendar.get(Calendar.HOUR_OF_DAY)));
        binding.evFen.setText(getPointTwo(nowCalendar.get(Calendar.MINUTE)));
        binding.evMiao.setText(getPointTwo(nowCalendar.get(Calendar.SECOND)));

        for (int i = 0; i < 100; i++) {
            arry_years.add(getPointTwo(i));
        }
        for (int i = 1; i < 13; i++) {
            arry_month.add(getPointTwo(i));
        }
        for (int i = 1; i < 32; i++) {
            arry_day.add(getPointTwo(i));
        }
        for (int i = 0; i < 24; i++) {
            arry_time.add(getPointTwo(i));
        }
        for (int i = 0; i < 60; i++) {
            arry_timeM.add(getPointTwo(i));
        }
        for (int i = 0; i < 60; i++) {
            arry_times.add(getPointTwo(i));
        }

        binding.wheelViewY.setAdapter(new ArrayWheelAdapter(arry_years));
        binding.wheelViewMoth.setAdapter(new ArrayWheelAdapter(arry_month));
        binding.wheelViewDay.setAdapter(new ArrayWheelAdapter(arry_day));
        binding.wheelViewH.setAdapter(new ArrayWheelAdapter(arry_time));
        binding.wheelViewM.setAdapter(new ArrayWheelAdapter(arry_timeM));
        binding.wheelViewS.setAdapter(new ArrayWheelAdapter(arry_times));

        String format = String.format("%s-%s-%s %s:%s:%s", AtyUtils.getText(binding.evYear),
                AtyUtils.getText(binding.evMoth),
                AtyUtils.getText(binding.evDay),
                AtyUtils.getText(binding.evHour),
                AtyUtils.getText(binding.evFen),
                AtyUtils.getText(binding.evMiao)
        );
        setTime(format);
        binding.wheelViewY.setOnItemSelectedListener(index -> {
            getTime();
        });
        binding.wheelViewMoth.setOnItemSelectedListener(index -> {
            getTime();
        });
        binding.wheelViewDay.setOnItemSelectedListener(index -> {
            getTime();
        });
        binding.wheelViewH.setOnItemSelectedListener(index -> {
            getTime();
        });
        binding.wheelViewM.setOnItemSelectedListener(index -> {
            getTime();
        });
        binding.wheelViewS.setOnItemSelectedListener(index -> {
            getTime();
        });
        setEvText();
    }


    public void setEvText() {

        for (int i = 0; i < arry_years.size(); i++) {
            if (arry_years.get(i).equals(AtyUtils.getText(binding.evYear))) {
                binding.wheelViewY.setCurrentItem(i);
            }
        }

        for (int i = 0; i < arry_month.size(); i++) {
            if (arry_month.get(i).equals(AtyUtils.getText(binding.evMoth))) {
                binding.wheelViewMoth.setCurrentItem(i);
            }
        }
        for (int i = 0; i < arry_day.size(); i++) {
            if (arry_day.get(i).equals(AtyUtils.getText(binding.evDay))) {
                binding.wheelViewDay.setCurrentItem(i);
            }
        }
        for (int i = 0; i < arry_time.size(); i++) {
            if (arry_time.get(i).equals(AtyUtils.getText(binding.evHour))) {
                binding.wheelViewH.setCurrentItem(i);
            }
        }

        for (int i = 0; i < arry_timeM.size(); i++) {
            if (arry_timeM.get(i).equals(AtyUtils.getText(binding.evFen))) {
                binding.wheelViewM.setCurrentItem(i);
            }
        }
        for (int i = 0; i < arry_times.size(); i++) {
            if (arry_times.get(i).equals(AtyUtils.getText(binding.evMiao))) {
                binding.wheelViewS.setCurrentItem(i);
            }
        }

    }

    public void getTime() {
        String format = String.format("%s-%s-%s %s:%s:%s", arry_years.get(binding.wheelViewY.getCurrentItem()),
                arry_month.get(binding.wheelViewMoth.getCurrentItem()),
                arry_day.get(binding.wheelViewDay.getCurrentItem()),
                arry_time.get(binding.wheelViewH.getCurrentItem()),
                arry_timeM.get(binding.wheelViewM.getCurrentItem()),
                arry_times.get(binding.wheelViewS.getCurrentItem())
        );
        setTime(format);
    }

    public String getPointTwo(int a) {
        if (a < 10) {
            return "0" + String.valueOf(a);
        }
        return String.valueOf(a);
    }

    public void setTime(String SetTime) {
        binding.evMiao.setText(TimeUtils.getMiao(SetTime, dateFromat));
        binding.evFen.setText(TimeUtils.getMinute(SetTime, dateFromat));
        binding.evHour.setText(TimeUtils.getHour(SetTime, dateFromat));
        binding.evDay.setText(TimeUtils.getDay(SetTime, dateFromat));
        binding.evMoth.setText(TimeUtils.getMonth(SetTime, dateFromat));
        binding.evYear.setText(TimeUtils.getYear(SetTime, dateFromat));

    }
} 
