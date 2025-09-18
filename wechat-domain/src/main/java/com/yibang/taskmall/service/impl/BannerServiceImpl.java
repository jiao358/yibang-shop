package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yibang.taskmall.entity.Banner;
import com.yibang.taskmall.mapper.BannerMapper;
import com.yibang.taskmall.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long TTL_HOURS = 1L;

    private String key(String client) { return "wx:dict:banners:" + client; }

    @Override
    public List<Banner> getEnabledBanners(String client) {
        String k = key(client);
        Object cached = redisTemplate.opsForValue().get(k);
        if (cached instanceof List) {
            return (List<Banner>) cached;
        }
        LocalDateTime now = LocalDateTime.now();
        List<Banner> list = bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getClient, client)
                .eq(Banner::getEnabled, true)
                .and(w -> w.isNull(Banner::getStartTime).or().le(Banner::getStartTime, now))
                .and(w -> w.isNull(Banner::getEndTime).or().ge(Banner::getEndTime, now))
                .orderByDesc(Banner::getSort));
        if (list == null || list.isEmpty()) {
            list = DefaultBannerFallback.get(client);
        }
        redisTemplate.opsForValue().set(k, list, TTL_HOURS, TimeUnit.HOURS);
        return list;
    }

    @Override
    public void evictBannersCache(String client) {
        redisTemplate.delete(key(client));
    }

    @Override
    public List<Banner> list(String client) {
        return bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(client != null && !client.isEmpty(), Banner::getClient, client)
                .orderByDesc(Banner::getSort));
    }

    @Override
    public Banner create(Banner banner) {
        bannerMapper.insert(banner);
        evictBannersCache(banner.getClient());
        return banner;
    }

    @Override
    public Banner update(Banner banner) {
        bannerMapper.updateById(banner);
        evictBannersCache(banner.getClient());
        return banner;
    }

    @Override
    public void delete(Long id) {
        Banner exist = bannerMapper.selectById(id);
        if (exist != null) {
            bannerMapper.deleteById(id);
            evictBannersCache(exist.getClient());
        }
    }
}
