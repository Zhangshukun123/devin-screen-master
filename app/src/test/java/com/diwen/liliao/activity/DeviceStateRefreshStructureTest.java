package com.diwen.liliao.activity;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceStateRefreshStructureTest {

    @Test
    public void mainPageQueriesEveryConnectedDeviceEveryFiveSeconds() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/MainActivity.java");

        assertTrue(source.contains("DEVICE_STATE_QUERY_INTERVAL_MS = 5000L"));
        assertTrue(source.contains("queryConnectedDeviceStates()"));
        assertTrue(source.contains("model.isConnectTcp()"));
        assertTrue(source.contains("sendInquiryMQTT(jsonObject, deviceName)"));
        assertTrue(source.contains("PadSAttribute.Launch.getAttribute()"));
        assertTrue(source.contains("PadSAttribute.PluseMode.getAttribute()"));
    }

    @Test
    public void clicksRouteStoppedDevicesToModesAndActiveDevicesToLauncher() throws Exception {
        String mainSource = readSource("src/main/java/com/diwen/liliao/activity/MainActivity.java");
        String listSource = readSource("src/main/java/com/diwen/liliao/activity/DeviceListActivity.java");

        assertTrue(mainSource.contains("DeviceLaunchStateController.opensModeSelection(device.getLaunch())"));
        assertTrue(mainSource.contains("new Intent(mContext, DeviceModelActivity.class)"));
        assertTrue(mainSource.contains("new Intent(mContext, DeviceLauncherActivity.class)"));
        assertTrue(listSource.contains("DeviceLaunchStateController.opensModeSelection(device.getLaunch())"));
        assertFalse(mainSource.contains("if (Launch == 2)"));
    }

    @Test
    public void deviceCardsUseProvidedRedYellowAndGreenStatusAssets() throws Exception {
        String adapterSource = readSource("src/main/java/com/diwen/liliao/adapter/DeviceListAdapter.java");
        String layoutSource = readSource("src/main/res/layout/item_devicelist.xml");

        assertTrue(adapterSource.contains("ic_device_state_stopped"));
        assertTrue(adapterSource.contains("ic_device_state_paused"));
        assertTrue(adapterSource.contains("ic_device_state_active"));
        assertTrue(layoutSource.contains("@+id/launchState"));
        assertTrue(layoutSource.contains("android:layout_gravity=\"end|bottom\""));
        assertTrue(new File("src/main/res/mipmap-xxhdpi/ic_device_state_stopped.png").isFile());
        assertTrue(new File("src/main/res/mipmap-xxhdpi/ic_device_state_paused.png").isFile());
        assertTrue(new File("src/main/res/mipmap-xxhdpi/ic_device_state_active.png").isFile());
    }

    private String readSource(String path) throws Exception {
        return new String(Files.readAllBytes(new File(path).toPath()), StandardCharsets.UTF_8);
    }
}
