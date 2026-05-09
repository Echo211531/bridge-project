package com.bridge.lifecycle.controller;

import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.service.LifecycleService;
import com.bridge.lifecycle.vo.LifecycleEventVO;
import com.bridge.lifecycle.vo.LifecycleSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生命周期管理控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "生命周期", description = "设备生命周期事件、时间轴展示、累计TCO")
@RestController
@RequestMapping("/api/v1/lifecycle")
@RequiredArgsConstructor
public class LifecycleController {

    private final LifecycleService lifecycleService;

    /**
     * 设备生命周期事件查询
     */
    @Operation(summary = "设备生命周期事件查询")
    @GetMapping("/events/{deviceId}")
    public Result<List<LifecycleEventVO>> getEvents(
            @Parameter(description = "设备ID") @PathVariable String deviceId) {
        return Result.success(lifecycleService.getEvents(deviceId));
    }

    /**
     * 生命周期汇总查询
     */
    @Operation(summary = "生命周期汇总查询（累计TCO）")
    @GetMapping("/summary/{deviceId}")
    public Result<LifecycleSummaryVO> getSummary(
            @Parameter(description = "设备ID") @PathVariable String deviceId) {
        return Result.success(lifecycleService.getSummary(deviceId));
    }

    /**
     * 生命周期成本追溯
     */
    @Operation(summary = "生命周期成本追溯")
    @GetMapping("/trace/{deviceId}")
    public Result<String> getCostTrace(
            @Parameter(description = "设备ID") @PathVariable String deviceId) {
        return Result.success(lifecycleService.getCostTrace(deviceId));
    }
}