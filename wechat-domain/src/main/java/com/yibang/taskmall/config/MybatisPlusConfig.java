package com.yibang.taskmall.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis Plus 拦截器配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件 - 暂时注释掉，因为MyBatis Plus 3.5.9版本可能不支持PaginationInnerInterceptor
        // PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // // 设置最大单页限制数量，默认 500 条，-1 不受限制
        // paginationInnerInterceptor.setMaxLimit(1000L);
        // // 溢出总页数后是否进行处理
        // paginationInnerInterceptor.setOverflow(false);
        // 
        // interceptor.addInnerInterceptor(paginationInnerInterceptor);
        
        return interceptor;
    }
}
