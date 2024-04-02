package com.hnjd.controller;

import com.github.pagehelper.PageException;
import com.hnjd.base.BaseController;
import com.hnjd.base.ResultInfo;
import com.hnjd.exceptions.ParamsException;
import com.hnjd.model.UserModel;
import com.hnjd.query.UserQuery;
import com.hnjd.service.UserService;
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
@RequestMapping("user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo resultInfo = new ResultInfo();

        //调用service层登录方法
        UserModel userModel = userService.userLogin(userName,userPwd);

        //设置ResultInfo
        resultInfo.setResult(userModel);

        return resultInfo;
    }


    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPassword,String newPassword,String repeatPassword){
        ResultInfo resultInfo = new ResultInfo();
        //获取cookie中的userid
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service层修改密码方法
        userService.updatePassWord(userId,oldPassword,newPassword,repeatPassword);

        return resultInfo;
    }

    /**
     * 进入修改密码页面
     * @return
     */
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){

        return "user/password";
    }

    /**
     * 查询所有的销售人员
     * @return
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return userService.queryAllSales();
    }

    /**
     * 查询用户列表
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> selectByParams(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);
    }

    /**
     * 进入用户列表页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("用户添加成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户修改成功");
    }

    /**
     * 打开修改或添加页面
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id,HttpServletRequest request){
        //判断id是否为空
        if (id!=null){
            //通过id查询用户对象
            User user = userService.selectByPrimaryKey(id);
            //将数据设置到请求域中
            request.setAttribute("userInfo",user);
        }
        return "user/add_update";
    }

    /**
     * 删除用户
     * @param ids
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteByIds(ids);
        return success("删除成功");
    }


    /**
     * 查询所有的客户经理
     * @return
     */
    @RequestMapping("queryAllCustomerManager")
    @ResponseBody
    public List<Map<String,Object>> queryAllCustomerManager(){
        return userService.queryAllCustomerManager();
    }

}
