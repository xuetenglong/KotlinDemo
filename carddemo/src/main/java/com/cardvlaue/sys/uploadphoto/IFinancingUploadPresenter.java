package com.cardvlaue.sys.uploadphoto;

/**
 * Created by Administrator on 2016/7/14.
 */


import com.cardvlaue.sys.apply.UpdateUserInfoBO;
import java.util.List;

/**
 * Created by cardvalue on 2016/6/6.
 */
public interface IFinancingUploadPresenter {

    void toast(String msg);

    void updateUserInfo(UpdateUserInfoBO infoBO);

    /**
     * 获取图片列表
     */
    void newFileLists();

    /**
     * 获取补件列表
     */
    void newChecklists();

    /**
     * 删除图片
     */
    void newDeletelists(String fileId);

    void newFileListsSuccess(List<NewFileListsItemBO> results);

    // void colseDialog();
}
