package com.hnjd.query;

import com.hnjd.base.BaseQuery;

public class OrderDetailsQuery extends BaseQuery {

    private Integer orderId;//订单id

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
