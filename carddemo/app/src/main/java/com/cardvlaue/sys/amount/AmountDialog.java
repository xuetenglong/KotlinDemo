package com.cardvlaue.sys.amount;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * <p>手机验证和征信弹出来的dialog<p/> Created by Administrator on 2016/7/8.
 */
public class AmountDialog extends DialogFragment {

    public static final String BUS_COUNTAMOUNT_CODE = "BUS_COUNTAMOUNT_CODE";
    public static final String BUS_AMOUNT_CODE = "BUS_AMOUNT_CODE";
    private Animator anim1;
    private Animator anim2;
    private OnClickPhone phoneListener;
    private OnClickCredit creditListener;
    private OnClickNext clickNext;
    private ImageView iv_isJxlValid;
    private String TypeId;
    private TextView tv_isJxlValid;
    private Subscriber mMoneySubscriber;

    public static AmountDialog newInstance(int status) {
        AmountDialog fragment = new AmountDialog();
        Bundle args = new Bundle();
        args.putInt("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // setStyle(STYLE_NORMAL,R.style.TransparentStatusBarTheme);
     /*   Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        final View view = inflater.inflate(R.layout.dialog_amount, container, false);
        //取消
        view.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());

        iv_isJxlValid = (ImageView) view.findViewById(R.id.iv_isJxlValid);
        tv_isJxlValid = (TextView) view.findViewById(R.id.tv_isJxlValid);

        int statusStr = getArguments().getInt("status");
        if (statusStr == 0) {
            iv_isJxlValid.setImageResource(R.mipmap.icon_phone_verificat);
            tv_isJxlValid.setTextColor(Color.parseColor("#359DF5"));

            ((ImageView) view.findViewById(R.id.iv_creditReportStatus))
                .setImageResource(R.mipmap.ic_credits);
            ((TextView) view.findViewById(R.id.tv_creditReportStatus))
                .setTextColor(Color.parseColor("#ff9000"));
        }
        if (statusStr == 1) {////0 都没有验证  1 手机验证   2征信验证
            Timber.e("status");
            iv_isJxlValid.setImageResource(R.mipmap.is_successful);
            ((TextView) view.findViewById(R.id.tv_isJxlValid))
                .setTextColor(Color.parseColor("#c5c5c5"));
        } else if (statusStr == 2) {
            ((ImageView) view.findViewById(R.id.iv_creditReportStatus))
                .setImageResource(R.mipmap.is_successful);
            ((TextView) view.findViewById(R.id.tv_creditReportStatus))
                .setTextColor(Color.parseColor("#c5c5c5"));
        }

        RxBus.getDefaultBus().toObserverable()
            .observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (CountAmountActivity.BUS_COUNTAMOUNT_CODE.equals(busIndustrySelect.getBus())
                    || AmountDialog.BUS_AMOUNT_CODE.equals(busIndustrySelect.getBus())) {
                    Timber.e("手机验证的返回AmountDialog：" + busIndustrySelect.getTypeId());
                    iv_isJxlValid.setImageResource(R.mipmap.is_successful);
                    ((TextView) view.findViewById(R.id.tv_isJxlValid))
                        .setTextColor(Color.parseColor("#c5c5c5"));
                    Timber.e("手机验证的返回AmountDialog2222：" + busIndustrySelect.getTypeId());
                    TypeId = busIndustrySelect.getTypeId();
                    Timber.e("11111111111111111TypeId：" + TypeId);
                }
            }
        });
/*
         //首次
        RxBus.getDefaultBus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (AmountDialog.BUS_AMOUNT_CODE.equals(busIndustrySelect.getBus())) {
                    Timber.e("手机验证的返回AmountDialog："+busIndustrySelect.getTypeId());
                    iv_isJxlValid.setImageResource(R.mipmap.is_successful);
                    //  view.findViewById(R.id.iv_isJxlValid).setTextColor();
                    ((TextView)view.findViewById(R.id.tv_isJxlValid)).setTextColor(Color.parseColor("#c5c5c5"));
                    Timber.e("手机验证的返回AmountDialog2222："+busIndustrySelect.getTypeId());
                    TypeId=busIndustrySelect.getTypeId();
                    Timber.e("11111111111111111TypeId："+TypeId);
                }
            }
        });*/
       /* Timber.e("判断为空前11111TypeId  isJxlValids："+TypeId);
        if(!TextUtils.isEmpty(TypeId)){
            Timber.e("11111111111111111：");
            iv_isJxlValid.setImageResource(R.mipmap.is_successful);
            tv_isJxlValid.setTextColor(Color.parseColor("#c5c5c5"));
        }else{
            Timber.e("222222222222222222222："+TextUtils.isEmpty(TypeId));
            iv_isJxlValid.setImageResource(R.mipmap.icon_phone_verificat);
            tv_isJxlValid.setTextColor(Color.parseColor("#359DF5"));
        }*/
        //手机验证
        view.findViewById(R.id.rl_phone).setOnClickListener(v -> {
            if (phoneListener != null) {
                phoneListener.phone();
            }
            //dismiss();
        });

        view.findViewById(R.id.tv_next).setOnClickListener(view1 -> {
            if (clickNext != null) {
                clickNext.next();
            }

        });

        //征信验证
        view.findViewById(R.id.rl_credit).setOnClickListener(v -> {
            if (creditListener != null) {
                creditListener.credit();
            }
            // dismiss();
        });

        LinearLayout image = (LinearLayout) view.findViewById(R.id.layout);
       /* PropertyValuesHolder valueHolder_1 = PropertyValuesHolder.ofFloat(
                "scaleX", 1f, 0.93f);
        PropertyValuesHolder valuesHolder_2 = PropertyValuesHolder.ofFloat(
                "scaleY", 1f, 0.93f);
        anim1 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_1,
                valuesHolder_2);
        anim1.setDuration(250);
        anim1.setInterpolator(new LinearInterpolator());*/

        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat(
            "scaleX", 0.1f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
            "scaleY", 0.1f, 1f);
        anim2 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_3,
            valuesHolder_4);
        anim2.setDuration(300);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.start();

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                anim2.end();
                anim1.start();
            }
        },300);*/
        return view;
    }


    /**
     * 手机验证
     */
    public void setPhoneListener(OnClickPhone phoneListener) {
        this.phoneListener = phoneListener;
    }

    /**
     * 征信验证
     */
    public void setCreditListener(OnClickCredit creditListener) {
        this.creditListener = creditListener;
    }

    public void setClickNext(OnClickNext clickNext) {
        this.clickNext = clickNext;
    }

    public interface OnClickPhone {

        void phone();
    }

    public interface OnClickCredit {

        void credit();
    }

    public interface OnClickNext {

        void next();
    }
}