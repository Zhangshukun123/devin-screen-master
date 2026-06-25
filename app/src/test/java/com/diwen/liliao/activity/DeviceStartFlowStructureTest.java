package com.diwen.liliao.activity;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceStartFlowStructureTest {

    @Test
    public void stopStateStartButtonSendsPrepareLaunchState() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("PadSAttribute.Launch.getAttribute(), 3"));
        assertTrue(source.contains("startPrepareCountdown()"));
    }

    @Test
    public void prepareBackReturnsToModeSelectionAndClosesLauncher() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("returnToModeSelection()"));
        assertTrue(source.contains("ActivityUtils.startActivity(new Intent(mContext, DeviceModelActivity.class)"));
        assertTrue(source.contains("finish();"));
    }

    @Test
    public void modeSelectionDoesNotAutoStartPrepareCountdown() throws Exception {
        String modelSource = readSource("src/main/java/com/diwen/liliao/activity/DeviceModelActivity.java");
        String maiChongSource = readSource("src/main/java/com/diwen/liliao/activity/MaiChongSettingActivity.java");
        String launcherSource = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertFalse(modelSource.contains("EXTRA_AUTO_PREPARE_COUNTDOWN"));
        assertFalse(maiChongSource.contains("EXTRA_AUTO_PREPARE_COUNTDOWN"));
        assertFalse(launcherSource.contains("requestAutoPrepareCountdown"));
        assertFalse(launcherSource.contains("waitingForReadySecond"));
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
