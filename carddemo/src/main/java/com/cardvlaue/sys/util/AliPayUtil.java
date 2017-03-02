package com.cardvlaue.sys.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by cardvalue on 2016/10/24.
 */
public class AliPayUtil {

    /**
     * com.eg.android.AlipayGphone.AlipayLogin
     */
    public static final String APP_PACKAGE_NAME = "com.eg.android.AlipayGphone";//包名

    /**
     * 启动支付宝
     */
    public static void launchapp(Context context) {
        // 判断是否安装过App，否则去市场下载
        if (isAppInstalled(context, APP_PACKAGE_NAME)) {
            context
                .startActivity(
                    context.getPackageManager().getLaunchIntentForPackage(APP_PACKAGE_NAME));
        } else {
            goToMarket(context, APP_PACKAGE_NAME);
        }
    }

    /**
     * 检测某个应用是否安装
     */
    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 去市场下载页面
     */
    private static void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("http://sj.qq.com/myapp/detail.htm?apkName=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
        }
    }

}
