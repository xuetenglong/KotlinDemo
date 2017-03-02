package com.cardvlaue.sys;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cardvlaue.sys.data.SplashDataResponse;
import com.cardvlaue.sys.data.source.DaggerTasksRepositoryComponent;
import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.data.source.TasksRepositoryModule;
import com.cardvlaue.sys.data.source.remote.RequestConstants;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.util.AQueue;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.FlagKey;
import com.cardvlaue.sys.util.FrescoImageAdapter;
import com.cardvlaue.sys.util.PrefUtil;
import com.cardvlaue.sys.util.ReadUtil;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.util.WeexModule;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.tencent.bugly.crashreport.CrashReport;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.socialize.PlatformConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.callback.UpdateDownloadCB;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.model.UpdateParser;
import org.lzh.framework.updatepluginlib.strategy.UpdateStrategy;
import timber.log.Timber;

public class CVApplication extends Application {

    /**
     * 文件夹名称
     */
    public static final String APP_FILE_NAME = "CardValue";

    private TasksRepositoryComponent mRepositoryComponent;
    /**
     * 存放所有的activity
     */
    private static List<Activity> activitys = new ArrayList<>();
    private static CVApplication mApplication;

    private AQueue queue;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRepositoryComponent = DaggerTasksRepositoryComponent.builder()
            .applicationModule(new ApplicationModule((getApplicationContext())))
            .tasksRepositoryModule(new TasksRepositoryModule()).build();
        // 日志工具
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        // Fresco
        ImagePipelineConfig frescoConfig = OkHttpImagePipelineConfigFactory
            .newBuilder(getApplicationContext(), new OkHttpClient())
            .build();
        Fresco.initialize(getApplicationContext(), frescoConfig);
        // ARouter
        ARouter.init(this);
        try {
            InitConfig weexConfig = new InitConfig.Builder().setImgAdapter(new FrescoImageAdapter())
                .build();
            WXSDKEngine.initialize(this, weexConfig);
            WXSDKEngine.registerModule("weexModule", WeexModule.class);
        } catch (WXException e) {
            Timber.e("WXException>>>:%s", e.getMessage());
        }
        imageLoader();

        // 版本更新
        UpdateConfig.getConfig()
            .url(UrlConstants.VERSION_CHECK + "?type=android&platform=" + ReadUtil
                .readKey(this, "TD_CHANNEL_ID"))
            .jsonParser(new UpdateParser() {
                @Override
                public Update parse(String httpResponse) {
                    Timber.e("新版本UpdateConfig:%s", httpResponse);
                    Update update = new Update(httpResponse);
                    try {
                        SplashDataResponse dataResponse = JSON
                            .parseObject(httpResponse, SplashDataResponse.class);
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
                    } catch (Exception e) {
                        Timber.i("新版本Error:%s", e.getMessage());
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
                    ToastUtil.showFailure(getApplicationContext(), "下载异常");
                }
            });

        // TODO Bugly true 表示测试环境，false 表示生产环境
        CrashReport.initCrashReport(getApplicationContext(), "998bd74772", BuildConfig.DEBUG);

        // TODO 极光推送
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);

        // TODO 同盾 FMAgent.ENV_SANDBOX 表示测试环境，FMAgent.ENV_PRODUCTION 表示生产环境
        try {
            if (BuildConfig.DEBUG) {
                FMAgent.init(this, FMAgent.ENV_SANDBOX);
            } else {
                FMAgent.init(this, FMAgent.ENV_PRODUCTION);
            }
        } catch (FMException e) {
            Timber.e("同盾:%s", e.getMessage());
        }

        // TalkingData   行为数据分析
        TCAgent.init(this);

        // 友盟
        PlatformConfig.setQQZone("1104815116", "Jh8AfrIInHHXcvk8");
        PlatformConfig.setSinaWeibo("3162626970", "ed61c86a6893b43afe335859a0729021");
        PlatformConfig.setWeixin("wx6f3bcef9f46c30b6", "df58b1a03a34fe7e0da55ab3ab4c676c");

        mApplication = this;
    }

    public TasksRepositoryComponent getTasksRepositoryComponent() {
        return mRepositoryComponent;
    }

    public static CVApplication getApplication() {
        return mApplication;
    }

    /**
     * 添加activity
     */
    public void addActivity(Activity activity) {
        activitys.add(activity);
    }

    /**
     * 移除所有Activity
     */
    public static void removeAll() {
        for (Activity ac : activitys) {
            ac.finish();
        }
        activitys.clear();
    }

    public AQueue getQueue() {
        return queue;
    }

    private void imageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.empty_photo)
            .showImageOnFail(R.drawable.empty_photo).cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
            getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions).diskCacheSize(50 * 1024 * 1024)
            .diskCacheFileCount(100) // 缓存一百张图片
            .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 返回上一级
     */
    public CVApplication() {
        queue = new AQueue();
        queue.setListener(new AQueue.GoBackListener() {
            @Override
            public void afterGoBack(FlagKey page, Context context) {
                Intent intent = new Intent(context, page.activity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }

            @Override
            public void afterNext(FlagKey page, Context context) {
                Intent intent = new Intent(context, page.activity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 添加用户行为记录
     */
    public void getEditData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
            UrlConstants.BASE_URL + "behavior/addBehaviorRecords", null, jsonObject -> {
            Timber.e("添加用户通讯录行为记录 response=" + jsonObject.toString());
            Map<String, Object> ret = new Gson().fromJson(jsonObject.toString(), Map.class);
            if (ret.get("resultCode").toString().equals("1")) {
                PrefUtil.saveInfo(getApplicationContext(), "finals", new ArrayList<>());
            }
        }, volleyError -> Timber.e("TAG" + volleyError.getMessage(), volleyError)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Penguin-Driver-Type", "Android");
                headers.put("X-Penguin-Driver-Identifier",
                    DeviceUtil.getImei(getApplicationContext()));//imei
                headers.put("X-Penguin-Platform",
                    ReadUtil.readKey(getApplicationContext(), "TD_CHANNEL_ID"));//用英文表示平台（应用宝等用拼音）
                headers.put("X-Penguin-App-Version", RequestConstants.VERSION);
                headers.put("content-type", "application/json;");
                Timber.e("添加用户行为记录 headers=" + new Gson().toJson(headers));
                return headers;
            }

            @Override
            public byte[] getBody() {
                Map<String, Object> map = new HashMap<>();
                String info = new Gson()
                    .toJson(PrefUtil.getInfo(getApplicationContext(), "finals"));
                map.put("behaviorRecords", info);
                Timber.e("添加用户行为记录 body=" + new Gson().toJson(map));
                return new Gson().toJson(map).getBytes();
            }
        };
        jsonRequest.setRetryPolicy(
            new DefaultRetryPolicy(
                120000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        );
        requestQueue.add(jsonRequest);
    }

}
