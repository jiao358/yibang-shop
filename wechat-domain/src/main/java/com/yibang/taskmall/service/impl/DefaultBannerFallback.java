package com.yibang.taskmall.service.impl;

import com.yibang.taskmall.entity.Banner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class DefaultBannerFallback {
    static List<Banner> get(String client) {
        List<Banner> list = new ArrayList<>();
        Banner b = new Banner();
        b.setTitleZh("欢迎来到任务商城");
        b.setTitleEn("Welcome");
        b.setImageUrl("/static/images/banner1.jpg");
        b.setEnableJump(true);
        b.setJumpTarget("/pages/task/task");
        b.setEnabled(true);
        b.setSort(100);
        b.setClient(client == null ? "wx" : client);
        b.setCreatedAt(LocalDateTime.now());
        b.setUpdatedAt(LocalDateTime.now());
        list.add(b);
        return list;
    }
}
