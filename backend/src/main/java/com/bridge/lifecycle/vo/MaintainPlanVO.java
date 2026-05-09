package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 保养计划VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "保养计划")
public class MaintainPlanVO {

    /**
     * 计划ID
     */
    @Schema(description = "计划ID")
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
     * 桥梁名称
     */
    @Schema(description = "桥梁名称")
    private String bridgeName;

    /**
     * 计划保养日期
     */
    @Schema(description = "计划保养日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;

    /**
     * 实际周期天数
     */
    @Schema(description = "实际周期天数")
    private Integer cycleDays;

    /**
     * 标准周期天数
     */
    @Schema(description = "标准周期天数")
    private Integer standardCycleDays;

    /**
     * 动态调整系数(α×β)
     */
    @Schema(description = "动态调整系数")
    private String dynamicFactor;

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}