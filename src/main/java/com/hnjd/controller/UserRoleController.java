package com.hnjd.controller;

import com.hnjd.base.BaseController;
import com.hnjd.service.UserRoleService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class UserRoleController extends BaseController {

    @Resource
    private UserRoleService userRoleService;
}
