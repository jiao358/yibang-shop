package com.yibang.taskmall.hsf;

import com.yibang.taskmall.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 时间工具服务控制器
 * 提供时间相关的HTTP接口
 *
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/api/hsf/time")
@RequiredArgsConstructor
@Tag(name = "时间工具服务", description = "提供当前时间相关的工具方法")
public class TimeController {

    private final TimeService timeService;

    /**
     * 获取当前时间
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前时间", description = "返回格式化的当前时间字符串")
    public Result<String> getCurrentTime() {
        log.info("获取当前时间");
        String currentTime = timeService.getCurrentTime();
        return Result.success(currentTime);
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    @GetMapping("/timestamp")
    @Operation(summary = "获取当前时间戳", description = "返回当前时间戳（毫秒）")
    public Result<Long> getCurrentTimestamp() {
        log.info("获取当前时间戳");
        long timestamp = timeService.getCurrentTimestamp();
        return Result.success(timestamp);
    }

    /**
     * 获取当前时间戳（秒）
     */
    @GetMapping("/timestamp/seconds")
    @Operation(summary = "获取当前时间戳（秒）", description = "返回当前时间戳（秒）")
    public Result<Long> getCurrentTimestampSeconds() {
        log.info("获取当前时间戳（秒）");
        long timestamp = timeService.getCurrentTimestampSeconds();
        return Result.success(timestamp);
    }

    /**
     * 获取格式化的当前时间
     */
    @GetMapping("/current/formatted")
    @Operation(summary = "获取格式化的当前时间", description = "根据指定格式返回当前时间")
    public Result<String> getCurrentTimeFormatted(
            @Parameter(description = "时间格式，如：yyyy-MM-dd HH:mm:ss") 
            @RequestParam(defaultValue = "yyyy-MM-dd HH:mm:ss") String pattern) {
        log.info("获取格式化的当前时间: pattern={}", pattern);
        String formattedTime = timeService.getCurrentTime(pattern);
        return Result.success(formattedTime);
    }

    /**
     * 获取时间信息详情
     */
    @GetMapping("/info")
    @Operation(summary = "获取时间信息详情", description = "返回包含各种时间格式的详细信息")
    public Result<Map<String, Object>> getTimeInfo() {
        log.info("获取时间信息详情");
        Map<String, Object> timeInfo = timeService.getTimeInfo();
        return Result.success(timeInfo);
    }

    /**
     * 获取当前日期
     */
    @GetMapping("/date")
    @Operation(summary = "获取当前日期", description = "返回当前日期字符串")
    public Result<String> getCurrentDate() {
        log.info("获取当前日期");
        String currentDate = timeService.getCurrentDate();
        return Result.success(currentDate);
    }

    /**
     * 获取当前时间（仅时间部分）
     */
    @GetMapping("/time-only")
    @Operation(summary = "获取当前时间（仅时间部分）", description = "返回当前时间字符串（不含日期）")
    public Result<String> getCurrentTimeOnly() {
        log.info("获取当前时间（仅时间部分）");
        String currentTimeOnly = timeService.getCurrentTimeOnly();
        return Result.success(currentTimeOnly);
    }

    /**
     * 获取当前周几
     */
    @GetMapping("/day-of-week")
    @Operation(summary = "获取当前周几", description = "返回当前是周几（1-7，1为周一）")
    public Result<Integer> getCurrentDayOfWeek() {
        log.info("获取当前周几");
        int dayOfWeek = timeService.getCurrentDayOfWeek();
        return Result.success(dayOfWeek);
    }

    /**
     * 获取当前年份
     */
    @GetMapping("/year")
    @Operation(summary = "获取当前年份", description = "返回当前年份")
    public Result<Integer> getCurrentYear() {
        log.info("获取当前年份");
        int year = timeService.getCurrentYear();
        return Result.success(year);
    }

    /**
     * 获取当前月份
     */
    @GetMapping("/month")
    @Operation(summary = "获取当前月份", description = "返回当前月份（1-12）")
    public Result<Integer> getCurrentMonth() {
        log.info("获取当前月份");
        int month = timeService.getCurrentMonth();
        return Result.success(month);
    }

    /**
     * 获取当前日期（几号）
     */
    @GetMapping("/day")
    @Operation(summary = "获取当前日期（几号）", description = "返回当前是几号（1-31）")
    public Result<Integer> getCurrentDayOfMonth() {
        log.info("获取当前日期（几号）");
        int day = timeService.getCurrentDayOfMonth();
        return Result.success(day);
    }
}
