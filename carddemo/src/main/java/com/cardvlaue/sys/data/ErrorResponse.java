package com.cardvlaue.sys.data;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.cardvlaue.sys.login.LoginActivity;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.util.CacheUtil;
import com.cardvlaue.sys.util.ToastUtil;

/**
 * 请求失败
 */
public class ErrorResponse {

    private int code;

    private String error;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int responseSuccess(Context context) {
        if (code == 0) {
            return 0;
        } else if (code == 401) {
            if (!TextUtils.isEmpty(error)) {
                ToastUtil.showFailure(context, error);
            } else {
                ToastUtil.showFailure(context, "未知错误401");
            }
            CacheUtil.clearCache(context);
            context.startActivity(new Intent(context, LoginActivity.class));
            return 401;
        } else if (code == 400) {//新增error400,抛出400申请未通过，直接到申请列表
            if (!TextUtils.isEmpty(error)) {
                ToastUtil.showFailure(context, error);
            } else {
                ToastUtil.showFailure(context, "未知错误400");
            }
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("apply", "011111");
            context.startActivity(intent);
            return 400;
        }

        return -1;
    }

}
