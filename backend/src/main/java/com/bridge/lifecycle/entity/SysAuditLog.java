package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("sys_audit_log")
public class SysAuditLog {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 操作人用户名
     */
    private String operator;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作类型: create/update/delete/status_change
     */
    private String action;

    /**
     * 操作目标类型
     */
    private String targetType;

    /**
     * 操作目标ID
     */
    private String targetId;

    /**
     * 操作详情(JSON)
     */
    private String detail;

    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime operateTime;
}