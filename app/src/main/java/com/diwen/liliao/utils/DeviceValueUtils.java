package com.diwen.liliao.utils;

public final class DeviceValueUtils {
    public static final int MIN_PREPARE_SECONDS = 3;
    public static final int MAX_PREPARE_SECONDS = 99;

    private DeviceValueUtils() {
    }

    public static int toFahrenheit(int celsius) {
        return Math.round(celsius * 9f / 5f + 32);
    }

    public static int toCelsius(int fahrenheit) {
        return Math.round((fahrenheit - 32) * 5f / 9f);
    }

    public static int coercePrepareSeconds(int seconds) {
        if (seconds < MIN_PREPARE_SECONDS) {
            return MIN_PREPARE_SECONDS;
        }
        if (seconds > MAX_PREPARE_SECONDS) {
            return MAX_PREPARE_SECONDS;
        }
        return seconds;
    }
}
