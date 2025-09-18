package com.yibang.taskmall.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 手动分页工具类
 * 用于替代MyBatis-Plus自动分页，提高查询性能
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
public class PageUtils {

    /**
     * 默认页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 创建分页对象
     */
    public static <T> Page<T> createPage(Integer page, Integer size) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        return new Page<>(currentPage, pageSize);
    }

    /**
     * 计算偏移量
     */
    public static int getOffset(Integer page, Integer size) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        return (currentPage - 1) * pageSize;
    }

    /**
     * 构建分页结果
     */
    public static <T> Page<T> buildPage(List<T> records, long total, Integer page, Integer size) {
        Page<T> pageResult = createPage(page, size);
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setPages(calculateTotalPages(total, pageResult.getSize()));
        return pageResult;
    }

    /**
     * 计算总页数
     */
    public static long calculateTotalPages(long total, long pageSize) {
        if (total <= 0 || pageSize <= 0) {
            return 0;
        }
        return (total + pageSize - 1) / pageSize;
    }

    /**
     * 验证分页参数
     */
    public static void validatePageParams(Integer page, Integer size) {
        if (page != null && page < 1) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (size != null && (size < 1 || size > MAX_PAGE_SIZE)) {
            throw new IllegalArgumentException("页大小必须在1到" + MAX_PAGE_SIZE + "之间");
        }
    }

    /**
     * 构建LIMIT子句
     */
    public static String buildLimitClause(Integer page, Integer size) {
        int offset = getOffset(page, size);
        int limit = size == null || size < 1 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        return String.format("LIMIT %d, %d", offset, limit);
    }

    /**
     * 日志记录分页信息
     */
    public static void logPageInfo(String operation, Integer page, Integer size, long total, long queryTime) {
        log.debug("分页查询 - 操作: {}, 页码: {}, 页大小: {}, 总记录数: {}, 查询时间: {}ms", 
                  operation, page, size, total, queryTime);
    }
}