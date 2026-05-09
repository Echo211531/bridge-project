package com.bridge.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.PurchaseOrderDTO;
import com.bridge.lifecycle.dto.PurchaseStatusDTO;
import com.bridge.lifecycle.service.PurchaseService;
import com.bridge.lifecycle.vo.PurchaseOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 采购订单管理控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "采购订单", description = "订单创建、状态流转")
@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * 订单列表查询
     */
    @Operation(summary = "订单列表查询")
    @GetMapping
    public Result<Page<PurchaseOrderVO>> listOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return Result.success(purchaseService.listOrders(pageNum, pageSize, status));
    }

    /**
     * 订单详情查询
     */
    @Operation(summary = "订单详情查询")
    @GetMapping("/{id}")
    public Result<PurchaseOrderVO> getOrderById(
            @Parameter(description = "订单ID") @PathVariable String id) {
        return Result.success(purchaseService.getOrderById(id));
    }

    /**
     * 订单创建
     */
    @Operation(summary = "订单创建（总金额计算）")
    @PostMapping
    public Result<PurchaseOrderVO> createOrder(@RequestBody PurchaseOrderDTO orderDTO) {
        return Result.success(purchaseService.createOrder(orderDTO));
    }

    /**
     * 订单状态流转
     */
    @Operation(summary = "订单状态流转（pending→shipping→received）")
    @PutMapping("/{id}/status")
    public Result<PurchaseOrderVO> updateStatus(
            @Parameter(description = "订单ID") @PathVariable String id,
            @RequestBody PurchaseStatusDTO statusDTO) {
        statusDTO.setOrderId(id);
        return Result.success(purchaseService.updateStatus(statusDTO));
    }
}