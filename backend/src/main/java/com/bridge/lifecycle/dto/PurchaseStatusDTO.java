package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 订单状态更新请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "订单状态更新请求")
public class PurchaseStatusDTO {

    /**
     * 订单ID
     */
    @Schema(description = "订单ID", required = true)
    private String orderId;

    /**
     * 新状态
     */
    @Schema(description = "新状态", required = true)
    private String status;

    /**
     * 发货日期
     */
    @Schema(description = "发货日期")
    private LocalDate shipDate;

    /**
     * 入库日期
     */
    @Schema(description = "入库日期")
    private LocalDate receiveDate;
}