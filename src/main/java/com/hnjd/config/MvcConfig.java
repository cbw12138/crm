package com.hnjd.config;

import com.hnjd.interceptor.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration//配置类
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public NoLoginInterceptor noLoginInterceptor(){
        return new NoLoginInterceptor();
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //要一个实现拦截器功能的实例对象，这里用noLoginInterceptor
        registry.addInterceptor(noLoginInterceptor())
                .addPathPatterns("/**")//默认拦截所有资源
                .excludePathPatterns("/css/**","/images/**","/js/**","/lib/**","/index","/user/login");//放行
    }
}
