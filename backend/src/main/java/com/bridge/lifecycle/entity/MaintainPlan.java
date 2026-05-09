package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 保养计划实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("maintain_plan")
public class MaintainPlan {

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
     * 计划保养日期
     */
    private LocalDate planDate;

    /**
     * 实际周期天数(动态计算后)
     */
    private Integer cycleDays;

    /**
     * 状态: pending/completed/overdue
     */
    private String status;

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