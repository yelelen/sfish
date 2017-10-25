package com.yelelen.sfish.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

import com.yelelen.sfish.R;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.lang.Math.sqrt;

/**
 * Created by yelelen on 17-9-4.
 */

public class Utils {
    private static Toast sToast;

    public static void showToast(Context context, String text) {
        if (sToast == null) {
            sToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {

            sToast.setText(text);
        }

        sToast.show();
    }

    public static void close(Closeable... streams) {
        for (Closeable stream : streams) {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static Bitmap compressBitmap(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private static int STATUS_BAR_HEIGHT = -1;

    /**
     * 得到我们的状态栏的高度
     *
     * @param activity Activity
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && STATUS_BAR_HEIGHT == -1) {
            try {
                final Resources res = activity.getResources();
                // 尝试获取status_bar_height这个属性的Id对应的资源int值
                int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId <= 0) {
                    Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                    Object object = clazz.newInstance();
                    resourceId = Integer.parseInt(clazz.getField("status_bar_height")
                            .get(object).toString());
                }


                // 如果拿到了就直接调用获取值
                if (resourceId > 0)
                    STATUS_BAR_HEIGHT = res.getDimensionPixelSize(resourceId);

                // 如果还是未拿到
                if (STATUS_BAR_HEIGHT <= 0) {
                    // 通过Window拿取
                    Rect rectangle = new Rect();
                    Window window = activity.getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    STATUS_BAR_HEIGHT = rectangle.top;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        //int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        //int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return displayMetrics.heightPixels;
    }

    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int px2sp(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scale + 0.5f);
    }

    public static int sp2px(Context context, int sp) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    /**
     * 获取String的MD5值
     *
     * @param info 字符串
     * @return 该字符串的MD5值
     */
    public static String getMD5(String info) {
        try {
            //获取 MessageDigest 对象，参数为 MD5 字符串，表示这是一个 MD5 算法（其他还有 SHA1 算法等）：
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //update(byte[])方法，输入原数据
            //类似StringBuilder对象的append()方法，追加模式，属于一个累计更改的过程
            md5.update(info.getBytes("UTF-8"));
            //digest()被调用后,MessageDigest对象就被重置，即不能连续再次调用该方法计算原数据的MD5值。可以手动调用reset()方法重置输入源。
            //digest()返回值16位长度的哈希值，由byte[]承接
            byte[] md5Array = md5.digest();
            //byte[]通常我们会转化为十六进制的32位长度的字符串来使用,本文会介绍三种常用的转换方法
            return bytesToHex1(md5Array);
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @NonNull
    private static String bytesToHex1(byte[] md5Array) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < md5Array.length; i++) {
            int temp = 0xff & md5Array[i];//TODO:此处为什么添加 0xff & ？
            String hexString = Integer.toHexString(temp);
            if (hexString.length() == 1) {//如果是十六进制的0f，默认只显示f，此时要补上0
                strBuilder.append("0").append(hexString);
            } else {
                strBuilder.append(hexString);
            }
        }
        return strBuilder.toString();
    }

    //通过java提供的BigInteger 完成byte->HexString
    private static String bytesToHex2(byte[] md5Array) {
        BigInteger bigInt = new BigInteger(1, md5Array);
        return bigInt.toString(16);
    }

    //通过位运算 将字节数组到十六进制的转换
    public static String bytesToHex3(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    public static boolean copyFile(String source, String dest) {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        if (source == null || dest == null || TextUtils.isEmpty(source))
            return false;
        File saveFile = new File(dest);
        if (saveFile.exists())
            return true;

        try {
            fos = new FileOutputStream(dest);
            fis = new FileInputStream(source);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(fis, fos);
        }
    }

    /**
     * 判断手机是否有SD卡。
     *
     * @return 有SD卡返回true，没有返回false。
     */
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    public static int getMaxMemory() {
        return (int) Runtime.getRuntime().maxMemory();
    }

    public static boolean createDirectory(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        try {
            File file = new File(path);
            if (file.exists() && !file.isDirectory()) {
                file.delete();
                file.mkdirs();
            } else if (!file.exists())
                file.mkdirs();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showWifiDlg(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog wifiDialog = builder.setIcon(R.drawable.ic_logo)
                .setTitle(R.string.wifi_title)
                .setMessage(R.string.wifi_message)
                .setPositiveButton(R.string.wifi_go_setting, new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到系统的网络设置界面
                        Intent intent = null;
                        // 先判断当前系统版本
                        if (Build.VERSION.SDK_INT > 10) {  // 3.0以上
                            intent = new Intent(Settings
                                    .ACTION_WIRELESS_SETTINGS);
                        } else {
                            intent = new Intent();
                            intent.setClassName("com.android.settings",
                                    Settings.ACTION_WIFI_SETTINGS);
                        }
//                        if ((context instanceof Application)) {
//                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                        }
                        context.startActivity(intent);

                    }
                })
                .setNegativeButton(R.string.wifi_known, null)
                .create();
        wifiDialog.show();
    }

    public static float twoPointDistance(PointF point1, PointF point2) {
        float x1 = point1.x;
        float y1 = point1.y;
        float x2 = point2.x;
        float y2 = point2.y;
        float x = Math.abs(x1 - x2);
        float y = Math.abs(y1 - y2);

        if (x1 == x2)
            return y;
        if (y1 == y2)
            return x;

        return (float) sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    // 已知三个点坐标，求得夹角ABC, AB 和 BC 组成的夹角， 最大为Math.PI, 其补角为360减去返回的角度
    public static float threePointAngle(PointF a, PointF center, PointF c) {
//        float x1 = a.x - b.x;
//        float y1 = a.y - b.y;
//        float x2 = c.x - b.x;
//        float y2 = c.y - b.y;
//
//        float x = x1 * x2 + y1 * y2;
//        float y = x1 * y2 - x2 * y1;
//
//        float angle = (float) Math.toDegrees(Math.acos(x/Math.sqrt(x*x + y*y)));
//        if (c.x < b.x)
//            angle = 360 - angle;
//        return angle;

        float dx1, dx2, dy1, dy2;
        float angle;

        dx1 = a.x - center.x;
        dy1 = a.y - center.y;

        dx2 = c.x - center.x;

        dy2 = c.y - center.y;

        float result = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1) * (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);

        if (result == 0) return -1;

        angle = (float) Math.acos((dx1 * dx2 + dy1 * dy2) / result);
        angle = (float) Math.toDegrees(angle);

        return angle;
    }

    public static String getDurationText(int second) {
        int s = second % 60;
        int m = second / 60;
        int h = m / 60;
        m %= 60;
        String str = s < 10 ? ("0" + String.valueOf(s)) : String.valueOf(s);
        if (second < 0) {
            return "0:00";
        } else if (second >= 0 && second < 60) {
            return "0:" + str;
        } else if (second >= 60 && second < 3600) {
            return String.valueOf(m) + ":" + str;
        } else {
            return String.valueOf(h) + ":" + String.valueOf(m) + ":" + str;
        }
    }
}

