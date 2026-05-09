package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计概览VO（8项关键率）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "统计概览")
public class StatsOverviewVO {

    /**
     * 设备总数
     */
    @Schema(description = "设备总数")
    private Long totalDevices;

    /**
     * 设备在用率(%)
     */
    @Schema(description = "设备在用率(%)")
    private BigDecimal inUseRate;

    /**
     * 设备完好率(%)
     */
    @Schema(description = "设备完好率(%)")
    private BigDecimal goodConditionRate;

    /**
     * 年故障率(%)
     */
    @Schema(description = "年故障率(%)")
    private BigDecimal faultRate;

    /**
     * 保养完成率(%)
     */
    @Schema(description = "保养完成率(%)")
    private BigDecimal maintainCompletionRate;

    /**
     * 工单关闭率(%)
     */
    @Schema(description = "工单关闭率(%)")
    private BigDecimal orderCloseRate;

    /**
     * MTBF(小时)
     */
    @Schema(description = "MTBF(小时)")
    private BigDecimal mtbf;

    /**
     * MTTR(小时)
     */
    @Schema(description = "MTTR(小时)")
    private BigDecimal mttr;

    /**
     * 累计运维成本
     */
    @Schema(description = "累计运维成本")
    private BigDecimal totalOperationCost;

    /**
     * 本月新增成本
     */
    @Schema(description = "本月新增成本")
    private BigDecimal monthlyCost;
}