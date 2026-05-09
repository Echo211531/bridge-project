package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * TCO决策面板VO（三栏决策面板数据）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "TCO决策面板")
public class TcoDecisionVO {

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String deviceId;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    private String deviceCode;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 当前残值
     */
    @Schema(description = "当前残值")
    private BigDecimal currentResidualValue;

    /**
     * 残值警戒线(%)
     */
    @Schema(description = "残值警戒线(%)")
    private BigDecimal residualThreshold;

    /**
     * 是否低于残值警戒线
     */
    @Schema(description = "是否低于残值警戒线")
    private Boolean belowResidualThreshold;

    // ==================== 维修方案 ====================

    /**
     * 当前维修费用（修复费）
     */
    @Schema(description = "当前维修费用")
    private BigDecimal currentRepairCost;

    /**
     * 修复警戒线(%)
     */
    @Schema(description = "修复警戒线(%)")
    private BigDecimal repairThreshold;

    /**
     * 是否超过修复警戒线
     */
    @Schema(description = "是否超过修复警戒线")
    private Boolean aboveRepairThreshold;

    /**
     * 预计未来保养费用（剩余寿命内）
     */
    @Schema(description = "预计未来保养费用")
    private BigDecimal futureMaintainCost;

    /**
     * 预计未来维修费用（剩余寿命内）
     */
    @Schema(description = "预计未来维修费用")
    private BigDecimal futureRepairCost;

    /**
     * TCO维修方案总成本
     */
    @Schema(description = "TCO维修方案总成本")
    private BigDecimal tcoRepair;

    /**
     * 维修方案明细
     */
    @Schema(description = "维修方案明细")
    private String repairPlanDetail;

    // ==================== 更换方案 ====================

    /**
     * 新设备购置价
     */
    @Schema(description = "新设备购置价")
    private BigDecimal newDevicePrice;

    /**
     * 新设备预计未来保养费用
     */
    @Schema(description = "新设备预计未来保养费用")
    private BigDecimal newFutureMaintainCost;

    /**
     * 当前设备残值（可抵扣）
     */
    @Schema(description = "当前设备残值")
    private BigDecimal salvageValue;

    /**
     * TCO更换方案总成本
     */
    @Schema(description = "TCO更换方案总成本")
    private BigDecimal tcoReplace;

    /**
     * 更换方案明细
     */
    @Schema(description = "更换方案明细")
    private String replacePlanDetail;

    // ==================== 结论 ====================

    /**
     * TCO差值（维修-更换）
     */
    @Schema(description = "TCO差值(维修-更换)")
    private BigDecimal tcoDifference;

    /**
     * 推荐结论
     */
    @Schema(description = "推荐结论")
    private String recommendation;

    /**
     * 推荐结论名称
     */
    @Schema(description = "推荐结论名称")
    private String recommendationName;

    /**
     * 决策依据说明
     */
    @Schema(description = "决策依据说明")
    private String decisionReason;
}