package com.bridge.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.Result;
import com.bridge.lifecycle.service.AuditService;
import com.bridge.lifecycle.vo.AuditLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 审计日志控制器
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Tag(name = "审计日志", description = "操作审计、可追溯查询")
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * 审计日志分页查询
     */
    @Operation(summary = "审计日志分页查询")
    @GetMapping
    public Result<Page<AuditLogVO>> listLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "操作类型") @RequestParam(required = false) String action,
            @Parameter(description = "操作人") @RequestParam(required = false) String operator,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(auditService.listLogs(pageNum, pageSize, action, operator, startTime, endTime));
    }
}