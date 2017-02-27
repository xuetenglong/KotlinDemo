package com.cardvlaue.sys.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.VerifyCodeResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 图形验证码
 */
public class CodeObtainDialog extends DialogFragment {

    public static final String ARGUMENT_MOBILE_PHONE = "mobilePhone";

    public static final String ARGUMENT_BUS_EVENT = "busEvent";
    /**
     * 验证码图片
     */
    @BindView(R.id.sdv_sms_code_img)
    SimpleDraweeView mImgView;
    /**
     * 验证码输入框
     */
    @BindView(R.id.et_sms_code_input) EditText mInputView;
    private TasksDataSource mTasksRepository;
    private Animator anim2;
    /**
     * 验证码获取中
     */
    private boolean isLoading;

    public static CodeObtainDialog newInstance(@NonNull String mobilePhone, String bus) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_MOBILE_PHONE, mobilePhone);
        args.putString(ARGUMENT_BUS_EVENT, bus);
        CodeObtainDialog dialog = new CodeObtainDialog();
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * 确认
     */
    @OnClick(R.id.btn_sms_code_ok)
    void clickOK() {
        String codeStr = mInputView.getText().toString();
        if (TextUtils.isEmpty(codeStr)) {
            ToastUtil.showFailure(getContext(), "请输入验证码");
        } else {
            loadOrVerifyCode(codeStr);
        }
    }

    /**
     * 刷新图片验证码
     */
    @OnClick(R.id.sdv_sms_code_img)
    void clickImg() {
        loadOrVerifyCode(null);
    }

    /**
     * 关闭界面
     */
    @OnClick(R.id.btn_sms_code_cancel)
    void clickCancel() {
        dismiss();
    }

    /**
     * 获取图片验证码或发送短信验证码
     *
     * @param inputCode null 获取图片验证码 not null 发送短信验证码
     */
    private void loadOrVerifyCode(String inputCode) {
        if (isLoading) {
            ToastUtil.showFailure(getContext(), "获取中...");
            return;
        }

        if (!CheckUtil.isOnline(getContext())) {
            ToastUtil.showFailure(getContext(), "网络未连接");
            return;
        }

        isLoading = true;

        String phoneStr = getArguments().getString(ARGUMENT_MOBILE_PHONE);
        String eventStr = getArguments().getString(ARGUMENT_BUS_EVENT);
        if (TextUtils.isEmpty(phoneStr) || TextUtils.isEmpty(eventStr)) {
            ToastUtil.showFailure(getContext(), "异常，请重新打开此界面");
            return;
        }

        mTasksRepository.sendSmsCode(phoneStr, inputCode,
            new TasksDataSource.LoadResponseNewCallback<VerifyCodeResponse, String>() {
                @Override
                public void onResponseSuccess(VerifyCodeResponse s) {
                    isLoading = false;
                    switch (s.responseSuccess(getContext())) {
                        case -1:
                            ToastUtil.showFailure(getContext(), s.getError());
                            break;
                        case 0:
                            String urlStr = s.imgUrl;
                            String timeStr = s.createdAt;
                            if (!TextUtils.isEmpty(urlStr)) {
                                mImgView.setImageURI(Uri.parse(urlStr));
                            }
                            if (!TextUtils.isEmpty(timeStr)) {
                                RxBus2.Companion.get().send(eventStr);
                                RxBus.getDefaultBus().send(eventStr);
                                ToastUtil.showSuccess(getContext(), "短信验证码已发送");
                                dismiss();
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isLoading = false;
                    if (getActivity() != null) {
                        ToastUtil.showFailure(getActivity(), f);
                    }
                }
            });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTasksRepository = ((CVApplication) getActivity().getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_sms_code_check, container, false);
        ButterKnife.bind(this, view);

        LinearLayout image = (LinearLayout) view.findViewById(R.id.layout);
        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat(
            "scaleX", 0.1f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
            "scaleY", 0.1f, 1f);
        anim2 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_3,
            valuesHolder_4);
        anim2.setDuration(300);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.start();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadOrVerifyCode(null);
    }

}
