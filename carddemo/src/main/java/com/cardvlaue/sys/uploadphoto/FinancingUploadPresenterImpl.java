package com.cardvlaue.sys.uploadphoto;

/**
 * Created by Administrator on 2016/7/14.
 */

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.apply.UpdateUserInfoBO;
import com.cardvlaue.sys.util.CheckUtil;
import java.util.List;
import timber.log.Timber;

/**
 * Created by cardvalue on 2016/6/6.
 */
public class FinancingUploadPresenterImpl implements IFinancingUploadPresenter {

    private Context context;
    private IFinancingUploadView uploadView;
    private IFinancingUploadModel uploadModel;

    public FinancingUploadPresenterImpl(Activity context, IFinancingUploadView uploadView) {
        this.context = context;
        this.uploadView = uploadView;
        uploadModel = new FinancingUploadModelImpl(context, this);
    }

    @Override
    public void toast(String msg) {
        uploadView.toast(msg);
    }

    @Override
    public void updateUserInfo(UpdateUserInfoBO infoBO) {
        if (!CheckUtil.isOnline(context)) {
            uploadView.toast(TipConstant.LOGIN_NOT_NETWORK);
            return;
        }

        if (TextUtils.isEmpty(infoBO.getOwnerAddress()) || TextUtils
            .isEmpty(infoBO.getDirectType())) {
        }

        uploadModel.updateUserInfo(infoBO);
    }

    @Override
    public void newFileLists() {
        if (!CheckUtil.isOnline(context)) {
            uploadView.toast(TipConstant.LOGIN_NOT_NETWORK);
            return;
        }

        uploadModel.newFileLists();
    }


    @Override
    public void newChecklists() {
        if (!CheckUtil.isOnline(context)) {
            uploadView.toast(TipConstant.LOGIN_NOT_NETWORK);
            return;
        }

        uploadModel.newChecklists();
    }

    @Override
    public void newDeletelists(String fileId) {

    }

    @Override
    public void newFileListsSuccess(List<NewFileListsItemBO> results) {
        uploadView.updateFileLists(results);
        Timber.e("newFileListsSuccess:%s", JSON.toJSONString(results));
    }

/*
    @Override
    public void colseDialog() {
        uploadView.colseDialog();
    }*/
}
