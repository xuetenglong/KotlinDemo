package com.cardvlaue.sys.splash;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.confirm.ConfirmRest;
import com.cardvlaue.sys.data.SplashDataResponse;
import com.cardvlaue.sys.data.SplashSetResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.ReadUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;
import rx.schedulers.Schedulers;
import timber.log.Timber;

final class SplashPresenter implements SplashContract.Presenter {

    @Inject
    Context mContext;

    @Inject
    TasksRepository mTasksRepository;

    @Inject
    SplashContract.View mSplashView;

    @Inject
    CompositeDisposable mDisposables;

    /**
     * 定位客户端
     */
    @Inject
    LocationClient mLocationClient;

    private ConfirmRest mConfirmRest;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    SplashPresenter() {
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mSplashView.setPresenter(this);
        mLocationClient.registerLocationListener(new MyLocationListener());
        mConfirmRest = HttpConfig.getClientWeiXin().create(ConfirmRest.class);
    }

    /**
     * 保存 IP
     */
    private void getIpAddress() {
        if (CheckUtil.isOnline(mContext)) {
            mDisposables.add(mTasksRepository.getIpAddress("http://crm.cardvalue.cn/login.php")
                .subscribe(s -> {
                    if (!TextUtils.isEmpty(s)) {
                        String startTag = "<br>";
                        String endTag = "</span><br />";
                        int startIndex = s.indexOf(startTag);
                        int endIndex = s.indexOf(endTag);
                        if (startIndex != -1 && endIndex != -1) {
                            String ipStr = s.substring(startIndex + startTag.length(), endIndex);
                            if (!TextUtils.isEmpty(ipStr)) {
                                Timber.e("IP 地址：%s", ipStr);
                                mTasksRepository.saveIpAddress(ipStr);
                            }
                        }
                    }
                }, throwable -> {
                    Timber.e("getIpAddressERR:%s", throwable.getMessage());
                }));
        }
    }

    /**
     * 检查新版本
     */
    private void checkNewVersion() {
        if (CheckUtil.isOnline(mContext)) {
            Timber.i("checkNewVersion" + ReadUtil.readKey(mContext, "TD_CHANNEL_ID"));
            mDisposables
                .add(mTasksRepository.checkNewVersion(ReadUtil.readKey(mContext, "TD_CHANNEL_ID"))
                    .observeOn(AndroidSchedulers.mainThread())
                    // 是否请求成功
                    .filter(splashResponse -> {
                        Timber.i("checkNewVersion:%s", JSON.toJSONString(splashResponse));
                        if (splashResponse.requestSuccess()) {
                            return true;
                        } else {
                            mSplashView.autoOver();
                            return false;
                        }
                    })
                    // 启动页图片
                    .map(splashResponse -> {
                        SplashDataResponse dataResponse = splashResponse.resultData;
                        SplashSetResponse setResponse = dataResponse.welecomeSet;
                        if (setResponse != null) {
                            String urlStr = setResponse.forwordUrl;
                            if (!TextUtils.isEmpty(urlStr)) {
                                mSplashView.setUrlInfo(setResponse.pageTitle, urlStr);
                            }
                            StringBuilder sb = new StringBuilder();
                            sb.append(UrlConstants.BASE_URL);
                            sb.append("resources/image/welcome/");
                            sb.append(setResponse.picName);
                            sb.append("640-1136.");
                            sb.append(setResponse.suffix);
                            Timber.e("showSplashImage:%s", sb.toString());
                            try {
                                mSplashView.showSplashImage(Uri.parse(sb.toString()));
                            } catch (Exception e) {
                                Timber.e("启动页图片显示错误");
                            }
                        }
                        return dataResponse;
                    })
                    // 新版本
                    .subscribe(dataResponse -> {
                        Timber.i("新版本新版本新版本" + dataResponse.versionCode + "|||" + DeviceUtil
                            .getVersionCode(mContext));
                        if (dataResponse.versionCode > DeviceUtil.getVersionCode(mContext)) {
//                            ToastUtil.showSuccess(mContext, "发现新版本");
                            mSplashView.updateNewVersion(dataResponse);
                            mTasksRepository.saveVersionCheckInfo(JSON.toJSONString(dataResponse));
                        } else {
                            mSplashView.autoOver();
                        }
                    }, throwable -> {
                        Timber.e("更新错误：%s", throwable.getMessage());
                        mSplashView.autoOver();
                    }));
        } else {
            mSplashView.autoOver();
        }
    }

    @Override
    public void startLoc() {
        mLocationClient.start();
    }

    @Override
    public void stopLoc() {
        if (mLocationClient != null) {
            Timber.e("关闭百度定位");
            mLocationClient.stop();
        }
    }

    @Override
    public void subscribe() {
        startLoc();
        getIpAddress();
        checkNewVersion();
    }

    @Override
    public void unsubscribe() {
        stopLoc();
        mDisposables.clear();
    }

    /**
     * 定位
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                String bdLoc = bdLocation.getLongitude() + "," + bdLocation.getLatitude();
                Timber.e("启动地位：%s", bdLoc);
                mTasksRepository.saveGpsAddress(bdLoc);
            }
        }
    }

    /**
     * 激活设备
     */
    @Override
    public void getImei(String id, String channel, String cache) {
        try {
            mConfirmRest
                .getImei(id, channel)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(imei -> {
                    Timber.e(imei + "====激活设备====" + JSON.toJSONString(imei));
                    if ("1".equals(imei.getResultCode())) {
                        mTasksRepository.saveUdid(imei.getResultData());
                        ReadUtil.write(cache);
                    }
                }, throwable -> Timber
                    .e(throwable + "====激活设备throwable====" + throwable.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
