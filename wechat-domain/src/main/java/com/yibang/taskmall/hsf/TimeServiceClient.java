package com.yibang.taskmall.hsf;

import com.yibang.taskmall.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 时间服务Feign客户端
 * 用于调用时间工具服务
 *
 * @author yibang
 * @since 2024-01-15
 */
@FeignClient(name = "yibang-taskmall", path = "/api/hsf/time")
public interface TimeServiceClient {

    /**
     * 获取当前时间
     */
    @GetMapping("/current")
    Result<String> getCurrentTime();

    /**
     * 获取当前时间戳（毫秒）
     */
    @GetMapping("/timestamp")
    Result<Long> getCurrentTimestamp();

    /**
     * 获取当前时间戳（秒）
     */
    @GetMapping("/timestamp/seconds")
    Result<Long> getCurrentTimestampSeconds();

    /**
     * 获取格式化的当前时间
     */
    @GetMapping("/current/formatted")
    Result<String> getCurrentTimeFormatted(@RequestParam(defaultValue = "yyyy-MM-dd HH:mm:ss") String pattern);

    /**
     * 获取时间信息详情
     */
    @GetMapping("/info")
    Result<Map<String, Object>> getTimeInfo();

    /**
     * 获取当前日期
     */
    @GetMapping("/date")
    Result<String> getCurrentDate();

    /**
     * 获取当前时间（仅时间部分）
     */
    @GetMapping("/time-only")
    Result<String> getCurrentTimeOnly();

    /**
     * 获取当前周几
     */
    @GetMapping("/day-of-week")
    Result<Integer> getCurrentDayOfWeek();

    /**
     * 获取当前年份
     */
    @GetMapping("/year")
    Result<Integer> getCurrentYear();

    /**
     * 获取当前月份
     */
    @GetMapping("/month")
    Result<Integer> getCurrentMonth();

    /**
     * 获取当前日期（几号）
     */
    @GetMapping("/day")
    Result<Integer> getCurrentDayOfMonth();
}
