package com.hnjd.controller;

import com.hnjd.base.BaseController;
import com.hnjd.base.ResultInfo;
import com.hnjd.query.CustomerServeQuery;
import com.hnjd.service.CustomerServeService;
import com.hnjd.utils.LoginUserUtil;
import com.hnjd.vo.CustomerServe;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_serve")
public class CustomerServeController extends BaseController {
    @Resource
    private CustomerServeService customerServeService;


    /**
     * 多条件分页查询服务数据列表
     *
     * @param customerServeQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCustomerServeByParams(CustomerServeQuery customerServeQuery, Integer flag, HttpServletRequest request) {
        //判断是否执行服务处理，是则查询分配给当前登录用户的服务记录
        if (flag!=null&&flag==1){
            //设置查询条件 分配人
            customerServeQuery.setAssigner(LoginUserUtil.releaseUserIdFromCookie(request));
        }
        return customerServeService.queryCustomerServeByParams(customerServeQuery);

    }

    /**
     * 通过不同的类型进入不同的服务页面
     * @param type
     * @return
     */
    @RequestMapping("index/{type}")
    public String index(@PathVariable Integer type) {
        if (type != null) {
            if (type == 1) {//服务创建
                return "customerServe/customer_serve";
            } else if (type == 2) {//分配
                return "customerServe/customer_serve_assign";
            } else if (type == 3) {//处理
                return "customerServe/customer_serve_proce";
            } else if (type == 4) {//反馈
                return "customerServe/customer_serve_feed_back";
            } else if (type == 5) {//归档
                return "customerServe/customer_serve_archive";
            }
        }
            return "";

    }

    /**
     * 打开创建服务页面
     * @return
     */
    @RequestMapping("addCustomerServePage")
    public String addCustomerServePage(){
        return "customerServe/customer_serve_add";
    }

    /**
     * 创建服务
     * @param customerServe
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCustomerServe(CustomerServe customerServe){
        customerServeService.addCustomerServe(customerServe);
        return success("创建服务成功");
    }

    /**
     * 修改服务
     * @param customerServe
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCustomerServe(CustomerServe customerServe){
        customerServeService.updateCustomerServe(customerServe);
        return success("服务更新成功");
    }

    /**
     * 打开服务分配页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addCustomerServeAssignPage")
    public String addCustomerServeAssignPage(Integer id, Model model){
        model.addAttribute("customerServe",customerServeService.selectByPrimaryKey(id));
        return "customerServe/customer_serve_assign_add";
    }

    /**
     * 打开服务处理页面
     * @return
     */
    @RequestMapping("toCustomerServeProcePage")
    public String toCustomerServeProcePage(Integer id,Model model){
        //通过id查询服务记录，并设置到请求域中
        model.addAttribute("customerServe",customerServeService.selectByPrimaryKey(id));
        return "customerServe/customer_serve_proce_add";

    }

    /**
     * 打开服务反馈页面
     * @return
     */
    @RequestMapping("toCustomerServeBackPage")
    public String toCustomerServeBackPage(Integer id,Model model){
        //通过id查询服务记录，并设置到请求域中
        model.addAttribute("customerServe",customerServeService.selectByPrimaryKey(id));
        return "customerServe/customer_serve_feed_back_add";
    }

}