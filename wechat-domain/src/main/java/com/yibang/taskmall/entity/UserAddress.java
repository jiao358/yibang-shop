package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户收货地址实体
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@TableName("user_addresses")
public class UserAddress {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id") // 用户ID
    private Long userId;

    @TableField("name") // 收货人姓名
    private String receiverName;

    @TableField("phone") // 收货人电话
    private String receiverPhone;

    @TableField("province") // 省份
    private String province;

    @TableField("city") // 城市
    private String city;

    @TableField("district") // 区县
    private String district;

    @TableField("detail_address") // 详细地址
    private String detailAddress;

    @TableField("full_address") // 完整地址
    private String fullAddress;

    @TableField("is_default") // 是否默认地址
    private Boolean isDefault;

    @TableField(value = "created_at", fill = FieldFill.INSERT) // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE) // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}
