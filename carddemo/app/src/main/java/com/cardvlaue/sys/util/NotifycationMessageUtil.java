package com.cardvlaue.sys.util;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;
import android.widget.Toast;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.message.MessageActivity;
import java.util.Map;
import java.util.Random;

/**
 * 显示消息 Created by Administrator on 2016/9/28.
 */
public class NotifycationMessageUtil {

    public static void showNotifycationMessage(Map<String, Object> param, Context context) {
        try {
            Bitmap btm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.icon_logo)
                .setContentTitle(param.get("title").toString())
                .setContentText(param.get("content").toString());
            mBuilder.setTicker(param.get("title").toString());//第一次提示消息的时候显示在通知栏上
            mBuilder.setNumber(1);
            mBuilder.setLargeIcon(btm);
            mBuilder.setAutoCancel(true);//自己维护通知的消失
            //构建一个Intent
            Intent resultIntent = new Intent(context, MessageActivity.class);
            resultIntent.putExtra("type", param.get("type").toString());
            //封装一个Intent
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, new Random().nextInt(), resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
            // 设置通知主题的意图
            mBuilder.setContentIntent(resultPendingIntent);
            //获取通知管理器对象
            NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NormalAlert1(String title, String msg, Context context,
        DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(null);
        builder.setPositiveButton("立即查看", ok);
        builder.setNegativeButton("稍后查看", cancel);
        //builder.create().show();
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                alert.show();
            } else {
                Toast.makeText(context, "无消息弹出权限", Toast.LENGTH_LONG).show();
            }
        } else {
            alert.show();
        }
    }

}
