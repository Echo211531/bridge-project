package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报废鉴定实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("scrap_decision")
public class ScrapDecision {

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
     * TCO维修方案成本
     */
    private BigDecimal tcoRepair;

    /**
     * TCO更换方案成本
     */
    private BigDecimal tcoReplace;

    /**
     * 推荐结论: repair/replace
     */
    private String recommendation;

    /**
     * 鉴定备注
     */
    private String conclusionNotes;

    /**
     * 状态: pending_approval/approved/rejected
     */
    private String status;

    /**
     * 鉴定日期
     */
    private LocalDate decisionDate;

    /**
     * 鉴定人
     */
    private String decider;

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