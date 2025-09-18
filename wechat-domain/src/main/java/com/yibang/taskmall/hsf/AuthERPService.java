package com.yibang.taskmall.hsf;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.hsf.dto.UserAuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "yibang-erp", path = "/api/hsf/auth")
public interface AuthERPService {
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserAuthRequest request,@RequestHeader("X-API-Key") String apiKey) ;
}
