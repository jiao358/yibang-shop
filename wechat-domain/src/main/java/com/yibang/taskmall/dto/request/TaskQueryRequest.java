package com.yibang.taskmall.dto.request;

import lombok.Data;

/**
 * 任务查询请求DTO
 */
@Data
public class TaskQueryRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 任务类型
     * ad-广告任务, video-视频任务, app_install-应用安装, survey-问卷调查, share-分享任务
     */
    private String taskType;
    
    /**
     * 佣金等级
     * low-低, medium-中, high-高
     */
    private String rewardLevel;
    
    /**
     * 用户等级
     * normal-普通用户, signed-签约用户, vip-VIP用户, all-所有用户
     */
    private String userLevel;
    
    /**
     * 任务状态过滤
     * all-全部, available-可领取, in_progress-进行中, completed-已完成
     */
    private String statusFilter = "all";
    
    /**
     * 页码（从1开始）
     */
    private int page = 1;
    
    /**
     * 页大小
     */
    private int size = 10;
    
    /**
     * 排序方式
     * reward_desc-按奖励降序, reward_asc-按奖励升序, time_desc-按时间降序, time_asc-按时间升序
     */
    private String sortBy = "reward_desc";
    
    /**
     * 获取偏移量
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}
