package com.bridge.lifecycle.controller;

import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.ScrapDecisionDTO;
import com.bridge.lifecycle.entity.ScrapDecision;
import com.bridge.lifecycle.service.ScrapService;
import com.bridge.lifecycle.vo.ScrapCandidateVO;
import com.bridge.lifecycle.vo.TcoDecisionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报废鉴定管理控制器（TCO核心功能）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "报废鉴定", description = "TCO决策算法、三栏决策面板、报废审批")
@RestController
@RequestMapping("/api/v1/scrap")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    /**
     * 待鉴定设备筛选
     */
    @Operation(summary = "待鉴定设备筛选（寿命警戒线、故障次数）")
    @GetMapping("/candidates")
    public Result<List<ScrapCandidateVO>> listCandidates() {
        return Result.success(scrapService.listCandidates());
    }

    /**
     * 获取TCO决策面板数据
     */
    @Operation(summary = "获取TCO决策面板数据（三栏决策面板）")
    @GetMapping("/tco/{deviceId}")
    public Result<TcoDecisionVO> getTcoDecision(
            @Parameter(description = "设备ID") @PathVariable String deviceId) {
        return Result.success(scrapService.getTcoDecision(deviceId));
    }

    /**
     * 提交鉴定结论
     */
    @Operation(summary = "提交鉴定结论")
    @PostMapping("/decision")
    public Result<ScrapDecision> submitDecision(@RequestBody ScrapDecisionDTO decisionDTO) {
        return Result.success(scrapService.submitDecision(decisionDTO));
    }

    /**
     * 查询鉴定历史
     */
    @Operation(summary = "查询鉴定历史")
    @GetMapping("/history/{deviceId}")
    public Result<List<ScrapDecision>> getDecisionHistory(
            @Parameter(description = "设备ID") @PathVariable String deviceId) {
        return Result.success(scrapService.getDecisionHistory(deviceId));
    }

    /**
     * 报废审批
     */
    @Operation(summary = "报废审批")
    @PostMapping("/approve/{decisionId}")
    public Result<ScrapDecision> approveDecision(
            @Parameter(description = "鉴定ID") @PathVariable String decisionId,
            @Parameter(description = "审批人") @RequestParam String approveUser,
            @Parameter(description = "审批结果") @RequestParam String approveResult,
            @Parameter(description = "审批备注") @RequestParam(required = false) String approveNotes) {
        return Result.success(scrapService.approveDecision(decisionId, approveUser, approveResult, approveNotes));
    }
}