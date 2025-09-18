package com.yibang.taskmall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件上传配置属性类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@Component
@ConfigurationProperties(prefix = "system.file.upload")
public class FileUploadProperties {

    /**
     * 文件上传根路径
     */
    private String path = "./uploads/";

    /**
     * 图片上传配置
     */
    private ImageConfig image = new ImageConfig();

    @Data
    public static class ImageConfig {
        /**
         * 最大文件大小
         */
        private String maxSize = "5MB";

        /**
         * 允许的文件类型
         */
        private List<String> allowedTypes = List.of("jpg", "jpeg", "png", "gif", "webp");

        /**
         * 图片存储子路径
         */
        private String path = "images/products";

        /**
         * 访问配置
         */
        private AccessConfig access = new AccessConfig();

        @Data
        public static class AccessConfig {
            /**
             * 访问基础URL
             */
            private String baseUrl = "http://localhost:8080/api/static/";
        }
    }

    /**
     * 获取图片最大大小（字节）
     */
    public long getImageMaxSizeBytes() {
        String maxSize = image.getMaxSize().toUpperCase();
        if (maxSize.endsWith("MB")) {
            return Long.parseLong(maxSize.replace("MB", "")) * 1024 * 1024;
        } else if (maxSize.endsWith("KB")) {
            return Long.parseLong(maxSize.replace("KB", "")) * 1024;
        } else if (maxSize.endsWith("B")) {
            return Long.parseLong(maxSize.replace("B", ""));
        }
        return 5 * 1024 * 1024; // 默认5MB
    }

    /**
     * 检查文件类型是否允许
     */
    public boolean isAllowedImageType(String fileType) {
        if (fileType == null) return false;
        String lowerType = fileType.toLowerCase();
        return image.getAllowedTypes().stream()
                .anyMatch(allowedType -> lowerType.contains(allowedType.toLowerCase()));
    }

    /**
     * 获取完整的图片访问URL
     */
    public String getImageAccessUrl(String filename) {
        return image.getAccess().getBaseUrl() + image.getPath() + "/" + filename;
    }

    /**
     * 获取图片存储的完整路径
     */
    public String getImageStoragePath(String filename) {
        return path + image.getPath() + "/" + filename;
    }
}
