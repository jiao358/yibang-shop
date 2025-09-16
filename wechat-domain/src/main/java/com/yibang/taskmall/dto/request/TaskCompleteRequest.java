package com.yibang.taskmall.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 任务完成请求DTO
 */
@Data
public class TaskCompleteRequest {
    
    /**
     * 用户任务ID
     */
    private Long userTaskId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 完成凭证图片URLs
     */
    private List<String> proofImages;
    
    /**
     * 完成说明
     */
    private String completionNote;
    
    /**
     * 其他凭证数据（JSON格式）
     * 例如：应用安装包名、分享链接、问卷答案等
     */
    private String additionalProofData;
}
