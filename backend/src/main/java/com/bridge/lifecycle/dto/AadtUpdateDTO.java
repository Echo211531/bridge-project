package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AADT更新请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "AADT更新请求")
public class AadtUpdateDTO {

    /**
     * 桥梁ID
     */
    @Schema(description = "桥梁ID", required = true)
    private String bridgeId;

    /**
     * 新的AADT值
     */
    @Schema(description = "新的AADT值", required = true)
    private Integer newAadt;

    /**
     * 变更原因
     */
    @Schema(description = "变更原因")
    private String reason;
}