package com.diwen.liliao.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UiTextUtilsTest {

    @Test
    public void removesTrailingColonFromInlineLabels() {
        assertEquals("Frequency", UiTextUtils.withoutTrailingColon("Frequency："));
        assertEquals("Frequency", UiTextUtils.withoutTrailingColon("Frequency:"));
        assertEquals("Treatment Time", UiTextUtils.withoutTrailingColon("Treatment Time"));
    }
}
