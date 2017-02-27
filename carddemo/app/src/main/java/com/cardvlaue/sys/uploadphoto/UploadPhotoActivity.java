package com.cardvlaue.sys.uploadphoto;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.tongdun.android.shell.FMAgent;
import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.CountAmountActivity;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.cardverify.UpdateApplyBO;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.data.source.remote.RequestConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.financeway.FinanceWayActivity;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.registerverify.RegisterOkDialog;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import jp.wasabeef.blurry.Blurry;
import me.shaohui.advancedluban.Luban;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import timber.log.Timber;

/**
 * 上传照片
 */
public class UploadPhotoActivity extends BaseActivity implements IFinancingUploadView {

    public static final String BUS_UPLOADPHOTO_CODE = "BUS_UPLOADPHOTO_CODE";
    /**
     * 拍照
     */
    public static final int TAKE_PICTURE = 1002;
    /**
     * 选择图片
     */
    public static final int CHOOSE_PICTURE = 1003;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x11;
    private static final int PERMISSIONS_REQUEST_CAMERA = 0x12;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0x13;
    private static final int PERMISSIONS_ACCESS_FINE_LOCATION = 0x14;
    private static final String PROG_DIALOG_TAG = "UploadPhotoActivity_tag";
    private static final String UPLOAD_PHOTO_STATUS = "restart_activity";

    private NewFileListsAdapter plateAdapter;
    private IFinancingUploadPresenter uploadPresenter;
    private FillPhotoDialog fillPhotoDialog;
    private RecyclerView mPlateView;
    private Toolbar mToolbarView;
    private TextView mBackView,mTitleTextView;
    private TasksRepository repository;
    private String objectId, token, phone, appId, listId, fn;
    private List<NewFileListsItemBO> list = new ArrayList<>();
    private IFinancingRest rest;//更新申请
    private ContentLoadingDialog mLoadingDialog;
    private UploadPhotoDailog uploadPhotoDailog;
    private double longitude, Latitude;
    private RegisterOkDialog registerOkDialog;
    private boolean mShowDialog = false;
    private String content = "请先进入系统设置->应用->小企额->权限,开启所有权限后,再进行上传操作!";
    private String dialogButton = "我知道了，马上开启权限";
    /**
     * 定位客户端
     */
    private LocationClient mLocationClient;

    /**
     * 默认照片名
     */
    private String imageFileName = "android.jpg";

    /**
     * 拍照后图片在存储卡中的路径
     */
    private File sdImageFile;

    /**
     * 公司联系地址
     */
    @BindView(R.id.tv_upload_photo_address)
    TextView mAddressView;

    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        ButterKnife.bind(this);
        textview=(TextView) findViewById(R.id.tv_copy);
        textview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textview.getPaint().setAntiAlias(true);
        initView();
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(new MyLocationListener());
        uploadPresenter = new FinancingUploadPresenterImpl(this, this);
        mLoadingDialog = ContentLoadingDialog.newInstance("上传中...");
        mLoadingDialog.setCancelable(false);
        //提交的按钮,更新用户，更新申请,判断照片有没有上传
        findViewById(R.id.btn_apply_commit).setOnClickListener(v -> {
            for (NewFileListsItemBO img : list) {
                if (img.getLackFiles() != 0) {
                    if(!img.getTitle().equals("结婚证/离婚证")){
                        ToastUtil.showFailure(this, "请上传" + img.getTitle());
                        return;
                    }
                }
            }
            mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show(getSupportFragmentManager(), "UploadPhotoActivity_tag");
            if (getIntent().getStringExtra("addFile") != null)//补件
            {
                UpdateUserInfo2();
            } else if (getIntent().getStringExtra("confirmation") != null) //上传确认书
            {
                queryApply();
            } else if (getIntent().getStringExtra("again") != null)//重新上传照片
            {
                updateApply(1);
            } else {
                updateApply(0);
            }
        });

        mPlateView.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mPlateView.setNestedScrollingEnabled(false);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        if (getIntent().getStringExtra("addFile") != null) {
            /**
             * 获取补件的图片列表
             */
            findViewById(R.id.view_confirmation).setVisibility(View.GONE);
            findViewById(R.id.ly_confirmation).setVisibility(View.GONE);
            findViewById(R.id.rl_upload_photo_tip).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_upload_photo_tip_text))
                .setText("抱歉!由于您提交申请时,上传资料不合我司规范,以下照片需重新提交");
            ((TextView) findViewById(R.id.tv_upload_photo_hint_text)).setText(
                "小提示：尊敬的用户，为了加快审核进度，请按照标准样张拍摄图片。您可以点击下面资料分类右上角的蓝色小图标查看标准样张，您已上传的图片也可以用相同方式查看。点击图片左上角黄色问号图标，可查看补件原因。");
            ((ImageView) findViewById(R.id.iv_pic)).setImageResource(R.mipmap.ic_add_file);
            mTitleTextView.setText("补全资料");
            uploadPresenter.newChecklists();
            plateAdapter = new NewFileListsAdapter(1);
        } else if (getIntent().getStringExtra("confirmation") != null) {
            queryApplyConfirmation();
            mTitleTextView.setText("上传确认书照片");

            findViewById(R.id.tv_copy).setOnLongClickListener(view -> {
                String code =textview.getText().toString();
                setClipBoard(code);
                return true;
            });

            ((TextView) findViewById(R.id.tv_upload_photo_hint_text))
                .setText(
                    "小提示：尊敬的用户，为了加快审核进度，请按照标准样张拍摄图片。您可以点击下面资料分类右上角的蓝色小图标查看标准样张，您已上传的图片也可以用相同方式查看。");
            findViewById(R.id.view_confirmation).setVisibility(View.VISIBLE);
            findViewById(R.id.ly_confirmation).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_upload_photo_tip).setVisibility(View.GONE);
            uploadPresenter.newFileLists();
            plateAdapter = new NewFileListsAdapter(0);
        } else {
            Timber.e("NewFileListsAdapter00000000000");
            findViewById(R.id.view_confirmation).setVisibility(View.GONE);
            findViewById(R.id.ly_confirmation).setVisibility(View.GONE);
            findViewById(R.id.rl_upload_photo_tip).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_upload_photo_tip_text))
                .setText("最后一步啦！完成此步骤，您的融资申请将会被提交。请务必保证以下照片上传时清晰可见！");
            ((TextView) findViewById(R.id.tv_upload_photo_hint_text))
                .setText(
                    "小提示：尊敬的用户，为了加快审核进度，请按照标准样张拍摄图片。您可以点击下面资料分类右上角的蓝色小图标查看标准样张，您已上传的图片也可以用相同方式查看。");
            mTitleTextView.setText(getString(R.string.upload_photo));
            ((ImageView) findViewById(R.id.iv_pic)).setImageResource(R.mipmap.icon_photo_shop);
            uploadPresenter.newFileLists();
            plateAdapter = new NewFileListsAdapter(0);
        }

        RxBus.getDefaultBus().toObserverable()
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread()).subscribe(o -> {
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (UploadPhotoActivity.BUS_UPLOADPHOTO_CODE.equals(busIndustrySelect.getBus())) {
                    if (busIndustrySelect.getTypeId().equals("cancel")) {
                        Blurry.delete((ViewGroup) findViewById(R.id.rl_upload_photo_container));
                    } else if (busIndustrySelect.getTypeId().equals("delete")) {
                        if (getIntent().getStringExtra("addFile") != null) {//补件
                            uploadPresenter.newChecklists();
                        } else {
                            uploadPresenter.newFileLists();
                        }
                    }
                }
            }
        });
        mPlateView.setAdapter(plateAdapter);
        Timber.i("oncreate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (sdImageFile != null) {
            Timber.i("onSaveInstanceState -- not -- null");
            outState.putString(UPLOAD_PHOTO_STATUS, sdImageFile.getAbsolutePath());
        }
        Timber.i("onSaveInstanceState----outState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Timber.i("onRestoreInstanceState");
        String photoStatus = savedInstanceState.getString(UPLOAD_PHOTO_STATUS);
        if (sdImageFile == null && !TextUtils.isEmpty(photoStatus)) {
            Timber.i("new File(photoStatus);");
            sdImageFile = new File(photoStatus);
        }
    }

    public void initView() {
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mToolbarView.setBackgroundResource(R.color.white);
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mPlateView = (RecyclerView) findViewById(R.id.rv_financing_upload_door_plate);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();
        LoginResponse loginResponse = repository.getLogin();
        token = loginResponse.accessToken;
        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        objectId = userInfoNewResponse.objectId;
        appId = userInfoNewResponse.applicationId;
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            appId = repository.getApplicationId();
        }
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        mBackView.setOnClickListener(v -> {
            try {
                ((CVApplication) getApplicationContext()).getQueue().back(this);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void updateFileLists(final List<NewFileListsItemBO> results) {
        Timber.e("删除图片有没有收到：" + JSON.toJSONString(results));
        list = results;
        plateAdapter.updateData(list);
        //补件
        plateAdapter.setItemReasonClickListener(position -> {
            fillPhotoDialog = FillPhotoDialog
                .newInstance(list.get(position).getTitle(), list.get(position).getRfe());
            if (!fillPhotoDialog.isVisible()) {
                fillPhotoDialog.show(getFragmentManager(), "fillPhotoDialog");
            }
        });
        //显示全部的图片247,247,247
        plateAdapter.setOnItemUploadClickListener(position -> {
            Timber.e("显示全部的图片" + JSON.toJSONString(list));
            if (list.get(position).getFiles().size() > 0) {
                Blurry.with(this).radius(25).sampling(2).color(Color.argb(60, 39, 28, 28))
                    .onto((ViewGroup) findViewById(R.id.rl_upload_photo_container));
                uploadPhotoDailog = UploadPhotoDailog.newInstance(list.get(position).getTitle(),
                    JSON.toJSONString(list.get(position).getFiles()));
                if (!uploadPhotoDailog.isVisible()) {
                    uploadPhotoDailog.show(getFragmentManager(), "UploadPhotoActivity_tag");
                }
            }
        });

        plateAdapter.setOnItemAddClickListener(position -> {
            if (position <= list.size() - 1) {
                NewFileListsItemBO newFileListsItemBO = list.get(position);
                listId = newFileListsItemBO.getChecklistId();
            }
            //版本大于6.0动态授权
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    //申请ACCESS_COARSE_LOCATION权限
                    requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);//
                    return;
                }
                if (ContextCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    //申请ACCESS_FINE_LOCATION权限
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_ACCESS_FINE_LOCATION);//
                    return;
                }
                if (ContextCompat
                    .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    return;
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                    //申请CAMERA权限
                    requestPermissions(new String[]{
                        Manifest.permission.CAMERA
                    }, PERMISSIONS_REQUEST_CAMERA);
                    return;
                }
            }
            LocationManager locManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
                // ToastUtil.showFailure(this, "请打开手机位置服务");
                new Builder(this)
                    .setPositiveButton("确定", (dialog, which) -> {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    })
                    .setMessage("上传照片前，需打开手机位置服务")
                    .show();
                return;
            }
            mLocationClient.start();

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
                        .format(new Date());
                    imageFileName = "CV_" + timeStamp + "_";
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    sdImageFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                    );
                } catch (IOException e) {
                    Timber.e("storageDir=====%s", e.getMessage());
                }

                if (sdImageFile != null) {
                    Timber.i("-sdImageFile-:%s", sdImageFile.getAbsolutePath());
                    Uri photoURI = FileProvider.getUriForFile(this,
                        "com.cardvlaue.sys.android.fileprovider",
                        sdImageFile);
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        List<ResolveInfo> resInfoList = getPackageManager()
                            .queryIntentActivities(takePictureIntent,
                                PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, photoURI,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, TAKE_PICTURE);
                }
            }
        });
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.e("拍照>>>" + requestCode + "||" + resultCode);
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            if (sdImageFile != null) {
                String imgName = sdImageFile.getPath();
                Timber.e("拍照--压缩:::%s", imgName);
                Luban.compress(this, sdImageFile)
                    .setMaxSize(500)
                    .setMaxHeight(1920)
                    .setMaxWidth(1080)
                    .setCompressFormat(CompressFormat.JPEG)
                    .putGear(Luban.CUSTOM_GEAR)
                    .asObservable()
                    .map(file -> {
                        String upFile = file.getAbsolutePath();
                        Timber.i("Luban_file_:::%s", upFile);
                        try {
                            ExifInterface exifInterface = new ExifInterface(upFile);
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
                                Latitude + "");//经度
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
                                longitude + "");//纬度
                            exifInterface.saveAttributes(); // 把Exif信息写入目标图片
                        } catch (IOException e) {
                            Timber.e("EXIF:%s", e.getMessage());
                        }
                        return upFile;
                    })
                    .subscribe(s -> {
                        Timber.e("拍照上传........." + imageFileName + "|||" + s);
                        uploadImg(listId, "android.jpg", s);
                    }, throwable -> {
                        Timber.e("上传照片失败：%s", throwable.getMessage());
                    });
            }
        }
    }



    /**
     * 上传照片
     *
     * @param listId 编号
     * @param fileName 文件名称
     * @param path 照片路径
     */
    public void uploadImg(String listId, String fileName, String path) {
        Timber.i("uploadImg--method");
        mLoadingDialog = ContentLoadingDialog.newInstance("上传中...");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show(getSupportFragmentManager(), "UploadPhotoActivity_uploadImg");
        String requestUrl = String
            .format(RequestConstants.BASE_URL + "merchants/%s/checklists/%s/files/%s",
                objectId, listId, fileName);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), new File(path));

        final Request request = new Request.Builder().url(requestUrl).post(requestBody)
            .addHeader("X-CRM-Application-Id", RequestConstants.APPLICATION_ID)
            .addHeader("X-CRM-Version", RequestConstants.VERSION)
            .addHeader("Content-Type", "image/jpeg")
            .addHeader("X-CRM-Merchant-Id", objectId)
            .addHeader("X-CRM-Access-Token", token)
            .build();

        Timber.i("uploadImg---start");
        Observable.just(new OkHttpClient().newCall(request))
            .map(call -> call.execute().body().string())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                Timber.e("Upload-Image-onResponse>>>:%s", s);
                mLoadingDialog.dismissAllowingStateLoss();

                UploadFileBO uploadFileBO = JSON.parseObject(s, UploadFileBO.class);
                if (uploadFileBO.responseSuccess(this) == 0) {
                    if (!TextUtils.isEmpty(uploadFileBO.getCreatedAt())) {
                        //ToastUtil.showSuccess(this, "上传图片成功");
                        /**
                         * 获取补件的图片列表
                         */
                        if (getIntent().getStringExtra("addFile") != null) {
                            uploadPresenter.newChecklists();
                        } else {
                            uploadPresenter.newFileLists();
                        }
                    } else {
                        ToastUtil.showFailure(this, "服务器返回错误");
                    }
                } else if (uploadFileBO.responseSuccess(this) == -1) {
                    // uploadPresenter.updateDataSuccess(false);
                }
            }, throwable -> {
                Timber.e("uploadImgEEE:%s:", throwable.getMessage());
                mLoadingDialog.dismissAllowingStateLoss();
                ToastUtil.showFailure(this, "解析服务器数据异常");
            });
    }

    /**
     * 更新申请
     */
    public void updateApply(int i) {
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        if (i == 1) {
            updateApplyBO.setIsUploadComplete("1");
        } else {
            updateApplyBO.setIsSubmitApplication("1");
            updateApplyBO.setBlackBox(FMAgent.onEvent(this));
            updateApplyBO.setIpAddress(repository.getIpAddress());
        }
        rest.updateApply(objectId, token, appId, updateApplyBO)
            .subscribeOn(rx.schedulers.Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                mLoadingDialog.dismissAllowingStateLoss();
                Timber.e("更新申请上传照片" + loginBO.getUpdatedAt() + "==getUpdatedAt====" + JSON
                    .toJSONString(loginBO));
                if (!TextUtils.isEmpty(loginBO.getError())) {
                    ToastUtil.showFailure(this, loginBO.getError());
                    if (loginBO.getCode() == 269) {//过期
                        startActivity(new Intent(this, CountAmountActivity.class));
                    } else if (loginBO.getCode() == 272) {//还没有额度
                        startActivity(new Intent(this, FinanceWayActivity.class));
                    } else if (loginBO.getCode() == 400) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("apply", "011111");
                        startActivity(intent);
                        finish();
                    }
                }
                if (!TextUtils.isEmpty(loginBO.getUpdatedAt())) {
                    if (getIntent().getStringExtra("again") != null) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("apply", "011111");
                        startActivity(intent);
                        finish();
                    } else {
                        queryApply();
                    }
                }
            }, throwable -> {
                mLoadingDialog.dismiss();
                Timber.e("更新申请上传照片" + throwable.getMessage() + "==throwable==");
            });
    }

    /**
     * 获取最新的申请
     */
    public void queryApply() {
        rest.queryApply(appId, objectId, token)
            .subscribeOn(rx.schedulers.Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                mLoadingDialog.dismiss();
                Timber.e("获取最新的申请上传照片e" + JSON.toJSONString(queryApplyBO));
                if (TextUtils.isEmpty(queryApplyBO.getError())) {
                    if (getIntent().getStringExtra("confirmation") != null) {
                        if ("1".equals(queryApplyBO.isConfirmation)) {
                            Intent intentConfirmation = new Intent(this, MainActivity.class);
                            intentConfirmation.putExtra("apply", "011111");
                            startActivity(intentConfirmation);
                            finish();
                        } else {
                            ToastUtil.showFailure(this, "请上传所有图片");
                        }
                    } else {
                        UpdateUserInfo();
                    }
                } else {
                    ToastUtil.showFailure(this, queryApplyBO.getError());
                }
            }, throwable -> {
                ToastUtil.showFailure(this, "申请查询异常，请稍后再试");
                Timber.e("申请查询异常，请稍后再试上传照片e" + JSON.toJSONString(throwable));
            });
    }


    /**
     * 更新申请
     */
    public void UpdateUserInfo() {
        if (!TextUtils.isEmpty(repository.getSetStep())
            && Integer.parseInt(repository.getSetStep()) > 50) {
            try {
                ((CVApplication) getApplicationContext()).getQueue().next(this, 50);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        updateApplyBO.setSetStep(50);
        rest.updateApply(objectId, token, appId, updateApplyBO)
            .subscribeOn(rx.schedulers.Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                Timber
                    .e(loginBO.getUpdatedAt() + "==更新申请response====" + JSON.toJSONString(loginBO));
                mLoadingDialog.dismiss();
                if (loginBO.getCode() == 400) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("apply", "011111");
                    startActivity(intent);
                    finish();
                } else if (!TextUtils.isEmpty(loginBO.getError())) {
                    ToastUtil.showFailure(this, loginBO.getError());
                } else {
                    try {
                        ((CVApplication) getApplication()).getQueue().next(this, 50);
                         finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, throwable -> {
                mLoadingDialog.dismiss();
                Timber.e(throwable.getMessage() + "==throwable==");
            });
    }

    /**
     * 更新申请
     */
    public void UpdateUserInfo2() {
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        updateApplyBO.setType("6");
        rest.updateApply(objectId, token, appId, updateApplyBO)
            .subscribeOn(rx.schedulers.Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                mLoadingDialog.dismiss();
                Timber.e(loginBO.getUpdatedAt() + "==更新申请提交补件response====" + JSON
                    .toJSONString(loginBO));
                if (!TextUtils.isEmpty(loginBO.getError())) {
                    ToastUtil.showFailure(this, loginBO.getError());
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("apply", "apply");
                    startActivity(intent);
                    ToastUtil.showSuccess(this, "成功");
                    finish();
                }
            }, throwable -> {
                mLoadingDialog.dismiss();
                Timber.e(throwable.getMessage() + "==throwable==");
            });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        //请求打开摄像头权限
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                        return;
                    }
                } else {
                    if (!shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mShowDialog = true;
                    } else {
                        mShowDialog = true;
                    }
                }
                return;
            }
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        //请求打开PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION权限
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                        return;
                    }
                } else {
                    if (!shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        mShowDialog = true;
                    } else {
                        mShowDialog = true;
                    }
                }
                return;
            }

            case PERMISSIONS_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        //请求打开PERMISSIONS_ACCESS_FINE_LOCATION权限
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_ACCESS_FINE_LOCATION);
                        return;
                    }
                } else {
                    if (!shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                        mShowDialog = true;
                    } else {
                        mShowDialog = true;
                    }
                }
                return;
            }

            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        //请求打开摄像头权限
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                        return;
                    }
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        mShowDialog = true;
                    } else {
                        mShowDialog = true;
                    }
                }
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mShowDialog) {
            mShowDialog = false;
            if (getSupportFragmentManager().findFragmentByTag(PROG_DIALOG_TAG) == null) {
                registerOkDialog = RegisterOkDialog.newInstance(content, dialogButton);
                registerOkDialog.show(getSupportFragmentManager(), "tag");
                registerOkDialog
                    .setOnClickOkListener(
                        () -> startActivity(DeviceUtil.getAppDetailSettingIntent(this)));
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    /**
     * 获取最新的申请
     */
    public void queryApplyConfirmation() {
        rest.queryApply(appId, objectId, token)
            .subscribeOn(rx.schedulers.Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                Timber.e("获取最新的申请上传照片e==============" + JSON.toJSONString(queryApplyBO));
                if (TextUtils.isEmpty(queryApplyBO.getError()) && !TextUtils
                    .isEmpty(queryApplyBO.baoliContractUrl)) {
                    ((TextView) findViewById(R.id.tv_copy)).setText(queryApplyBO.baoliContractUrl);
                    String companyAddress = queryApplyBO.CompanyAddress;
                    SpannableString ss = new SpannableString(companyAddress);
                    ss.setSpan(new ForegroundColorSpan(
                            ContextCompat.getColor(this, R.color.app_main_color)),
                        companyAddress.indexOf("'") + 1, companyAddress.lastIndexOf("'"),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mAddressView.setText(ss);
                } else if (!TextUtils.isEmpty(queryApplyBO.getError())) {
                    ToastUtil.showFailure(this, queryApplyBO.getError());
                }
            }, throwable -> {
                ToastUtil.showFailure(this, "申请查询异常，请稍后再试");
                Timber.e("申请查询异常，请稍后再试上传照片e=============" + JSON.toJSONString(throwable));
            });
    }



    /**
     **  content要分享的字符串
     **/

    public void setClipBoard(String content) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", content);
            clipboard.setPrimaryClip(clip);
            Timber.e("label------------------");
            Toast.makeText(this,"已复制到剪贴板",Toast.LENGTH_LONG).show();
            return;
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(content);
            Timber.e("setText----------------");
        }
    }
    /**
     * 定位
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                longitude = bdLocation.getLongitude();
                Latitude = bdLocation.getLatitude();
                Timber.e("定位up" + longitude + "-----------" + Latitude + "");
            }
        }
    }
}
