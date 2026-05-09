package com.bridge.lifecycle.controller;

import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.service.StatsService;
import com.bridge.lifecycle.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 统计分析控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "统计分析", description = "仪表盘8项关键率、费用趋势、故障排行、TCO对比")
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * 获取仪表盘完整数据
     */
    @Operation(summary = "获取仪表盘完整数据")
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        return Result.success(statsService.getDashboard());
    }

    /**
     * 获取统计概览
     */
    @Operation(summary = "获取统计概览")
    @GetMapping("/overview")
    public Result<StatsOverviewVO> getOverview() {
        return Result.success(statsService.getOverview());
    }

    /**
     * 设备在用率
     */
    @Operation(summary = "设备在用率")
    @GetMapping("/in-use-rate")
    public Result<BigDecimal> getInUseRate() {
        return Result.success(statsService.calculateInUseRate());
    }

    /**
     * 设备完好率
     */
    @Operation(summary = "设备完好率")
    @GetMapping("/good-condition-rate")
    public Result<BigDecimal> getGoodConditionRate() {
        return Result.success(statsService.calculateGoodConditionRate());
    }

    /**
     * 年故障率
     */
    @Operation(summary = "年故障率")
    @GetMapping("/fault-rate")
    public Result<BigDecimal> getFaultRate(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(statsService.calculateFaultRate(year));
    }

    /**
     * 保养完成率
     */
    @Operation(summary = "保养完成率")
    @GetMapping("/maintain-completion-rate")
    public Result<BigDecimal> getMaintainCompletionRate(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(statsService.calculateMaintainCompletionRate(year));
    }

    /**
     * 工单关闭率
     */
    @Operation(summary = "工单关闭率")
    @GetMapping("/order-close-rate")
    public Result<BigDecimal> getOrderCloseRate(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(statsService.calculateOrderCloseRate(year));
    }

    /**
     * MTBF
     */
    @Operation(summary = "MTBF（平均故障间隔时间）")
    @GetMapping("/mtbf")
    public Result<BigDecimal> getMTBF(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(statsService.calculateMTBF(year));
    }

    /**
     * MTTR
     */
    @Operation(summary = "MTTR（平均修复时间）")
    @GetMapping("/mttr")
    public Result<BigDecimal> getMTTR(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(statsService.calculateMTTR(year));
    }

    /**
     * 累计运维成本
     */
    @Operation(summary = "累计运维成本")
    @GetMapping("/total-operation-cost")
    public Result<BigDecimal> getTotalOperationCost() {
        return Result.success(statsService.calculateTotalOperationCost());
    }

    /**
     * 30天费用趋势
     */
    @Operation(summary = "30天费用趋势")
    @GetMapping("/cost-trend")
    public Result<List<DailyCostVO>> get30DayCostTrend() {
        return Result.success(statsService.get30DayCostTrend());
    }

    /**
     * 待办事项
     */
    @Operation(summary = "待办事项")
    @GetMapping("/todo-items")
    public Result<List<TodoItemVO>> getTodoItems() {
        return Result.success(statsService.getTodoItems());
    }

    /**
     * 设备分类分布
     */
    @Operation(summary = "设备分类分布")
    @GetMapping("/category-distribution")
    public Result<List<CategoryStatsVO>> getCategoryDistribution() {
        return Result.success(statsService.getCategoryDistribution());
    }

    /**
     * 故障排行
     */
    @Operation(summary = "故障排行")
    @GetMapping("/fault-ranking")
    public Result<List<FaultRankVO>> getFaultRanking(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(statsService.getFaultRanking(limit));
    }

    /**
     * TCO对比
     */
    @Operation(summary = "TCO对比统计")
    @GetMapping("/tco-comparison")
    public Result<List<TcoCompareVO>> getTcoComparison() {
        return Result.success(statsService.getTcoComparison());
    }
}
