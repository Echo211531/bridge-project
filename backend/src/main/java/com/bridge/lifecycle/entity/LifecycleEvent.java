package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 生命周期事件实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("lifecycle_event")
public class LifecycleEvent {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 事件类型: purchase/commission/maintain/repair/inspect/scrap
     */
    private String eventType;

    /**
     * 事件标题
     */
    private String title;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 事件成本
     */
    private BigDecimal cost;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 事件时间
     */
    private LocalDateTime eventTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}