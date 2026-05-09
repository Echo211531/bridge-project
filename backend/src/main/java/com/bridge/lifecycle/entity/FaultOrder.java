package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 故障工单实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("fault_order")
public class FaultOrder {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 工单编号
     */
    private String orderNo;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 申报日期
     */
    private LocalDate reportDate;

    /**
     * 故障描述
     */
    private String faultDesc;

    /**
     * 维修费用
     */
    private BigDecimal repairCost;

    /**
     * 维修内容
     */
    private String repairContent;

    /**
     * 状态: open/processing/closed
     */
    private String status;

    /**
     * 关闭日期
     */
    private LocalDate closeDate;

    /**
     * 处理人
     */
    private String handler;

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