package com.yibang.taskmall.bkservice;

import com.yibang.taskmall.bkdto.response.DashboardChartResponse;
import com.yibang.taskmall.bkdto.response.DashboardOverviewResponse;

import java.util.Map;

public interface DashboardBkService {
    DashboardOverviewResponse getOverview();

    DashboardChartResponse getUserGrowthChart(Integer days);

    DashboardChartResponse getTaskCompletionChart(Integer days);

    DashboardChartResponse getRevenueChart(Integer days, String type);

    DashboardChartResponse getUserLevelDistribution();

    DashboardChartResponse getTaskTypeStats();

    DashboardChartResponse getWithdrawTrend(Integer days);

    Map<String, Object> getTopTasks(Integer limit);

    Map<String, Object> getTopUsers(Integer limit, String sortBy);

    Map<String, Object> getRealtimeData();
}


