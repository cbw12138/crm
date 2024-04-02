package com.hnjd.controller;

import com.hnjd.base.BaseController;
import com.hnjd.base.ResultInfo;
import com.hnjd.dao.CustomerMapper;
import com.hnjd.query.CustomerQuery;
import com.hnjd.service.CustomerService;
import com.hnjd.vo.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("customer")
public class CustomerController extends BaseController {

    @Resource
    private CustomerService customerService;

    /**
     * 进入客户信息管理界面
     *
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "customer/customer";
    }

    /**
     * 分页查询客户列表
     *
     * @param customerQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCustomerByParams(CustomerQuery customerQuery) {
        return customerService.queryCustomerByParams(customerQuery);
    }

    /**
     * 添加客户信息
     *
     * @param customer
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCustomer(Customer customer) {
        customerService.addCustomer(customer);
        return success("添加客户信息成功");
    }


    /**
     * 修改客户信息
     *
     * @param customer
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCustomer(Customer customer) {
        customerService.updateCustomer(customer);
        return success("修改客户信息成功");
    }

    /**
     * 删除客户信息
     *
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteCustomer(Integer id) {
        customerService.deleteCustomer(id);
        return success("删除客户信息成功");
    }


    /**
     * 进入添加/修改客户信息页面
     *
     * @return
     */
    @RequestMapping("addOrUpdateCustomerPage")
    public String addOrUpdateCustomerPage(Integer id, HttpServletRequest request) {
        //id不为空 则查询客户记录
        if (id != null) {
            //通过id查询客户记录
            Customer customer = customerService.selectByPrimaryKey(id);
            //将客户记录存在作用域中
            request.setAttribute("customer", customer);
        }

        return "customer/add_update";
    }

    /**
     * 打开客户的订单页面
     * @return
     */
    @RequestMapping("toCustomerOrderPage")
    public String toCustomerOrderPage(Integer customerId, Model model){
        //通过客户id查询客户记录，设置到请求域中
        model.addAttribute("customer",customerService.selectByPrimaryKey(customerId));
        return "customer/customer_order";
    }

    /**
     * 客户贡献分析
     * @param customerQuery
     * @return
     */
    @RequestMapping("queryCustomerContributionByParams")
    @ResponseBody
    public Map<String,Object> queryCustomerContributionByParams(CustomerQuery customerQuery){
        return customerService.queryCustomerContributionByParams(customerQuery);
    }

    /**
     * 查询客户构成 折线图
     * @return
     */
    @RequestMapping("countCustomerMake")
    @ResponseBody
    public Map<String,Object> countCustomerMake(){
        return customerService.countCustomerMake();
    }

    /**
     * 查询客户构成 饼状图
     * @return
     */
    @RequestMapping("countCustomerMake02")
    @ResponseBody
    public Map<String,Object> countCustomerMake02(){
        return customerService.countCustomerMake02();
    }
}
