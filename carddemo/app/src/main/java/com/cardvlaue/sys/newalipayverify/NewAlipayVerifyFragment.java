package com.cardvlaue.sys.newalipayverify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.alipayverify.AlipayVerifyActivity;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialogs;
import com.cardvlaue.sys.util.CacheUtil;
import com.cardvlaue.sys.util.ImgUtil;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;
import java.io.File;
import org.lzh.framework.updatepluginlib.UpdateBuilder;
import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.callback.UpdateDownloadCB;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.model.UpdateParser;
import org.lzh.framework.updatepluginlib.strategy.UpdateStrategy;
import timber.log.Timber;

/**
 * Created by cardvalue on 2016/6/27.
 */
public class NewAlipayVerifyFragment extends Fragment implements NewAlipayVerifyContract.View {

    private static final int TIME = 0;//倒计时总时间120000
    private static final long ALL_TIME = 120000;//倒计时总时间120000
    private static final long ALL_TIME1 = 1200000;//倒计时总时间
    private static final long STEP_TIME = 1000;//间隔时间
    private static final String time1 = "加载中.  ";

    //private boolean mShowUrl = false;//是否返回url

    //private boolean onclick= false;//是否点击按钮
    private static final String time2 = "加载中.. ";
    private static final String time3 = "加载中...";
    /**
     * 帮助
     */
    @BindView(R.id.web_pay_help)
    WebView mHelpView;
    private NewAlipayVerifyContract.Presenter mPresenter;
    private Toolbar mToolbarView;
    private TextView mBackView, mTitleRight;
    private TextView mTitleTextView;
    private Button mNewalipayCommit;
    private boolean start = false;//是否启动计时器
    private Handler timeHandler;
    private CountDownTimer mTimer, mTimer1;
    private UserInfoNewResponse userInfoNewResponse;
    private TasksRepository mTasksRepository;
    /**
     * 加载框
     */
    private ContentLoadingDialogs mLoadingDialog, mLoadingDialogStar;
    private Context mContext;

    public static NewAlipayVerifyFragment newInstance() {
        return new NewAlipayVerifyFragment();
    }

    /**
     * 打开支付宝扫一扫
     */
    private void toAliPayScan() {
        try {
            //利用Intent打开支付宝
            //支付宝跳过开启动画打开扫码和付款码的url scheme分别是alipayqr://platformapi/startapp?saId=10000007和
            //alipayqr://platformapi/startapp?saId=20000056
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            try {
                //若无法正常跳转，在此进行错误处理
                UpdateConfig.getConfig()
                    .url("http://www.google.cn/")
                    .jsonParser(new UpdateParser() {
                        @Override
                        public Update parse(String httpResponse) {
                            Update update = new Update(null);
                            update.setVersionCode(999);
                            update.setVersionName("");
                            update.setUpdateUrl(
                                "http://www.wandoujia.com/apps/com.eg.android.AlipayGphone/download");
                            update.setUpdateContent("您尚未安装支付宝APP, 请安装后重试！");
                            update.setForced(true);
                            return update;
                        }
                    })
                    .strategy(new UpdateStrategy() {
                        @Override
                        public boolean isShowUpdateDialog(Update update) {
                            return true;
                        }

                        @Override
                        public boolean isAutoInstall() {
                            return true;
                        }

                        @Override
                        public boolean isShowDownloadDialog() {
                            return true;
                        }
                    })
                    .updateChecker(update -> true)
                    .updateDialogCreator(new AlipayDownloadDialog())
                    .downloadCB(new UpdateDownloadCB() {
                        @Override
                        public void onUpdateStart() {
                        }

                        @Override
                        public void onUpdateComplete(File file) {
                        }

                        @Override
                        public void onUpdateProgress(long current, long total) {
                        }

                        @Override
                        public void onUpdateError(int code, String errorMsg) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://mobile.alipay.com/index.htm"));
                            startActivity(intent);
                        }
                    });
                UpdateBuilder.create().check(getActivity());
            } catch (Exception e1) {
                Timber.e("%s", e1.getMessage());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_new_alipay_verify, container, false);
        ButterKnife.bind(this, root);
        mContext = getContext();
        mLoadingDialog = ContentLoadingDialogs.newInstance("加载信息中...");
        mLoadingDialog.setCancelable(false);
        mLoadingDialogStar = ContentLoadingDialogs.newInstance("验证中...");
        mLoadingDialogStar.setCancelable(false);
        mToolbarView = (Toolbar) root.findViewById(R.id.title_default_toolbar);
        mToolbarView.setBackgroundResource(R.color.white);
        mBackView = (TextView) root.findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) root.findViewById(R.id.title_default_middle);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText("验证支付宝");
        mTitleRight = (TextView) root.findViewById(R.id.title_default_right);
        mTitleRight.setTextColor(ContextCompat.getColor(getContext(), R.color.app_main_color));
        mTitleRight.setText("账号密码登录");
        mTitleRight.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), AlipayVerifyActivity.class));
            getActivity().finish();
        });

        mTasksRepository = ((CVApplication) getActivity().getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
        userInfoNewResponse = mTasksRepository.getUserInfo();
        root.findViewById(R.id.ly_agreement).setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(WebShowActivity.EXTRA_TITLE, "支付宝授权");
            intent.putExtra(WebShowActivity.EXTRA_URL, UrlConstants.ALIPAY_PAYAGREEMENT);
            intent.setClass(getActivity(), WebShowActivity.class);
            startActivity(intent);
        });

        mBackView.setOnClickListener(view -> {
            timeHandler.removeMessages(10003);
            start = false;
            mTimer1.onFinish();
            mTimer1.cancel();
            //startActivity(new Intent(mContext, FinanceWayActivity.class));
            getActivity().finish();
        });

        mNewalipayCommit = (Button) root.findViewById(R.id.btn_newalipay_commit);
        getUrlsuccess();
        mPresenter.getalipayStatus();

        mNewalipayCommit.setOnClickListener(view -> {
            String json = CacheUtil.getAlipayUrlFlag(mContext, userInfoNewResponse.applicationId);
            AlipayBean alipay = JSON.parseObject(json, AlipayBean.class);
            if (!TextUtils.isEmpty(alipay.url)) {
                /*
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(alipay.url));
                startActivity(intent);
                */

                if (!ImgUtil.isQrImageExists()) {
                    ImgUtil.saveQrImage(getActivity(), alipay.url);
                }
                toAliPayScan();

                alipay.onclick = true;
                String json1 = JSON.toJSONString(alipay);
                CacheUtil.putAlipayUrlFlag(mContext, userInfoNewResponse.applicationId, json1);

                mPresenter.getalipayStatus();
            } else {
                mPresenter.clickCommit();
                mNewalipayCommit.setEnabled(false);
                mTimer1.start();
                alipay.onclick = true;
                String json1 = JSON.toJSONString(alipay);
                CacheUtil.putAlipayUrlFlag(mContext, userInfoNewResponse.applicationId, json1);
            }
        });
        return root;
    }

    @Override
    public void setPresenter(NewAlipayVerifyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebSettings settings = mHelpView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getActivity().getCacheDir().getPath() + "/web");
        mHelpView.loadUrl(UrlConstants.PAY_HELP);
    }

    @Override
    public void closeDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isAdded()) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void showLoadingDialog() {
       /* if (!mLoadingDialog.isAdded())
         mLoadingDialog.show(getFragmentManager(), "showLoadingDialog");*/
    }

    /**
     * 成功获取url  AlipayBean
     *
     * @param urls 已获取到二维码链接
     */
    @Override
    public void flag(String urls) {
        String json = CacheUtil.getAlipayUrlFlag(mContext, userInfoNewResponse.applicationId);
        AlipayBean alipay = JSON.parseObject(json, AlipayBean.class);
        alipay.url = urls;
        String json1 = JSON.toJSONString(alipay);
        CacheUtil.putAlipayUrlFlag(mContext, userInfoNewResponse.applicationId, json1);

        Timber.e("获取到的链接:%s", urls);
        ImgUtil.saveQrImage(getActivity(), urls);

        /*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String json22 = CacheUtil.getAlipayUrlFlag(mContext, userInfoNewResponse.applicationId);
        */

        mTimer1.cancel();
        mNewalipayCommit.setEnabled(true);
        mNewalipayCommit.setText("立即验证");
    }

    @Override
    public void Status(String status) {
        String json = CacheUtil.getAlipayUrlFlag(mContext, userInfoNewResponse.applicationId);
        AlipayBean alipay = JSON.parseObject(json, AlipayBean.class);
        Timber.e("============Status===========" + json + "||" + status);
        if (alipay.onclick) {
            if ("1".equals(status)) {
                ToastUtil.showSuccess(mContext, "验证成功");
                timeHandler.sendEmptyMessageDelayed(113, 2500);
            } else if ("2".equals(status)) {
                if (!start) {
                    mTimer1.cancel();
                    mTimer.start();
                    start = true;
                }
                timeHandler.sendEmptyMessageDelayed(10003, 3000);
            } else if ("3".equals(status)) {   //获取url中
                mNewalipayCommit.setEnabled(false);
                mTimer1.start();
            } else {//状态等于0，就获取url,并且按钮不可以点击
                mPresenter.clickCommit();
                mNewalipayCommit.setEnabled(false);
                mTimer1.start();
                mTimer.cancel();
                timeHandler.removeMessages(10003);
                if (mLoadingDialogStar != null && mLoadingDialogStar.isAdded()) {
                    mLoadingDialogStar.dismiss();
                }
                start = false;
            }
        } else {
            if ("1".equals(status)) {
                ToastUtil.showSuccess(mContext, "验证成功");
                timeHandler.sendEmptyMessageDelayed(113, 2500);
            } else if ("2".equals(status)) {

            } else if ("3".equals(status)) {   //获取url中
                mNewalipayCommit.setEnabled(false);
                mTimer1.start();
            } else {//状态等于0，就获取url,并且按钮不可以点击
                mPresenter.clickCommit();
                mNewalipayCommit.setEnabled(false);
                mTimer1.start();
                mTimer.cancel();
                timeHandler.removeMessages(10003);
                if (mLoadingDialogStar != null && mLoadingDialogStar.isAdded()) {
                    mLoadingDialogStar.dismiss();
                }
                start = false;
            }
        }
    }

    /**
     * 获取url 失败
     */
    @Override
    public void failure() {
        mTimer1.cancel();
        mNewalipayCommit.setEnabled(true);
        mNewalipayCommit.setText("立即验证");
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    public void getUrlsuccess() {
        timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 10003) {
                    mPresenter.getalipayStatus();
                } else if (msg.what == 113) {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        };

        mTimer = new CountDownTimer(ALL_TIME, STEP_TIME) {

            @Override
            public void onTick(long millisUntilFinished) {
                mNewalipayCommit.setEnabled(false);
                mNewalipayCommit.setText("支付宝验证中(" + millisUntilFinished / 1000 + "秒)");
                if (millisUntilFinished / 1000 == 1) {
                    if (null != mContext) {
                        String json = CacheUtil
                            .getAlipayUrlFlag(mContext, userInfoNewResponse.applicationId);
                        AlipayBean alipay = JSON.parseObject(json, AlipayBean.class);
                        alipay.onclick = false;
                        String json1 = JSON.toJSONString(alipay);
                        CacheUtil
                            .putAlipayUrlFlag(mContext, userInfoNewResponse.applicationId, json1);
                        Timber.e(mContext + "====getActivity========" + getActivity());
                    }
                }
            }

            @Override
            public void onFinish() {
                timeHandler.removeMessages(10003);
                if (mLoadingDialogStar != null && mLoadingDialogStar.isAdded()) {
                    mLoadingDialogStar.dismiss();
                }
                start = false;
                if (null != mContext) {
                    String json = CacheUtil
                        .getAlipayUrlFlag(mContext, userInfoNewResponse.applicationId);
                    AlipayBean alipay = JSON.parseObject(json, AlipayBean.class);
                    alipay.onclick = false;
                    String json1 = JSON.toJSONString(alipay);
                    CacheUtil.putAlipayUrlFlag(mContext, userInfoNewResponse.applicationId, json1);
                    Timber.e(mContext + "====getActivity========" + getActivity());
                }

                mNewalipayCommit.setEnabled(true);
                mNewalipayCommit.setText("重新验证");
            }
        };

        mTimer1 = new CountDownTimer(ALL_TIME1, STEP_TIME) {

            @Override
            public void onTick(long millisUntilFinished) {
                mNewalipayCommit.setEnabled(false);
                long longtime = millisUntilFinished / 1000;
                if (longtime % 3 == 0) {
                    mNewalipayCommit.setText(time2);
                } else if (longtime % 3 == 1) {
                    mNewalipayCommit.setText(time1);
                } else if (longtime % 3 == 2) {
                    mNewalipayCommit.setText(time3);
                }
            }

            @Override
            public void onFinish() {
                timeHandler.removeMessages(10003);
                if (mLoadingDialogStar != null && mLoadingDialogStar.isAdded()) {
                    mLoadingDialogStar.dismiss();
                }
                start = false;
                mNewalipayCommit.setEnabled(true);
                mNewalipayCommit.setText("重新验证");
            }
        };
    }
}
