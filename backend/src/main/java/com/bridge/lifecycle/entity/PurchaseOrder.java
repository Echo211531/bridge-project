package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购订单实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("purchase_order")
public class PurchaseOrder {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 设备分类编码
     */
    private String category;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 目标桥梁ID
     */
    private String bridgeId;

    /**
     * 状态: pending/shipping/received
     */
    private String status;

    /**
     * 发货日期
     */
    private LocalDate shipDate;

    /**
     * 入库日期
     */
    private LocalDate receiveDate;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}