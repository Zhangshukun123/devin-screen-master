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
    public void startButtonTransitionsBetweenRunPauseAndPrepareFromStopped() throws Exception {
        Class<?> controllerClass = Class.forName("com.diwen.liliao.activity.DeviceLaunchStateController");
        Method nextLaunchForStartButton = controllerClass.getDeclaredMethod("nextLaunchForStartButton", int.class);

        int unknown = controllerClass.getDeclaredField("LAUNCH_UNKNOWN").getInt(null);
        int paused = controllerClass.getDeclaredField("LAUNCH_PAUSED").getInt(null);
        int running = controllerClass.getDeclaredField("LAUNCH_RUNNING").getInt(null);
        int stopped = controllerClass.getDeclaredField("LAUNCH_STOPPED").getInt(null);
        int preparing = controllerClass.getDeclaredField("LAUNCH_PREPARING").getInt(null);
        int noCommand = controllerClass.getDeclaredField("NO_LAUNCH_COMMAND").getInt(null);

        assertEquals(paused, nextLaunchForStartButton.invoke(null, running));
        assertEquals(running, nextLaunchForStartButton.invoke(null, paused));
        assertEquals(preparing, nextLaunchForStartButton.invoke(null, stopped));
        assertEquals(noCommand, nextLaunchForStartButton.invoke(null, preparing));
        assertEquals(noCommand, nextLaunchForStartButton.invoke(null, unknown));
    }

    @Test
    public void longPressAlwaysSendsStopped() throws Exception {
        Class<?> controllerClass = Class.forName("com.diwen.liliao.activity.DeviceLaunchStateController");
        Method nextLaunchForLongPress = controllerClass.getDeclaredMethod("nextLaunchForLongPress", int.class);

        int unknown = controllerClass.getDeclaredField("LAUNCH_UNKNOWN").getInt(null);
        int paused = controllerClass.getDeclaredField("LAUNCH_PAUSED").getInt(null);
        int running = controllerClass.getDeclaredField("LAUNCH_RUNNING").getInt(null);
        int stopped = controllerClass.getDeclaredField("LAUNCH_STOPPED").getInt(null);
        int preparing = controllerClass.getDeclaredField("LAUNCH_PREPARING").getInt(null);

        assertEquals(stopped, nextLaunchForLongPress.invoke(null, running));
        assertEquals(stopped, nextLaunchForLongPress.invoke(null, paused));
        assertEquals(stopped, nextLaunchForLongPress.invoke(null, stopped));
        assertEquals(stopped, nextLaunchForLongPress.invoke(null, preparing));
        assertEquals(stopped, nextLaunchForLongPress.invoke(null, unknown));
    }

    @Test
    public void launcherDefaultsToStoppedBeforeDeviceStateArrives() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("private int Launch = DeviceLaunchStateController.LAUNCH_STOPPED;"));
        assertTrue(source.contains("DeviceLaunchStateController.nextLaunchForStartButton(Launch)"));
        assertTrue(source.contains("Launch = DeviceLaunchStateController.LAUNCH_STOPPED;"));
    }

    @Test
    public void stoppedStateStartButtonWaitsForPrepareConfirmation() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("jsonObject.put(PadSAttribute.Launch.getAttribute(), nextLaunch);"));
        assertFalse(source.contains("if (nextLaunch == DeviceLaunchStateController.LAUNCH_PREPARING)"));
        assertTrue(source.contains("Launch == DeviceLaunchStateController.LAUNCH_PREPARING"));
        assertTrue(source.contains("startPrepareCountdown()"));
    }

    @Test
    public void stopRequiresFullThreeSecondHold() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("STOP_HOLD_DURATION_MS = 3000L"));
        assertTrue(source.contains("handler.postDelayed(stopHoldRunnable, STOP_HOLD_DURATION_MS)"));
        assertTrue(source.contains("handler.removeCallbacks(stopHoldRunnable)"));
        assertFalse(source.contains("binding.rlStart.setOnLongClickListener"));
    }

    @Test
    public void draggingOutsideMainControlCancelsStopAndShortClick() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("MotionEvent.ACTION_MOVE"));
        assertTrue(source.contains("isTouchInsideView(v, event)"));
        assertTrue(source.contains("stopHoldEligible"));
    }

    @Test
    public void longPressUsesControllerForStoppedCommand() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("DeviceLaunchStateController.nextLaunchForLongPress(Launch)"));
        assertFalse(source.contains("jsonObject.put(PadSAttribute.Launch.getAttribute(), 0);"));
    }

    @Test
    public void treatmentTimeComesOnlyFromDeviceMessages() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertFalse(source.contains("handler.sendEmptyMessage(826)"));
        assertFalse(source.contains("allTime--"));
        assertTrue(source.contains("PadSAttribute.DeviceTimeMin.getAttribute()"));
        assertTrue(source.contains("PadSAttribute.DeviceTimeSecond.getAttribute()"));
    }

    @Test
    public void prepareBackOnlyHidesOverlay() throws Exception {
        String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertTrue(source.contains("hidePrepareOverlay()"));
        assertFalse(source.contains("returnToModeSelection()"));
    }

    @Test
    public void modeSelectionOnlyChangesModeAndOpensLauncher() throws Exception {
        String modelSource = readSource("src/main/java/com/diwen/liliao/activity/DeviceModelActivity.java");
        String maiChongSource = readSource("src/main/java/com/diwen/liliao/activity/MaiChongSettingActivity.java");
        String launcherSource = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");

        assertFalse(modelSource.contains("jsonObject.put(PadSAttribute.Launch.getAttribute(), DeviceLaunchStateController.LAUNCH_PREPARING);"));
        assertFalse(modelSource.contains("intent.putExtra(DeviceLauncherActivity.EXTRA_START_PREPARE_COUNTDOWN, true);"));
        assertFalse(maiChongSource.contains("jsonObject.put(PadSAttribute.Launch.getAttribute(), DeviceLaunchStateController.LAUNCH_PREPARING);"));
        assertFalse(maiChongSource.contains("intent.putExtra(DeviceLauncherActivity.EXTRA_START_PREPARE_COUNTDOWN, true);"));
        assertFalse(launcherSource.contains("EXTRA_START_PREPARE_COUNTDOWN"));
        assertTrue(launcherSource.contains("startPrepareCountdown();"));
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
