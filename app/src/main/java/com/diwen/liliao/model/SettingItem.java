package com.diwen.liliao.model;

import lombok.Data;

/**
 * Created By  tian on 2024/7/16
 * Describe:
 */
@Data
public class SettingItem {
    private  int  icon_src;
    private  String title;
   private  boolean chose;
   private  String  PulseDuty="100";
   private  String  PulseSetting="1000";
   private  int deviceModel;
    public SettingItem() {
    }

    public SettingItem(int icon_src, String title) {
        this.icon_src = icon_src;
        this.title = title;
    } public SettingItem(int icon_src, String title,int  deviceModel) {
        this.icon_src = icon_src;
        this.title = title;
        this.deviceModel = deviceModel;
    }
} 
