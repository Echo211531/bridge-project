package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户更新请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateDTO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", required = true)
    private String id;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String roleCode;

    /**
     * 状态: active/frozen
     */
    @Schema(description = "状态")
    private String status;
}