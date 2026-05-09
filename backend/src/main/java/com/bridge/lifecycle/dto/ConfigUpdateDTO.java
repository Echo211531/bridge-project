package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 配置更新DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "配置更新请求")
public class ConfigUpdateDTO {

    /**
     * 配置键
     */
    @Schema(description = "配置键", required = true)
    private String configKey;

    /**
     * 配置值
     */
    @Schema(description = "配置值", required = true)
    private String configValue;
}