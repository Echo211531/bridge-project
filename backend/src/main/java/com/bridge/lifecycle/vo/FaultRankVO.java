package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 故障排行VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "故障排行")
public class FaultRankVO {

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "故障次数")
    private Integer faultCount;

    @Schema(description = "累计维修费用")
    private BigDecimal totalRepairCost;

    @Schema(description = "排名")
    private Integer rank;
}