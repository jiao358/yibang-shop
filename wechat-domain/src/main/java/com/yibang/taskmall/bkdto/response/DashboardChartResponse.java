package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardChartResponse {
    @Schema(description = "X轴标签")
    private List<String> xAxis;

    @Schema(description = "系列数据")
    private List<Map<String, Object>> series;

    @Schema(description = "图例")
    private List<String> legend;
}


