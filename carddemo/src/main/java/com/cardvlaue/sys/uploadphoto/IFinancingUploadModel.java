package com.cardvlaue.sys.uploadphoto;

import com.cardvlaue.sys.apply.UpdateUserInfoBO;

/**
 * Created by cardvalue on 2016/6/6.
 */
public interface IFinancingUploadModel {

    /**
     * 更新用户信息
     */
    void updateUserInfo(UpdateUserInfoBO user);

    /**
     * 查询固定类型文件清单(上传资料图片)
     */
    void newFileLists();

    // void colseDailog();

    /**
     * 获取补件的图片列表
     */
    void newChecklists();


    /**
     * 删除图片
     */
    void newDeletelists(String fileId);

}
