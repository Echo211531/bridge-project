package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设备创建请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "设备创建请求")
public class DeviceDTO {

    /**
     * 设备名称
     */
    @Schema(description = "设备名称", required = true)
    private String deviceName;

    /**
     * 设备分类编码
     */
    @Schema(description = "设备分类编码", required = true)
    private String category;

    /**
     * 所属桥梁ID
     */
    @Schema(description = "所属桥梁ID", required = true)
    private String bridgeId;

    /**
     * 购置成本
     */
    @Schema(description = "购置成本", required = true)
    private BigDecimal purchaseCost;

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
     * 采购日期
     */
    @Schema(description = "采购日期")
    private LocalDate purchaseDate;

    /**
     * 投入使用日期
     */
    @Schema(description = "投入使用日期")
    private LocalDate inUseDate;

    /**
     * 保修年限
     */
    @Schema(description = "保修年限")
    private BigDecimal warrantyYears;

    /**
     * 桥梁位置
     */
    @Schema(description = "桥梁位置")
    private String locationOnBridge;
}