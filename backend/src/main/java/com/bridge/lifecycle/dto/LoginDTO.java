package com.bridge.lifecycle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "登录请求")
public class LoginDTO {

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
}