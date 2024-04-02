package com.hnjd.controller;

import com.hnjd.base.BaseController;
import com.hnjd.query.OrderDetailsQuery;
import com.hnjd.service.OrderDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("order_details")
public class OrderDetailsController extends BaseController {
    @Resource
    private OrderDetailsService orderDetailsService;

    /**
     * 分页条件查询订单详细列表
     * @param orderDetailsQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryOrderDetailsByParams(OrderDetailsQuery orderDetailsQuery){
        return orderDetailsService.queryOrderDetailsByParams(orderDetailsQuery);

    }
}
