package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 保养记录上报请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "保养记录上报请求")
public class MaintainRecordDTO {

    /**
     * 设备ID
     */
    @Schema(description = "设备ID", required = true)
    private String deviceId;

    /**
     * 保养日期
     */
    @Schema(description = "保养日期", required = true)
    private LocalDate recordDate;

    /**
     * 实际费用
     */
    @Schema(description = "实际费用", required = true)
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
}