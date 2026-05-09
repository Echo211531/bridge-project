package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 登录响应VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "登录响应")
public class LoginVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

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
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 菜单权限列表
     */
    @Schema(description = "菜单权限列表")
    private List<String> menus;
}