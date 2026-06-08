package com.diwen.liliao.activity;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceStartFlowStructureTest {

    @Test
    public void modeSelectionStartsLauncherInPrepareCountdownMode() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceModelActivity.java");

        assertTrue(source.contains("DeviceLauncherActivity.EXTRA_AUTO_PREPARE_COUNTDOWN"));
        assertTrue(source.contains("putExtra(DeviceLauncherActivity.EXTRA_AUTO_PREPARE_COUNTDOWN, true)"));
    }

    @Test
    public void prepareBackReturnsToModeSelectionAndClosesLauncher() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("returnToModeSelection()"));
        assertTrue(source.contains("ActivityUtils.startActivity(new Intent(mContext, DeviceModelActivity.class)"));
        assertTrue(source.contains("finish();"));
    }

    @Test
    public void manualModeSaveStartsLauncherInPrepareCountdownMode() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/MaiChongSettingActivity.java");

        assertTrue(source.contains("DeviceLauncherActivity.EXTRA_AUTO_PREPARE_COUNTDOWN"));
        assertTrue(source.contains("putExtra(DeviceLauncherActivity.EXTRA_AUTO_PREPARE_COUNTDOWN, true)"));
    }

    @Test
    public void autoPrepareCountdownWaitsForReadySecondFromDevice() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("requestAutoPrepareCountdown()"));
        assertTrue(source.contains("waitingForReadySecond = true"));
        assertTrue(source.contains("if (waitingForReadySecond && Launch == 2)"));
        assertTrue(source.contains("waitingForReadySecond = false"));
    }

    @Test
    public void offlineMainListClickDoesNotOpenModeSelection() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/MainActivity.java")
                .replace("\r\n", "\n");

        assertTrue(source.contains("ToastUtils.show(\"No networking\");"));
        assertFalse(source.contains("ToastUtils.show(\"No networking\");\n"
                + "                ActivityUtils.startActivity(new Intent(mContext, DeviceModelActivity.class));"));
    }

    private String readSource(String path) throws Exception {
        return new String(Files.readAllBytes(new File(path).toPath()), StandardCharsets.UTF_8);
    }
}
