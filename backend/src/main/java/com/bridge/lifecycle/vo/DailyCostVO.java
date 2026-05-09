package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 每日费用趋势VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "每日费用趋势")
public class DailyCostVO {

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "保养费用")
    private BigDecimal maintainCost;

    @Schema(description = "维修费用")
    private BigDecimal repairCost;

    @Schema(description = "总费用")
    private BigDecimal totalCost;
}