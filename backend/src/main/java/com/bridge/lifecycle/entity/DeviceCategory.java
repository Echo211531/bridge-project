package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备分类实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("device_category")
public class DeviceCategory {

    /**
     * 分类编码(主键)
     */
    @TableId(type = IdType.INPUT)
    private String code;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 设计寿命(年)
     */
    private Integer designLifeYears;

    /**
     * 标准保养周期(天)
     */
    private Integer maintainCycleDays;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}