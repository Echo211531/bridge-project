package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 监测推送记录实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("monitoring_record")
public class MonitoringRecord {

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
     * 上报AADT值
     */
    private Integer reportedAadt;

    /**
     * 推送时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime pushedAt;
}