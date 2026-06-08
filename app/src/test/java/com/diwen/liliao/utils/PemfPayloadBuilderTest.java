package com.diwen.liliao.utils;

import com.diwen.liliao.netty.PadSAttribute;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PemfPayloadBuilderTest {

    @Test
    public void buildsPayloadFromCurrentPemfSettings() {
        Map<String, Integer> payload = PemfPayloadBuilder.build("12", 3, "20", 1);

        assertEquals(Integer.valueOf(12), payload.get(PadSAttribute.PemfFrequncy.getAttribute()));
        assertEquals(Integer.valueOf(3), payload.get(PadSAttribute.PemfIntensity.getAttribute()));
        assertEquals(Integer.valueOf(20), payload.get(PadSAttribute.PemfTreatmentTime.getAttribute()));
        assertEquals(Integer.valueOf(1), payload.get(PadSAttribute.PemfState.getAttribute()));
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
