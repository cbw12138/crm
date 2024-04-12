package com.hnjd.controller;

import com.hnjd.base.BaseController;
import com.hnjd.query.CustomerOrderQuery;
import com.hnjd.service.CustomerOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("order")
public class CustomerOrderController extends BaseController {
    @Resource
    private CustomerOrderService customerOrderService;

    /**
     * 分页多条件查询客户订单
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerOrderByParams(CustomerOrderQuery customerOrderQuery){
        return customerOrderService.queryCustomerOrderByParams(customerOrderQuery);
    }


    /**
     * 打开订单详情的页面
     */
    @RequestMapping("orderDetailPage")
    public String orderDetailPage(Integer orderId, Model model){
        //通过订单id查询对应的订单记录
        Map<String,Object> map = customerOrderService.queryOrderById(orderId);
        //将数据设置到请求域中
        model.addAttribute("order",map);
        return "customer/customer_order_detail";
    }
}
