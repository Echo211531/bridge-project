package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设备更新请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "设备更新请求")
public class DeviceUpdateDTO {

    /**
     * 设备ID
     */
    @Schema(description = "设备ID", required = true)
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 生产厂商
     */
    @Schema(description = "生产厂商")
    private String manufacturer;

    /**
     * 型号规格
     */
    @Schema(description = "型号规格")
    private String model;

    /**
     * 桥梁位置
     */
    @Schema(description = "桥梁位置")
    private String locationOnBridge;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;
}