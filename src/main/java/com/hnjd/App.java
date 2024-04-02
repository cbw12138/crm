package com.hnjd;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.hnjd.dao")
@EnableScheduling//启用定时任务
public class App {
    public static void main( String[] args ){
        SpringApplication.run(App.class,args);    }
}
