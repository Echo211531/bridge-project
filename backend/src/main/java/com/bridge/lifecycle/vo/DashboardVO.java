package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表盘统计数据VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "仪表盘统计数据")
public class DashboardVO {

    /**
     * 概览统计（8项关键率）
     */
    @Schema(description = "概览统计")
    private StatsOverviewVO overview;

    /**
     * 30天费用趋势
     */
    @Schema(description = "30天费用趋势")
    private List<DailyCostVO> costTrend;

    /**
     * 设备分类分布
     */
    @Schema(description = "设备分类分布")
    private List<CategoryStatsVO> categoryDistribution;

    /**
     * 故障排行
     */
    @Schema(description = "故障排行")
    private List<FaultRankVO> faultRanking;

    /**
     * 待办事项
     */
    @Schema(description = "待办事项")
    private List<TodoItemVO> todoItems;

    /**
     * TCO对比统计
     */
    @Schema(description = "TCO对比统计")
    private List<TcoCompareVO> tcoComparison;
}