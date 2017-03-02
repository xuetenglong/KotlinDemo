package com.cardvlaue.sys.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.SplashDataResponse;
import com.cardvlaue.sys.data.source.remote.RequestConstants;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.ReadUtil;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.lzh.framework.updatepluginlib.UpdateBuilder;
import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.callback.UpdateDownloadCB;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.model.UpdateParser;
import org.lzh.framework.updatepluginlib.strategy.UpdateStrategy;
import timber.log.Timber;

public class SplashFragment extends Fragment implements SplashContract.View {

    @BindView(R.id.tv_splash_overlap)
    TextView mOverView;

    @BindView(R.id.sdv_splash_image)
    SimpleDraweeView mShowView;

    private SplashContract.Presenter mPresenter;
    private Disposable mDisposable;

    /**
     * 活动链接 / 活动页标题
     */
    private String forwordUrl, pageTitle;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    /**
     * 点击活动链接
     */
    @OnClick(R.id.sdv_splash_image)
    void clickUrl() {
        if (!TextUtils.isEmpty(forwordUrl)) {
            startActivity(new Intent(getContext(), WebShowActivity.class)
                .putExtra(WebShowActivity.EXTRA_COLOR, "1001")
                .putExtra(WebShowActivity.EXTRA_TITLE, pageTitle + "")
                .putExtra(WebShowActivity.EXTRA_URL, forwordUrl));
        }
    }

    @Override
    public void setUrlInfo(String title, String url) {
        pageTitle = title;
        forwordUrl = url;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults,
            new PermissionListener() {
                @Override
                public void onSucceed(int requestCode) {
                    Timber.e("requestCode.onSucceed:%d", requestCode);
                }

                @Override
                public void onFailed(int requestCode) {
                    Timber.e("requestCode.onFailed:%d", requestCode);
                }
            });
    }

    @Override
    public void autoOver() {
        Timber.e("启动页自动跳过");
        mDisposable = Observable.timer(5, TimeUnit.SECONDS).subscribe(aLong -> gotoNext());
    }

    @Override
    public void updateNewVersion(SplashDataResponse dataResponse) {
        Timber.e("发现新版本");
        UpdateConfig.getConfig()
            .url("http://www.google.cn/")
            .jsonParser(new UpdateParser() {
                @Override
                public Update parse(String httpResponse) {
                    Update update = new Update(null);
                    update.setVersionCode(dataResponse.versionCode);
                    update.setVersionName(dataResponse.version);
                    update.setUpdateUrl(dataResponse.url);
                    String contentStr = dataResponse.memo;
                    update.setUpdateContent(TextUtils.isEmpty(contentStr) ? "" : contentStr);
                    if ("1".equals(dataResponse.isForceUpdate)) {
                        update.setForced(true);
                    } else {
                        update.setIgnore(true);
                    }
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
                    ToastUtil.showFailure(getContext(), "下载异常");
                }
            });

        UpdateBuilder.create().check(getActivity());
    }

    @Override
    public void showSplashImage(Uri uri) {
        mOverView.setVisibility(View.VISIBLE);
        mShowView.setImageURI(uri);
    }

    @OnClick(R.id.tv_splash_overlap)
    void clickOverlap() {
        gotoNext();
    }

    /**
     * 启动首页
     */
    private void gotoNext() {
        Timber.e("gotoNext");
        if (getActivity() != null && !getActivity().isFinishing()) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void setPresenter(@NonNull SplashContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * 判断是否要调用Imei
         * 1.sdcard里面没有数据
         * 2.就是sdcard里面的数据和本地读取的数据不一致   WRITE_MEDIA_STORAGE
         */
        if (PackageManager.PERMISSION_GRANTED == ContextCompat
            .checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Timber.e("cv权限");
            // 渠道编号
            String channel = ReadUtil.readKey(getActivity(), "TD_CHANNEL_ID");
            String imei = DeviceUtil.getImei(getActivity());
            String cacheStr = ReadUtil.read();
            String newStr = channel + RequestConstants.VERSION + imei;
            if (TextUtils.isEmpty(cacheStr) || !newStr.equals(cacheStr)) {
                mPresenter.getImei(imei, channel, newStr);
            }
        } else {
            Timber.e("cv无权限");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_splash, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AndPermission.with(this)
            .requestCode(101)
            .permission(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA)
            .rationale((requestCode, rationale) -> {
                new AlertDialog.Builder(getContext())
                    .setTitle("友好提醒")
                    .setMessage("请把权限赐给我吧！")
                    .setPositiveButton("同意", (dialog, which) -> {
                        dialog.cancel();
                        rationale.resume();// 用户同意继续申请。
                    })
                    .setNegativeButton("拒绝", (dialog, which) -> {
                        dialog.cancel();
                        rationale.cancel(); // 用户拒绝申请。
                    }).show();
            })
            .send();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();

        if (mDisposable != null && !mDisposable.isDisposed()) {
            Timber.e("onStop-dispose");
            mDisposable.dispose();
        }
    }

}
