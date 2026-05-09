package com.bridge.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bridge.lifecycle.entity.SysAuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志 Mapper 接口
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<SysAuditLog> {
}