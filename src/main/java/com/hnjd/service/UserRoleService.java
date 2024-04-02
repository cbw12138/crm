package com.hnjd.service;

import com.hnjd.base.BaseService;
import com.hnjd.dao.UserRoleMapper;
import com.hnjd.vo.UserRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserRoleService extends BaseService<UserRole,Integer> {
    @Resource
    private UserRoleMapper userRoleMapper;
}
