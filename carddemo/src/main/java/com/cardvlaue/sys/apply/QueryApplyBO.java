package com.cardvlaue.sys.apply;

/**
 * Created by Administrator on 2016/7/14.
 */


import com.cardvlaue.sys.data.ErrorResponse;
import java.util.List;

/**
 * 查询申请<br/> Created by cardvalue on 2016/5/11.
 */
public class QueryApplyBO extends ErrorResponse {

    private List<QueryApplyItemBO> results;

    public List<QueryApplyItemBO> getResults() {
        return results;
    }

    public void setResults(List<QueryApplyItemBO> results) {
        this.results = results;
    }
}
