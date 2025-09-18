package com.yibang.taskmall.controller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.entity.Banner;
import com.yibang.taskmall.entity.DictItem;
import com.yibang.taskmall.service.BannerService;
import com.yibang.taskmall.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicDictController {

    private final DictService dictService;
    private final BannerService bannerService;

    @GetMapping("/dicts/{group}")
    public Result<List<DictItem>> getDict(@PathVariable("group") String group) {
        return Result.success(dictService.getEnabledItemsByGroup(group));
    }

    @GetMapping("/banners")
    public Result<List<Banner>> getBanners(@RequestParam(defaultValue = "wx") String client) {
        return Result.success(bannerService.getEnabledBanners(client));
    }
}
