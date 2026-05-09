package com.bridge.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.dto.DeviceDTO;
import com.bridge.lifecycle.dto.DeviceUpdateDTO;
import com.bridge.lifecycle.entity.DeviceCategory;
import com.bridge.lifecycle.service.DeviceService;
import com.bridge.lifecycle.vo.DeviceDetailVO;
import com.bridge.lifecycle.vo.DeviceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备档案管理控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "设备档案", description = "设备CRUD、残值估算、编码生成、状态流转")
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * 设备列表分页查询
     */
    @Operation(summary = "设备列表分页查询")
    @GetMapping
    public Result<Page<DeviceVO>> listDevices(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "桥梁ID") @RequestParam(required = false) String bridgeId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        return Result.success(deviceService.listDevices(pageNum, pageSize, category, bridgeId, status));
    }

    /**
     * 设备详情查询
     */
    @Operation(summary = "设备详情查询（含残值计算）")
    @GetMapping("/{id}")
    public Result<DeviceDetailVO> getDeviceDetail(
            @Parameter(description = "设备ID") @PathVariable String id) {
        return Result.success(deviceService.getDeviceDetail(id));
    }

    /**
     * 创建设备
     */
    @Operation(summary = "创建设备（自动生成编码和生命周期事件）")
    @PostMapping
    public Result<DeviceVO> createDevice(@RequestBody DeviceDTO deviceDTO) {
        return Result.success(deviceService.createDevice(deviceDTO));
    }

    /**
     * 更新设备
     */
    @Operation(summary = "更新设备")
    @PutMapping("/{id}")
    public Result<DeviceVO> updateDevice(
            @Parameter(description = "设备ID") @PathVariable String id,
            @RequestBody DeviceUpdateDTO deviceUpdateDTO) {
        deviceUpdateDTO.setId(id);
        return Result.success(deviceService.updateDevice(deviceUpdateDTO));
    }

    /**
     * 超期保养设备查询
     */
    @Operation(summary = "超期保养设备查询")
    @GetMapping("/overdue")
    public Result<List<DeviceVO>> listOverdueDevices() {
        return Result.success(deviceService.listOverdueDevices());
    }

    /**
     * 设备分类查询
     */
    @Operation(summary = "设备分类查询")
    @GetMapping("/categories")
    public Result<List<DeviceCategory>> listCategories() {
        return Result.success(deviceService.listCategories());
    }

    /**
     * 设备状态流转
     */
    @Operation(summary = "设备状态流转")
    @PutMapping("/{id}/status")
    public Result<DeviceVO> changeStatus(
            @Parameter(description = "设备ID") @PathVariable String id,
            @Parameter(description = "状态") @RequestParam String status) {
        return Result.success(deviceService.changeStatus(id, status));
    }
}