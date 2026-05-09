package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 生命周期事件VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "生命周期事件")
public class LifecycleEventVO {

    /**
     * 事件ID
     */
    @Schema(description = "事件ID")
    private String id;

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String deviceId;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    private String deviceCode;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 事件类型
     */
    @Schema(description = "事件类型")
    private String eventType;

    /**
     * 事件类型名称
     */
    @Schema(description = "事件类型名称")
    private String eventTypeName;

    /**
     * 事件标题
     */
    @Schema(description = "事件标题")
    private String title;

    /**
     * 事件描述
     */
    @Schema(description = "事件描述")
    private String description;

    /**
     * 事件成本
     */
    @Schema(description = "事件成本")
    private BigDecimal cost;

    /**
     * 操作人
     */
    @Schema(description = "操作人")
    private String operator;

    /**
     * 事件时间
     */
    @Schema(description = "事件时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}