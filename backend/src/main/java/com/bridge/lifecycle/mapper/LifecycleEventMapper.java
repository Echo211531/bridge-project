package com.bridge.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bridge.lifecycle.entity.LifecycleEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生命周期事件 Mapper 接口
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Mapper
public interface LifecycleEventMapper extends BaseMapper<LifecycleEvent> {
}