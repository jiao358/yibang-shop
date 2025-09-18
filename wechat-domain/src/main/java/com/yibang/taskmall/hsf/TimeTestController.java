package com.yibang.taskmall.hsf;

import com.yibang.taskmall.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 时间服务测试控制器
 * 用于测试HSF时间服务的调用
 *
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/hsf/test")
@RequiredArgsConstructor
@Tag(name = "时间服务测试", description = "测试HSF时间服务的调用")
public class TimeTestController {

    private final TimeService timeService;
    private final TimeServiceClient timeServiceClient;

    /**
     * 测试本地时间服务
     */
    @GetMapping("/local")
    @Operation(summary = "测试本地时间服务", description = "直接调用本地时间服务实现")
    public Result<Map<String, Object>> testLocalTimeService() {
        log.info("测试本地时间服务");
        try {
            Map<String, Object> timeInfo = timeService.getTimeInfo();
            return Result.success(timeInfo);
        } catch (Exception e) {
            log.error("本地时间服务调用失败", e);
            return Result.error("本地时间服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 测试Feign客户端调用
     */
    @GetMapping("/feign")
    @Operation(summary = "测试Feign客户端调用", description = "通过Feign客户端调用时间服务")
    public Result<Map<String, Object>> testFeignTimeService() {
        log.info("测试Feign客户端调用时间服务");
        try {
            Result<Map<String, Object>> result = timeServiceClient.getTimeInfo();
            if (result.getCode() == 200) {
                return result;
            } else {
                return Result.error("Feign调用失败: " + result.getMessage());
            }
        } catch (Exception e) {
            log.error("Feign客户端调用失败", e);
            return Result.error("Feign客户端调用失败: " + e.getMessage());
        }
    }

    /**
     * 测试服务注册状态
     */
    @GetMapping("/status")
    @Operation(summary = "测试服务注册状态", description = "检查服务是否已注册到Nacos")
    public Result<Map<String, Object>> testServiceStatus() {
        log.info("测试服务注册状态");
        Map<String, Object> status = Map.of(
            "serviceName", "yibang-taskmall",
            "localTime", timeService.getCurrentTime(),
            "timestamp", timeService.getCurrentTimestamp(),
            "status", "running",
            "message", "服务运行正常，请检查Nacos控制台确认注册状态"
        );
        return Result.success(status);
    }
}
