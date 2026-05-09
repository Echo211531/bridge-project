package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 故障申报请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "故障申报请求")
public class FaultOrderDTO {

    /**
     * 设备ID
     */
    @Schema(description = "设备ID", required = true)
    private String deviceId;

    /**
     * 故障描述
     */
    @Schema(description = "故障描述", required = true)
    private String faultDesc;

    /**
     * 申报日期
     */
    @Schema(description = "申报日期")
    private LocalDate reportDate;
}