package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 生命周期汇总VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "生命周期汇总")
public class LifecycleSummaryVO {

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
     * 设备分类名称
     */
    @Schema(description = "设备分类名称")
    private String categoryName;

    /**
     * 桥梁名称
     */
    @Schema(description = "桥梁名称")
    private String bridgeName;

    /**
     * 购置成本
     */
    @Schema(description = "购置成本")
    private BigDecimal purchaseCost;

    /**
     * 累计保养费用
     */
    @Schema(description = "累计保养费用")
    private BigDecimal totalMaintainCost;

    /**
     * 累计维修费用
     */
    @Schema(description = "累计维修费用")
    private BigDecimal totalRepairCost;

    /**
     * 累计TCO
     */
    @Schema(description = "累计TCO")
    private BigDecimal totalTco;

    /**
     * 保养次数
     */
    @Schema(description = "保养次数")
    private Integer maintainCount;

    /**
     * 维修次数
     */
    @Schema(description = "维修次数")
    private Integer repairCount;

    /**
     * 当前残值
     */
    @Schema(description = "当前残值")
    private BigDecimal currentResidualValue;

    /**
     * 当前状态
     */
    @Schema(description = "当前状态")
    private String currentStatus;

    /**
     * 状态名称
     */
    @Schema(description = "状态名称")
    private String statusName;

    /**
     * TCO占比分析
     */
    @Schema(description = "TCO占比分析")
    private String tcoAnalysis;
}