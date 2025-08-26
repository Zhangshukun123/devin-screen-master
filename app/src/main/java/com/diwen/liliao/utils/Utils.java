package com.diwen.liliao.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    /*******************************
     *
     *     在setContentView之前設定
     *     不會被手機系統設定文字大小影響
     *     強制設定放大倍率為 configuration.fontScale
     *
     * @param activity
     * @param configuration - getResources().getConfiguration()
     */
    public static void adjustFontScale(Activity activity, Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(metrics);
        }
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        activity.getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    /**************************************
     *
     *      把圖片轉換成byte
     *
     * @param activity
     * @param img - drawable file id
     * @return
     */
    public static byte[] changeImgIdToByte(Activity activity, int img) {
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), img);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    /**
     * 取得螢幕 大小
     *
     * @param context context
     * @return {寬,高}
     */
    public static int[] getDisplaySize(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }


    /**
     * 震动
     *
     * @param context
     * @param milliseconds
     */
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    public static void vibrateGently(Context context) {
        vibrate(context, 50);
    }

    public static void gotoActivity(Context context, Class clazz) {
        context.startActivity(new Intent(context, clazz));
    }

    /**
     * 获取应用的versionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Point getScreenSize(Context context) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;
        return new Point(width, height);
    }

    /**
     * 判断屏幕是否点亮
     *
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context) {
        PowerManager powermanager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powermanager.isInteractive();
    }

    /**
     * Check if any apps are installed on the app to receive this intent.
     */
    public static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 将一个异常转化成字符串
     *
     * @param throwable
     * @return
     */
    public static String getStackTrace(final Throwable throwable) {
        if (throwable != null) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }
        return null;
    }

    /**
     * ViewGroup截图
     */
    public static Bitmap getViewGroupBitmapNoBg(ViewGroup viewGroup) {
        // 创建对应大小的bitmap(重点)
        Bitmap bitmap = Bitmap.createBitmap(viewGroup.getWidth(), viewGroup.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        viewGroup.draw(canvas);
        return bitmap;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    /**
     * 擷取圖片
     *
     * @param view   View
     * @param width  View 寬
     * @param height View 高
     * @return Bitmap
     */
    public static Bitmap captureView(View view, int width, int height) throws Throwable {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bm));
        return bm;
    }

    /**
     * @param view 需要截取图片的view（含有底色）
     * @return Bitmap
     */
    public static Bitmap getViewBitmap(Activity activity, View view) {
        View screenView = activity.getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        //获取屏幕整张图片
        Bitmap bitmap = screenView.getDrawingCache();

        if (bitmap != null) {
            //需要截取的长和宽
            int outWidth = view.getWidth();
            int outHeight = view.getHeight();

            //获取需要截图部分的在屏幕上的坐标(view的左上角坐标）
            int[] viewLocationArray = new int[2];
            view.getLocationOnScreen(viewLocationArray);

            //从屏幕整张图片中截取指定区域
            bitmap = Bitmap.createBitmap(bitmap, viewLocationArray[0], viewLocationArray[1], outWidth, outHeight);
        }
        return bitmap;
    }

    public Bitmap getSurfaceViewBitmap(SurfaceView surfaceView) {
//        Canvas canvas = surfaceView.getHolder().lockCanvas(null);//获取画布
        Bitmap bitmap = Bitmap.createBitmap(surfaceView.getMeasuredWidth(), surfaceView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        doDraw(canvas);
        return bitmap;
    }

    public static Bitmap ConvertToBitmap(View view) {

        view.setDrawingCacheEnabled(true);

        view.setDrawingCacheBackgroundColor(Color.TRANSPARENT);

//        surfaceView.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());

        bitmap.setHasAlpha(true);

        view.setDrawingCacheEnabled(false);

        return bitmap;

    }

    public static Bitmap takeScreenShot(View screenView) {
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 拼合两张Bitmap图片
     */
    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        //draw bg into
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        //draw fg into
        cv.drawBitmap(foreground, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        //save all clip
//        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.save();  //保存
        //store
        cv.restore();//存储
        return newbmp;
    }

    @Nullable
    public static Bitmap imageToBitmap(@NonNull Image image) {
        @Nullable Image.Plane[] planes = image.getPlanes();
        if (planes == null || planes.length <= 0) {
            return null;
        }
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        buffer.clear();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String getEncodeFromBitmap(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        byte[] someImg = stream.toByteArray();

        Log.e("checkImgByte", someImg.length + "");

        return Base64.encodeToString(someImg,
                Base64.NO_WRAP);
    }

    public static Bitmap getDecodeFromBitmap(String img) {
        byte[] byteImg = Base64.decode(img, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
    }

    /**
     * @param input 需要壓縮的字串
     * @return 壓縮後的字串
     * @throws IOException IO
     */
    public static String compress(String input) throws IOException {
        if (input == null || input.length() == 0) {
            return input;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipOs = new GZIPOutputStream(out);
        gzipOs.write(input.getBytes());
        gzipOs.close();
        return out.toString("ISO-8859-1");
    }

    /**
     * @param zippedStr 壓縮後的字串
     * @return 解壓縮後的
     * @throws IOException IO
     */
    public static String uncompress(String zippedStr) throws IOException {
        if (zippedStr == null || zippedStr.length() == 0) {
            return zippedStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(zippedStr
                .getBytes(StandardCharsets.ISO_8859_1));
        GZIPInputStream gzipIs = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gzipIs.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
// toString()使用平臺預設編碼，也可以顯式的指定如toString("GBK")
        return out.toString();
    }

    /**
     * Bitmap 旋轉
     *
     * @param bitmap Bitmap
     * @param rotate 角度
     * @return Bitmap
     */
    public static Bitmap bitmapRotate(Bitmap bitmap, float rotate) {
        Matrix mMatrix = new Matrix();
        mMatrix.setRotate(rotate);
        return bitmap = Bitmap.createBitmap(bitmap
                , 0
                , 0
                , bitmap.getWidth()   // 寬度
                , bitmap.getHeight()  // 高度
                , mMatrix
                , true
        );
    }

    /**
     * 取得圖片方向
     *
     * @param imagePath 圖片路徑
     * @return 方向角度
     */
    private static int getOrientationFromExif(String imagePath) {
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    orientation = 0;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to get image exif orientation", e);
        }
        return orientation;
    }

    /************
     * URI轉PATH
     * @param context
     * @param data
     * @return
     */
    public static String getPathByUri(Context context, Uri data) {
        String filename = null;
        if (data == null) return null;
        if (data.getScheme().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(data, new String[]{"_data"}, null, null, null);
            if (cursor.moveToFirst()) {
                filename = cursor.getString(0);
            }
        } else if (data.getScheme().compareTo("file") == 0) {// file:///开头的uri
            filename = data.toString().replace("file://", "");// 替换file://
            if (!filename.startsWith("/mnt")) {// 加上"/mnt"头
                filename += "/mnt";
            }
        }
        return filename;
    }

    /**
     * 獲取頂部statusBar高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Status height:" + height);
        return height;
    }

    /**
     * 獲取底部navigationBar高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }

    /**
     * 取設備是否存在NavigationBar
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }

        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    public static String getAssetToString(@NonNull Context context, @NonNull String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));

            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }

            reader.close();
            String string = stringBuilder.toString();
            return string.replace("\\n", System.getProperty("line.separator"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;
    static WeakReference<Activity> sTopActivityWeakRef;
    static List<Activity> sActivityList = new LinkedList<>();

    private static final Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            sActivityList.add(activity);
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            sActivityList.remove(activity);
        }
    };

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    public static void init(@NonNull final Application app) {
        Utils.sApplication = app;
        app.registerActivityLifecycleCallbacks(mCallbacks);
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    private static void setTopActivityWeakRef(Activity activity) {
        if (sTopActivityWeakRef == null || !activity.equals(sTopActivityWeakRef.get())) {
            sTopActivityWeakRef = new WeakReference<>(activity);
        }
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    /**
     * 保存Bitmap为文件;可能报空指针是因为没有配置权限
     *
     * @param bmp
     * @param filename
     * @return
     */
    public static void saveBitmap2file(Bitmap bmp, String filename) {
        File appDir = new File(getDCIM());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = filename + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {

        }
    }

    private static String getDCIM() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return "";
        }

        String path = Environment
                .getExternalStorageDirectory().getPath()
                + "/";

        if (new File(path).exists()) {
            return path;
        }
        path = Environment
                .getExternalStorageDirectory().getPath()
                + "/";
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return "";
            }
        }
        return path;

    }

    /**
     * 读取文件为Bitmap
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmapFromFile(String filename) {
        try {
            return BitmapFactory.decodeStream(new FileInputStream(getDCIM()
                    + filename
                    + ".jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeResource(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
