package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色信息VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "角色信息")
public class RoleVO {

    /**
     * 角色ID
     */
    @Schema(description = "角色ID")
    private String id;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String code;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;

    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String description;
}