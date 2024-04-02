package com.hnjd.query;

import com.hnjd.base.BaseQuery;


public class CustomerLossQuery extends BaseQuery {

    private String customerNo;
    private String customerName;
    private String state;

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
