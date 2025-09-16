package com.yibang.taskmall.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件上传响应DTO
 *
 * @author yibang
 * @since 2024-01-15
 */
@Data
@Schema(description = "文件上传响应")
public class FileUploadDTO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "原始文件名")
    private String fileName;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件扩展名")
    private String fileExtension;

    @Schema(description = "上传类型")
    private String uploadType;

    @Schema(description = "文件状态")
    private String status;

    @Schema(description = "上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
}
