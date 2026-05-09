package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 待鉴定设备候选VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "待鉴定设备候选")
public class ScrapCandidateVO {

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
     * 设备分类
     */
    @Schema(description = "设备分类")
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
     * 残值估算
     */
    @Schema(description = "残值估算")
    private BigDecimal residualValue;

    /**
     * 残值率(%)
     */
    @Schema(description = "残值率(%)")
    private BigDecimal residualRate;

    /**
     * 已使用年限
     */
    @Schema(description = "已使用年限")
    private BigDecimal usedYears;

    /**
     * 设计寿命(年)
     */
    @Schema(description = "设计寿命(年)")
    private Integer designLifeYears;

    /**
     * 累计维修次数
     */
    @Schema(description = "累计维修次数")
    private Integer faultCount;

    /**
     * 累计维修费用
     */
    @Schema(description = "累计维修费用")
    private BigDecimal totalRepairCost;

    /**
     * 累计保养费用
     */
    @Schema(description = "累计保养费用")
    private BigDecimal totalMaintainCost;

    /**
     * 累计TCO
     */
    @Schema(description = "累计TCO")
    private BigDecimal totalTco;

    /**
     * 投入使用日期
     */
    @Schema(description = "投入使用日期")
    private LocalDate inUseDate;

    /**
     * 预警类型
     */
    @Schema(description = "预警类型")
    private String alertType;

    /**
     * 预警说明
     */
    @Schema(description = "预警说明")
    private String alertMessage;
}