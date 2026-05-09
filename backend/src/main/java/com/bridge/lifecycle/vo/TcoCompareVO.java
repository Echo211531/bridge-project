package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * TCO对比统计VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "TCO对比统计")
public class TcoCompareVO {

    @Schema(description = "分类编码")
    private String categoryCode;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "平均购置成本")
    private BigDecimal avgPurchaseCost;

    @Schema(description = "平均保养成本")
    private BigDecimal avgMaintainCost;

    @Schema(description = "平均维修成本")
    private BigDecimal avgRepairCost;

    @Schema(description = "平均TCO")
    private BigDecimal avgTco;

    @Schema(description = "设备数量")
    private Long deviceCount;
}