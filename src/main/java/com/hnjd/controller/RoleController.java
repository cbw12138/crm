package com.hnjd.controller;

import com.hnjd.base.BaseController;
import com.hnjd.base.ResultInfo;
import com.hnjd.query.RoleQuery;
import com.hnjd.service.RoleService;
import com.hnjd.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;

    /**
     * 查询所有的角色列表
     * @param userId
     * @return
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }


    /**
     * 分页条件查询角色列表
     * @param roleQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> selectByParams(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }

    /**
     * 进入角色管理页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
       return "role/role";
    }

    /**
     * 添加角色
     * @param role
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addRole(Role role){
        roleService.addRole(role);
        return success("添加角色成功");
    }

    /**
     * 修改角色
     * @param role
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("修改角色成功");
    }


    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer roleId){
        roleService.deleteRole(roleId);
        return success("删除角色成功");
    }

    /**
     * 进入添加/修改页面
     * @return
     */
    @RequestMapping("toAddOrUpdateRolePage")
    public String toAddOrUpdateRolePage(Integer id, HttpServletRequest request){
        //如果不为空,则表示修改操作，通过角色ID查询角色记录，存到请求域中
        if (id!=null){
            //通过角色id查询角色记录
            Role role =  roleService.selectByPrimaryKey(id);
            //设置到请求域中
            request.setAttribute("role",role);
        }
        return "role/add_update";
    }

    /**
     * 角色授权操作
     * @return
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId,Integer[] mIds){

        roleService.addGrant(roleId,mIds);
        return success("角色授权成功");
    }
}
