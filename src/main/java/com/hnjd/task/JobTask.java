package com.hnjd.task;

import com.hnjd.service.CustomerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务的执行
 */
@Component
public class JobTask {
    @Resource
    private CustomerService customerService;

    //@Scheduled(cron = "0/2 * * * * ?") //两秒执行一次
    public void job(){
        System.out.println("定时任务开始执行---->"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        customerService.updateCustomerState();
    }
}
