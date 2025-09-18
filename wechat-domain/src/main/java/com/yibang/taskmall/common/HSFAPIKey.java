package com.yibang.taskmall.common;

import com.alibaba.nacos.common.utils.MapUtil;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HSFAPIKey {
    @Value("${hsf.security.api-keys:yibang-erp:erp-2024-abc123def456,other-system:other-2024-xyz789}")
    private String apiKeysConfig;

    private Map<String, String> validApiKeys;

   public  String getApiKeysConfig(){
      if(MapUtil.isEmpty(validApiKeys)){
          validApiKeys=parseApiKeys(apiKeysConfig);
      }
      return validApiKeys.values().stream().findAny().get();
  }
    private Map<String, String> parseApiKeys(String config) {
        Map<String, String> apiKeys = new java.util.HashMap<>();

        if (config == null || config.trim().isEmpty()) {
            return apiKeys;
        }

        // 解析格式: system1:key1,system2:key2
        String[] pairs = config.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.trim().split(":");
            if (keyValue.length == 2) {
                apiKeys.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return apiKeys;
    }
}
