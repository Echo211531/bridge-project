package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 保养记录实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("maintain_record")
public class MaintainRecord {

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
     * 保养日期
     */
    private LocalDate recordDate;

    /**
     * 实际费用
     */
    private BigDecimal actualCost;

    /**
     * 工时(小时)
     */
    private BigDecimal manhour;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 保养内容
     */
    private String content;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}