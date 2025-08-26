package com.diwen.liliao.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created By  tian on 2022/9/19
 * Describe:
 */
public class AtyUtils {


    /**
     * String 是否为空
     *
     * @param textView
     * @return
     */
    public static boolean isStringEmpty(String textView) {
        return !TextUtils.isEmpty(textView) && !textView.equals("null");
    }
    /**
     * list size >0 是否为空
     *
     * @param lite
     * @return
     */
    public static boolean  isListEmpty(List lite) {
        if (lite==null || lite.size()==0) {
            return false;
        }
        return true;
    }

    /**
     * 获取TextView的字符串
     *
     * @param textView
     * @return
     */
    public static String getText(TextView textView) {
        String text = "";
        if (textView != null) {
            if (isTextEmpty(textView)) {
                text = "";
            } else {
                text = textView.getText().toString().trim();
            }
        }
        return text;
    }

    /**
     * TextView是否为空
     *
     * @param textView
     * @return
     */
    public static boolean isTextEmpty(TextView textView) {
        if (textView != null) {
            String msg = textView.getText().toString().trim();
            return TextUtils.isEmpty(msg);
        }
        return true;
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 小数点前后大小不一致
     *
     * @param values
     * @return
     */
    public static SpannableString getMoneySize(double values) {
        if (values < 0) {
            return new SpannableString("");
        }
        String value = "¥" + AtyUtils.get2Point(values);
        SpannableString spannableString = new SpannableString(value);
        if (value.contains(".")) {
            spannableString.setSpan(new RelativeSizeSpan(0.8f), value.indexOf("¥"), 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.8f), value.indexOf("."), value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    /**
     * 小数点前后大小不一致
     *
     * @param values
     * @return
     */
    public static SpannableString getVipSmallSize(double values) {
        if (values < 0) {
            values = 0;
        }
        String value = "¥" + AtyUtils.get2Point(values);
        SpannableString spannableString = new SpannableString(value);
        if (value.contains(".")) {
            spannableString.setSpan(new RelativeSizeSpan(0.8f), value.indexOf("¥"), 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.8f), value.indexOf("."), value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }


    /**
     * 保留两位小数
     */
    public static String get2Point(double point) {
        String format = new DecimalFormat("#.00").format(point);
        if (format.startsWith(".")) {
            return "0" + format;
        }
        return format;
    }

    /**
     * 保留两位小数
     */
    public static String getTenThousand(int point) {
        String Count = point + "";
        if (point > 10000) {
            Count = (point / 10000) + "w";
        }
        return Count;
    }

    //获取当前时间前后几天的时间
    public static String beforeAfterDate(int days, boolean defor_after, String format) {
        long nowTime = System.currentTimeMillis();
        long changeTimes = days * 24L * 60 * 60 * 1000;
        if (defor_after) {
            return getStrTime(String.valueOf(nowTime + changeTimes), format);
        }
        return getStrTime(String.valueOf(nowTime - changeTimes), format);
    }

    //时间戳转字符串
    public static String getStrTime(String timeStamp, String format) {

        String timeString = null;

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        long l = Long.valueOf(timeStamp);

        timeString = sdf.format(new Date(l));//单位秒

        return timeString;
    }

    public static String format(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

    public static String format(String value) {
        return value.length() < 2 ? "0" + value : value;
    }

    public static int getVersionCode(Context cxt) {
        // 获取packagemanager的实例
        PackageManager packageManager = cxt.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        int version = -1;
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(cxt.getPackageName(), 0);
            version = packInfo.versionCode;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getVersionName(Context cxt) {
        // 获取packagemanager的实例
        PackageManager packageManager = cxt.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        String version = "";
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(cxt.getPackageName(), 0);
            version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static void InitRecyclerView(Context context, RecyclerView recyclerView, int orientation) {
        if (orientation == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        } else if (orientation == 2) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        } else if (orientation == 3) {
            GridLayoutManager gridLayoutManager =
                    new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else if (orientation == 4) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }  else {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }



    @SuppressLint("WrongConstant")
    public static void callPhone(Activity activity, String phone) {
        // 判断有没有拨打电话权限
        if (!TextUtils.isEmpty(phone)) {
            if (PackageManager.PERMISSION_GRANTED == PermissionChecker.checkSelfPermission(activity,
                    Manifest.permission.CALL_PHONE)) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                activity.startActivity(intent);

            } else {
                Uri packageURI = Uri.parse("package:" + "cn.appoa.chefutech");
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                activity.startActivity(intent);
            }
        }
    }

    public static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

    public static String getCommaSplitString(List<String> stringList) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : stringList) {
            if (s.equals("www")) {
                continue;
            }
            if (stringBuffer.length() > 0) {
                stringBuffer.append(",");
            }

            stringBuffer.append(s);
        }
        return stringBuffer.toString();


    }

    public static boolean isUrlHasVideo(String url) {
        return url.endsWith(".mp4");
    }

    public static boolean isSuffixOfImage(String name) {
        return !TextUtils.isEmpty(name) && name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")
                || name.endsWith(".bmp");
    }

    public static String gettimeFs(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second >= 3600) {
            h = second / 3600;
            if (h > 23) {
                h = h % 24;
            }
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        return format(h) + ":" + format(d) + ":" + format(s);
    }

} 
