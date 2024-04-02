package com.hnjd.controller;

import com.hnjd.annoation.RequiredPermission;
import com.hnjd.base.BaseController;
import com.hnjd.base.ResultInfo;
import com.hnjd.enums.StateStatus;
import com.hnjd.query.SaleChanceQuery;
import com.hnjd.service.PermissionService;
import com.hnjd.service.SaleChanceService;
import com.hnjd.service.UserService;
import com.hnjd.utils.CookieUtil;
import com.hnjd.utils.LoginUserUtil;
import com.hnjd.vo.SaleChance;
import com.hnjd.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private PermissionService permissionService;

    /**
     * 营销机会数据查询
     * 如果flag值不为空，且值为1，则表示当前查询的是客户开发计划 否则查询营销机会数据
     *
     * @param saleChanceQuery
     * @return
     */
    @RequiredPermission(code = "101001")
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery, Integer flag, HttpServletRequest request) {
        //判断flag的值
        if (flag != null && flag == 1) {
            //查询客户开发计划
            //设置分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //设置指派人（当前登录用户的id）
            //从cookie中获取当前登录用户的id
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }
        Map<String, Object> map = saleChanceService.querySaleChanceByParams(saleChanceQuery);
        return map;
    }



    @RequestMapping("index")
    public String index(HttpServletRequest request) {
        return "saleChance/sale_chance";
    }

    /**
     * 添加营销机会
     *
     * @param saleChance
     * @param request
     * @return
     */
    @RequiredPermission(code = "101002")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest request) {
        //cookie中获取当前登录的用户名
        String userName = CookieUtil.getCookieValue(request, "userName");
        //设置用户名到营销机会对象
        saleChance.setCreateMan(userName);
        //调用service层的添加方法
        saleChanceService.addSaleChance(saleChance);
        return success("添加成功");
    }

    /**
     * 进入添加/修改营销机会数据页面
     *
     * @param saleChanceId
     * @param req
     * @return
     */
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer saleChanceId, HttpServletRequest req) {
        //判断saleChanceId是否为空
        if (saleChanceId != null) {
            //通过id查询营销机会数据
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
            //将数据设置到请求域中
            req.setAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 修改营销机会
     *
     * @param saleChance
     * @return
     */
    @RequiredPermission(code = "101004")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance) {

        //调用service层的添加方法
        saleChanceService.updateSaleChance(saleChance);
        return success("修改成功");
    }

    /**
     * 删除营销机会
     *
     * @param ids
     * @return
     */
    @RequiredPermission(code = "101003")
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSelaChance(Integer[] ids) {
        //调用Service层的删除方法
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功");
    }


    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     * @return
     */
    @PostMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功");
    }
}
