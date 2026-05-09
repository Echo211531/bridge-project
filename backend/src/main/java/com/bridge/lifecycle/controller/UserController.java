package com.bridge.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.UserDTO;
import com.bridge.lifecycle.dto.UserUpdateDTO;
import com.bridge.lifecycle.service.UserService;
import com.bridge.lifecycle.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "用户管理", description = "用户CRUD、状态切换、密码重置")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户列表分页查询
     */
    @Operation(summary = "用户列表分页查询")
    @GetMapping
    public Result<Page<UserVO>> listUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return Result.success(userService.listUsers(pageNum, pageSize, username, status));
    }

    /**
     * 用户详情查询
     */
    @Operation(summary = "用户详情查询")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable String id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 创建用户
     */
    @Operation(summary = "创建用户")
    @PostMapping
    public Result<UserVO> createUser(@RequestBody UserDTO userDTO) {
        return Result.success(userService.createUser(userDTO));
    }

    /**
     * 更新用户
     */
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<UserVO> updateUser(
            @Parameter(description = "用户ID") @PathVariable String id,
            @RequestBody UserUpdateDTO userUpdateDTO) {
        userUpdateDTO.setId(id);
        return Result.success(userService.updateUser(userUpdateDTO));
    }

    /**
     * 启用/禁用用户
     */
    @Operation(summary = "启用/禁用用户")
    @PutMapping("/{id}/status")
    public Result<UserVO> toggleUserStatus(
            @Parameter(description = "用户ID") @PathVariable String id,
            @Parameter(description = "状态") @RequestParam String status) {
        return Result.success(userService.toggleUserStatus(id, status));
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @PutMapping("/{id}/password")
    public Result<UserVO> resetPassword(
            @Parameter(description = "用户ID") @PathVariable String id,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        return Result.success(userService.resetPassword(id, newPassword));
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable String id) {
        userService.deleteUser(id);
        return Result.success(null);
    }
}