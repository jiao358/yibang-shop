package com.yibang.taskmall.service;

import com.yibang.taskmall.entity.DictItem;
import java.util.List;

public interface DictService {
    List<DictItem> getEnabledItemsByGroup(String groupCode);
    void evictGroupCache(String groupCode);
    // 管理端
    List<DictItem> list(String groupCode);
    DictItem create(DictItem item);
    DictItem update(DictItem item);
    void delete(Long id);
}
