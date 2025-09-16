package com.yibang.taskmall.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 分页查询优化工具类
 * 
 * @author yibang
 * @since 2024-01-15
 */
public class PageUtils {

    /**
     * 构建分页对象
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 分页对象
     */
    public static <T> IPage<T> buildPage(Integer page, Integer size) {
        // 参数校验和默认值
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : Math.min(size, 500); // 最大500条
        
        return new Page<>(page, size);
    }

    /**
     * 添加时间范围查询条件
     * 
     * @param queryWrapper 查询构造器
     * @param timeField 时间字段名
     * @param startDate 开始日期 (yyyy-MM-dd)
     * @param endDate 结束日期 (yyyy-MM-dd)
     */
    public static <T> void addDateRange(QueryWrapper<T> queryWrapper, String timeField, 
                                       String startDate, String endDate) {
        if (StringUtils.hasText(startDate)) {
            LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .atStartOfDay();
            queryWrapper.ge(timeField, startDateTime);
        }
        
        if (StringUtils.hasText(endDate)) {
            LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .atTime(23, 59, 59);
            queryWrapper.le(timeField, endDateTime);
        }
    }

    /**
     * 添加关键词模糊查询条件
     * 
     * @param queryWrapper 查询构造器
     * @param keyword 关键词
     * @param fields 搜索字段列表
     */
    public static <T> void addKeywordSearch(QueryWrapper<T> queryWrapper, String keyword, String... fields) {
        if (StringUtils.hasText(keyword) && fields.length > 0) {
            queryWrapper.and(wrapper -> {
                for (int i = 0; i < fields.length; i++) {
                    if (i == 0) {
                        wrapper.like(fields[i], keyword);
                    } else {
                        wrapper.or().like(fields[i], keyword);
                    }
                }
            });
        }
    }

    /**
     * 添加状态查询条件
     * 
     * @param queryWrapper 查询构造器
     * @param statusField 状态字段名
     * @param status 状态值
     */
    public static <T> void addStatusCondition(QueryWrapper<T> queryWrapper, String statusField, String status) {
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(statusField, status);
        }
    }

    /**
     * 添加排序条件
     * 
     * @param queryWrapper 查询构造器
     * @param orderBy 排序字段
     * @param isAsc 是否升序
     */
    public static <T> void addOrderBy(QueryWrapper<T> queryWrapper, String orderBy, Boolean isAsc) {
        if (StringUtils.hasText(orderBy)) {
            if (isAsc != null && isAsc) {
                queryWrapper.orderByAsc(orderBy);
            } else {
                queryWrapper.orderByDesc(orderBy);
            }
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc("created_at");
        }
    }

    /**
     * 选择查询字段（避免SELECT *）
     * 
     * @param queryWrapper 查询构造器
     * @param selectFields 要查询的字段
     */
    public static <T> void selectFields(QueryWrapper<T> queryWrapper, String... selectFields) {
        if (selectFields.length > 0) {
            queryWrapper.select(selectFields);
        }
    }

    /**
     * 构建任务列表查询条件
     * 优化：使用覆盖索引 idx_type_status_created_at
     */
    public static <T> QueryWrapper<T> buildTaskListQuery(String type, String status, 
                                                        String keyword, String startDate, String endDate) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        
        // 选择列表必要字段，避免SELECT *
        selectFields(queryWrapper, "id", "title", "type", "reward_amount", "reward_level", 
                    "status", "created_at");
        
        // 优先使用索引字段过滤
        addStatusCondition(queryWrapper, "type", type);
        addStatusCondition(queryWrapper, "status", status);
        
        // 关键词搜索（会导致索引失效，放在最后）
        addKeywordSearch(queryWrapper, keyword, "title", "description");
        
        // 时间范围查询
        addDateRange(queryWrapper, "created_at", startDate, endDate);
        
        // 排序：利用索引
        addOrderBy(queryWrapper, "created_at", false);
        
        return queryWrapper;
    }

    /**
     * 构建用户列表查询条件
     * 优化：使用覆盖索引 idx_user_level_status_created_at
     */
    public static <T> QueryWrapper<T> buildUserListQuery(String level, String status, 
                                                        String keyword, String startDate, String endDate) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        
        // 选择列表必要字段
        selectFields(queryWrapper, "id", "nickname", "phone", "user_level", "available_balance", 
                    "total_tasks", "status", "last_login_time", "created_at");
        
        // 优先使用索引字段
        addStatusCondition(queryWrapper, "user_level", level);
        addStatusCondition(queryWrapper, "status", status);
        
        // 关键词搜索
        addKeywordSearch(queryWrapper, keyword, "nickname", "phone");
        
        // 时间范围
        addDateRange(queryWrapper, "created_at", startDate, endDate);
        
        // 排序
        addOrderBy(queryWrapper, "created_at", false);
        
        return queryWrapper;
    }

    /**
     * 构建提现列表查询条件
     * 优化：使用覆盖索引 idx_status_created_at
     */
    public static <T> QueryWrapper<T> buildWithdrawListQuery(String status, Long userId, 
                                                           String startDate, String endDate) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        
        // 选择列表必要字段
        selectFields(queryWrapper, "id", "user_id", "amount", "status", "created_at", "completed_at");
        
        // 优先使用索引字段
        addStatusCondition(queryWrapper, "status", status);
        
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        
        // 时间范围
        addDateRange(queryWrapper, "created_at", startDate, endDate);
        
        // 排序：待审核的优先显示
        if ("pending".equals(status)) {
            addOrderBy(queryWrapper, "created_at", true); // 升序，最早申请的先处理
        } else {
            addOrderBy(queryWrapper, "created_at", false);
        }
        
        return queryWrapper;
    }

    /**
     * 计算缓存键的哈希值
     * 
     * @param params 参数对象
     * @return 哈希值
     */
    public static String calculateParamsHash(Object... params) {
        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            if (param != null) {
                sb.append(param.toString()).append("|");
            }
        }
        return String.valueOf(sb.toString().hashCode());
    }
}
