package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.entity.SysAuditLog;
import com.bridge.lifecycle.mapper.AuditLogMapper;
import com.bridge.lifecycle.vo.AuditLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计日志服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogMapper auditLogMapper;

    // 操作类型名称映射
    private static final java.util.Map<String, String> ACTION_NAME_MAP = java.util.Map.of(
            "create", "创建",
            "update", "更新",
            "delete", "删除",
            "status_change", "状态变更"
    );

    /**
     * 审计日志分页查询
     *
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @param action      操作类型筛选
     * @param operator    操作人筛选
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @return 分页审计日志列表
     */
    public Page<AuditLogVO> listLogs(Integer pageNum, Integer pageSize, String action,
                                      String operator, LocalDateTime startTime, LocalDateTime endTime) {
        Page<SysAuditLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysAuditLog> wrapper = new LambdaQueryWrapper<>();

        // 操作类型筛选
        if (action != null && !action.isEmpty()) {
            wrapper.eq(SysAuditLog::getAction, action);
        }
        // 操作人筛选
        if (operator != null && !operator.isEmpty()) {
            wrapper.like(SysAuditLog::getOperator, operator);
        }
        // 时间范围筛选
        if (startTime != null) {
            wrapper.ge(SysAuditLog::getOperateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysAuditLog::getOperateTime, endTime);
        }
        // 按操作时间降序
        wrapper.orderByDesc(SysAuditLog::getOperateTime);

        Page<SysAuditLog> logPage = auditLogMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<AuditLogVO> voPage = new Page<>(logPage.getCurrent(), logPage.getSize(), logPage.getTotal());
        List<AuditLogVO> voList = logPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 记录审计日志
     *
     * @param operator    操作人用户名
     * @param operatorName 操作人姓名
     * @param action      操作类型
     * @param targetType  操作目标类型
     * @param targetId    操作目标ID
     * @param detail      操作详情(JSON)
     */
    public void log(String operator, String operatorName, String action,
                    String targetType, String targetId, String detail) {
        SysAuditLog auditLog = new SysAuditLog();
        auditLog.setOperator(operator);
        auditLog.setOperatorName(operatorName);
        auditLog.setAction(action);
        auditLog.setTargetType(targetType);
        auditLog.setTargetId(targetId);
        auditLog.setDetail(detail);
        auditLog.setOperateTime(LocalDateTime.now());
        auditLogMapper.insert(auditLog);

        log.info("审计日志记录: {} {} {}", operator, action, targetType);
    }

    /**
     * 转换实体为VO
     */
    private AuditLogVO convertToVO(SysAuditLog log) {
        AuditLogVO vo = new AuditLogVO();
        vo.setId(log.getId());
        vo.setOperator(log.getOperator());
        vo.setOperatorName(log.getOperatorName());
        vo.setAction(log.getAction());
        vo.setActionName(ACTION_NAME_MAP.getOrDefault(log.getAction(), ""));
        vo.setTargetType(log.getTargetType());
        vo.setTargetId(log.getTargetId());
        vo.setDetail(log.getDetail());
        vo.setOperateTime(log.getOperateTime());
        return vo;
    }
}