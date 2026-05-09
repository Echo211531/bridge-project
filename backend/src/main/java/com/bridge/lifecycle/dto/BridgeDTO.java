package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 桥梁创建请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "桥梁创建请求")
public class BridgeDTO {

    /**
     * 桥梁编码
     */
    @Schema(description = "桥梁编码", required = true)
    private String bridgeCode;

    /**
     * 桥梁名称
     */
    @Schema(description = "桥梁名称", required = true)
    private String bridgeName;

    /**
     * 气候带: cold/severe_cold/temperate/humid/coastal
     */
    @Schema(description = "气候带", required = true)
    private String climateZone;

    /**
     * 年平均日交通量(AADT)
     */
    @Schema(description = "年平均日交通量", required = true)
    private Integer aadt;

    /**
     * 地理位置
     */
    @Schema(description = "地理位置")
    private String location;

    /**
     * 建成年份
     */
    @Schema(description = "建成年份")
    private Integer buildYear;

    /**
     * 桥梁长度(米)
     */
    @Schema(description = "桥梁长度(米)")
    private BigDecimal length;
}