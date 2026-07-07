package com.diwen.liliao.utils;

import com.diwen.liliao.netty.PadSAttribute;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class PemfPayloadBuilder {
    private static final int MIN_FREQUENCY_TENTHS = 5;
    private static final int MAX_FREQUENCY_TENTHS = 720;

    private PemfPayloadBuilder() {
    }

    public static Map<String, Integer> build(
            String frequency,
            int intensity,
            String treatmentTime,
            int state
    ) {
        Map<String, Integer> payload = new LinkedHashMap<>();
        putIfFrequency(payload, frequency);
        payload.put(PadSAttribute.PemfIntensity.getAttribute(), intensity);
        payload.put(PadSAttribute.PemfState.getAttribute(), state);
        return payload;
    }

    public static String formatFrequencyForDisplay(int deviceFrequencyTenths) {
        return String.format(Locale.US, "%.1f", coerceFrequencyTenths(deviceFrequencyTenths) / 10f);
    }

    private static void putIfFrequency(Map<String, Integer> payload, String value) {
        Integer parsedValue = parseFrequencyTenths(value);
        if (parsedValue != null) {
            payload.put(PadSAttribute.PemfFrequncy.getAttribute(), parsedValue);
        }
    }

    private static Integer parseFrequencyTenths(String value) {
        if (value == null) {
            return null;
        }
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0) {
            return null;
        }
        try {
            return coerceFrequencyTenths(Math.round(Float.parseFloat(trimmedValue) * 10f));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int coerceFrequencyTenths(int frequencyTenths) {
        if (frequencyTenths < MIN_FREQUENCY_TENTHS) {
            return MIN_FREQUENCY_TENTHS;
        }
        if (frequencyTenths > MAX_FREQUENCY_TENTHS) {
            return MAX_FREQUENCY_TENTHS;
        }
        return frequencyTenths;
    }
}
