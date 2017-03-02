package com.cardvlaue.sys.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;
import android.content.pm.ApplicationInfo;
import timber.log.Timber;

public class DeviceUtil {

    private static final String SPACE = " ";

    /**
     * 获取屏幕宽度
     *
     * @param context Context
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取版本名
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("getVersionNameEEE:%s", e.getLocalizedMessage());
            return "";
        }
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("getVersionCodeEEE:%s", e.getLocalizedMessage());
            return -1;
        }
    }

    /**
     * 获取设备信息
     */
    public static String getDeviceInfo(Context context) {
        return Build.BRAND + SPACE + Build.MODEL + SPACE + getImei(context);
    }

    public static String getImei(Context context) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat
            .checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            Timber.e("getImei权限");
            return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                .getDeviceId();
        } else {
            // Can't create handler inside thread that has not called Looper.prepare()
            // Toast.makeText(context, "无权限", Toast.LENGTH_LONG).show();
            Timber.e("getImei无权限");
            return null;
        }
    }

    /**
     * 获取浏览器 UserAgent
     *
     * @param context Context
     * @return UA
     */
    public static String getUA(Context context) {
        return new WebView(context).getSettings().getUserAgentString();
    }

    /**
     * 获取应用详情页面 Intent
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent
                .setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent
                .putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }


    public static List<PackageInfo> getAllApps(Context context) {

        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);

            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                apps.add(pak);
                Log.e("添加自己已经安装的应用程序  ", pak.packageName);
            }

        }
        return apps;
    }

}
