package com.yibang.taskmall.service;

import com.yibang.taskmall.entity.Banner;
import java.util.List;

public interface BannerService {
    List<Banner> getEnabledBanners(String client);
    void evictBannersCache(String client);
    // 管理端
    List<Banner> list(String client);
    Banner create(Banner banner);
    Banner update(Banner banner);
    void delete(Long id);
}
