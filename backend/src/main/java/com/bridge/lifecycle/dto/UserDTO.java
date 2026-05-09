package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户创建请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "用户创建请求")
public class UserDTO {

    /**
     * 用户名
     */
    @Schema(description = "用户名", required = true)
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", required = true)
    private String password;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", required = true)
    private String realName;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码", required = true)
    private String roleCode;

    /**
     * 状态: active/frozen
     */
    @Schema(description = "状态", defaultValue = "active")
    private String status;
}