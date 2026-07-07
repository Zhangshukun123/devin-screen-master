package com.diwen.liliao.utils;

import com.diwen.liliao.netty.PadSAttribute;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PemfPayloadBuilderTest {

    @Test
    public void buildsPayloadFromCurrentPemfSettings() {
        Map<String, Integer> payload = PemfPayloadBuilder.build("12.3", 3, "20", 1);

        assertEquals(Integer.valueOf(123), payload.get(PadSAttribute.PemfFrequncy.getAttribute()));
        assertEquals(Integer.valueOf(3), payload.get(PadSAttribute.PemfIntensity.getAttribute()));
        assertFalse(payload.containsKey(PadSAttribute.PemfTreatmentTime.getAttribute()));
        assertEquals(Integer.valueOf(1), payload.get(PadSAttribute.PemfState.getAttribute()));
    }

    @Test
    public void scalesPemfFrequencyBetweenDisplayedHzAndDeviceTenths() {
        assertEquals("0.5", PemfPayloadBuilder.formatFrequencyForDisplay(5));
        assertEquals("12.3", PemfPayloadBuilder.formatFrequencyForDisplay(123));
        assertEquals("72.0", PemfPayloadBuilder.formatFrequencyForDisplay(720));

        Map<String, Integer> minPayload = PemfPayloadBuilder.build("0.5", 1, "15", 1);
        Map<String, Integer> maxPayload = PemfPayloadBuilder.build("72.0", 1, "15", 1);

        assertEquals(Integer.valueOf(5), minPayload.get(PadSAttribute.PemfFrequncy.getAttribute()));
        assertEquals(Integer.valueOf(720), maxPayload.get(PadSAttribute.PemfFrequncy.getAttribute()));
        assertFalse(minPayload.containsKey(PadSAttribute.PemfTreatmentTime.getAttribute()));
        assertFalse(maxPayload.containsKey(PadSAttribute.PemfTreatmentTime.getAttribute()));
    }

    @Test
    public void coercesPemfFrequencyToSupportedRange() {
        Map<String, Integer> lowPayload = PemfPayloadBuilder.build("0.1", 1, "15", 1);
        Map<String, Integer> highPayload = PemfPayloadBuilder.build("99.9", 1, "15", 1);

        assertEquals(Integer.valueOf(5), lowPayload.get(PadSAttribute.PemfFrequncy.getAttribute()));
        assertEquals(Integer.valueOf(720), highPayload.get(PadSAttribute.PemfFrequncy.getAttribute()));
    }

    @Test
    public void omitsBlankNumberFields() {
        Map<String, Integer> payload = PemfPayloadBuilder.build("", 2, " ", 0);

        assertFalse(payload.containsKey(PadSAttribute.PemfFrequncy.getAttribute()));
        assertEquals(Integer.valueOf(2), payload.get(PadSAttribute.PemfIntensity.getAttribute()));
        assertFalse(payload.containsKey(PadSAttribute.PemfTreatmentTime.getAttribute()));
        assertEquals(Integer.valueOf(0), payload.get(PadSAttribute.PemfState.getAttribute()));
    }

    @Test
    public void omitsInvalidNumberFields() {
        Map<String, Integer> payload = PemfPayloadBuilder.build("-", 4, "abc", 1);

        assertFalse(payload.containsKey(PadSAttribute.PemfFrequncy.getAttribute()));
        assertEquals(Integer.valueOf(4), payload.get(PadSAttribute.PemfIntensity.getAttribute()));
        assertFalse(payload.containsKey(PadSAttribute.PemfTreatmentTime.getAttribute()));
        assertEquals(Integer.valueOf(1), payload.get(PadSAttribute.PemfState.getAttribute()));
    }
}
