package com.yibang.taskmall.bkcontroller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.entity.Banner;
import com.yibang.taskmall.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bk/banners")
@RequiredArgsConstructor
public class BannerBkController {

    private final BannerService bannerService;

    @GetMapping
    public Result<List<Banner>> list(@RequestParam(required = false, defaultValue = "wx") String client) {
        return Result.success(bannerService.list(client));
    }

    @PostMapping
    public Result<Banner> create(@RequestBody Banner banner) {
        return Result.success(bannerService.create(banner));
    }

    @PutMapping
    public Result<Banner> update(@RequestBody Banner banner) {
        return Result.success(bannerService.update(banner));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return Result.success(true);
    }

    @PostMapping("/cache/evict")
    public Result<Boolean> evict(@RequestParam(defaultValue = "wx") String client) {
        bannerService.evictBannersCache(client);
        return Result.success(true);
    }
}
