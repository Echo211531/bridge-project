package com.bridge.lifecycle.controller;

import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.LoginDTO;
import com.bridge.lifecycle.service.AuthService;
import com.bridge.lifecycle.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录登出相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名密码登录")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginVO loginVO = authService.login(loginDTO);
            return Result.success(loginVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录")
    public Result<Void> logout(@RequestParam String username) {
        authService.logout(username);
        return Result.success();
    }
}