package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 保养记录VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "保养记录")
public class MaintainRecordVO {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
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
     * 保养日期
     */
    @Schema(description = "保养日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    /**
     * 实际费用
     */
    @Schema(description = "实际费用")
    private BigDecimal actualCost;

    /**
     * 工时(小时)
     */
    @Schema(description = "工时(小时)")
    private BigDecimal manhour;

    /**
     * 操作人
     */
    @Schema(description = "操作人")
    private String operator;

    /**
     * 保养内容
     */
    @Schema(description = "保养内容")
    private String content;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}