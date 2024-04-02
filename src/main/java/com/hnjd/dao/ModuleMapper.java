package com.hnjd.dao;

import com.hnjd.base.BaseMapper;
import com.hnjd.model.TreeModel;
import com.hnjd.vo.Module;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

    //查询所有的资源列表
    public List<TreeModel> queryAllModules();

    //查询所有的资源数据
    public List<Module> queryModuleList();

    //通过层级与模块名称查询资源对象
    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade,@Param("moduleName")  String moduleName);

    //通过层级与URL地址查询资源对象
    Module queryModuleByGradeAndUrl(@Param("grade") Integer grade, @Param("url") String url);

    //通过权限码查询资源对象
    Module queryModuleByOptValue(String optValue);

    //查询指定资源是否存在子记录
    Integer queryModuleByParentId(Integer id);
}