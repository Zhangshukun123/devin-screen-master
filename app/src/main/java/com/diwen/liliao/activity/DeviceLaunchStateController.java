package com.diwen.liliao.activity;

final class DeviceLaunchStateController {
    static final int LAUNCH_UNKNOWN = -1;
    static final int LAUNCH_PAUSED = 0;
    static final int LAUNCH_RUNNING = 1;
    static final int LAUNCH_STOPPED = 2;
    static final int LAUNCH_PREPARING = 3;
    static final int NO_LAUNCH_COMMAND = Integer.MIN_VALUE;

    private DeviceLaunchStateController() {
    }

    static int nextLaunchForStartButton(int launch) {
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
}
