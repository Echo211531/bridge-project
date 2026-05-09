package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 设备分类统计VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "设备分类统计")
public class CategoryStatsVO {

    @Schema(description = "分类编码")
    private String categoryCode;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "设备数量")
    private Long deviceCount;

    @Schema(description = "占比(%)")
    private BigDecimal percentage;

    @Schema(description = "平均TCO")
    private BigDecimal avgTco;
}