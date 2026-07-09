package com.diwen.liliao.activity;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeviceStartFlowStructureTest {

    @Test
    public void startButtonOnlyPreparesWhenLaunchStateIsConfirmedPaused() throws Exception {
        Class<?> controllerClass = Class.forName("com.diwen.liliao.activity.DeviceLaunchStateController");
        Method nextLaunchForStartButton = controllerClass.getDeclaredMethod("nextLaunchForStartButton", int.class);

        int unknown = controllerClass.getDeclaredField("LAUNCH_UNKNOWN").getInt(null);
        int paused = controllerClass.getDeclaredField("LAUNCH_PAUSED").getInt(null);
        int running = controllerClass.getDeclaredField("LAUNCH_RUNNING").getInt(null);
        int stopped = controllerClass.getDeclaredField("LAUNCH_STOPPED").getInt(null);
        int preparing = controllerClass.getDeclaredField("LAUNCH_PREPARING").getInt(null);
        int noCommand = controllerClass.getDeclaredField("NO_LAUNCH_COMMAND").getInt(null);

        assertEquals(paused, nextLaunchForStartButton.invoke(null, running));
        assertEquals(preparing, nextLaunchForStartButton.invoke(null, paused));
        assertEquals(noCommand, nextLaunchForStartButton.invoke(null, stopped));
        assertEquals(noCommand, nextLaunchForStartButton.invoke(null, preparing));
        assertEquals(noCommand, nextLaunchForStartButton.invoke(null, unknown));
    }

    @Test
    public void launcherDoesNotTreatUnknownDeviceLaunchAsStopped() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("private int Launch = DeviceLaunchStateController.LAUNCH_UNKNOWN;"));
        assertTrue(source.contains("DeviceLaunchStateController.nextLaunchForStartButton(Launch)"));
        assertTrue(source.contains("Launch = DeviceLaunchStateController.LAUNCH_UNKNOWN;"));
        assertFalse(source.contains("Launch = 2;\n            setLaunch();"));
    }

    @Test
    public void pausedStateStartButtonSendsPrepareLaunchState() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("jsonObject.put(PadSAttribute.Launch.getAttribute(), nextLaunch);"));
        assertTrue(source.contains("nextLaunch == DeviceLaunchStateController.LAUNCH_PREPARING"));
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
    public void modeSelectionSendsPrepareLaunchAndOpensLauncher() throws Exception {
        String modelSource = readSource("src/main/java/com/diwen/liliao/activity/DeviceModelActivity.java");
        String maiChongSource = readSource("src/main/java/com/diwen/liliao/activity/MaiChongSettingActivity.java");
        String launcherSource = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(modelSource.contains("jsonObject.put(PadSAttribute.Launch.getAttribute(), DeviceLaunchStateController.LAUNCH_PREPARING);"));
        assertTrue(modelSource.contains("intent.putExtra(DeviceLauncherActivity.EXTRA_START_PREPARE_COUNTDOWN, true);"));
        assertFalse(maiChongSource.contains("jsonObject.put(PadSAttribute.Launch.getAttribute(), DeviceLaunchStateController.LAUNCH_PREPARING);"));
        assertFalse(maiChongSource.contains("intent.putExtra(DeviceLauncherActivity.EXTRA_START_PREPARE_COUNTDOWN, true);"));
        assertTrue(launcherSource.contains("static final String EXTRA_START_PREPARE_COUNTDOWN"));
        assertTrue(launcherSource.contains("Launch = DeviceLaunchStateController.LAUNCH_PREPARING;"));
        assertTrue(launcherSource.contains("startPrepareCountdown();"));
    }

    @Test
    public void modeSelectionLauncherEntrySkipsInitialStatusQuerySpam() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("private boolean skipNextOnlineAttributeQuery;"));
        assertTrue(source.contains("if (startPrepareCountdownFromModeSelection) {"));
        assertTrue(source.contains("} else {\n            getAllAttributes();\n        }"));
        assertTrue(source.contains("if (skipNextOnlineAttributeQuery) {"));
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
