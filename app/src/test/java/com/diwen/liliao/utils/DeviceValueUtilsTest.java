package com.diwen.liliao.utils;

import com.diwen.liliao.netty.PadSAttribute;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeviceValueUtilsTest {

    @Test
    public void convertsCelsiusAndFahrenheitWithRoundedIntegers() {
        assertEquals(32, DeviceValueUtils.toFahrenheit(0));
        assertEquals(212, DeviceValueUtils.toFahrenheit(100));
        assertEquals(0, DeviceValueUtils.toCelsius(32));
        assertEquals(100, DeviceValueUtils.toCelsius(212));
    }

    @Test
    public void clampsPrepareCountdownSecondsToSupportedRange() {
        assertEquals(3, DeviceValueUtils.coercePrepareSeconds(0));
        assertEquals(3, DeviceValueUtils.coercePrepareSeconds(3));
        assertEquals(45, DeviceValueUtils.coercePrepareSeconds(45));
        assertEquals(99, DeviceValueUtils.coercePrepareSeconds(120));
    }

    @Test
    public void exposesReadyCountdownProtocolAttribute() {
        assertEquals("GetReadySecond", PadSAttribute.GetReadySecond.getAttribute());
    }
}
