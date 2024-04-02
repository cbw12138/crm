package com.hnjd.interceptor;

import com.hnjd.dao.UserMapper;
import com.hnjd.exceptions.NoLoginException;
import com.hnjd.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserMapper userMapper;
    /**
     * 拦截用户是否是登录状态
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //判断用户id是否为空，且数据库中存在该id的用户记录
        if (null==userId || userMapper.selectByPrimaryKey(userId)==null){
            //抛出未登录异常
            throw new NoLoginException();
        }
        return true;
    }
}
