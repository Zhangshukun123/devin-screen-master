package com.diwen.liliao.activity;

public final class DeviceLaunchStateController {
    public static final int LAUNCH_UNKNOWN = -1;
    public static final int LAUNCH_PAUSED = 0;
    public static final int LAUNCH_RUNNING = 1;
    public static final int LAUNCH_STOPPED = 2;
    public static final int LAUNCH_PREPARING = 3;
    public static final int NO_LAUNCH_COMMAND = Integer.MIN_VALUE;

    private DeviceLaunchStateController() {
    }

    public static int nextLaunchForStartButton(int launch) {
        if (launch == LAUNCH_RUNNING) {
            return LAUNCH_PAUSED;
        }
        if (launch == LAUNCH_PAUSED) {
            return LAUNCH_RUNNING;
        }
        if (launch == LAUNCH_STOPPED) {
            return LAUNCH_PREPARING;
        }
        return NO_LAUNCH_COMMAND;
    }

    public static int nextLaunchForLongPress(int launch) {
        return LAUNCH_STOPPED;
    }

    public static boolean isKnown(int launch) {
        return launch >= LAUNCH_PAUSED && launch <= LAUNCH_PREPARING;
    }

    public static boolean opensModeSelection(int launch) {
        return launch == LAUNCH_STOPPED;
    }

    public static boolean isActive(int launch) {
        return launch == LAUNCH_PAUSED
                || launch == LAUNCH_RUNNING
                || launch == LAUNCH_PREPARING;
    }
}
