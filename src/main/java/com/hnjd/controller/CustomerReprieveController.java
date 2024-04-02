package com.hnjd.controller;

import com.hnjd.base.BaseController;
import com.hnjd.base.ResultInfo;
import com.hnjd.query.CustomerReprieveQuery;
import com.hnjd.service.CustomerReprieveService;
import com.hnjd.vo.CustomerReprieve;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_rep")
public class CustomerReprieveController extends BaseController {

    @Resource
    private CustomerReprieveService customerReprieveService;

    /**
     * 分页查询流失客户暂缓操作的列表
     * @param customerReprieveQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerReprieveByParams(CustomerReprieveQuery customerReprieveQuery){
        return customerReprieveService.queryCustomerReprieveByParams(customerReprieveQuery);
    }

    /**
     * 添加暂缓数据
     * @param customerReprieve
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCustomerReprieve(CustomerReprieve customerReprieve){
        customerReprieveService.addCustomerReprieve(customerReprieve);
        return success("暂缓数据添加成功");
    }

    /**
     * 修改暂缓数据
     * @param customerReprieve
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCustomerReprieve(CustomerReprieve customerReprieve){
        customerReprieveService.updateCustomerReprieve(customerReprieve);
        return success("暂缓数据修改成功");
    }

    /**
     * 删除暂缓数据
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteCustomerReprieve(Integer id){
        customerReprieveService.deleteCustomerReprieve(id);
        return success("暂缓数据删除成功");
    }

    /**
     * 打开添加/修改暂缓数据的页面
     * @param lossId
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("addOrUpdateCustomerRepPage")
    public String addOrUpdateCustomerReprPage(Integer lossId,Integer id, HttpServletRequest request){
        request.setAttribute("lossId",lossId);
        //判断id是否为空
        if (id!=null){
            //通过主键id查询暂缓数据
            CustomerReprieve customerRep=customerReprieveService.selectByPrimaryKey(id);
            //设置到作用域中
            request.setAttribute("customerRep",customerRep);
        }
        return "customerLoss/customer_rep_add_update";
    }

}
