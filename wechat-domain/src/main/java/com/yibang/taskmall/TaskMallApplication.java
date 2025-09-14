package com.yibang.taskmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 懿邦任务商城启动类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@SpringBootApplication
@MapperScan("com.yibang.taskmall.mapper")
@EnableAsync
@EnableScheduling
public class TaskMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMallApplication.class, args);
        System.out.println("=================================");
        System.out.println("懿邦任务商城后端服务启动成功！");
        System.out.println("=================================");
    }
}
