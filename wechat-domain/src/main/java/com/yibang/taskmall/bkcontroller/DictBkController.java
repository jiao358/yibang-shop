package com.yibang.taskmall.bkcontroller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.entity.DictItem;
import com.yibang.taskmall.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bk/dicts")
@RequiredArgsConstructor
public class DictBkController {

    private final DictService dictService;

    @GetMapping
    public Result<List<DictItem>> list(@RequestParam(required = false) String group) {
        return Result.success(dictService.list(group));
    }

    @PostMapping
    public Result<DictItem> create(@RequestBody DictItem item) {
        return Result.success(dictService.create(item));
    }

    @PutMapping
    public Result<DictItem> update(@RequestBody DictItem item) {
        return Result.success(dictService.update(item));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        dictService.delete(id);
        return Result.success(true);
    }

    @PostMapping("/{group}/cache/evict")
    public Result<Boolean> evict(@PathVariable String group) {
        dictService.evictGroupCache(group);
        return Result.success(true);
    }
}
