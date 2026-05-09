package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色-菜单关联实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}