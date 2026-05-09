package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购订单VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "采购订单")
public class PurchaseOrderVO {

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private String id;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

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
     * 数量
     */
    @Schema(description = "数量")
    private Integer quantity;

    /**
     * 单价
     */
    @Schema(description = "单价")
    private BigDecimal unitPrice;

    /**
     * 总金额
     */
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

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

    /**
     * 桥梁名称
     */
    @Schema(description = "桥梁名称")
    private String bridgeName;

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
     * 发货日期
     */
    @Schema(description = "发货日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate shipDate;

    /**
     * 入库日期
     */
    @Schema(description = "入库日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;

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