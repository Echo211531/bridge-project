package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AADT历史记录实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("bridge_aadt_history")
public class BridgeAadtHistory {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 桥梁ID
     */
    private String bridgeId;

    /**
     * AADT值
     */
    private Integer aadt;

    /**
     * 数据来源
     */
    private String source;

    /**
     * 变更时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime changedAt;
}