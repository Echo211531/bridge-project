package com.bridge.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.MaintainRecordDTO;
import com.bridge.lifecycle.service.MaintainService;
import com.bridge.lifecycle.vo.MaintainPlanVO;
import com.bridge.lifecycle.vo.MaintainRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 保养管理控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "保养管理", description = "保养计划、保养记录、动态周期计算")
@RestController
@RequestMapping("/api/v1/maintain")
@RequiredArgsConstructor
public class MaintainController {

    private final MaintainService maintainService;

    /**
     * 保养计划列表查询
     */
    @Operation(summary = "保养计划列表查询")
    @GetMapping("/plans")
    public Result<Page<MaintainPlanVO>> listPlans(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return Result.success(maintainService.listPlans(pageNum, pageSize, status));
    }

    /**
     * 保养记录列表查询
     */
    @Operation(summary = "保养记录列表查询")
    @GetMapping("/records")
    public Result<Page<MaintainRecordVO>> listRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "设备ID") @RequestParam(required = false) String deviceId) {
        return Result.success(maintainService.listRecords(pageNum, pageSize, deviceId));
    }

    /**
     * 保养记录上报
     */
    @Operation(summary = "保养记录上报（生成生命周期事件）")
    @PostMapping("/records")
    public Result<MaintainRecordVO> submitRecord(@RequestBody MaintainRecordDTO recordDTO) {
        return Result.success(maintainService.submitRecord(recordDTO));
    }

    /**
     * 保养完成率统计
     */
    @Operation(summary = "保养完成率统计")
    @GetMapping("/completion-rate")
    public Result<BigDecimal> getCompletionRate(
            @Parameter(description = "年份") @RequestParam(defaultValue = "2026") Integer year) {
        return Result.success(maintainService.calculateCompletionRate(year));
    }
}