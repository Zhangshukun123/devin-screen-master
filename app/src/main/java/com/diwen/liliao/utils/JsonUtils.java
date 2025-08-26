package com.diwen.liliao.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;


import com.alibaba.fastjson.JSON;
import com.diwen.liliao.model.BeanFieldAnnotation;
import com.diwen.liliao.model.MyKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.databinding.ObservableField;


/**
 * 生成Json数组
 * Created by Administrator on 2017/12/21 0021.
 */

public class JsonUtils {
    public static final String SEPARATOR = "-";

    public static <T> String parseBeanToString(T model) {
        String s = JSON.toJSON(model).toString();
        return s;
    }


    public static <T> ArrayList<T> parseJson(String json, Class<T> clazz) {
        ArrayList<T> list = new ArrayList<T>();
        if (AtyUtils.isStringEmpty(json)) {
            list = (ArrayList<T>) JSON.parseArray(json, clazz);
        }
        return list;
    }


    public static <T> T parseObject(String text, Class<T> clazz) {
        T value = JSON.parseObject(text, clazz);
        return value;
    }

    private static String[] partition(String value) {
        int sI_separator = value.indexOf(SEPARATOR);
        String sS_key = value.substring(0, sI_separator);
        String sS_value = value.substring(sI_separator + 1, value.length());
        return new String[]{sS_key, sS_value};
    }

    public static String modelGetValue(Object obj, String key) {
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (field.getName().equals(key)) {
                    field.setAccessible(true);
                    return (String) field.get(obj);
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 从JSON中取出值
     *
     * @param jsonObject 对象
     * @param s_key      key
     * @return 有则取出，无则反空
     */
    public static String getValue(JSONObject jsonObject, String s_key) {
        if (jsonObject == null) {
            return "";
        }
        return jsonObject.optString(s_key);

    }

    public static boolean getBooleanValue(String json, String key) {
        try {
            JSONObject sJSONObject = new JSONObject(json);
            return sJSONObject.optBoolean(key, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getIntValue(String json, String key) {
        try {
            JSONObject sJSONObject = new JSONObject(json);
            return sJSONObject.optInt(key, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getValue(String json, String key) {
        try {
            JSONObject sJSONObject = new JSONObject(json);
            return getValue(sJSONObject, key);
        } catch (JSONException e) {
            return "";
        }
    }
    public static JSONArray getValueArray(String json, String key) {
        try {
            JSONObject sJSONObject = new JSONObject(json);
            return sJSONObject.getJSONArray("key");
        } catch (JSONException e) {
            return new JSONArray();
        }
    }
    /**
     * 从JSON中取出值
     *
     * @param jsonObject 对象
     * @param s_key      key
     * @return 有则取出，无则反空
     */
    public static Object getToValue(JSONObject jsonObject, String s_key) {
        if (jsonObject == null) {
            return "";
        }
        try {
            return jsonObject.get(s_key);
        } catch (JSONException e) {
            return "";
        }
    }

    /**
     * 从JSON中取出值
     *
     * @param json  字符串
     * @param s_key key
     * @return 有则取出，无则返回“”
     */
    public static Object getToValue(String json, String s_key) {
        try {
            JSONObject sJSONObject = new JSONObject(json);
            return getToValue(sJSONObject, s_key);
        } catch (JSONException e) {
            return "";
        }
    }


    /**
     * 拼接 key和值
     *
     * @param s_key         key
     * @param hashMap_param 值
     * @return 拼接好的字符串
     */
    public static String jointCommand(String s_key, HashMap<String, String> hashMap_param) {
        return s_key + SEPARATOR + hashMap_param.get(s_key);
    }

    /**
     * 拼接 key和值
     *
     * @param s_key key
     * @param value 值
     * @return 拼接好的字符串
     */
    public static String jointCommand(String s_key, String value) {
        return s_key + SEPARATOR + value;
    }

    public static String createJsonArray(ArrayList<JSONObject> arrayList) {
        JSONArray sJSONArray = new JSONArray();
        for (JSONObject item : arrayList) {
            sJSONArray.put(item);
        }
        return sJSONArray.toString();
    }

    public static <T> JSONArray List2JsonArray(ArrayList<T> arrayList) {
        try {
            String s = JSON.toJSONString(arrayList);
            JSONArray jsonArray = new JSONArray(s);
            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject mapToJSONObject(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (map != null) {
                for (String key : map.keySet()) {
                    jsonObject.put(key, map.get(key));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject mapStringToJSONObject(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (map != null) {
                for (String key : map.keySet()) {
                    jsonObject.put(key, map.get(key));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 生成JSON对象
     *
     * @param keyAndValue 数组
     * @return 对象
     */
    public static JSONObject createJson(ArrayList<String> keyAndValue) {
        try {
            JSONObject sJSONObject = new JSONObject();
            for (int sI = 0; sI < keyAndValue.size(); sI++) {
                String[] sS_data = partition(keyAndValue.get(sI));
                StringBuilder s_value = new StringBuilder();
                if (sS_data.length > 2) {
                    for (int a = 1; a < sS_data.length; a++) {
                        if (sS_data[a].equals("")) {
                            s_value.append(SEPARATOR);
                        } else {
                            s_value.append(sS_data[a]);
                        }
                        if (a != sS_data.length - 1) {
                            s_value.append(SEPARATOR);
                        }
                    }
                } else {
                    s_value = new StringBuilder(sS_data[1]);
                }
                sJSONObject.put(sS_data[0], s_value);
            }
            return sJSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成Json
     *
     * @param userInfo     用户的的固定信息
     * @param commandParam 命令的参数
     * @return 完整数组
     */
    public static JSONObject createJson(ArrayList<String> userInfo, ArrayList<String> commandParam) {
        JSONObject sJSONObject;
        String sS_paramName;
        try {
            sJSONObject = new JSONObject();
            for (int sI = 0; sI < userInfo.size(); sI++) {
                String[] sS_data = partition(userInfo.get(sI));
                sJSONObject.put(sS_data[0], sS_data[1]);
            }

            JSONObject param = new JSONObject();
            for (int i = 0; i < commandParam.size(); i++) {
                if (i > 0) {
                    String[] sS_data = partition(commandParam.get(i));
                    param.put(sS_data[0], sS_data[1]);
                }
            }
            sS_paramName = commandParam.get(0);
            sJSONObject.put(sS_paramName, param);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return sJSONObject;
    }

    /**
     * 排序后  生成返回JSONObject对象
     */
    public static <T> JSONObject createJsonSort(T model) {
        JSONObject sJSONObject;
        try {
            sJSONObject = new JSONObject();
            List<Field> sFields = getOrderedField(model.getClass().getDeclaredFields());
            for (Field sField : sFields) {
                sField.setAccessible(true);
                String sS_majorKey;
                String sS_assistantKey;
                Object s0_value;
                try {
                    MyKey sMyKey = sField.getAnnotation(MyKey.class);
                    if (sMyKey == null) {
                        continue;
                    }
                    Class sClassType = sField.getType();
                    Object sO = sField.get(model);
                    if (sO == null) {
                        continue;
                    }
                    if (sClassType == String.class) {
                        if (StringUtils.isEmpty(sO.toString())) {
                            continue;
                        }
                    }
                    if (sClassType == int.class) {
                        if (((int) sO) == 0) {
                            continue;
                        }
                    }
                    if (sClassType == String.class
                            || sClassType == Integer.class || sClassType == int.class
                            || sClassType == Long.class || sClassType == long.class
                            || sClassType == Boolean.class || sClassType == boolean.class
                            || sClassType == Double.class || sClassType == double.class
                            || sClassType == Float.class || sClassType == float.class
                            || sClassType == Byte.class || sClassType == byte.class
                            || sClassType == Byte[].class || sClassType == byte[].class
                            || sClassType == Short.class || sClassType == short.class
                            || sClassType == ObservableField.class) {
                        if (sClassType == ObservableField.class) {
                            Object value = ((ObservableField) sO).get();
                            if (value != null && !StringUtils.isEmpty(String.valueOf(value))) {
                                s0_value = String.valueOf(value);
                            } else {
                                continue;
                            }
                        } else {
                            s0_value = sO;
                        }
                    } else if (sClassType == JSONObject.class || sClassType == JSONArray.class) {
                        s0_value = sO;
                    } else {
                        try {
                            s0_value = createJson(sO);
                        } catch (Exception e) {
                            s0_value = "";
                        }
                    }
                    sS_majorKey = sMyKey.majorKey();
                    sS_assistantKey = sMyKey.assistantKey();
                    sJSONObject.put(sS_majorKey, s0_value);

                    if (!sS_assistantKey.equals("")) {
                        sJSONObject.put(sS_assistantKey, s0_value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            sJSONObject = null;
        }

        return sJSONObject;
    }

    @SuppressLint("NewApi")
    private static List<Field> getOrderedField(Field[] fields) {
        // 用来存放所有的属性域
        List<Field> fieldList = new ArrayList<>();
        // 过滤带有注解的Field
        for (Field f : fields) {
            if (f.getAnnotation(BeanFieldAnnotation.class) != null) {
                fieldList.add(f);
            }

        }
        // 这个比较排序的语法依赖于java 1.8
        fieldList.sort(Comparator.comparingInt(
                m -> m.getAnnotation(BeanFieldAnnotation.class).order()
        ));
        return fieldList;
    }

    /**
     * 生成Json对象
     *
     * @param model 对象
     * @param <T>   泛型
     * @return 返回JSONObject对象
     */
    public static <T> JSONObject createJson(T model) {
        JSONObject sJSONObject;
        try {
            sJSONObject = new JSONObject();
            Field[] sFields = model.getClass().getDeclaredFields();
            for (Field sField : sFields) {
                sField.setAccessible(true);
                String sS_majorKey;
                String sS_assistantKey;
                Object s0_value;
                try {
                    MyKey sMyKey = sField.getAnnotation(MyKey.class);
                    if (sMyKey == null) {
                        continue;
                    }
                    Class sClassType = sField.getType();
                    Object sO = sField.get(model);
                    if (sO == null) {
                        continue;
                    }
                    if (sClassType == String.class) {
                        if (StringUtils.isEmpty(sO.toString())) {
                            continue;
                        }
                    }
                    /**
                     * 等于零的时候  会把字段去掉
                     */
                    if (sClassType == int.class) {//sClassType == Integer.class || 
                        if (((int) sO) == 0) {
                            continue;
                        }
                    }
                    if (sClassType == String.class
                            || sClassType == Integer.class || sClassType == int.class
                            || sClassType == Long.class || sClassType == long.class
                            || sClassType == Boolean.class || sClassType == boolean.class
                            || sClassType == Double.class || sClassType == double.class
                            || sClassType == Float.class || sClassType == float.class
                            || sClassType == Byte.class || sClassType == byte.class
                            || sClassType == Byte[].class || sClassType == byte[].class
                            || sClassType == Short.class || sClassType == short.class
                            || sClassType == ObservableField.class) {
                        if (sClassType == ObservableField.class) {
                            Object value = ((ObservableField) sO).get();
                            if (value != null && !StringUtils.isEmpty(String.valueOf(value))) {
                                s0_value = String.valueOf(value);
                            } else {
                                continue;
                            }
                        } else {
                            s0_value = sO;
                        }
                    } else if (sClassType == JSONObject.class || sClassType == JSONArray.class) {
                        s0_value = sO;
                    } else {
                        try {
                            s0_value = createJson(sO);
                        } catch (Exception e) {
                            s0_value = "";
                        }
                    }
                    sS_majorKey = sMyKey.majorKey();
                    sS_assistantKey = sMyKey.assistantKey();
                    sJSONObject.put(sS_majorKey, s0_value);
                    if (!sS_assistantKey.equals("")) {
                        sJSONObject.put(sS_assistantKey, s0_value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            sJSONObject = null;
        }

        return sJSONObject;
    }

    public static ArrayList<Integer> gettcplist(JSONArray jsonArray) throws JSONException {
        ArrayList<Integer> list = new ArrayList<>();
        int a = 0;
        int mTemperature = 0;
        //25, 1, 2, 1, 0, 1, 1
        for (int i = 0; i < jsonArray.length(); i++) {
            a = jsonArray.getInt(i);
            if (jsonArray.length() > 11) {
                switch (i) {
                    case 4:
                        a = 25;
                        break;
                    case 5:
                        a = 1;
                        break;
                    case 6:
                        a = 2;
                        break;
                    case 7:
                        a = 1;
                        break;
                    case 8:
                        a = 1;
                        break;
                    case 9:
                        a = 1;
                        break;
                    case 10:
                        a = 1;
                        break;
                }
            }


            list.add(a);
        }
        if (jsonArray.length() > 11) {
            int number = list.get(list.size() - 1);
            list.remove(list.size() - 1);
            list.add(number + 32);
        }
        return list;
    }


    public static byte[] tobyte(com.alibaba.fastjson.JSONArray jsonArray) {
        byte[] mKeyValue = new byte[jsonArray.size()];
        byte a = 0;
        int size = jsonArray.size() - 1;
        for (int i = 0; i < jsonArray.size(); i++) {
            a = jsonArray.getByteValue(i);
            mKeyValue[i] = a;
        }
        return mKeyValue;
    }

    public static byte[] getbyte(String my) {
        String sb = my.substring(1, my.length() - 1);
        String[] number = sb.split(",");
        byte[] mKeyValue = new byte[number.length];
        for (int i = 0; i < number.length; i++) {
            int a = Integer.parseInt(number[i]);
            mKeyValue[i] = (byte) a;
        }

        return mKeyValue;
    }

    public static String getstring(String name) {
        String serch = "";
        if (!TextUtils.isEmpty(name)) {
            StringBuffer s = new StringBuffer();
            String[] na = name.split(" ");

            for (int i = 0; i < na.length; i++) {
                if (!TextUtils.isEmpty(na[i])) {
                    s.append(na[i]);
                }
            }
            serch = s.toString();
        }

        return serch;
    }

}
