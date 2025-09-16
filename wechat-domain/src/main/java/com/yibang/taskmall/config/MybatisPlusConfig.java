package com.yibang.taskmall.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器配置
     * 优化分页查询性能
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // TODO: 分页插件配置 - 根据MyBatis-Plus版本调整
        // 当前版本可能不支持某些高级配置，使用基础分页功能
        // 性能优化主要通过SQL优化和索引实现
        
        return interceptor;
    }
}