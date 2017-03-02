package com.cardvlaue.sys.face;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat.MessagingStyle.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.AmountCountdownActivity;
import com.cardvlaue.sys.amount.CountAmountActivity;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.cardverify.CardPrivateActivity;
import com.cardvlaue.sys.cardverify.CardPublicActivity;
import com.cardvlaue.sys.cardverify.UpdateApplyBO;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.registerverify.RegisterOkDialog;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.uploadphoto.IImgUploadRest;
import com.cardvlaue.sys.util.CacheUtil;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import com.oliveapp.liveness.sample.liveness.SampleLivenessActivity;
import com.oliveapp.liveness.sample.util.UrlUtils;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 人脸识别 <p> Intenti=newIntent(MainActivity.this,SampleLivenessActivity.class); <br>
 * i.putExtra("methodName",UrlUtils.METHODNAME_VALIDATE_FACE);//方法名，固定为：UrlUtils.METHODNAME_VALIDATE_FACE
 * <br> i.putExtra("isDebug",true);//true：测试环境；false：生产环境 <br> i.putExtra("idCardCode",idCardCode);//正确格式的身份证号
 * <br> i.putExtra("idCardName",idCardName);//正确格式的身份证姓名 <br> i.putExtra("customerId",customerId);//客户号，由考拉征信分配
 * <br> i.putExtra("customerKey",customerKey);//客户key，由考拉征信分配
 */
public class FaceActivity extends BaseActivity {

    private static final String PROG_DIALOG_TAG = "FaceActivity_PROG_DIALOG_TAG";
    private static final int TAG_VERIFY_CODE = 1100;
    private static final String PERMISSION_TAG = "PERMISSION_TAG";
    private static final String content = "请先进入系统设置->应用->小企额->权限,开启所有权限后,再进行操作!";
    private static final String dialogButton = "我知道了，马上开启权限";

    @BindView(R.id.tv_white_back)
    TextView mTitleTextView;

    private TasksRepository repository;
    private boolean mShowDialog = false;
    private String idCardCode;
    private String idCardName;
    private boolean mPermission = false;

    private Handler timeHandler;

    /**
     * 更新申请
     */
    private IFinancingRest rest;
    private IImgUploadRest uploadRest;
    private String applicationId, objectId, token;
    private ContentLoadingDialog mLoadingDialog;
    private String dialogContent = "--";

    /**
     * 点击提交
     */
    @OnClick(R.id.btn_face_commit)
    void clickCommit() {
        List<String> permissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new
                String[permissionsNeeded.size()]), 1);
        } else {
            startKala();
        }
    }

    /**
     * 启动卡拉识别
     */
    private void startKala() {
        if (TextUtils.isEmpty(idCardCode) && TextUtils.isEmpty(idCardName)) {
            showFailureMsg("信息不完整，需先完善个人信息");
            return;
        }

        int errorCount = 0;
        String json = CacheUtil.getFaceFlag(this, objectId);
        if (!TextUtils.isEmpty(json)) {
            FaceVerifyBean fvb = JSON.parseObject(json, FaceVerifyBean.class);
            errorCount = fvb.count;
        }
        Timber.e("startKala face count:%s", errorCount);
        if (errorCount > 2) {
            faceOk(null, false);
            Timber.e("错误-直接过");
            return;
        }

        Intent i = new Intent(this, SampleLivenessActivity.class);
        i.putExtra("isDebug", false);
        i.putExtra("methodName", UrlUtils.METHODNAME_VALIDATE_FACE);
        i.putExtra("idCardCode", idCardCode);//idCardCode
        i.putExtra("idCardName", idCardName);//idCardName
        i.putExtra("customerId", "201602010000000002");
        i.putExtra("customerKey", "K1D6W1L5");
        startActivityForResult(i, TAG_VERIFY_CODE);
    }

    /**
     * 点击后退
     */
    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        ButterKnife.bind(this);
        mTitleTextView.setText("刷脸识别");
        uploadRest = HttpConfig.getRequestStringClient().create(IImgUploadRest.class);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        idCardCode = userInfoNewResponse.ownerSSN;
        idCardName = userInfoNewResponse.ownerName;
        objectId = userInfoNewResponse.objectId;
        applicationId = userInfoNewResponse.applicationId;
        LoginResponse loginResponse = repository.getLogin();
        token = loginResponse.accessToken;

        if (TextUtils.isEmpty(objectId)) {
            objectId = loginResponse.objectId;
        }
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            applicationId = repository.getApplicationId();
        }
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }


        timeHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                if (msg.what == 111113) {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismissAllowingStateLoss();
                    }
                    BusIndustrySelect select = new BusIndustrySelect(
                        CountAmountActivity.BUS_COUNTAMOUNT_CODE);
                    select.setTypeId("isFace");
                    RxBus.getDefaultBus().send(select);
                    finish();
                }
            }
        };
    }





    private void showFailureMsg(String msg) {
        ToastUtil.showFailure(this, msg);
        mShowDialog = true;
    }

    private void putStatus(boolean status, int current) {
        FaceVerifyBean fvb = new FaceVerifyBean(status, current + 1);
        String json = JSON.toJSONString(fvb);
        CacheUtil.putFaceFlag(this, objectId, json);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == TAG_VERIFY_CODE) {
            String result = data.getStringExtra("RESULT");
            int errorCount = 0;
            String json = CacheUtil.getFaceFlag(this, objectId);
            if (!TextUtils.isEmpty(json)) {
                FaceVerifyBean fvb = JSON.parseObject(json, FaceVerifyBean.class);
                errorCount = fvb.count;
                Timber.e("FaceVerifyBean:%s", JSON.toJSONString(fvb));
            }
            Timber.e("Current face count:%s", errorCount);
            if (errorCount >= 2) {
                putStatus(false, errorCount);
                faceOk(null, false);
                Timber.e("三次失败");
                return;
            }
            try {
                FaceBean face = JSON.parseObject(result, FaceBean.class);
                Timber.e("FaceResult:%s", JSON.toJSONString(face));
                if (!face.success()) {
                    putStatus(false, errorCount);
                    ToastUtil.showFailure(this, face.retMsg);
                } else {
                    FaceBean.RetData retData = face.retData;
                    if (retData.unknown()) {
                        putStatus(false, errorCount);
                        String retMsg = retData.message;
                        if (!TextUtils.isEmpty(retMsg)) {
                            mShowDialog = true;
                            dialogContent = retMsg;
                        } else {
                            mShowDialog = true;
                            dialogContent = "匹配失败";
                        }
                    } else if (!retData.idSuccess()) {
                        putStatus(false, errorCount);
                        mShowDialog = true;
                        dialogContent = "姓名和身份证不匹配";
                    } else if (!retData.photoSuccess()) {
                        putStatus(false, errorCount);
                        mShowDialog = true;
                        dialogContent = "刷脸识别不匹配";
                    } else {
                        putStatus(true, errorCount);
                        faceOk(retData, true);
                        Timber.e("OKOKGOGO");
                    }
                }
            } catch (Exception e) {
                putStatus(false, errorCount);
                mShowDialog = true;
                dialogContent = "刷脸识别验证失败";
                Timber.e("FaceBeanEEE:%s", e.getMessage());
            }
        } else {
            ToastUtil.showFailure(this, "未收到刷脸识别结果");
        }
    }

    /**
     * 验证完成
     *
     * @param retData 识别后的数据
     */
    private void faceOk(FaceBean.RetData retData, boolean verifyStatus) {
        if (retData != null) {
            // 验证通过 //userPhotoBase64 用户照片   idCardPhotoBase64 身份证网格照片
            String idCard = retData.idCardPhotoBase64;
            String user = retData.userPhotoBase64;
            Timber.e("身份证网格照片>%s", idCard);
            Timber.e("用户照片>%s", user);
            if (!TextUtils.isEmpty(idCard)) {
                uploadImg("96", "身份证网格照片5.png", idCard);
            }
            if (!TextUtils.isEmpty(user)) {
                uploadImg("96", "用户照片5.png", user);
            }
        }
        updateApply(verifyStatus);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mShowDialog) {
            mShowDialog = false;
            if (getSupportFragmentManager().findFragmentByTag(PROG_DIALOG_TAG) == null) {
                int errorCount = 0;
                String json = CacheUtil.getFaceFlag(this, objectId);
                if (!TextUtils.isEmpty(json)) {
                    FaceVerifyBean fvb = JSON.parseObject(json, FaceVerifyBean.class);
                    errorCount = fvb.count;
                }
                Timber.e("onResumeFragments Count:%s", errorCount);
                if (errorCount > 2) {
                    return;
                }
                FaceDialog faceDialog = FaceDialog.newInstance("人脸识别", dialogContent);
                faceDialog.show(getSupportFragmentManager(), PROG_DIALOG_TAG);
                faceDialog.setOnClickOkListener(() -> clickCommit());
            } else if (mPermission) {
                mPermission = false;
                if (getSupportFragmentManager().findFragmentByTag(PERMISSION_TAG) == null) {
                    RegisterOkDialog registerOkDialog = RegisterOkDialog
                        .newInstance(content, dialogButton);
                    registerOkDialog.show(getSupportFragmentManager(), PERMISSION_TAG);
                    registerOkDialog
                        .setOnClickOkListener(() -> startActivity(getAppDetailSettingIntent()));
                }
            }
        }
    }

    /**
     * 获取最新的申请
     */
    public void queryApply() {
        rest.queryApply(applicationId, objectId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                Timber.e("Face_queryApply" + JSON.toJSONString(queryApplyBO));
                if (!TextUtils.isEmpty(queryApplyBO.getError())) {
                    ToastUtil.showFailure(this, queryApplyBO.getError());
                } else {
                    if ("1".equals(queryApplyBO.isWithdrawConfirm)) {
                        if (queryApplyBO.secondaryBankAccountType.equals("对私")) {
                            //对私
                            Intent intentCardPrivate = new Intent(this, CardPrivateActivity.class);
                            startActivity(intentCardPrivate);
                            finish();
                        } else {
                            //对公
                            Intent intentCardPublic = new Intent(this, CardPublicActivity.class);
                            startActivity(intentCardPublic);
                            finish();
                        }
                    }
                }
            }, throwable -> Timber.e("人脸识别错误信息：%s" + throwable.getMessage()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                boolean allowed = true;
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            Timber.e("PERMISSION_GRANTED");
                        } else {
                            allowed = false;
                            mPermission = true;
                            break;
                        }
                    }
                }
                if (!allowed) {
                    mPermission = true;
                    Snackbar.make(mTitleTextView, "您未授予权限", Snackbar.LENGTH_LONG).show();
                } else {
                    startKala();
                }
                break;
            }
        }
    }

    /**
     * 获取应用详情页面intent
     */
    protected Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent
                .setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        return localIntent;
    }

    /**
     * 更新申请
     */
    public void updateApply(boolean verifyStatus) {
        mLoadingDialog = ContentLoadingDialog.newInstance("加载中...");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show(getSupportFragmentManager(), "updateApply_tag");
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        if (!verifyStatus) {
            updateApplyBO.setIsKalaRecognize("2");
        } else {
            updateApplyBO.setIsKalaRecognize("1");//
        }
        rest.updateApply(objectId, token, applicationId, updateApplyBO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {

                    Timber.e(loginBO.getUpdatedAt() + "==更新申请IsWithdrawConfirm====" + JSON
                        .toJSONString(loginBO));
                    mLoadingDialog.dismissAllowingStateLoss();
                    if (!TextUtils.isEmpty(loginBO.getError())) {
                        ToastUtil.showFailure(this, loginBO.getError());
                    } else {
                        putStatus(false, -1);
                        if(!verifyStatus){
                            BusIndustrySelect select = new BusIndustrySelect(
                                CountAmountActivity.BUS_COUNTAMOUNT_CODE);
                            select.setTypeId("isFace");
                            RxBus.getDefaultBus().send(select);
                            finish();
                        }else{
                            ToastUtil.showSuccess(FaceActivity.this, "验证成功");
                            timeHandler.sendEmptyMessageDelayed(111113, 2500);
                        }


                        //  queryApply();
                    }
                }, throwable -> {
                    mLoadingDialog.dismissAllowingStateLoss();
                    Timber.e(throwable.getMessage() + "==throwable==" + JSON.toJSONString(throwable));
                }
            );
    }

    /**
     * 上传照片
     */
    public void uploadImg(String listId, String fileName, String body) {
        uploadRest.updateUserInfo(objectId, token, objectId, listId, fileName, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(uploadFileBO -> {
                mLoadingDialog.dismissAllowingStateLoss();
                if (uploadFileBO.responseSuccess(this) == 0) {
                    if (!TextUtils.isEmpty(uploadFileBO.getCreatedAt())) {
                        Timber.e("uploadImgface上传图片返回成功:%s:", JSON.toJSONString(uploadFileBO));
                    } else {
                        ToastUtil.showFailure(this, "服务器返回错误");
                    }
                } else if (uploadFileBO.responseSuccess(this) == -1) {
                    ToastUtil.showFailure(this, uploadFileBO.getError());
                }
            }, throwable -> {
                if (mLoadingDialog.isVisible()) {
                    mLoadingDialog.dismiss();
                }
                Timber.e("uploadImgfaceEEE:%s:", throwable.getMessage());
                ToastUtil.showFailure(this, "上传图片出现异常");
            });
    }
}
