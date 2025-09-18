package com.yibang.taskmall.bkservice.impl;

import com.yibang.taskmall.bkdto.response.DashboardChartResponse;
import com.yibang.taskmall.bkdto.response.DashboardOverviewResponse;
import com.yibang.taskmall.bkservice.DashboardBkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DashboardBkServiceImpl implements DashboardBkService {

    @Override
    public DashboardOverviewResponse getOverview() {
        DashboardOverviewResponse resp = new DashboardOverviewResponse();
        // 占位默认值，避免空指针
        resp.setNewUsers(0L);
        resp.setActiveUsers(0L);
        resp.setCompletedTasks(0L);
        resp.setWithdrawRequests(0L);
        resp.setGmv(0L);
        return resp;
    }

    @Override
    public DashboardChartResponse getUserGrowthChart(Integer days) {
        return emptyChart(days, "users");
    }

    @Override
    public DashboardChartResponse getTaskCompletionChart(Integer days) {
        return emptyChart(days, "tasks");
    }

    @Override
    public DashboardChartResponse getRevenueChart(Integer days, String type) {
        return emptyChart(days, "revenue");
    }

    @Override
    public DashboardChartResponse getUserLevelDistribution() {
        DashboardChartResponse resp = new DashboardChartResponse();
        resp.setLegend(java.util.Arrays.asList("L1", "L2", "L3"));
        resp.setXAxis(java.util.Collections.singletonList("levels"));
        java.util.List<java.util.Map<String, Object>> series = new java.util.ArrayList<>();
        series.add(mapOf("name", "L1", "data", java.util.Collections.singletonList(0)));
        series.add(mapOf("name", "L2", "data", java.util.Collections.singletonList(0)));
        series.add(mapOf("name", "L3", "data", java.util.Collections.singletonList(0)));
        resp.setSeries(series);
        return resp;
    }

    @Override
    public DashboardChartResponse getTaskTypeStats() {
        DashboardChartResponse resp = new DashboardChartResponse();
        resp.setLegend(java.util.Arrays.asList("share", "follow", "comment"));
        resp.setXAxis(java.util.Collections.singletonList("types"));
        java.util.List<java.util.Map<String, Object>> series = new java.util.ArrayList<>();
        series.add(mapOf("name", "share", "data", java.util.Collections.singletonList(0)));
        series.add(mapOf("name", "follow", "data", java.util.Collections.singletonList(0)));
        series.add(mapOf("name", "comment", "data", java.util.Collections.singletonList(0)));
        resp.setSeries(series);
        return resp;
    }

    @Override
    public DashboardChartResponse getWithdrawTrend(Integer days) {
        return emptyChart(days, "withdraw");
    }

    @Override
    public Map<String, Object> getTopTasks(Integer limit) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getTopUsers(Integer limit, String sortBy) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getRealtimeData() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", System.currentTimeMillis());
        map.put("qps", 0);
        map.put("onlineUsers", 0);
        return map;
    }

    private DashboardChartResponse emptyChart(Integer days, String seriesName) {
        DashboardChartResponse resp = new DashboardChartResponse();
        java.util.List<String> xAxis = new java.util.ArrayList<>();
        java.util.List<Integer> data = new java.util.ArrayList<>();
        int n = days != null && days > 0 ? days : 7;
        for (int i = n; i > 0; i--) {
            xAxis.add(LocalDate.now().minusDays(i - 1).toString());
            data.add(0);
        }
        resp.setXAxis(xAxis);
        resp.setLegend(java.util.Collections.singletonList(seriesName));
        java.util.List<java.util.Map<String, Object>> series = new java.util.ArrayList<>();
        series.add(mapOf("name", seriesName, "data", data));
        resp.setSeries(series);
        return resp;
    }

    private java.util.Map<String, Object> mapOf(Object... kv) {
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }
}


