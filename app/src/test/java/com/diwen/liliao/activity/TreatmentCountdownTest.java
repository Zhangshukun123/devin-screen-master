package com.diwen.liliao.activity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TreatmentCountdownTest {

    @Test
    public void pauseAndResumeContinueFromFrozenRemainingTime() {
        TreatmentCountdown countdown = new TreatmentCountdown(14, 0);
        assertTrue(countdown.updateFromDevice(14, 0, DeviceLaunchStateController.LAUNCH_STOPPED));

        for (int i = 0; i < 30; i++) {
            assertTrue(countdown.tick(DeviceLaunchStateController.LAUNCH_RUNNING));
        }
        assertEquals(13, countdown.getMinutes());
        assertEquals(30, countdown.getSeconds());

        assertFalse(countdown.tick(DeviceLaunchStateController.LAUNCH_PAUSED));
        assertFalse(countdown.updateFromDevice(14, 0, DeviceLaunchStateController.LAUNCH_PAUSED));
        assertFalse(countdown.updateFromDevice(14, 0, DeviceLaunchStateController.LAUNCH_RUNNING));
        assertEquals(13, countdown.getMinutes());
        assertEquals(30, countdown.getSeconds());

        assertTrue(countdown.tick(DeviceLaunchStateController.LAUNCH_RUNNING));
        assertEquals(13, countdown.getMinutes());
        assertEquals(29, countdown.getSeconds());
    }

    @Test
    public void stoppedStateAcceptsFreshDeviceTime() {
        TreatmentCountdown countdown = new TreatmentCountdown(10, 0);
        countdown.setFromUser(3, 15);

        assertTrue(countdown.updateFromDevice(20, 45, DeviceLaunchStateController.LAUNCH_STOPPED));
        assertEquals(20, countdown.getMinutes());
        assertEquals(45, countdown.getSeconds());
    }

    @Test
    public void countdownNeverGoesBelowZero() {
        TreatmentCountdown countdown = new TreatmentCountdown(0, 1);
        assertTrue(countdown.tick(DeviceLaunchStateController.LAUNCH_RUNNING));
        assertFalse(countdown.tick(DeviceLaunchStateController.LAUNCH_RUNNING));
        assertEquals(0, countdown.getRemainingSeconds());
    }

    @Test
    public void lateInitialDeviceTimeStillReplacesTheDefaultCountdown() {
        TreatmentCountdown countdown = new TreatmentCountdown(10, 0);
        assertTrue(countdown.tick(DeviceLaunchStateController.LAUNCH_RUNNING));

        assertTrue(countdown.updateFromDevice(13, 42, DeviceLaunchStateController.LAUNCH_RUNNING));
        assertEquals(13, countdown.getMinutes());
        assertEquals(42, countdown.getSeconds());
    }

    @Test
    public void separatelyReturnedMinuteAndSecondBothCalibrateAnActiveSession() {
        TreatmentCountdown countdown = new TreatmentCountdown(10, 0);

        assertTrue(countdown.updateFromDevice(
                14, 0, true, false, DeviceLaunchStateController.LAUNCH_RUNNING));
        assertTrue(countdown.tick(DeviceLaunchStateController.LAUNCH_RUNNING));
        assertTrue(countdown.updateFromDevice(
                countdown.getMinutes(), 42, false, true, DeviceLaunchStateController.LAUNCH_RUNNING));
        assertEquals(13, countdown.getMinutes());
        assertEquals(42, countdown.getSeconds());
    }
}
