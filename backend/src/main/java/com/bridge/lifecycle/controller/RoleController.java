package com.bridge.lifecycle.controller;

import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.service.RoleService;
import com.bridge.lifecycle.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "角色管理", description = "角色查询")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取所有角色列表
     */
    @Operation(summary = "获取所有角色列表")
    @GetMapping
    public Result<List<RoleVO>> listAllRoles() {
        return Result.success(roleService.listAllRoles());
    }

    /**
     * 根据角色编码获取角色信息
     */
    @Operation(summary = "根据角色编码获取角色信息")
    @GetMapping("/code/{code}")
    public Result<RoleVO> getRoleByCode(
            @Parameter(description = "角色编码") @PathVariable String code) {
        return Result.success(roleService.getRoleByCode(code));
    }
}