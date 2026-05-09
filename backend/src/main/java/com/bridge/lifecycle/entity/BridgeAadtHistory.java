package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
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
     * 旧AADT值
     */
    private Integer oldAadt;

    /**
     * 新AADT值
     */
    private Integer newAadt;

    /**
     * 旧β系数
     */
    private BigDecimal oldBetaCoef;

    /**
     * 新β系数
     */
    private BigDecimal newBetaCoef;

    /**
     * 变更原因
     */
    private String reason;

    /**
     * 变更时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime changeTime;
}