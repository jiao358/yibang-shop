package com.yibang.taskmall.service.impl;

import com.yibang.taskmall.entity.DictItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class DefaultDictFallback {
    static List<DictItem> get(String groupCode) {
        List<DictItem> list = new ArrayList<>();
        switch (groupCode) {
            case "task_type":
                list.add(item(groupCode, "ad", "广告任务", "Ad", 100));
                list.add(item(groupCode, "video", "视频任务", "Video", 90));
                list.add(item(groupCode, "app_install", "应用安装", "App Install", 80));
                list.add(item(groupCode, "survey", "问卷调查", "Survey", 70));
                list.add(item(groupCode, "share", "分享任务", "Share", 60));
                break;
            case "user_level":
                list.add(item(groupCode, "normal", "普通用户", "Normal", 100));
                list.add(item(groupCode, "signed", "签约用户", "Signed", 90));
                list.add(item(groupCode, "vip", "VIP用户", "VIP", 80));
                break;
            case "commission_level":
                list.add(item(groupCode, "low", "低", "Low", 100));
                list.add(item(groupCode, "medium", "中", "Medium", 90));
                list.add(item(groupCode, "high", "高", "High", 80));
                break;
            case "withdraw_status":
                list.add(item(groupCode, "pending", "待处理", "Pending", 100));
                list.add(item(groupCode, "processing", "处理中", "Processing", 90));
                list.add(item(groupCode, "completed", "已完成", "Completed", 80));
                list.add(item(groupCode, "failed", "失败", "Failed", 70));
                list.add(item(groupCode, "cancelled", "已取消", "Cancelled", 60));
                break;
            case "order_status":
                list.add(item(groupCode, "pending", "待支付", "Pending", 100));
                list.add(item(groupCode, "paid", "已支付", "Paid", 90));
                list.add(item(groupCode, "shipped", "已发货", "Shipped", 80));
                list.add(item(groupCode, "received", "已收货", "Received", 70));
                list.add(item(groupCode, "completed", "已完成", "Completed", 60));
                list.add(item(groupCode, "refunding", "退款中", "Refunding", 50));
                list.add(item(groupCode, "refunded", "已退款", "Refunded", 40));
                break;
            case "notify_type":
                list.add(item(groupCode, "system", "系统通知", "System", 100));
                list.add(item(groupCode, "task", "任务通知", "Task", 90));
                break;
            case "agreements":
                list.add(item(groupCode, "privacy", "隐私协议", "Privacy Policy", 100));
                list.add(item(groupCode, "user_guide", "用户须知", "User Guide", 90));
                break;
            default:
                break;
        }
        return list;
    }

    private static DictItem item(String group, String code, String zh, String en, int sort) {
        DictItem d = new DictItem();
        d.setGroupCode(group);
        d.setItemCode(code);
        d.setLabelZh(zh);
        d.setLabelEn(en);
        d.setEnabled(true);
        d.setSort(sort);
        d.setCreatedAt(LocalDateTime.now());
        d.setUpdatedAt(LocalDateTime.now());
        return d;
    }
}
