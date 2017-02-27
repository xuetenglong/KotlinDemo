package com.cardvlaue.sys.uploadphoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap.CompressFormat;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.remote.RequestConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
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
import me.shaohui.advancedluban.Luban;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

/**
 * Created by cardvalue on 2017/1/22.
 */
@Deprecated
public class UploadPhotoActivityNew extends BaseActivity {

    public static final String EVENT_UPLOAD_PHOTO_CODE = "UploadPhotoActivity_EVENT_UPLOAD_PHOTO_CODE";
    /**
     * 拍照
     */
    public static final int TAKE_PICTURE = 1002;
    /**
     * 选择图片
     */
    public static final int CHOOSE_PICTURE = 1003;
    private static final String HINT_TEXT = "小提示：尊敬的用户，为了加快审核进度，请按照标准样张拍摄图片。"
        + "您可以点击下面资料分类右上角的蓝色小图标查看标准样张，您已上传的图片也可以用相同方式查看。";

    /**
     * 后退键
     */
    @BindView(R.id.title_default_left)
    TextView mBackView;
    @BindView(R.id.title_default_toolbar)
    Toolbar mToolbarView;
    @BindView(R.id.title_default_middle)
    TextView mTitleView;
    @BindView(R.id.tv_upload_photo_hint_text)
    TextView mHintView;
    /**
     * 照片列表
     */
    @BindView(R.id.rv_financing_upload_door_plate)
    RecyclerView mPlateView;

    private TasksDataSource mTasksRepository;
    private IFinancingRest mFinancingRest;
    /**
     * 定位客户端
     */
    private LocationClient mLocationClient;
    private double longitude, Latitude;
    private String addFileIntentStr, confirmationIntentStr;
    private NewFileListsAdapter plateAdapter;
    private int adapterTag;
    /**
     * 默认照片名
     */
    private String imageFileName = "android.jpg";
    /**
     * 拍照后图片在存储卡中的路径
     */
    private File sdImageFile;
    private String objectIdStr, tokenStr;
    private ContentLoadingDialog mLoadingDialog, mCommitDialog;
    /**
     * 照片列表
     */
    private List<NewFileListsItemBO> photoList = new ArrayList<>();
    /**
     * 分类编号
     */
    private String listId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        ButterKnife.bind(this);
        mTasksRepository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        mFinancingRest = HttpConfig.getClient().create(IFinancingRest.class);
        // 获取登录信息
        LoginResponse loginInfo = mTasksRepository.getLogin();
        objectIdStr = loginInfo.objectId;
        tokenStr = loginInfo.accessToken;
        Timber.i("objectIdStr:%s||tokenStr:%s", objectIdStr, tokenStr);
        // 百度定位
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(new MyLocationListener());
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleView.setTextColor(ContextCompat.getColor(this, R.color.lists_item_text_left));
        mHintView.setText(HINT_TEXT);
        // 获取Intent 数据
        addFileIntentStr = getIntent().getStringExtra("addFile");
        confirmationIntentStr = getIntent().getStringExtra("confirmation");
        if (!TextUtils.isEmpty(addFileIntentStr)) {
            mTitleView.setText("补全资料");
            adapterTag = 1;
        } else if (!TextUtils.isEmpty(confirmationIntentStr)) {
            mTitleView.setText("上传确认书照片");
            loadPhotoLists();
        } else {
            mTitleView.setText("上传店铺照片");
            loadPhotoLists();
        }
        // 列表
        mPlateView.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        plateAdapter = new NewFileListsAdapter(adapterTag);
        mPlateView.setAdapter(plateAdapter);
        plateAdapter.setOnItemAddClickListener(position -> {
            if (position >= photoList.size()) {
                return;
            }
            listId = photoList.get(position).getChecklistId();

            // 定位 GPS
            LocationManager locManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
                ToastUtil.showFailure(this, "未打开位置开关，可能导致定位失败或定位不准");
            }
            mLocationClient.start();

            // 启动照相机
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
                    Timber.e("%s", e.getMessage());
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
        // 上传提示框
        mLoadingDialog = ContentLoadingDialog.newInstance("上传中...");
        mLoadingDialog.setCancelable(false);
        mCommitDialog = ContentLoadingDialog.newInstance("提交中...");
        mCommitDialog.setCancelable(false);
    }

    @OnClick(R.id.title_default_left)
    void clickBack() {
        try {
            ((CVApplication) getApplicationContext()).getQueue().back(this);
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
        finish();
    }

    @OnClick(R.id.btn_apply_commit)
    void clickCommit() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.e("onActivityResult 拍照 >>>" + requestCode + "||" + resultCode + "||" + sdImageFile);
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
                        uploadPhoto(listId, "android.jpg", s);
                    }, throwable -> {
                        Timber.e("上传照片失败：%s", throwable.getMessage());
                    });
            }
        }
    }

    /**
     * 获取照片列表
     */
    private void loadPhotoLists() {
        Timber.i("获取照片列表-loadPhotoLists");
        if (TextUtils.isEmpty(objectIdStr) || TextUtils.isEmpty(tokenStr)) {
            return;
        }
        mFinancingRest.newFileLists(objectIdStr, tokenStr, objectIdStr)
            .subscribeOn(rx.schedulers.Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .filter(newFileListsBO -> {
                Timber.e("获取图片成功:::%s", JSON.toJSONString(newFileListsBO));
                // 请求是否成功
                if (newFileListsBO.responseSuccess(this) == 0) {
                    return true;
                } else if (newFileListsBO.responseSuccess(this) == -1) {
                    Toast.makeText(this, newFileListsBO.getError(), Toast.LENGTH_LONG).show();
                }
                return false;
            })
            .map(NewFileListsBO::getResults)
            .filter(newFileListsItemBOs -> {
                if (newFileListsItemBOs != null) {
                    return true;
                }
                Toast.makeText(this, "服务器异常，查询图片清单无结果", Toast.LENGTH_LONG).show();
                return false;
            })
            .subscribe(newFileListsItemBOs -> {
                // 显示到列表
                photoList.clear();
                photoList.addAll(newFileListsItemBOs);
                plateAdapter.updateData(photoList);
            }, throwable -> Toast.makeText(this, "查询清单异常，请稍后再试", Toast.LENGTH_LONG).show());
    }

    /**
     * 上传照片
     */
    private void uploadPhoto(String listId, String fileName, String path) {
        if (TextUtils.isEmpty(objectIdStr) || TextUtils.isEmpty(tokenStr) || TextUtils
            .isEmpty(listId) || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(path)) {
            return;
        }
        if (!mLoadingDialog.isVisible()) {
            mLoadingDialog.show(getSupportFragmentManager(), "UploadPhotoActivity_uploadPhoto");
        }
        String requestUrl = String
            .format(RequestConstants.BASE_URL + "merchants/%s/checklists/%s/files/%s", objectIdStr,
                listId, fileName);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), new File(path));

        final Request request = new Request.Builder().url(requestUrl).post(requestBody)
            .addHeader("X-CRM-Application-Id", RequestConstants.APPLICATION_ID)
            .addHeader("X-CRM-Version", RequestConstants.VERSION)
            .addHeader("Content-Type", "image/jpeg")
            .addHeader("X-CRM-Merchant-Id", objectIdStr)
            .addHeader("X-CRM-Access-Token", tokenStr)
            .build();

        Observable.just(new OkHttpClient().newCall(request))
            .map(call -> call.execute().body().string())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(s -> {
                Timber.e("Upload-Image-onResponse>>>:%s", s);
                mLoadingDialog.dismissAllowingStateLoss();
                return JSON.parseObject(s, UploadFileBO.class);
            })
            // 是否请求成功
            .filter(s -> s.responseSuccess(this) == 0)
            .filter(uploadFileBO -> {
                if (!TextUtils.isEmpty(uploadFileBO.getCreatedAt())) {
                    return true;
                }
                ToastUtil.showFailure(this, "服务器数据错误");
                return false;
            })
            .subscribe(uploadFileBO -> {
                if (!TextUtils.isEmpty(addFileIntentStr)) {
                    // 待补件图片列表
//                    uploadPresenter.newChecklists();
                } else {
                    loadPhotoLists();
                }
            }, throwable -> {
                Timber.e("uploadImgEEE:%s:", throwable.getMessage());
                mLoadingDialog.dismissAllowingStateLoss();
                ToastUtil.showFailure(this, "解析服务器数据异常");
            });
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
                Timber.e("定位结果:::%s|||%s", longitude, Latitude);
            }
        }
    }
}
