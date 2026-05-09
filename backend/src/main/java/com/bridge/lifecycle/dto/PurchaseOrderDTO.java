package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购订单创建请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "采购订单创建请求")
public class PurchaseOrderDTO {

    /**
     * 设备分类编码
     */
    @Schema(description = "设备分类编码", required = true)
    private String category;

    /**
     * 数量
     */
    @Schema(description = "数量", required = true)
    private Integer quantity;

    /**
     * 单价
     */
    @Schema(description = "单价", required = true)
    private BigDecimal unitPrice;

    /**
     * 供应商
     */
    @Schema(description = "供应商")
    private String supplier;

    /**
     * 目标桥梁ID
     */
    @Schema(description = "目标桥梁ID")
    private String bridgeId;
}