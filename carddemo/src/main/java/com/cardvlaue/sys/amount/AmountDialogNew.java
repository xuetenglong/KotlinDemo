package com.cardvlaue.sys.amount;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * <p>手机验证和征信弹出来的dialog<p/> Created by Administrator on 2016/7/8.
 */
public class AmountDialogNew extends DialogFragment {

    public static final String BUS_COUNTAMOUNT_CODE = "BUS_COUNTAMOUNT_CODE";
    public static final String BUS_AMOUNT_CODE = "BUS_AMOUNT_CODE";
    private Animator anim1;
    private Animator anim2;
    private OnClickPhone phoneListener;
    private OnClickCredit creditListener;
    private OnClickFace  clickFace;
    private OnClickNext clickNext;
    private TextView tv_creditReportStatus;
    private TextView tv_isJxlValid,tv_isFace;
    private CardView cv_phone,cv_credit,cv_face;

    public static AmountDialogNew newInstance(int isJlValidStatus,int isCreditReportStatus,int isKalaRecognizeStatus) {
        AmountDialogNew fragment = new AmountDialogNew();
        Bundle args = new Bundle();
        args.putInt("isJlValidStatus", isJlValidStatus);
        args.putInt("isCreditReportStatus", isCreditReportStatus);
        args.putInt("isKalaRecognizeStatus", isKalaRecognizeStatus);
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
        final View view = inflater.inflate(R.layout.dialog_amount_new, container, false);
        //取消
        view.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());

        tv_isJxlValid = (TextView) view.findViewById(R.id.tv_isJxlValid);
        tv_creditReportStatus= ((TextView) view.findViewById(R.id.tv_creditReportStatus));
        tv_isFace= ((TextView) view.findViewById(R.id.tv_isFace));

        cv_phone=((CardView) view.findViewById(R.id.cv_phone));
        cv_credit=((CardView) view.findViewById(R.id.cv_credit));
        cv_face=((CardView) view.findViewById(R.id.cv_face));

        int isJlValidStatus = getArguments().getInt("isJlValidStatus");
        int isCreditReportStatus = getArguments().getInt("isCreditReportStatus");
        int isKalaRecognizeStatus = getArguments().getInt("isKalaRecognizeStatus");

        Timber.e("isJlValidStatus==============="+isJlValidStatus);
        Timber.e("isCreditReportStatus==============="+isCreditReportStatus);
        Timber.e("isKalaRecognizeStatus==============="+isKalaRecognizeStatus);


        /**
         * 0 都没有验证  1 手机验证验证   2 征信验证验证  3 人脸识别验证
         * 1和2都验证为4
         * 1和3都验证为5
         * 2和3都验证为6
         */
        if(isJlValidStatus==0){
            tv_isJxlValid.setTextColor(Color.parseColor("#359DF5"));
            tv_isJxlValid.setText("未验证");
            tv_isJxlValid.setBackgroundResource(R.drawable.selector_register_get_code);
            cv_phone.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }else{
            tv_isJxlValid.setText("已验证");
            tv_isJxlValid.setBackgroundResource(R.drawable.shape_login_complete);
            tv_isJxlValid.setTextColor(Color.parseColor("#ffffff"));
            cv_phone.setCardBackgroundColor(Color.parseColor("#f7f7f7"));
        }



        if(isCreditReportStatus==0){
            tv_creditReportStatus.setText("未验证");
            tv_creditReportStatus.setTextColor(Color.parseColor("#359DF5"));
            tv_creditReportStatus.setBackgroundResource(R.drawable.selector_register_get_code);
            cv_credit.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }else{
            tv_creditReportStatus.setText("已验证");
            tv_creditReportStatus.setBackgroundResource(R.drawable.shape_login_complete);
            tv_creditReportStatus.setTextColor(Color.parseColor("#ffffff"));
            cv_credit.setCardBackgroundColor(Color.parseColor("#f7f7f7"));
        }


        if(isKalaRecognizeStatus==0){
            tv_isFace.setText("未验证");
            tv_isFace.setTextColor(Color.parseColor("#359DF5"));
            tv_isFace.setBackgroundResource(R.drawable.selector_register_get_code);
            cv_face.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }else{
            tv_isFace.setText("已验证");
            tv_isFace.setTextColor(Color.parseColor("#ffffff"));
            tv_isFace.setBackgroundResource(R.drawable.shape_login_complete);
            cv_face.setCardBackgroundColor(Color.parseColor("#f7f7f7"));
        }




   /*     if (statusStr == 0) {
            tv_isJxlValid.setTextColor(Color.parseColor("#359DF5"));
            tv_isJxlValid.setText("未验证");
            tv_isJxlValid.setBackgroundResource(R.drawable.selector_register_get_code);


            tv_creditReportStatus.setText("未验证");
            tv_creditReportStatus.setTextColor(Color.parseColor("#359DF5"));
            tv_creditReportStatus.setBackgroundResource(R.drawable.selector_register_get_code);

            tv_isFace.setText("未验证");
            tv_isFace.setTextColor(Color.parseColor("#359DF5"));
            tv_isFace.setBackgroundResource(R.drawable.selector_register_get_code);
        }
        if (statusStr == 1) {////0 都没有验证  1 手机验证   2征信验证
            tv_isJxlValid.setText("已验证");
            tv_isJxlValid.setBackgroundResource(R.drawable.shape_login_complete);
            tv_isJxlValid.setTextColor(Color.parseColor("#ffffff"));

            tv_creditReportStatus.setText("未验证");
            tv_creditReportStatus.setTextColor(Color.parseColor("#359DF5"));
            tv_creditReportStatus.setBackgroundResource(R.drawable.selector_register_get_code);


            tv_isFace.setText("未验证");
            tv_isFace.setTextColor(Color.parseColor("#359DF5"));
            tv_isFace.setBackgroundResource(R.drawable.selector_register_get_code);
        } else if (statusStr == 2) {
            tv_creditReportStatus.setText("已验证");
            tv_creditReportStatus.setBackgroundResource(R.drawable.shape_login_complete);
            tv_creditReportStatus.setTextColor(Color.parseColor("#ffffff"));

            tv_isJxlValid.setTextColor(Color.parseColor("#359DF5"));
            tv_isJxlValid.setText("未验证");
            tv_isJxlValid.setBackgroundResource(R.drawable.selector_register_get_code);

            tv_isFace.setText("未验证");
            tv_isFace.setTextColor(Color.parseColor("#359DF5"));
            tv_isFace.setBackgroundResource(R.drawable.selector_register_get_code);
        }else  if (statusStr ==3) {
            tv_isFace.setText("已验证");
            tv_isFace.setTextColor(Color.parseColor("#ffffff"));
            tv_isFace.setBackgroundResource(R.drawable.shape_login_complete);

            tv_isJxlValid.setTextColor(Color.parseColor("#359DF5"));
            tv_isJxlValid.setText("未验证");
            tv_isJxlValid.setBackgroundResource(R.drawable.selector_register_get_code);


            tv_creditReportStatus.setText("未验证");
            tv_creditReportStatus.setTextColor(Color.parseColor("#359DF5"));
            tv_creditReportStatus.setBackgroundResource(R.drawable.selector_register_get_code);
        }else  if(statusStr ==4){

        }else  if(statusStr ==5){

        }else  if(statusStr ==6){

        }*/




        RxBus.getDefaultBus().toObserverable()
            .observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (CountAmountActivity.BUS_COUNTAMOUNT_CODE.equals(busIndustrySelect.getBus())
                    || AmountDialogNew.BUS_AMOUNT_CODE.equals(busIndustrySelect.getBus())) {
                    if(busIndustrySelect.getTypeId().equals("isJxlValid")){
                        tv_isJxlValid.setText("已验证");
                        tv_isJxlValid.setBackgroundResource(R.drawable.shape_login_complete);
                        tv_isJxlValid.setTextColor(Color.parseColor("#ffffff"));
                        cv_phone.setCardBackgroundColor(Color.parseColor("#f7f7f7"));
                    }else if(busIndustrySelect.getTypeId().equals("isFace")){
                        tv_isFace.setText("已验证");
                        tv_isFace.setTextColor(Color.parseColor("#ffffff"));
                        tv_isFace.setBackgroundResource(R.drawable.shape_login_complete);
                        cv_face.setCardBackgroundColor(Color.parseColor("#f7f7f7"));
                    }else if(busIndustrySelect.getTypeId().equals("isCredit")){
                        tv_creditReportStatus.setText("已验证");
                        tv_creditReportStatus.setBackgroundResource(R.drawable.shape_login_complete);
                        tv_creditReportStatus.setTextColor(Color.parseColor("#ffffff"));
                        cv_credit.setCardBackgroundColor(Color.parseColor("#f7f7f7"));
                    }

                    Timber.e("手机验证的返回AmountDialog2222：" + busIndustrySelect.getTypeId());
                }
            }
        });

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
            dismiss();
        });

        //征信验证
        view.findViewById(R.id.rl_credit).setOnClickListener(v -> {
            if (creditListener != null) {
                creditListener.credit();
            }
            // dismiss();
        });

        //人脸识别
        view.findViewById(R.id.rl_face).setOnClickListener(v -> {
            if (clickFace != null) {
                clickFace.face();
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

    public void setClickFace(OnClickFace clickFace) {
        this.clickFace = clickFace;
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


    public interface  OnClickFace{
        void face();
    }

    public interface OnClickNext {

        void next();
    }
}