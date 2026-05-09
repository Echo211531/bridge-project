-- =====================================================
-- 桥梁设备全生命周期管理系统数据库表结构
-- 创建日期: 2026-05-09
-- 数据库: MySQL 8.x
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `role_code` VARCHAR(20) NOT NULL COMMENT '角色编码',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active/frozen',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 2. 系统角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `code` VARCHAR(20) NOT NULL COMMENT '角色编码',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(200) COMMENT '角色描述',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ----------------------------
-- 3. 角色-菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `role_code` VARCHAR(20) NOT NULL COMMENT '角色编码',
  `menu_code` VARCHAR(50) NOT NULL COMMENT '菜单编码',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关联表';

-- ----------------------------
-- 4. 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` VARCHAR(64) NOT NULL COMMENT '主键ID(配置键)',
  `config_key` VARCHAR(64) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(200) NOT NULL COMMENT '配置值',
  `description` VARCHAR(200) COMMENT '配置描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ----------------------------
-- 5. 审计日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_audit_log`;
CREATE TABLE `sys_audit_log` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `operator` VARCHAR(50) NOT NULL COMMENT '操作人用户名',
  `operator_name` VARCHAR(50) COMMENT '操作人姓名',
  `action` VARCHAR(20) NOT NULL COMMENT '操作类型: create/update/delete/status_change',
  `target_type` VARCHAR(50) COMMENT '操作目标类型',
  `target_id` VARCHAR(36) COMMENT '操作目标ID',
  `detail` TEXT COMMENT '操作详情(JSON)',
  `operate_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_operator` (`operator`),
  KEY `idx_action` (`action`),
  KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- ----------------------------
-- 6. 桥梁档案表
-- ----------------------------
DROP TABLE IF EXISTS `bridge`;
CREATE TABLE `bridge` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `bridge_code` VARCHAR(20) NOT NULL COMMENT '桥梁编码',
  `bridge_name` VARCHAR(100) NOT NULL COMMENT '桥梁名称',
  `climate_zone` VARCHAR(20) NOT NULL COMMENT '气候带: cold/severe_cold/temperate/humid/coastal',
  `alpha_coef` DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT 'α气候系数',
  `aadt` INT NOT NULL COMMENT '年平均日交通量(AADT)',
  `beta_coef` DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT 'βAADT系数',
  `location` VARCHAR(200) COMMENT '地理位置',
  `build_year` INT COMMENT '建成年份',
  `length` DECIMAL(10,2) COMMENT '桥梁长度(米)',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bridge_code` (`bridge_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桥梁档案表';

-- ----------------------------
-- 7. AADT历史记录表
-- ----------------------------
DROP TABLE IF EXISTS `bridge_aadt_history`;
CREATE TABLE `bridge_aadt_history` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `bridge_id` VARCHAR(36) NOT NULL COMMENT '桥梁ID',
  `aadt` INT NOT NULL COMMENT 'AADT值',
  `source` VARCHAR(50) COMMENT '数据来源',
  `changed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  PRIMARY KEY (`id`),
  KEY `idx_bridge_id` (`bridge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AADT历史记录表';

-- ----------------------------
-- 8. 监测推送记录表
-- ----------------------------
DROP TABLE IF EXISTS `monitoring_record`;
CREATE TABLE `monitoring_record` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `bridge_id` VARCHAR(36) NOT NULL COMMENT '桥梁ID',
  `reported_aadt` INT NOT NULL COMMENT '上报AADT值',
  `pushed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '推送时间',
  PRIMARY KEY (`id`),
  KEY `idx_bridge_id` (`bridge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监测推送记录表';

-- ----------------------------
-- 9. 设备档案表
-- ----------------------------
DROP TABLE IF EXISTS `device_archive`;
CREATE TABLE `device_archive` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `device_code` VARCHAR(30) NOT NULL COMMENT '设备编码',
  `device_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
  `category` VARCHAR(20) NOT NULL COMMENT '设备分类编码',
  `bridge_id` VARCHAR(36) NOT NULL COMMENT '所属桥梁ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'in_stock' COMMENT '状态: in_use/in_stock/maintaining/fault/scrapped/disabled',
  `purchase_cost` DECIMAL(12,2) NOT NULL COMMENT '购置成本',
  `manufacturer` VARCHAR(100) COMMENT '生产厂商',
  `model` VARCHAR(50) COMMENT '型号规格',
  `in_use_date` DATE COMMENT '投入使用日期',
  `purchase_date` DATE COMMENT '采购日期',
  `warranty_years` DECIMAL(3,1) COMMENT '保修年限',
  `location_on_bridge` VARCHAR(100) COMMENT '桥梁位置',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_code` (`device_code`),
  KEY `idx_category` (`category`),
  KEY `idx_bridge_id` (`bridge_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备档案表';

-- ----------------------------
-- 10. 设备分类表
-- ----------------------------
DROP TABLE IF EXISTS `device_category`;
CREATE TABLE `device_category` (
  `code` VARCHAR(20) NOT NULL COMMENT '分类编码',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `design_life_years` INT NOT NULL COMMENT '设计寿命(年)',
  `maintain_cycle_days` INT NOT NULL COMMENT '标准保养周期(天)',
  `description` VARCHAR(200) COMMENT '分类描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备分类表';

-- ----------------------------
-- 11. 保养计划表
-- ----------------------------
DROP TABLE IF EXISTS `maintain_plan`;
CREATE TABLE `maintain_plan` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `device_id` VARCHAR(36) NOT NULL COMMENT '设备ID',
  `plan_date` DATE NOT NULL COMMENT '计划保养日期',
  `cycle_days` INT NOT NULL COMMENT '实际周期天数(动态计算后)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/completed/overdue',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保养计划表';

-- ----------------------------
-- 12. 保养记录表
-- ----------------------------
DROP TABLE IF EXISTS `maintain_record`;
CREATE TABLE `maintain_record` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `device_id` VARCHAR(36) NOT NULL COMMENT '设备ID',
  `record_date` DATE NOT NULL COMMENT '保养日期',
  `actual_cost` DECIMAL(10,2) NOT NULL COMMENT '实际费用',
  `manhour` DECIMAL(5,1) COMMENT '工时(小时)',
  `operator` VARCHAR(50) COMMENT '操作人',
  `content` TEXT COMMENT '保养内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保养记录表';

-- ----------------------------
-- 13. 故障工单表
-- ----------------------------
DROP TABLE IF EXISTS `fault_order`;
CREATE TABLE `fault_order` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `order_no` VARCHAR(20) NOT NULL COMMENT '工单编号',
  `device_id` VARCHAR(36) NOT NULL COMMENT '设备ID',
  `report_date` DATE NOT NULL COMMENT '申报日期',
  `fault_desc` TEXT NOT NULL COMMENT '故障描述',
  `repair_cost` DECIMAL(10,2) COMMENT '维修费用',
  `repair_content` TEXT COMMENT '维修内容',
  `status` VARCHAR(20) NOT NULL DEFAULT 'open' COMMENT '状态: open/processing/closed',
  `close_date` DATE COMMENT '关闭日期',
  `handler` VARCHAR(50) COMMENT '处理人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故障工单表';

-- ----------------------------
-- 14. 采购订单表
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `order_no` VARCHAR(20) NOT NULL COMMENT '订单编号',
  `category` VARCHAR(20) NOT NULL COMMENT '设备分类编码',
  `quantity` INT NOT NULL COMMENT '数量',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_amount` DECIMAL(12,2) NOT NULL COMMENT '总金额',
  `supplier` VARCHAR(100) COMMENT '供应商',
  `bridge_id` VARCHAR(36) COMMENT '目标桥梁ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending/shipping/received',
  `ship_date` DATE COMMENT '发货日期',
  `receive_date` DATE COMMENT '入库日期',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

-- ----------------------------
-- 15. 报废鉴定表
-- ----------------------------
DROP TABLE IF EXISTS `scrap_decision`;
CREATE TABLE `scrap_decision` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `device_id` VARCHAR(36) NOT NULL COMMENT '设备ID',
  `tco_repair` DECIMAL(12,2) COMMENT 'TCO维修方案成本',
  `tco_replace` DECIMAL(12,2) COMMENT 'TCO更换方案成本',
  `recommendation` VARCHAR(20) NOT NULL COMMENT '推荐结论: repair/replace',
  `conclusion_notes` TEXT COMMENT '鉴定备注',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending_approval' COMMENT '状态: pending_approval/approved/rejected',
  `decision_date` DATE COMMENT '鉴定日期',
  `decider` VARCHAR(50) COMMENT '鉴定人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报废鉴定表';

-- ----------------------------
-- 16. 报废审批表
-- ----------------------------
DROP TABLE IF EXISTS `scrap_approval`;
CREATE TABLE `scrap_approval` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `decision_id` VARCHAR(36) NOT NULL COMMENT '鉴定ID',
  `approve_user` VARCHAR(50) NOT NULL COMMENT '审批人',
  `approve_date` DATE NOT NULL COMMENT '审批日期',
  `approve_result` VARCHAR(20) NOT NULL COMMENT '审批结果: approved/rejected',
  `approve_notes` TEXT COMMENT '审批备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_decision_id` (`decision_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报废审批表';

-- ----------------------------
-- 17. 生命周期事件表
-- ----------------------------
DROP TABLE IF EXISTS `lifecycle_event`;
CREATE TABLE `lifecycle_event` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
  `device_id` VARCHAR(36) NOT NULL COMMENT '设备ID',
  `event_type` VARCHAR(20) NOT NULL COMMENT '事件类型: purchase/commission/maintain/repair/inspect/scrap',
  `title` VARCHAR(100) NOT NULL COMMENT '事件标题',
  `description` TEXT COMMENT '事件描述',
  `cost` DECIMAL(10,2) COMMENT '事件成本',
  `operator` VARCHAR(50) COMMENT '操作人',
  `event_time` DATETIME NOT NULL COMMENT '事件时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_event_type` (`event_type`),
  KEY `idx_event_time` (`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生命周期事件表';

SET FOREIGN_KEY_CHECKS = 1;