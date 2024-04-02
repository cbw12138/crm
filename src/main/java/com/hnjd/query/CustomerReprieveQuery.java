package com.hnjd.query;

import com.hnjd.base.BaseQuery;

public class CustomerReprieveQuery extends BaseQuery {

    //流失客户id
    private Integer lossId;

    public Integer getLossId() {
        return lossId;
    }

    public void setLossId(Integer lossId) {
        this.lossId = lossId;
    }
}
