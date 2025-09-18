package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("dict_items")
public class DictItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("group_code")
    private String groupCode;

    @TableField("item_code")
    private String itemCode;

    @TableField("label_zh")
    private String labelZh;

    @TableField("label_en")
    private String labelEn;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("sort")
    private Integer sort;

    @TableField("remark")
    private String remark;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
