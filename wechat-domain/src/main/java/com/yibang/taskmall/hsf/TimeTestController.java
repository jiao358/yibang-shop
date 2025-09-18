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

}
