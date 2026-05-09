package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 工单关闭请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "工单关闭请求")
public class FaultCloseDTO {

    /**
     * 工单ID
     */
    @Schema(description = "工单ID", required = true)
    private String orderId;

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
     * 关闭日期
     */
    @Schema(description = "关闭日期")
    private LocalDate closeDate;

    /**
     * 处理人
     */
    @Schema(description = "处理人")
    private String handler;
}