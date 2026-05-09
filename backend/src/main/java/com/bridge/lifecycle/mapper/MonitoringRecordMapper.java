package com.bridge.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bridge.lifecycle.entity.MonitoringRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 监测推送 Mapper 接口
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Mapper
public interface MonitoringRecordMapper extends BaseMapper<MonitoringRecord> {
}