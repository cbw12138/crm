package com.hnjd.service;

import com.hnjd.base.BaseService;
import com.hnjd.dao.ModuleMapper;
import com.hnjd.dao.PermissionMapper;
import com.hnjd.model.TreeModel;
import com.hnjd.utils.AssertUtil;
import com.hnjd.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module, Integer> {

    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询所有的资源列表
     */
    public List<TreeModel> queryAllModules(Integer roleId) {
        //查询所有的资源列表
        List<TreeModel> treeModelList = moduleMapper.queryAllModules();
        //查询指定角色已经授权过的资源列表
        List<Integer> permissionIds = permissionMapper.queryRoleHasModuleIdByRoleId(roleId);
        //判断角色是否拥有资源id
        if (permissionIds != null && permissionIds.size() > 0) {
            //循环所有的资源列表，判断用户拥有的资源id中是否有匹配的。如果有，则设置checked属性为true
            treeModelList.forEach(treeModel -> {
                //判断角色拥有的资源id中是否有当前遍历的资源有匹配的，如果有，则设置checked属性为true
                if (permissionIds.contains(treeModel.getId())) {
                    //如果包含，则说明角色授权过，设置checked属性为true
                    treeModel.setChecked(true);
                }
            });
        }
        return treeModelList;
    }


    /**
     * 查询资源数据
     *
     * @return
     */
    public Map<String, Object> queryModuleList() {
        Map<String, Object> map = new HashMap<>();
        //查询资源列表
        List<Module> modules = moduleMapper.queryModuleList();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", modules.size());
        map.put("data", modules);

        return map;
    }


    /**
     * 添加资源
     *
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module) {
        //1.参数校验
        //层级 grade 非空 0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(grade == null || !(grade == 0 || grade == 1 || grade == 2), "菜单层次不合法");
        //模块名称 非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "模块名称不能为空");
        //模块名称 同一层级下模块名称唯一
        AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName()), "该层次下模块名称已存在");

        //二级菜单 (grade=1)
        if (grade == 1) {
            //地址 url 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "地址不能为空");
            //地址 url 同一层级下模块名称不能重复
            AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl()), "地址不可重复");
        }

        //父级菜单 parentId  一级菜单（目录，grade=0）
        if (grade == 0) {
            module.setParentId(-1);
        }
        //父级菜单 parentId 二级、三级菜单（菜单|按钮，grade=1或2） 非空 父级菜单必须存在
        if (grade != 0) {
            //非空
            AssertUtil.isTrue(null == module.getParentId(), "父级菜单不能为空");
            //父级菜单必须存在 父级的id作为主键，查询资源记录
            AssertUtil.isTrue(null == moduleMapper.selectByPrimaryKey(module.getParentId()), "请指定正确的父级菜单");
        }
        //权限码 非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不能为空");
        //权限码 不可重复
        AssertUtil.isTrue(null != moduleMapper.queryModuleByOptValue(module.getOptValue()), "权限码已存在");

        //2.设置参数的默认值
        //是否有效 1
        module.setIsValid((byte) 1);
        //创建时间 系统当前时间
        module.setCreateDate(new Date());
        //修改时间 系统当前时间
        module.setUpdateDate(new Date());

        //3.执行添加操作 ，判断受影响的行数
        AssertUtil.isTrue(moduleMapper.insertSelective(module) < 1, "添加资源失败");
    }


    /**
     * 修改资源
     *
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module) {
        //1.参数校验
        //id 非空 数据存在
        AssertUtil.isTrue(null == module.getId(), "待更新记录不存在");
        //通过id查询资源对象
        Module temp = moduleMapper.selectByPrimaryKey(module.getId());
        //判断记录是否存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在");

        //层级 grade 非空 0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(grade == null || !(grade == 0 || grade == 1 || grade == 2), "菜单层次不合法");
        //模块名称 非空  同一层级下模块名称唯一 (不包含当前修改记录本身)
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "模块名称不能为空");
        //通过层级和模块名称查询资源对象
        temp = moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName());
        if (temp != null) {
            AssertUtil.isTrue(!(temp.getId()).equals(module.getId()), "该层次下模块名称已存在");
        }

        //地址url 二级菜单(grade=1)，非空 且同一层级下不可重复(不包含当前修改记录本身)
        if (grade == 1) {
            //地址 url 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "地址不能为空");
            //通过层级与菜单URL查询资源对象
            temp = moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl());
            //判断是否存在
            if (temp != null) {
                AssertUtil.isTrue(!(temp.getId()).equals(module.getId()), "该层次下菜单url地址已存在");
            }
        }


        //权限码 非空  不可重复
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不能为空");
        //通过权限码查询资源对象
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        //判断是否为空
        if (temp != null) {
            AssertUtil.isTrue(!(temp.getId()).equals(module.getId()), "权限码已存在");
        }

        //2.设置参数的默认值
        //修改时间 系统当前时间
        module.setUpdateDate(new Date());

        //3.执行添加操作 ，判断受影响的行数
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) < 1, "修改资源失败");
    }


    /**
     * 删除资源
     *
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModule(Integer id) {
        // 判断id是否为空
        AssertUtil.isTrue(null == id, "待删除记录不存在");
        //通过id查询资源对象
        Module temp = moduleMapper.selectByPrimaryKey(id);
        //判断资源对象是否为空
        AssertUtil.isTrue(null == temp, "待删除记录不存在");
        //如果当前资源存在子记录(将id当做父id查询资源记录)
        Integer count = moduleMapper.queryModuleByParentId(id);
        //如果存在子记录。则不可删除
        AssertUtil.isTrue(count > 0, "该资源存在子记录,不可删除");
        //通过资源id查询权限表中是否存在数据
        count = permissionMapper.countPermissionByModuleId(id);
        //判断是否存在，存在则删除
        if (count > 0) {
            //删除指定资源id的权限记录
            permissionMapper.deletePermissionByModuleId(id);
        }

        //设置记录无效
        temp.setIsValid((byte) 0);
        temp.setUpdateDate(new Date());

        //执行更新
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp) < 1, "资源删除失败");
    }
}
