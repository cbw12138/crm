package com.hnjd.service;

import com.hnjd.base.BaseService;
import com.hnjd.dao.ModuleMapper;
import com.hnjd.dao.PermissionMapper;
import com.hnjd.dao.RoleMapper;
import com.hnjd.utils.AssertUtil;
import com.hnjd.vo.Permission;
import com.hnjd.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role, Integer> {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;


    /**
     * 查询所有的角色列表
     *
     * @return
     */
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 添加角色
     *
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role) {
        // 1.参数校验
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名称不能为空");
        //通过角色名查询角色记录
        Role temp = roleMapper.selectByRoleName(role.getRoleName());
        //判断角色记录是否存在(添加操作时，如果角色记录存在则表示名称不可用)
        AssertUtil.isTrue(temp != null, "角色名已存在，请重新输入");

        //2.设置默认值
        //是否有效
        role.setIsValid(1);
        //创建时间
        role.setCreateDate(new Date());
        //修改时间
        role.setUpdateDate(new Date());

        //3.执行添加操作，判断受影响的行数
        AssertUtil.isTrue(roleMapper.insertSelective(role) < 1, "添加角色失败");
    }


    /**
     * 修改角色
     *
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role) {
        //1.参数校验
        //角色id  非空 且数据存在
        AssertUtil.isTrue(null == role.getId(), "待更新记录不存在");
        //通过角色id查询角色记录
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        //判断角色记录是否存在
        AssertUtil.isTrue(null == temp, "待更新记录不存在");
        //角色名称 非空，名称唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名称不能为空");
        //通过角色名称查询角色记录
        temp = roleMapper.selectByRoleName(role.getRoleName());
        //判断角色记录是否存在  不存在：表示可用  存在，且角色id与当前更新的id不一致：表示不可用
        AssertUtil.isTrue(null != temp && (!temp.getId().equals(role.getId())), "角色名称已存在，不可使用");

        //2.设置参数的默认值
        role.setUpdateDate(new Date());
        //3.执行更新操作 判断受影响的行数
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1, "角色修改失败");
    }

    /**
     * 删除角色
     *
     * @param userId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer userId) {
        //判断角色id是否为空
        AssertUtil.isTrue(userId == null, "待删除记录不存在!!!");
        //通过角色id查询角色记录
        Role role = roleMapper.selectByPrimaryKey(userId);
        //判断角色记录是否存在
        AssertUtil.isTrue(role == null, "待删除记录不存在");

        //设置默认状态
        role.setIsValid(0);
        role.setUpdateDate(new Date());

        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1, "用户删除失败");
    }

    /**
     * 角色授权
     *
     * @param roleId
     * @param mIds
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId, Integer[] mIds) {
        //1.通过角色id查询对应的权限记录
        Integer count = permissionMapper.countPermissionByRoleId(roleId);
        //2.如果权限记录存在，则删除对应的角色拥有的权限记录
        if (count > 0) {
            //删除权限记录
            permissionMapper.deletePermissionByRoleId(roleId);
        }
        //3.如果有权限记录，则添加权限记录
        if (mIds != null && mIds.length > 0){
            //定义permission集合
            List<Permission> permissionList = new ArrayList<>();

            //遍历资源id数组
            for (Integer mId: mIds) {
                Permission permission = new Permission();
                permission.setModuleId(mId);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mId).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                //将对象设置到集合中
                permissionList.add(permission);
            }

            //执行批量添加操作，判断受影响的行数
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList)!=permissionList.size(),"角色授权失败");
        }
    }
}
