package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bridge.lifecycle.entity.SysRole;
import com.bridge.lifecycle.mapper.RoleMapper;
import com.bridge.lifecycle.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    public List<RoleVO> listAllRoles() {
        List<SysRole> roles = roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .orderByAsc(SysRole::getCreateTime)
        );
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据角色编码获取角色信息
     *
     * @param code 角色编码
     * @return 角色信息
     */
    public RoleVO getRoleByCode(String code) {
        SysRole role = roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getCode, code)
        );
        if (role == null) {
            return null;
        }
        return convertToVO(role);
    }

    /**
     * 转换实体为VO
     *
     * @param role 角色实体
     * @return 角色VO
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setCode(role.getCode());
        vo.setName(role.getName());
        vo.setDescription(role.getDescription());
        return vo;
    }
}