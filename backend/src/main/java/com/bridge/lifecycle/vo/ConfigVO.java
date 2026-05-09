package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "系统配置")
public class ConfigVO {

    /**
     * 配置ID
     */
    @Schema(description = "配置ID")
    private String id;

    /**
     * 配置键
     */
    @Schema(description = "配置键")
    private String configKey;

    /**
     * 配置值
     */
    @Schema(description = "配置值")
    private String configValue;

    /**
     * 配置描述
     */
    @Schema(description = "配置描述")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}