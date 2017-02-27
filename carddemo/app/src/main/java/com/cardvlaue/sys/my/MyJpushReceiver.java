package com.cardvlaue.sys.my;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.message.MessageActivity;
import com.cardvlaue.sys.util.NotifycationMessageUtil;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import timber.log.Timber;

public class MyJpushReceiver extends BroadcastReceiver {

    private static Lock lock = new ReentrantLock();
    private static boolean flag = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);//设置日期格式
        String dt = df.format(new Date());
        ArrayMap<String, Object> result = new ArrayMap<>();
        ;
        try {
            if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Timber.e("接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                result = new Gson()
                    .fromJson(bundle.getString("cn.jpush.android.EXTRA"), ArrayMap.class);
                if (result.get("aps") != null) {
                    Map<String, Object> info = (Map<String, Object>) result.get("aps");
                    result.put("create_date", dt);
                    result.put("title", "系统消息");
                    result.put("content", info.get("alert"));
                    result.put("messContent", info.get("alert"));
                    result.put("type", "1");
                    Timber.e("通知：1");
                } else {
                    result.put("create_date", dt);
                    //通知
                    result.put("title", result.get("notiTitle"));
                    result.put("content", result.get("notiContent"));
                    result.put("messContent", result.get("messContent"));
                    result.put("type", "0");
                    Timber.e("通知：2");
                }
            }
        } catch (Exception e) {
            result.clear();
            result.put("create_date", dt);
            result.put("title", "系统消息");
            result.put("content", bundle.getString(JPushInterface.EXTRA_MESSAGE));
            //通知
            result.put("messContent", result.get("messContent"));
            result.put("type", "0");
            Timber.e("通知3：" + JSON.toJSONString(result));
        }
        Timber
            .e("通知4：" + JSON.toJSONString(result) + !TextUtils.isEmpty(JSON.toJSONString(result)));
        if (!TextUtils.isEmpty(JSON.toJSONString(result)) && !JSON.toJSONString(result)
            .equals("null")
            && result.size() > 0) {
            NotifycationMessageUtil.showNotifycationMessage(result, context);

            new Thread() {
                public void run() {
                    Looper.prepare();
                    if (flag) {
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            lock.lock();
                            flag = true;
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            flag = false;
                            lock.unlock();
                        }
                    }.start();
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    NotifycationMessageUtil
                        .NormalAlert1("新的消息", "您有新的消息，是否查看?", context.getApplicationContext(),
                            (dialog, which) -> {
                                Intent intent = new Intent();
                                intent.putExtra("type", "");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(context.getApplicationContext(),
                                    MessageActivity.class);
                                context.getApplicationContext().startActivity(intent);
                                dialog.cancel();
                            }, (dialog, which) -> {
                                //系统中关机对话框就是这个属性
                                dialog.cancel();
                            });
                    Looper.loop();
                }
            }.start();
        }
    }

}
