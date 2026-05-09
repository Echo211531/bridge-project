package com.bridge.lifecycle.controller;

import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.ConfigUpdateDTO;
import com.bridge.lifecycle.service.ConfigService;
import com.bridge.lifecycle.vo.ConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "系统配置", description = "TCO阈值配置")
@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    /**
     * 配置列表查询
     */
    @Operation(summary = "配置列表查询")
    @GetMapping
    public Result<List<ConfigVO>> listConfigs() {
        return Result.success(configService.listConfigs());
    }

    /**
     * 根据键查询配置
     */
    @Operation(summary = "根据键查询配置")
    @GetMapping("/{key}")
    public Result<ConfigVO> getConfigByKey(
            @Parameter(description = "配置键") @PathVariable String key) {
        return Result.success(configService.getConfigByKey(key));
    }

    /**
     * 配置更新
     */
    @Operation(summary = "配置更新")
    @PutMapping
    public Result<ConfigVO> updateConfig(@RequestBody ConfigUpdateDTO updateDTO) {
        return Result.success(configService.updateConfig(updateDTO));
    }
}