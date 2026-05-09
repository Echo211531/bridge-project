package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 鉴定结论提交DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "鉴定结论提交")
public class ScrapDecisionDTO {

    /**
     * 设备ID
     */
    @Schema(description = "设备ID", required = true)
    private String deviceId;

    /**
     * TCO维修方案成本
     */
    @Schema(description = "TCO维修方案成本")
    private BigDecimal tcoRepair;

    /**
     * TCO更换方案成本
     */
    @Schema(description = "TCO更换方案成本")
    private BigDecimal tcoReplace;

    /**
     * 推荐结论: repair/replace
     */
    @Schema(description = "推荐结论", required = true)
    private String recommendation;

    /**
     * 鉴定备注
     */
    @Schema(description = "鉴定备注")
    private String conclusionNotes;

    /**
     * 鉴定人
     */
    @Schema(description = "鉴定人")
    private String decider;
}