package com.hnjd.query;

import com.hnjd.base.BaseQuery;

public class CustomerServeQuery extends BaseQuery {

    private String customer;//客户名称
    private Integer serveType;//服务类型
    private String state;//服务状态  fw_001创建 2分配 3处理 4反馈 5归档

    private Integer assigner;//分配人

    public Integer getAssigner() {
        return assigner;
    }

    public void setAssigner(Integer assigner) {
        this.assigner = assigner;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Integer getServeType() {
        return serveType;
    }

    public void setServeType(Integer serveType) {
        this.serveType = serveType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
