package com.cardvlaue.sys.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.cardvlaue.sys.CVApplication;
import timber.log.Timber;

public class CacheUtil {

    private static final String DEVICE_CACHE = "device";

    /**
     * 支付宝的url
     */

    private static final String ALIPAY_CACHE = "url";

    /**
     * 人脸识别
     */
    private static final String FACE_CACHE = "face";

    /**
     * 写入人脸识别错误次数
     *
     * @param context Context
     * @param id 店铺编号
     * @param content 错误次数
     */
    public static void putFaceFlag(Context context, String id, String content) {
        if (TextUtils.isEmpty(id)) {
            throw new RuntimeException("人脸识别-账号异常-putFaceFlag");
        }
        SharedPreferences.Editor editor = context
            .getSharedPreferences(FACE_CACHE, Context.MODE_PRIVATE)
            .edit();
        editor.putString(String.format("face%s", id), content);
        editor.apply();
    }

    /**
     * 获取人脸识别错误次数
     *
     * @param context Context
     * @param id 店铺编号
     * @return 错误次数
     */
    public static String getFaceFlag(Context context, String id) {
        if (TextUtils.isEmpty(id)) {
            throw new RuntimeException("人脸识别-账号异常-getFaceFlag");
        }
        SharedPreferences sp = context.getSharedPreferences(FACE_CACHE, Context.MODE_PRIVATE);
        return sp.getString(String.format("face%s", id), "");
    }

    /**
     * 保存支付宝的url
     *
     * @param context Context
     * @param id 每个店铺对应的appid
     * @param content url
     */
    public static void putAlipayUrlFlag(Context context, String id, String content) {
        if (TextUtils.isEmpty(id)) {
            throw new RuntimeException("支付宝-账号异常-putAlipayUrlFlag" + id);
        }
        SharedPreferences.Editor editor = context
            .getSharedPreferences(ALIPAY_CACHE, Context.MODE_PRIVATE).edit();
        editor.putString(String.format(ALIPAY_CACHE + "%s", id), content);
        editor.apply();
    }

    /**
     * 获取url
     *
     * @param context Context
     * @param id 每个店铺对应的appid
     * @return url
     */
    public static String getAlipayUrlFlag(Context context, String id) {
        if (TextUtils.isEmpty(id)) {
            throw new RuntimeException("支付宝-账号异常-getAlipayUrlFlag" + id);
        }
        SharedPreferences sp = context.getSharedPreferences(ALIPAY_CACHE, Context.MODE_PRIVATE);
        return sp.getString(String.format(ALIPAY_CACHE + "%s", id), "{}");
    }


    public static void putDeviceString(Context context, String[] keys, String[] values) {
        SharedPreferences.Editor editor = context
            .getSharedPreferences(DEVICE_CACHE, Context.MODE_PRIVATE).edit();
        for (int i = 0; i < keys.length; i++) {
            if (!TextUtils.isEmpty(keys[i])) {
                Timber.e("putDeviceString:%s", values[i]);
                editor.putString(keys[i], values[i]);
            }
        }
        editor.apply();
    }

    public static String[] getDeviceString(Context context, String[] keys) {
        String[] results = new String[keys.length];

        SharedPreferences sp = context.getSharedPreferences(DEVICE_CACHE, Context.MODE_PRIVATE);
        for (int i = 0; i < keys.length; i++) {
            results[i] = sp.getString(keys[i], null);
        }

        return results;
    }

    public static void putBoolean(Context context, String[] keys, boolean[] values) {
        SharedPreferences.Editor editor = context
            .getSharedPreferences(CVApplication.APP_FILE_NAME, Context.MODE_PRIVATE).edit();
        for (int i = 0; i < keys.length; i++) {
            if (!TextUtils.isEmpty(keys[i])) {
                Timber.e("putBoolean:%s", values[i]);
                editor.putBoolean(keys[i], values[i]);
            }
        }
        editor.apply();
    }

    public static boolean[] getBoolean(Context context, String[] keys) {
        boolean[] results = new boolean[keys.length];

        SharedPreferences sp = context
            .getSharedPreferences(CVApplication.APP_FILE_NAME, Context.MODE_PRIVATE);
        for (int i = 0; i < keys.length; i++) {
            results[i] = sp.getBoolean(keys[i], false);
        }

        return results;
    }

    public static void putString(Context context, String[] keys, String[] values) {
        SharedPreferences.Editor editor = context
            .getSharedPreferences(CVApplication.APP_FILE_NAME, Context.MODE_PRIVATE).edit();
        for (int i = 0; i < keys.length; i++) {
            if (!TextUtils.isEmpty(keys[i])) {
                Timber.e("putString:%s", values[i]);
                editor.putString(keys[i], values[i]);
            }
        }
        editor.apply();
    }

    public static String[] getString(Context context, String[] keys) {
        String[] results = new String[keys.length];

        SharedPreferences sp = context
            .getSharedPreferences(CVApplication.APP_FILE_NAME, Context.MODE_PRIVATE);
        for (int i = 0; i < keys.length; i++) {
            results[i] = sp.getString(keys[i], "");
        }

        return results;
    }

    public static void clearCache(Context context) {
        SharedPreferences.Editor editor = context
            .getSharedPreferences(CVApplication.APP_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}
