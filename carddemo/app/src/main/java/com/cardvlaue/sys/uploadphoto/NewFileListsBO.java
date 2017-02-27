package com.cardvlaue.sys.uploadphoto;

import com.cardvlaue.sys.data.ErrorResponse;
import java.util.List;

/**
 * 查询固定类型文件清单(上传资料图片)
 * <p/>
 * Created by cardvalue on 2016/6/13.
 */
public class NewFileListsBO extends ErrorResponse {

    private List<NewFileListsItemBO> results;

    public List<NewFileListsItemBO> getResults() {
        return results;
    }

    public void setResults(List<NewFileListsItemBO> results) {
        this.results = results;
    }
}
