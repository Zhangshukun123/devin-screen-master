package com.diwen.liliao.utils;


import com.diwen.liliao.model.MyKey;

import java.lang.reflect.Field;

/**
 *
 * Created by Administrator on 2018/3/12 0012.
 */

public class StringUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.equalsIgnoreCase("null") || value.equals("");
    }

    public static <T> boolean isEmpty(T model){
        boolean sB_result = false;

        Field[] sFields = model.getClass().getDeclaredFields();
        for (Field sField : sFields) {
            sField.setAccessible(true);

            MyKey sMyKey = sField.getAnnotation(MyKey.class);
            if (sMyKey == null){
                continue;
            }

            if (sMyKey.checkEmpty()){
                try {
                    Object sO_value = sField.get(model);
                    sB_result = sO_value == null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return sB_result;
    }
}
