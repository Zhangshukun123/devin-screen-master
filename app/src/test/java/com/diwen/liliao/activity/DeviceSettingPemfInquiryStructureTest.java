package com.diwen.liliao.activity;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceSettingPemfInquiryStructureTest {

    @Test
    public void bluetoothFragmentOwnsBluetoothAndPemfEnableInquiry() throws Exception {
        String deviceSettingSource = readSource(
                "src/main/java/com/diwen/liliao/activity/DeviceSettingActivity.java");
        String bluetoothSource = readSource(
                "src/main/java/com/diwen/liliao/fragment/BluetoothFragment.java");

        assertFalse(deviceSettingSource.contains("queryPemfEnable("));
        assertTrue(bluetoothSource.contains(
                "jsonObject.put(PadSAttribute.BtName.getAttribute(), 1);"));
        assertTrue(bluetoothSource.contains(
                "jsonObject.put(PadSAttribute.PemfEnable.getAttribute(), 1);"));
    }

    private String readSource(String path) throws Exception {
        return new String(Files.readAllBytes(new File(path).toPath()), StandardCharsets.UTF_8);
    }
}
