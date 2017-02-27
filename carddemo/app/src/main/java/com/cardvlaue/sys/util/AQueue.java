package com.cardvlaue.sys.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.cardvlaue.sys.amount.CountAmountActivity;
import com.cardvlaue.sys.applyinfo.ApplyInfoActivity;
import com.cardvlaue.sys.financeintention.FinanceIntentionActivity;
import com.cardvlaue.sys.financeway.FinanceWayActivity;
import com.cardvlaue.sys.uploadphoto.UploadPhotoActivity;
import com.cardvlaue.sys.userinfo.UserInfoActivity;
import java.util.ArrayList;
import java.util.List;

public class AQueue {

    public List<FlagKey> list = new ArrayList<>();           // 存放Activity的类名称
    private int currentPage = 0;
    private GoBackListener backListener;

    public AQueue() {
        list.add(new FlagKey(FinanceIntentionActivity.class, 10));//融资意向
        list.add(new FlagKey(UserInfoActivity.class, 20));//完善信息
        list.add(new FlagKey(FinanceWayActivity.class, 30));//算算融资额度
        list.add(new FlagKey(CountAmountActivity.class, 40));//计算额度的结果
        list.add(new FlagKey(UploadPhotoActivity.class, 50));//上传店铺照片
        list.add(new FlagKey(ApplyInfoActivity.class, 60));//申请信息确认
    }

    // 将数据放到列表里面
    public void push(Class activity, int index) {
        list.add(new FlagKey(activity, index));
    }

    // 获取当前的页面
    private FlagKey getCurrentPage(int position) {
        return list.get(position);
    }

    // 设置位置
    public void setPosition(int position, Context context) {
        int index = list.indexOf(new FlagKey(position));
        if (index == -1) {
            return;
        }
        this.currentPage = index;
        context.startActivity(new Intent(context, getCurrentPage(currentPage).activity));
    }

    // 设置侦听
    public void setListener(GoBackListener goBackListener) {
        backListener = goBackListener;
    }

    // 返回时执行
    public void back(Context context) throws Exception {
        if (backListener == null) {
            throw new Exception("必须设置侦听方法");
        }

        if (currentPage == 0) {
            return;
        }
        currentPage--;
        backListener.afterGoBack(getCurrentPage(currentPage), context);
    }

    // 向前进一步
    public void next(Context context, int step) throws Exception {
        if (backListener == null) {
            throw new Exception("必须设置侦听方法");
        }
        int index = list.indexOf(new FlagKey(step));
        if (index == -1) {
            return;
        } else {
            currentPage = index + 1;
        }
        if (currentPage == list.size()) {
            currentPage--;
            return;
        }

        backListener.afterNext(getCurrentPage(currentPage), context);
    }

    public void addActivity(Activity activity, int index) {
        int position = list.indexOf(new FlagKey(index));
        if (position == -1) {
            return;
        }
        list.get(position).ac = activity;
    }

    // 执行返回前或返回后的方法
    public interface GoBackListener {

        void afterGoBack(FlagKey page, Context context);

        void afterNext(FlagKey page, Context context);
    }

}
