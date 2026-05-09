package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 故障工单VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "故障工单")
public class FaultOrderVO {

    /**
     * 工单ID
     */
    @Schema(description = "工单ID")
    private String id;

    /**
     * 工单编号
     */
    @Schema(description = "工单编号")
    private String orderNo;

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
     * 桥梁名称
     */
    @Schema(description = "桥梁名称")
    private String bridgeName;

    /**
     * 申报日期
     */
    @Schema(description = "申报日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    /**
     * 故障描述
     */
    @Schema(description = "故障描述")
    private String faultDesc;

    /**
     * 维修费用
     */
    @Schema(description = "维修费用")
    private BigDecimal repairCost;

    /**
     * 维修内容
     */
    @Schema(description = "维修内容")
    private String repairContent;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 状态名称
     */
    @Schema(description = "状态名称")
    private String statusName;

    /**
     * 关闭日期
     */
    @Schema(description = "关闭日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate closeDate;

    /**
     * 处理时长(天)
     */
    @Schema(description = "处理时长(天)")
    private Long durationDays;

    /**
     * 处理人
     */
    @Schema(description = "处理人")
    private String handler;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}