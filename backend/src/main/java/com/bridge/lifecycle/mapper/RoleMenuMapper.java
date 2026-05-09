package com.bridge.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bridge.lifecycle.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色-菜单 Mapper 接口
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<SysRoleMenu> {
}