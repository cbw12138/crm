package com.hnjd.dao;

import com.hnjd.base.BaseMapper;
import com.hnjd.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    //通过角色id查询权限记录
    Integer countPermissionByRoleId(Integer roleId);
    //通过角色id删除权限记录
    void deletePermissionByRoleId(Integer roleId);

    //查询角色拥有的索引的资源id的集合
    List<Integer> queryRoleHasModuleIdByRoleId(Integer roleId);

    //通过用户id查询对应的资源列表(资源权限码)
    List<String> queryUserHasRoleHasPermissionByUserId(Integer userId);

    //通过资源id查询权限记录
    Integer countPermissionByModuleId(Integer id);

    //通过资源id删除权限记录
    Integer deletePermissionByModuleId(Integer id);
}