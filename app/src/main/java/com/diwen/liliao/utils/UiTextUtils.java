package com.diwen.liliao.utils;

public final class UiTextUtils {
    private UiTextUtils() {
    }

    public static String withoutTrailingColon(String text) {
        if (text == null) {
            return "";
        }
        String value = text.trim();
        while (value.endsWith(":") || value.endsWith("：")) {
            value = value.substring(0, value.length() - 1).trim();
        }
        return value;
    }
}
