package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yibang.taskmall.entity.DictItem;
import com.yibang.taskmall.mapper.DictItemMapper;
import com.yibang.taskmall.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictItemMapper dictItemMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long TTL_HOURS = 1L;

    private String key(String group) {
        return "wx:dict:" + group;
    }

    @Override
    public List<DictItem> getEnabledItemsByGroup(String groupCode) {
        String k = key(groupCode);
        Object cached = redisTemplate.opsForValue().get(k);
        if (cached instanceof List) {
            return (List<DictItem>) cached;
        }
        List<DictItem> list = dictItemMapper.selectList(new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getGroupCode, groupCode)
                .eq(DictItem::getEnabled, true)
                .orderByDesc(DictItem::getSort));
        redisTemplate.opsForValue().set(k, list, TTL_HOURS, TimeUnit.HOURS);
        return list;
    }

    @Override
    public void evictGroupCache(String groupCode) {
        redisTemplate.delete(key(groupCode));
    }

    @Override
    public List<DictItem> list(String groupCode) {
        return dictItemMapper.selectList(new LambdaQueryWrapper<DictItem>()
                .eq(groupCode != null && !groupCode.isEmpty(), DictItem::getGroupCode, groupCode)
                .orderByDesc(DictItem::getSort));
    }

    @Override
    public DictItem create(DictItem item) {
        dictItemMapper.insert(item);
        evictGroupCache(item.getGroupCode());
        return item;
    }

    @Override
    public DictItem update(DictItem item) {
        dictItemMapper.updateById(item);
        evictGroupCache(item.getGroupCode());
        return item;
    }

    @Override
    public void delete(Long id) {
        DictItem exist = dictItemMapper.selectById(id);
        if (exist != null) {
            dictItemMapper.deleteById(id);
            evictGroupCache(exist.getGroupCode());
        }
    }
}
