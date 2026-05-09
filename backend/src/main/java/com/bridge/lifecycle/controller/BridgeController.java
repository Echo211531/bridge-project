package com.bridge.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.AadtUpdateDTO;
import com.bridge.lifecycle.dto.BridgeDTO;
import com.bridge.lifecycle.dto.BridgeUpdateDTO;
import com.bridge.lifecycle.service.BridgeService;
import com.bridge.lifecycle.vo.AadtHistoryVO;
import com.bridge.lifecycle.vo.BridgeDetailVO;
import com.bridge.lifecycle.vo.BridgeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 桥梁管理控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "桥梁管理", description = "桥梁档案、α/β系数、AADT历史")
@RestController
@RequestMapping("/api/v1/bridges")
@RequiredArgsConstructor
public class BridgeController {

    private final BridgeService bridgeService;

    /**
     * 桥梁列表查询
     */
    @Operation(summary = "桥梁列表查询")
    @GetMapping
    public Result<Page<BridgeVO>> listBridges(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "桥梁名称") @RequestParam(required = false) String bridgeName) {
        return Result.success(bridgeService.listBridges(pageNum, pageSize, bridgeName));
    }

    /**
     * 桥梁详情查询
     */
    @Operation(summary = "桥梁详情查询（含AADT历史）")
    @GetMapping("/{id}")
    public Result<BridgeDetailVO> getBridgeDetail(
            @Parameter(description = "桥梁ID") @PathVariable String id) {
        return Result.success(bridgeService.getBridgeDetail(id));
    }

    /**
     * 创建桥梁
     */
    @Operation(summary = "创建桥梁")
    @PostMapping
    public Result<BridgeVO> createBridge(@RequestBody BridgeDTO bridgeDTO) {
        return Result.success(bridgeService.createBridge(bridgeDTO));
    }

    /**
     * 更新桥梁
     */
    @Operation(summary = "更新桥梁")
    @PutMapping("/{id}")
    public Result<BridgeVO> updateBridge(
            @Parameter(description = "桥梁ID") @PathVariable String id,
            @RequestBody BridgeUpdateDTO bridgeUpdateDTO) {
        bridgeUpdateDTO.setId(id);
        return Result.success(bridgeService.updateBridge(bridgeUpdateDTO));
    }

    /**
     * 更新AADT
     */
    @Operation(summary = "更新AADT（记录历史变更）")
    @PutMapping("/{id}/aadt")
    public Result<BridgeVO> updateAadt(
            @Parameter(description = "桥梁ID") @PathVariable String id,
            @RequestBody AadtUpdateDTO aadtUpdateDTO) {
        aadtUpdateDTO.setBridgeId(id);
        return Result.success(bridgeService.updateAadt(aadtUpdateDTO));
    }

    /**
     * 查询AADT历史
     */
    @Operation(summary = "查询AADT历史")
    @GetMapping("/{id}/aadt-history")
    public Result<List<AadtHistoryVO>> getAadtHistory(
            @Parameter(description = "桥梁ID") @PathVariable String id) {
        return Result.success(bridgeService.getAadtHistory(id));
    }
}