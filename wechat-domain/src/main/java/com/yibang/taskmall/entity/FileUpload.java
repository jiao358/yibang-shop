package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件上传记录实体
 *
 * @author yibang
 * @since 2024-01-15
 */
@Data
@TableName("file_uploads")
public class FileUpload {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id") // 上传用户ID
    private Long userId;

    @TableField("file_name") // 原始文件名
    private String fileName;

    @TableField("file_path") // 文件存储路径
    private String filePath;

    @TableField("file_url") // 文件访问URL
    private String fileUrl;

    @TableField("file_size") // 文件大小（字节）
    private Long fileSize;

    @TableField("file_type") // 文件MIME类型
    private String fileType;

    @TableField("file_extension") // 文件扩展名
    private String fileExtension;

    @TableField("upload_type") // 上传类型：avatar,document,image,video等
    private String uploadType;

    @TableField("business_id") // 关联业务ID
    private Long businessId;

    @TableField("business_type") // 关联业务类型
    private String businessType;

    @TableField("upload_ip") // 上传IP地址
    private String uploadIp;

    @TableField("status") // 文件状态：active,deleted,expired
    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT) // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE) // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}
