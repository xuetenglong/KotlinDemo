package com.cardvlaue.sys.uploadphoto;

/**
 * Created by Administrator on 2016/7/14.
 */

import java.util.List;

/**
 * Created by cardvalue on 2016/6/6.
 */
public interface IFinancingUploadView {

    void toast(String msg);

    void updateFileLists(List<NewFileListsItemBO> results);

    //  void colseDialog();
}

