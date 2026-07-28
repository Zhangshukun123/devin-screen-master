package com.diwen.liliao.activity;

public final class TreatmentCountdown {
    private int remainingSeconds;
    private boolean initialized;
    private boolean receivedDeviceMinutes;
    private boolean receivedDeviceSeconds;

    public TreatmentCountdown(int minutes, int seconds) {
        remainingSeconds = toTotalSeconds(minutes, seconds);
    }

    public boolean updateFromDevice(int minutes, int seconds, int incomingLaunch) {
        return updateFromDevice(minutes, seconds, true, true, incomingLaunch);
    }

    public boolean updateFromDevice(int minutes, int seconds, boolean hasMinutes,
                                    boolean hasSeconds, int incomingLaunch) {
        if (!hasMinutes && !hasSeconds) {
            return false;
        }
        if (initialized
                && incomingLaunch != DeviceLaunchStateController.LAUNCH_STOPPED
                && incomingLaunch != DeviceLaunchStateController.LAUNCH_PREPARING) {
            return false;
        }
        remainingSeconds = toTotalSeconds(minutes, seconds);
        receivedDeviceMinutes = receivedDeviceMinutes || hasMinutes;
        receivedDeviceSeconds = receivedDeviceSeconds || hasSeconds;
        initialized = receivedDeviceMinutes && receivedDeviceSeconds;
        return true;
    }

    public void setFromUser(int minutes, int seconds) {
        remainingSeconds = toTotalSeconds(minutes, seconds);
        initialized = true;
        receivedDeviceMinutes = true;
        receivedDeviceSeconds = true;
    }

    public boolean tick(int launch) {
        if (launch != DeviceLaunchStateController.LAUNCH_RUNNING || remainingSeconds <= 0) {
            return false;
        }
        remainingSeconds--;
        return true;
    }

    public int getMinutes() {
        return remainingSeconds / 60;
    }

    public int getSeconds() {
        return remainingSeconds % 60;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean isInitialized() {
        return initialized;
    }

    private int toTotalSeconds(int minutes, int seconds) {
        int safeMinutes = Math.max(0, minutes);
        int safeSeconds = Math.max(0, Math.min(59, seconds));
        return safeMinutes * 60 + safeSeconds;
    }
}
