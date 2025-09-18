package com.yibang.taskmall.hsf;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间工具服务接口
 * 提供当前时间相关的工具方法
 *
 * @author yibang
 * @since 2024-01-15
 */
public interface TimeService {

    /**
     * 获取当前时间
     *
     * @return 当前时间字符串 (yyyy-MM-dd HH:mm:ss)
     */
    String getCurrentTime();

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 当前时间戳
     */
    long getCurrentTimestamp();

    /**
     * 获取当前时间戳（秒）
     *
     * @return 当前时间戳（秒）
     */
    long getCurrentTimestampSeconds();

    /**
     * 获取格式化的当前时间
     *
     * @param pattern 时间格式
     * @return 格式化后的时间字符串
     */
    String getCurrentTime(String pattern);

    /**
     * 获取时间信息详情
     *
     * @return 包含各种时间格式的Map
     */
    Map<String, Object> getTimeInfo();

    /**
     * 获取当前日期
     *
     * @return 当前日期字符串 (yyyy-MM-dd)
     */
    String getCurrentDate();

    /**
     * 获取当前时间（仅时间部分）
     *
     * @return 当前时间字符串 (HH:mm:ss)
     */
    String getCurrentTimeOnly();

    /**
     * 获取当前周几
     *
     * @return 周几（1-7，1为周一）
     */
    int getCurrentDayOfWeek();

    /**
     * 获取当前年份
     *
     * @return 当前年份
     */
    int getCurrentYear();

    /**
     * 获取当前月份
     *
     * @return 当前月份（1-12）
     */
    int getCurrentMonth();

    /**
     * 获取当前日期（几号）
     *
     * @return 当前日期（1-31）
     */
    int getCurrentDayOfMonth();
}
