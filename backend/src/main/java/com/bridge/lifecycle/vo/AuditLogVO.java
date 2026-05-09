package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "审计日志")
public class AuditLogVO {

    /**
     * 日志ID
     */
    @Schema(description = "日志ID")
    private String id;

    /**
     * 操作人用户名
     */
    @Schema(description = "操作人用户名")
    private String operator;

    /**
     * 操作人姓名
     */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private String action;

    /**
     * 操作类型名称
     */
    @Schema(description = "操作类型名称")
    private String actionName;

    /**
     * 操作目标类型
     */
    @Schema(description = "操作目标类型")
    private String targetType;

    /**
     * 操作目标ID
     */
    @Schema(description = "操作目标ID")
    private String targetId;

    /**
     * 操作详情(JSON)
     */
    @Schema(description = "操作详情")
    private String detail;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;
}