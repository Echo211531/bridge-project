package com.bridge.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bridge.lifecycle.entity.MaintainPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 保养计划 Mapper 接口
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Mapper
public interface MaintainPlanMapper extends BaseMapper<MaintainPlan> {
}