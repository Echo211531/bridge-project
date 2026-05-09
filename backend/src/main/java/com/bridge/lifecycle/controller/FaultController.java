package com.bridge.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.FaultCloseDTO;
import com.bridge.lifecycle.dto.FaultOrderDTO;
import com.bridge.lifecycle.service.FaultService;
import com.bridge.lifecycle.vo.FaultOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 故障工单管理控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "故障工单", description = "故障申报、工单流转、MTTR/MTBF统计")
@RestController
@RequestMapping("/api/v1/faults")
@RequiredArgsConstructor
public class FaultController {

    private final FaultService faultService;

    /**
     * 工单列表分页查询
     */
    @Operation(summary = "工单列表分页查询")
    @GetMapping
    public Result<Page<FaultOrderVO>> listOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return Result.success(faultService.listOrders(pageNum, pageSize, status));
    }

    /**
     * 故障申报
     */
    @Operation(summary = "故障申报（设备状态变更、生成事件）")
    @PostMapping
    public Result<FaultOrderVO> reportFault(@RequestBody FaultOrderDTO orderDTO) {
        return Result.success(faultService.reportFault(orderDTO));
    }

    /**
     * 工单处理（状态流转）
     */
    @Operation(summary = "工单处理（状态流转）")
    @PutMapping("/{id}/process")
    public Result<FaultOrderVO> processOrder(
            @Parameter(description = "工单ID") @PathVariable String id,
            @Parameter(description = "状态") @RequestParam String status) {
        return Result.success(faultService.processOrder(id, status));
    }

    /**
     * 工单关闭
     */
    @Operation(summary = "工单关闭（设备状态恢复、MTTR计算）")
    @PutMapping("/{id}/close")
    public Result<FaultOrderVO> closeOrder(
            @Parameter(description = "工单ID") @PathVariable String id,
            @RequestBody FaultCloseDTO closeDTO) {
        closeDTO.setOrderId(id);
        return Result.success(faultService.closeOrder(closeDTO));
    }

    /**
     * MTTR计算
     */
    @Operation(summary = "MTTR计算（平均修复时间）")
    @GetMapping("/mttr")
    public Result<BigDecimal> getMTTR(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(faultService.calculateMTTR(year));
    }

    /**
     * MTBF计算
     */
    @Operation(summary = "MTBF计算（平均故障间隔时间）")
    @GetMapping("/mtbf")
    public Result<BigDecimal> getMTBF(
            @Parameter(description = "设备ID") @RequestParam String deviceId) {
        return Result.success(faultService.calculateMTBF(deviceId));
    }

    /**
     * 年故障率统计
     */
    @Operation(summary = "年故障率统计")
    @GetMapping("/fault-rate")
    public Result<BigDecimal> getFaultRate(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(faultService.calculateFaultRate(year));
    }

    /**
     * 工单关闭率统计
     */
    @Operation(summary = "工单关闭率统计")
    @GetMapping("/close-rate")
    public Result<BigDecimal> getCloseRate(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(faultService.calculateCloseRate(year));
    }
}