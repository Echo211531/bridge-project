package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.dto.UserDTO;
import com.bridge.lifecycle.dto.UserUpdateDTO;
import com.bridge.lifecycle.entity.SysRole;
import com.bridge.lifecycle.entity.SysUser;
import com.bridge.lifecycle.mapper.RoleMapper;
import com.bridge.lifecycle.mapper.UserMapper;
import com.bridge.lifecycle.utils.PasswordUtils;
import com.bridge.lifecycle.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    /**
     * 用户列表分页查询
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param username 用户名（模糊查询）
     * @param status   状态筛选
     * @return 分页用户列表
     */
    public Page<UserVO> listUsers(Integer pageNum, Integer pageSize, String username, String status) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        // 状态筛选
        if (StringUtils.hasText(status)) {
            wrapper.eq(SysUser::getStatus, status);
        }
        // 按创建时间降序
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> userPage = userMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> voList = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 用户详情查询
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public UserVO getUserById(String id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVO(user);
    }

    /**
     * 创建用户
     *
     * @param userDTO 用户创建请求
     * @return 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserDTO userDTO) {
        // 用户名唯一校验
        SysUser existUser = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, userDTO.getUsername())
        );
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户实体
        SysUser user = new SysUser();
        user.setUsername(userDTO.getUsername());
        user.setPassword(PasswordUtils.encrypt(userDTO.getPassword()));
        user.setRealName(userDTO.getRealName());
        user.setRoleCode(userDTO.getRoleCode());
        user.setStatus(StringUtils.hasText(userDTO.getStatus()) ? userDTO.getStatus() : "active");

        userMapper.insert(user);
        log.info("创建用户成功: {}", user.getUsername());

        return convertToVO(user);
    }

    /**
     * 更新用户
     *
     * @param userUpdateDTO 用户更新请求
     * @return 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUser(UserUpdateDTO userUpdateDTO) {
        SysUser user = userMapper.selectById(userUpdateDTO.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新字段
        if (StringUtils.hasText(userUpdateDTO.getRealName())) {
            user.setRealName(userUpdateDTO.getRealName());
        }
        if (StringUtils.hasText(userUpdateDTO.getRoleCode())) {
            user.setRoleCode(userUpdateDTO.getRoleCode());
        }
        if (StringUtils.hasText(userUpdateDTO.getStatus())) {
            user.setStatus(userUpdateDTO.getStatus());
        }

        userMapper.updateById(user);
        log.info("更新用户成功: {}", user.getUsername());

        return convertToVO(user);
    }

    /**
     * 启用/禁用用户
     *
     * @param id     用户ID
     * @param status 状态
     * @return 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVO toggleUserStatus(String id, String status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能禁用自己
        if ("frozen".equals(status)) {
            // TODO: 需要获取当前登录用户信息进行校验
        }

        user.setStatus(status);
        userMapper.updateById(user);
        log.info("用户状态变更: {} -> {}", user.getUsername(), status);

        return convertToVO(user);
    }

    /**
     * 重置密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     * @return 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVO resetPassword(String id, String newPassword) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(PasswordUtils.encrypt(newPassword));
        userMapper.updateById(user);
        log.info("重置密码成功: {}", user.getUsername());

        return convertToVO(user);
    }

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        userMapper.deleteById(id);
        log.info("删除用户成功: {}", user.getUsername());
    }

    /**
     * 转换实体为VO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRoleCode(user.getRoleCode());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        // 查询角色名称
        if (StringUtils.hasText(user.getRoleCode())) {
            SysRole role = roleMapper.selectOne(
                    new LambdaQueryWrapper<SysRole>()
                            .eq(SysRole::getCode, user.getRoleCode())
            );
            vo.setRoleName(role != null ? role.getName() : "");
        }

        return vo;
    }
}