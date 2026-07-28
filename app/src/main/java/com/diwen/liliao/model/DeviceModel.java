package com.diwen.liliao.model;

import lombok.Data;

/**
 * Created By  tian on 2024/7/19
 * Describe:
 */
@Data
public class DeviceModel {
    private String deviceIp = "192.168.0.83";
    private String deviceName = "";
    private String deviceId = "";
    private boolean connectUdp;
    private boolean connectTcp;
    private boolean connectWifi = true;
    private int launch = -1;
    private int pluseMode;
}
