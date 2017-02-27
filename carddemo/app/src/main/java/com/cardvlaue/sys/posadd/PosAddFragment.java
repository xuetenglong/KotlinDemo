package com.cardvlaue.sys.posadd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.PosBus;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.dialog.ContentLoadingDialogs;
import com.cardvlaue.sys.financeway.FinanceWayActivity;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import com.trello.rxlifecycle.components.support.RxFragment;
import java.util.ArrayList;
import timber.log.Timber;

public class PosAddFragment extends RxFragment implements PosAddContract.View {

    private static final String BUS_POS_VERIFY = "PosAddFragment_BUS_POS_VERIFY";
    @BindView(R.id.web_pos_help)
    WebView mHelpView;
    /**
     * 标题文字
     */
    @BindView(R.id.tv_white_back)
    TextView mTextView;
    /**
     * 商编输入框
     */
    @BindView(R.id.et_pos_add_id)
    EditText mIdView;
    private PosAddContract.Presenter mPresenter;
    /**
     * 正在添加POS商编...
     */
    private ContentLoadingDialogs mPosAddingDialog;

    /**
     * 正在获取POS流水
     */
    private ContentLoadingDialogs mPosGetingDialog;

    /**
     * 正在获取验证问题
     */
    private ContentLoadingDialogs mPosQuetionDialog;

    /**
     * 验证中
     */
    private ContentLoadingDialog mVerifyingDialog;

    public static PosAddFragment newInstance() {
        return new PosAddFragment();
    }

    @OnClick(R.id.btn_pos_add_commit)
    void clickCommit() {
        mPresenter.createPos(mIdView.getText().toString());
    }

    @Override
    public void showVerifyDialog(String verifyId, String question, ArrayList<String> data) {
        PosAddVerifyDialog pavd = PosAddVerifyDialog
            .newInstance(BUS_POS_VERIFY, verifyId, question, data, "1");
        if (pavd != null) {
            pavd.show(getFragmentManager(), "showVerifyDialog");
        }
    }

    @Override
    @OnClick(R.id.ibtn_white_back)
    public void closeMe() {
        getActivity().finish();
    }

    @Override
    public void reAddPos() {
        PosAddDialog posAddDialog = new PosAddDialog();
        //继续添加
        posAddDialog.setOnClickOkListener(() -> mIdView.getText().clear());
        //不添加
        posAddDialog.setOnClickCancleListener(() ->{
            startActivity(new Intent(getActivity(), FinanceWayActivity.class)) ;
            getActivity().finish();
        });

        posAddDialog.show(getActivity().getFragmentManager(), "showTip");
    }

    @Override
    public void setPresenter(@NonNull PosAddContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPosAddingDialog = ContentLoadingDialogs.newInstance("正在添加POS商编...");//正在添加POS商编...
        mPosAddingDialog.setCancelable(false);

        mPosGetingDialog = ContentLoadingDialogs.newInstance("正在获取POS流水..");//正在获取POS流水..
        mPosGetingDialog.setCancelable(false);

        mPosQuetionDialog = ContentLoadingDialogs.newInstance("正在获取验POS证问题");//正在获取验证问题...
        mPosQuetionDialog.setCancelable(false);

        mVerifyingDialog = ContentLoadingDialog.newInstance("验证中...");
        mVerifyingDialog.setCancelable(false);

        RxBus.getDefaultBus().toObserverable().compose(bindToLifecycle()).subscribe(o -> {
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (PosAddVerifyDialog.BUS_CLOSE.equals(busIndustrySelect.getBus())) {
                    Timber.e("BUS_CLOSEBUS_CLOSE：=====pos======");
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }

            if (o instanceof PosBus) {
                PosBus posBus = (PosBus) o;
                if (BUS_POS_VERIFY.equals(posBus.event)) {
                    Timber.e("验证商编-创建");
                    mPresenter.verifyMid(posBus.mId);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pos_add, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView.setText("添加商编");
        WebSettings settings = mHelpView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getActivity().getCacheDir().getPath() + "/web");
        mHelpView.loadUrl(UrlConstants.POS_HELP);
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.start();
    }

    /**
     * 正在添加商编
     */
    @Override
    public void showPosAddingDialog() {
        Timber.e("正在添加商编");
        if (!mPosAddingDialog.isVisible()) {
            mPosAddingDialog.show(getFragmentManager(), "showPosAddingDialog");
        }
    }

    /**
     * 关闭添加商编对话框
     */
    @Override
    public void dismissPosAddingDialog() {
        if (mPosAddingDialog != null) {
            mPosAddingDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * 正在获取流水
     */
    @Override
    public void showPosGetingDialog() {
        Timber.e("正在获取流水");
        mPosGetingDialog.show(getFragmentManager(), "showPosGetingDialog");
    }

    /**
     * 关闭获取流水对话框
     */
    @Override
    public void dismissPosGetingDialog() {
        if (mPosGetingDialog != null) {
            mPosGetingDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * 正在获取验证问题
     */
    @Override
    public void showPosQuetionDialogDialog() {
        Timber.e("正在获取验证问题");
        mPosQuetionDialog.show(getFragmentManager(), "showPosQuetionDialogDialog");
    }

    /**
     * 关闭获取验证问题对话框
     */
    @Override
    public void dismissPosQuetionDialogDialog() {
        if (mPosQuetionDialog != null) {
            mPosQuetionDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void showVerifyingDialog() {
        if (!mVerifyingDialog.isResumed()) {
            mVerifyingDialog.show(getFragmentManager(), "showVerifyingDialog");
        }
    }

    @Override
    public void dismissVerifyingDialog() {
        if (mVerifyingDialog != null) {
            mVerifyingDialog.dismissAllowingStateLoss();
        }
    }

}
