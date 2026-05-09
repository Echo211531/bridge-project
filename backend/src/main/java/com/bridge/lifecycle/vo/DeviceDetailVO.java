package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备详情VO（含残值计算详情）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "设备详情")
public class DeviceDetailVO {

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String id;

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
     * 设备分类编码
     */
    @Schema(description = "设备分类编码")
    private String category;

    /**
     * 设备分类名称
     */
    @Schema(description = "设备分类名称")
    private String categoryName;

    /**
     * 设计寿命(年)
     */
    @Schema(description = "设计寿命(年)")
    private Integer designLifeYears;

    /**
     * 标准保养周期(天)
     */
    @Schema(description = "标准保养周期(天)")
    private Integer maintainCycleDays;

    /**
     * 所属桥梁ID
     */
    @Schema(description = "所属桥梁ID")
    private String bridgeId;

    /**
     * 所属桥梁名称
     */
    @Schema(description = "所属桥梁名称")
    private String bridgeName;

    /**
     * α气候系数
     */
    @Schema(description = "α气候系数")
    private BigDecimal alphaCoef;

    /**
     * βAADT系数
     */
    @Schema(description = "βAADT系数")
    private BigDecimal betaCoef;

    /**
     * 动态保养周期(天)
     */
    @Schema(description = "动态保养周期(天)")
    private Integer dynamicMaintainCycle;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 状态名称
     */
    @Schema(description = "状态名称")
    private String statusName;

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
     * 残值率(百分比)
     */
    @Schema(description = "残值率(百分比)")
    private BigDecimal residualRate;

    /**
     * 已使用年限
     */
    @Schema(description = "已使用年限")
    private BigDecimal usedYears;

    /**
     * 剩余寿命(年)
     */
    @Schema(description = "剩余寿命(年)")
    private BigDecimal remainingLife;

    /**
     * 生产厂商
     */
    @Schema(description = "生产厂商")
    private String manufacturer;

    /**
     * 型号规格
     */
    @Schema(description = "型号规格")
    private String model;

    /**
     * 保修年限
     */
    @Schema(description = "保修年限")
    private BigDecimal warrantyYears;

    /**
     * 投入使用日期
     */
    @Schema(description = "投入使用日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inUseDate;

    /**
     * 采购日期
     */
    @Schema(description = "采购日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    /**
     * 桥梁位置
     */
    @Schema(description = "桥梁位置")
    private String locationOnBridge;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}