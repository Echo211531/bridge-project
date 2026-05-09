package com.bridge.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bridge.lifecycle.entity.FaultOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 故障工单 Mapper 接口
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Mapper
public interface FaultOrderMapper extends BaseMapper<FaultOrder> {
}