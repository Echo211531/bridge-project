package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bridge.lifecycle.dto.LoginDTO;
import com.bridge.lifecycle.entity.SysRole;
import com.bridge.lifecycle.entity.SysRoleMenu;
import com.bridge.lifecycle.entity.SysUser;
import com.bridge.lifecycle.mapper.RoleMapper;
import com.bridge.lifecycle.mapper.RoleMenuMapper;
import com.bridge.lifecycle.mapper.UserMapper;
import com.bridge.lifecycle.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return 登录响应（用户信息 + 菜单权限）
     */
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, loginDTO.getUsername())
        );

        // 用户不存在
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 密码验证（简化版，与原版一致）
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 账户状态检查
        if ("frozen".equals(user.getStatus())) {
            throw new RuntimeException("账户已被冻结，请联系管理员");
        }

        // 查询角色信息
        SysRole role = roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getCode, user.getRoleCode())
        );

        // 查询菜单权限
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleCode, user.getRoleCode())
        );
        List<String> menus = roleMenus.stream()
                .map(SysRoleMenu::getMenuCode)
                .collect(Collectors.toList());

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setRoleCode(user.getRoleCode());
        loginVO.setRoleName(role != null ? role.getName() : "");
        loginVO.setMenus(menus);

        log.info("用户登录成功: {}", user.getUsername());
        return loginVO;
    }

    /**
     * 用户登出
     *
     * @param username 用户名
     */
    public void logout(String username) {
        log.info("用户登出: {}", username);
    }

    /**
     * 获取菜单权限列表
     *
     * @param roleCode 角色编码
     * @return 菜单编码列表
     */
    public List<String> getMenusByRoleCode(String roleCode) {
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleCode, roleCode)
        );
        return roleMenus.stream()
                .map(SysRoleMenu::getMenuCode)
                .collect(Collectors.toList());
    }
}