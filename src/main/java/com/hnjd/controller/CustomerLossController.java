package com.hnjd.controller;

import com.hnjd.base.BaseController;
import com.hnjd.base.ResultInfo;
import com.hnjd.query.CustomerLossQuery;
import com.hnjd.service.CustomerLossService;
import com.hnjd.vo.CustomerLoss;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("customer_loss")
public class CustomerLossController
        extends BaseController {

    @Resource
    private CustomerLossService customerLossService;

    @RequestMapping("index")
    public String index(){
        return "customerLoss/customer_loss";
    }

    /**
     * 分页查询流失客户列表
     * @param customerLossQuery
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerLossByParams(CustomerLossQuery customerLossQuery){
        return customerLossService.queryCustomerLossByParams(customerLossQuery);
    }

    /**
     * 打开添加暂缓/详情
     * @param id
     */
    @RequestMapping("toCustomerLossPage")
    public String toCustomerLossPage(Integer id, Model model){
        //通过流失客户的id查询对应流失客户的记录
        CustomerLoss customerLoss = customerLossService.selectByPrimaryKey(id);
        //将流失客户对应的数据存到请求域中
        model.addAttribute("customerLoss",customerLoss);
        return "customerLoss/customer_rep";
    }


    /**
     * 更新流失客户的流失状态
     * @param id
     * @param lossReason
     */
    @RequestMapping("updateCustomerLossStateById")
    @ResponseBody
    public ResultInfo updateCustomerLossStateById(Integer id,String lossReason){
        customerLossService.updateCustomerLossStateById(id,lossReason);
        return success("确认流失成功");
    }
}
