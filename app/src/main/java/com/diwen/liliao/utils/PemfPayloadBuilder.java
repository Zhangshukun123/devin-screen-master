package com.diwen.liliao.utils;

import com.diwen.liliao.netty.PadSAttribute;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PemfPayloadBuilder {
    private PemfPayloadBuilder() {
    }

    public static Map<String, Integer> build(
            String frequency,
            int intensity,
            String treatmentTime,
            int state
    ) {
        Map<String, Integer> payload = new LinkedHashMap<>();
        putIfNumber(payload, PadSAttribute.PemfFrequncy.getAttribute(), frequency);
        payload.put(PadSAttribute.PemfIntensity.getAttribute(), intensity);
        putIfNumber(payload, PadSAttribute.PemfTreatmentTime.getAttribute(), treatmentTime);
        payload.put(PadSAttribute.PemfState.getAttribute(), state);
        return payload;
    }

    private static void putIfNumber(Map<String, Integer> payload, String key, String value) {
        Integer parsedValue = parseInteger(value);
        if (parsedValue != null) {
            payload.put(key, parsedValue);
        }
    }

    private static Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0) {
            return null;
        }
        try {
            return Integer.parseInt(trimmedValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
