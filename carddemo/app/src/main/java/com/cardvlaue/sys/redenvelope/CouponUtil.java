package com.cardvlaue.sys.redenvelope;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.cardverify.CheckingTools;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.ToastUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 红包提现 Created by Administrator on 2016/7/20.
 */
public class CouponUtil {

    public static Animator anim1;
    public static Animator anim2;


    public static Animator anim3;
    public static Animator anim4;

    public static String phone, objectId, token;


    public static Dialog showCoupon(final Activity context, final String coupId,
        final ProgressDialog dialog1) {//红包的id
        AlertDialog.Builder builder = new Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_coupon, null);
        final EditText tv = (EditText) view.findViewById(R.id.ed_name);//姓名
        final EditText ed_card = (EditText) view.findViewById(R.id.ed_card);//银行卡号
        builder.setView(view);
        final Dialog dialog = builder.show();
        Button cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        Button confirm_btn = (Button) view.findViewById(R.id.confirm_btn);
        cancel_btn.setOnClickListener(v -> dialog.dismiss());
        confirm_btn.setOnClickListener(v -> {
            if (tv.getText().toString().trim().equals("")) {
                ToastUtil.showFailure(context, "请填写姓名");
                return;
            } else if (ed_card.getText().toString().trim().equals("")) {
                ToastUtil.showFailure(context, "请填写银行卡号");
                return;
            } else if (!CheckingTools.BankPublic(ed_card.getText().toString().trim())) {
                ToastUtil.showFailure(context, "请输入正确的对银行账号");
                return;
            } else {
                showCouponConfirm(context,
                    ((EditText) view.findViewById(R.id.ed_name)).getText().toString(),
                    ed_card.getText().toString(), coupId);
                dialog.dismiss();
            }
        });

        return dialog;
    }


    public static Dialog showCouponConfirm(final Activity context, final String name,
        final String card, final String coupId) {//红包的id
        AlertDialog.Builder builder = new Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_coupon_confirm, null);
        final TextView tv = (TextView) view.findViewById(R.id.ed_name);//姓名
        tv.setText(name);
        final TextView ed_card = (TextView) view.findViewById(R.id.ed_card);//银行卡号
        ed_card.setText(card);
        builder.setView(view);
        final Dialog dialog = builder.show();
        Button cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        Button confirm_btn = (Button) view.findViewById(R.id.confirm_btn);
        cancel_btn.setOnClickListener(v -> dialog.dismiss());

        confirm_btn.setOnClickListener(v -> {
            Timber.e("name:" + name + "card:" + card + "coupId:" + coupId);
            GetCoupon(dialog, context, name, card, coupId);
                /*aa.businessProcess.QueryConvertCoupons(name,card, coupId);
                    MessageBox.show(aa.dialog, "正在提交...", "正在提交,请稍等...");*/
            dialog.dismiss();
        });
        return dialog;
    }

    /**
     * 兑换红包
     */
    public static void GetCoupon(final Dialog dialog, Activity context, final String name,
        final String card, String coupId) {
        IFinancingRest rest = HttpConfig.getClient().create(IFinancingRest.class);
        TasksRepository repository = ((CVApplication) context.getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
        phone = repository.getMobilePhone();
        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        JSONObject query = new JSONObject();
        query.put("ownerName", name);
        query.put("cardNo", card);
        Timber.e("兑换红包name:" + name + "card:" + card + "coupId:" + coupId);
        rest.convertCoupons(objectId, token, objectId, coupId, query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                Timber.e("红包兑换" + s);
                dialog.cancel();
                dialog.dismiss();
                Message msg = new Message();
                msg.what = 0x1017;//兑换红包成功
                Bundle bundl = new Bundle();
                msg.setData(bundl);
                CouponFrament.handler.sendMessage(msg);

            }, throwable -> Timber
                .e(JSON.toJSONString(throwable) + "-----" + throwable.getMessage()));
    }


}
