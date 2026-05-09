-- =====================================================
-- 桥梁设备全生命周期管理系统种子数据
-- 创建日期: 2026-05-09
-- =====================================================

SET NAMES utf8mb4;

-- ----------------------------
-- 1. 系统角色数据
-- ----------------------------
INSERT INTO `sys_role` (`id`, `code`, `name`, `description`) VALUES
('role-admin', 'admin', '系统管理员', '拥有全部权限'),
('role-engineer', 'engineer', '工程管理员', '负责设备运维管理'),
('role-purchaser', 'purchaser', '采购人员', '负责采购入库'),
('role-maintainer', 'maintainer', '运维人员', '负责保养故障处理'),
('role-scrap', 'scrap', '报废鉴定员', '负责报废鉴定审批');

-- ----------------------------
-- 2. 系统用户数据
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role_code`, `status`) VALUES
('user-admin', 'admin', 'admin123', '管理员', 'admin', 'active'),
('user-engineer1', 'engineer', 'engineer123', '工程师张三', 'engineer', 'active'),
('user-engineer2', 'engineer2', 'engineer123', '工程师李四', 'engineer', 'active'),
('user-purchaser', 'purchaser', 'purchase123', '采购员王五', 'purchaser', 'active'),
('user-maintainer1', 'maintainer', 'maintain123', '运维员赵六', 'maintainer', 'active'),
('user-maintainer2', 'maintainer2', 'maintain123', '运维员孙七', 'maintainer', 'active'),
('user-scrap', 'scrap', 'scrap123', '鉴定员周八', 'scrap', 'active'),
('user-frozen', 'frozen', 'frozen', '冻结测试账号', 'admin', 'frozen');

-- ----------------------------
-- 3. 角色-菜单关联数据
-- ----------------------------
-- 管理员菜单(全部12个)
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_code`) VALUES
('rm-admin-1', 'admin', 'dashboard'),
('rm-admin-2', 'admin', 'lifecycle'),
('rm-admin-3', 'admin', 'bridges'),
('rm-admin-4', 'admin', 'devices'),
('rm-admin-5', 'admin', 'maintain'),
('rm-admin-6', 'admin', 'fault'),
('rm-admin-7', 'admin', 'purchase'),
('rm-admin-8', 'admin', 'scrap'),
('rm-admin-9', 'admin', 'stats'),
('rm-admin-10', 'admin', 'config'),
('rm-admin-11', 'admin', 'users'),
('rm-admin-12', 'admin', 'audit');

-- 工程管理员菜单(9个)
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_code`) VALUES
('rm-engineer-1', 'engineer', 'dashboard'),
('rm-engineer-2', 'engineer', 'lifecycle'),
('rm-engineer-3', 'engineer', 'bridges'),
('rm-engineer-4', 'engineer', 'devices'),
('rm-engineer-5', 'engineer', 'maintain'),
('rm-engineer-6', 'engineer', 'fault'),
('rm-engineer-7', 'engineer', 'purchase'),
('rm-engineer-8', 'engineer', 'scrap'),
('rm-engineer-9', 'engineer', 'stats');

-- 采购人员菜单(2个)
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_code`) VALUES
('rm-purchaser-1', 'purchaser', 'dashboard'),
('rm-purchaser-2', 'purchaser', 'purchase');

-- 运维人员菜单(4个)
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_code`) VALUES
('rm-maintainer-1', 'maintainer', 'devices'),
('rm-maintainer-2', 'maintainer', 'maintain'),
('rm-maintainer-3', 'maintainer', 'fault'),
('rm-maintainer-4', 'maintainer', 'dashboard');

-- 报废鉴定员菜单(3个)
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_code`) VALUES
('rm-scrap-1', 'scrap', 'lifecycle'),
('rm-scrap-2', 'scrap', 'scrap'),
('rm-scrap-3', 'scrap', 'dashboard');

-- ----------------------------
-- 4. 系统配置数据
-- ----------------------------
INSERT INTO `sys_config` (`id`, `config_key`, `config_value`, `description`) VALUES
('config-residual', 'residual_warning_line', '0.30', '残值警戒线阈值(30%)'),
('config-repair', 'repair_warning_line', '0.50', '修复警戒线阈值(50%)'),
('config-life', 'life_warning_line', '0.80', '寿命警戒线阈值(80%)');

-- ----------------------------
-- 5. 桥梁档案数据(覆盖5个气候带)
-- ----------------------------
INSERT INTO `bridge` (`id`, `bridge_code`, `bridge_name`, `climate_zone`, `alpha_coef`, `aadt`, `beta_coef`, `location`, `build_year`, `length`) VALUES
('bridge-001', 'BR01', '长江大桥', 'humid', 1.15, 45000, 1.15, '江苏省南京市', 2010, 2580),
('bridge-002', 'BR02', '黄河大桥', 'temperate', 1.00, 28000, 1.00, '山东省济南市', 2015, 1200),
('bridge-003', 'BR03', '松花江大桥', 'severe_cold', 1.20, 8000, 0.85, '黑龙江省哈尔滨市', 2018, 850),
('bridge-004', 'BR04', '珠江大桥', 'coastal', 1.25, 120000, 1.50, '广东省广州市', 2012, 1680),
('bridge-005', 'BR05', '青藏高原大桥', 'cold', 1.30, 5000, 0.85, '西藏自治区拉萨市', 2020, 520);

-- ----------------------------
-- 6. 设备分类数据(11类)
-- ----------------------------
INSERT INTO `device_category` (`code`, `name`, `design_life_years`, `maintain_cycle_days`, `description`) VALUES
('LT', '照明设备', 15, 90, '桥梁照明灯具、配电箱'),
('MN', '监测设备', 10, 60, '结构监测传感器、数据采集仪'),
('EX', '伸缩缝装置', 20, 180, '桥梁伸缩缝及附属装置'),
('BP', '支座装置', 25, 365, '桥梁支座及减震装置'),
('DR', '排水系统', 20, 120, '排水管道、落水口'),
('AN', '锚固系统', 30, 365, '锚具、索具、吊杆'),
('EL', '电气系统', 15, 90, '电缆、配电柜、开关'),
('CR', '防腐系统', 10, 365, '防腐涂层、防护设施'),
('AC', '附属设施', 20, 180, '护栏、标志牌、隔音屏'),
('SE', '安全系统', 12, 90, '消防设施、安全监控'),
('CS', '结构构件', 50, 365, '梁体、墩柱、基础');

-- ----------------------------
-- 7. 设备档案数据(38台设备)
-- ----------------------------
-- 长江大桥设备(10台)
INSERT INTO `device_archive` (`id`, `device_code`, `device_name`, `category`, `bridge_id`, `status`, `purchase_cost`, `manufacturer`, `model`, `in_use_date`, `purchase_date`, `warranty_years`, `location_on_bridge`) VALUES
('dev-001', 'DEV-LT-BR01-0001', '主桥照明灯组A', 'LT', 'bridge-001', 'in_use', 150000.00, '飞利浦照明', 'LED-500W', '2011-03-15', '2011-01-10', 2.0, '主桥东侧'),
('dev-002', 'DEV-LT-BR01-0002', '主桥照明灯组B', 'LT', 'bridge-001', 'in_use', 150000.00, '飞利浦照明', 'LED-500W', '2011-03-15', '2011-01-10', 2.0, '主桥西侧'),
('dev-003', 'DEV-MN-BR01-0001', '结构应变监测仪', 'MN', 'bridge-001', 'in_use', 280000.00, '中科院测控', 'SM-2000', '2011-04-01', '2011-02-15', 3.0, '主跨跨中'),
('dev-004', 'DEV-EX-BR01-0001', '主桥伸缩缝A', 'EX', 'bridge-001', 'in_use', 350000.00, '毛勒伸缩缝', 'MF-400', '2010-12-01', '2010-09-20', 5.0, '主桥北端'),
('dev-005', 'DEV-EX-BR01-0002', '主桥伸缩缝B', 'EX', 'bridge-001', 'in_use', 350000.00, '毛勒伸缩缝', 'MF-400', '2010-12-01', '2010-09-20', 5.0, '主桥南端'),
('dev-006', 'DEV-BP-BR01-0001', '主桥支座组A', 'BP', 'bridge-001', 'in_use', 420000.00, '桥梁支座厂', 'QZ-800', '2010-11-01', '2010-08-15', 5.0, '主墩位置'),
('dev-007', 'DEV-DR-BR01-0001', '排水管道系统', 'DR', 'bridge-001', 'in_use', 80000.00, '排水设备公司', 'PVC-200', '2011-02-01', '2010-12-20', 1.0, '全桥'),
('dev-008', 'DEV-EL-BR01-0001', '配电柜系统', 'EL', 'bridge-001', 'in_use', 120000.00, '西门子电气', 'SC-1000', '2011-03-01', '2011-01-15', 2.0, '桥塔位置'),
('dev-009', 'DEV-AC-BR01-0001', '护栏系统A', 'AC', 'bridge-001', 'in_use', 200000.00, '钢结构公司', 'SG-150', '2011-02-15', '2011-01-01', 1.0, '主桥东侧'),
('dev-010', 'DEV-AC-BR01-0002', '护栏系统B', 'AC', 'bridge-001', 'in_use', 200000.00, '钢结构公司', 'SG-150', '2011-02-15', '2011-01-01', 1.0, '主桥西侧');

-- 黄河大桥设备(8台)
INSERT INTO `device_archive` (`id`, `device_code`, `device_name`, `category`, `bridge_id`, `status`, `purchase_cost`, `manufacturer`, `model`, `in_use_date`, `purchase_date`, `warranty_years`, `location_on_bridge`) VALUES
('dev-011', 'DEV-LT-BR02-0001', '桥面照明系统', 'LT', 'bridge-002', 'in_use', 120000.00, '欧普照明', 'LED-300W', '2016-01-01', '2015-11-15', 2.0, '全桥'),
('dev-012', 'DEV-MN-BR02-0001', '位移监测仪', 'MN', 'bridge-002', 'in_use', 180000.00, '同济科技', 'DM-100', '2016-02-01', '2015-12-20', 3.0, '主跨'),
('dev-013', 'DEV-EX-BR02-0001', '桥头伸缩缝', 'EX', 'bridge-002', 'in_use', 280000.00, '万宝伸缩缝', 'WB-300', '2015-12-01', '2015-09-01', 5.0, '桥头'),
('dev-014', 'DEV-BP-BR02-0001', '墩顶支座', 'BP', 'bridge-002', 'in_use', 320000.00, '支座专业厂', 'GP-600', '2015-11-01', '2015-08-15', 5.0, '墩顶'),
('dev-015', 'DEV-DR-BR02-0001', '排水系统', 'DR', 'bridge-002', 'in_use', 60000.00, '市政设备', 'HDPE-150', '2016-01-15', '2015-11-01', 1.0, '全桥'),
('dev-016', 'DEV-AN-BR02-0001', '锚固装置', 'AN', 'bridge-002', 'in_use', 250000.00, '锚固系统厂', 'AK-500', '2015-10-01', '2015-07-15', 5.0, '索塔'),
('dev-017', 'DEV-SE-BR02-0001', '消防系统', 'SE', 'bridge-002', 'in_use', 150000.00, '消防设备厂', 'FP-200', '2016-03-01', '2016-01-20', 2.0, '桥塔'),
('dev-018', 'DEV-CS-BR02-0001', '桥面板', 'CS', 'bridge-002', 'in_use', 5000000.00, '混凝土构件厂', 'PC-标准', '2015-12-15', '2015-06-01', 0, '主桥');

-- 松花江大桥设备(6台)
INSERT INTO `device_archive` (`id`, `device_code`, `device_name`, `category`, `bridge_id`, `status`, `purchase_cost`, `manufacturer`, `model`, `in_use_date`, `purchase_date`, `warranty_years`, `location_on_bridge`) VALUES
('dev-019', 'DEV-LT-BR03-0001', '防冻照明灯', 'LT', 'bridge-003', 'in_use', 180000.00, '耐寒照明厂', 'LED-CW', '2019-01-15', '2018-11-01', 2.0, '全桥'),
('dev-020', 'DEV-MN-BR03-0001', '温度监测仪', 'MN', 'bridge-003', 'in_use', 120000.00, '北方测控', 'TM-50', '2019-02-01', '2018-12-15', 3.0, '主跨'),
('dev-021', 'DEV-EX-BR03-0001', '防冻伸缩缝', 'EX', 'bridge-003', 'in_use', 400000.00, '寒冷专用厂', 'CF-350', '2018-12-01', '2018-08-01', 5.0, '两端'),
('dev-022', 'DEV-BP-BR03-0001', '抗冻支座', 'BP', 'bridge-003', 'in_use', 380000.00, '北方支座厂', 'KF-700', '2018-11-01', '2018-07-15', 5.0, '墩顶'),
('dev-023', 'DEV-CR-BR03-0001', '防腐涂层', 'CR', 'bridge-003', 'in_use', 50000.00, '防腐材料厂', 'AC-标准', '2019-01-01', '2018-10-01', 1.0, '全桥'),
('dev-024', 'DEV-AC-BR03-0001', '防撞护栏', 'AC', 'bridge-003', 'in_use', 160000.00, '北方钢构', 'FC-200', '2019-01-20', '2018-11-15', 1.0, '两侧');

-- 珠江大桥设备(10台)
INSERT INTO `device_archive` (`id`, `device_code`, `device_name`, `category`, `bridge_id`, `status`, `purchase_cost`, `manufacturer`, `model`, `in_use_date`, `purchase_date`, `warranty_years`, `location_on_bridge`) VALUES
('dev-025', 'DEV-LT-BR04-0001', '景观照明灯A', 'LT', 'bridge-004', 'in_use', 200000.00, '景观照明厂', 'LED-景观', '2013-01-01', '2012-11-01', 2.0, '主桥东侧'),
('dev-026', 'DEV-LT-BR04-0002', '景观照明灯B', 'LT', 'bridge-004', 'in_use', 200000.00, '景观照明厂', 'LED-景观', '2013-01-01', '2012-11-01', 2.0, '主桥西侧'),
('dev-027', 'DEV-MN-BR04-0001', '振动监测仪', 'MN', 'bridge-004', 'in_use', 350000.00, '南方测控', 'VM-300', '2013-02-01', '2012-12-01', 3.0, '索塔'),
('dev-028', 'DEV-MN-BR04-0002', '风速监测仪', 'MN', 'bridge-004', 'in_use', 150000.00, '气象设备厂', 'WS-100', '2013-02-01', '2012-12-01', 3.0, '桥顶'),
('dev-029', 'DEV-EX-BR04-0001', '大型伸缩缝A', 'EX', 'bridge-004', 'in_use', 500000.00, '大型伸缩缝厂', 'LF-500', '2012-12-01', '2012-08-01', 5.0, '北端'),
('dev-030', 'DEV-EX-BR04-0002', '大型伸缩缝B', 'EX', 'bridge-004', 'in_use', 500000.00, '大型伸缩缝厂', 'LF-500', '2012-12-01', '2012-08-01', 5.0, '南端'),
('dev-031', 'DEV-AN-BR04-0001', '主缆锚固', 'AN', 'bridge-004', 'in_use', 800000.00, '大型锚固厂', 'MC-1000', '2012-10-01', '2012-06-01', 5.0, '锚碇'),
('dev-032', 'DEV-AN-BR04-0002', '吊索系统', 'AN', 'bridge-004', 'fault', 600000.00, '索具专业厂', 'HS-500', '2012-11-01', '2012-07-01', 5.0, '全桥'),
('dev-033', 'DEV-SE-BR04-0001', '监控系统', 'SE', 'bridge-004', 'in_use', 280000.00, '安防设备厂', 'VS-专业', '2013-03-01', '2013-01-01', 2.0, '全桥'),
('dev-034', 'DEV-DR-BR04-0001', '大型排水系统', 'DR', 'bridge-004', 'in_use', 100000.00, '市政排水厂', 'HD-250', '2013-01-15', '2012-11-01', 1.0, '全桥');

-- 青藏高原大桥设备(4台)
INSERT INTO `device_archive` (`id`, `device_code`, `device_name`, `category`, `bridge_id`, `status`, `purchase_cost`, `manufacturer`, `model`, `in_use_date`, `purchase_date`, `warranty_years`, `location_on_bridge`) VALUES
('dev-035', 'DEV-LT-BR05-0001', '高原照明灯', 'LT', 'bridge-005', 'in_use', 220000.00, '高原照明厂', 'LED-高原', '2021-01-15', '2020-11-01', 2.0, '全桥'),
('dev-036', 'DEV-MN-BR05-0001', '高原监测仪', 'MN', 'bridge-005', 'in_use', 250000.00, '高原测控', 'AM-200', '2021-02-01', '2020-12-01', 3.0, '主跨'),
('dev-037', 'DEV-EX-BR05-0001', '高原伸缩缝', 'EX', 'bridge-005', 'in_use', 380000.00, '高原专用厂', 'PF-400', '2020-12-01', '2020-08-01', 5.0, '两端'),
('dev-038', 'DEV-CS-BR05-0001', '高原桥面板', 'CS', 'bridge-005', 'in_stock', 3000000.00, '高原构件厂', 'PC-高原', NULL, '2020-09-01', 0, '待安装');

-- ----------------------------
-- 8. 保养计划数据(部分)
-- ----------------------------
INSERT INTO `maintain_plan` (`id`, `device_id`, `plan_date`, `cycle_days`, `status`) VALUES
('mp-001', 'dev-001', '2026-05-15', 103, 'pending'),
('mp-002', 'dev-002', '2026-05-15', 103, 'pending'),
('mp-003', 'dev-003', '2026-04-01', 69, 'completed'),
('mp-004', 'dev-011', '2026-04-15', 90, 'pending'),
('mp-005', 'dev-025', '2026-05-20', 135, 'pending');

-- ----------------------------
-- 9. 保养记录数据(部分历史记录)
-- ----------------------------
INSERT INTO `maintain_record` (`id`, `device_id`, `record_date`, `actual_cost`, `manhour`, `operator`, `content`) VALUES
('mr-001', 'dev-001', '2025-05-01', 5000.00, 8.0, 'maintainer', '更换LED灯珠、清洁灯罩'),
('mr-002', 'dev-001', '2024-05-01', 4500.00, 8.0, 'maintainer', '清洁保养、检查线路'),
('mr-003', 'dev-003', '2025-03-01', 8000.00, 16.0, 'engineer', '传感器校准、数据校验'),
('mr-004', 'dev-003', '2026-03-01', 7500.00, 16.0, 'engineer', '年度校准维护'),
('mr-005', 'dev-011', '2025-01-01', 3000.00, 4.0, 'maintainer2', '清洁照明灯具'),
('mr-006', 'dev-025', '2025-01-15', 6000.00, 12.0, 'maintainer', '景观灯维护、控制检修'),
('mr-007', 'dev-032', '2026-01-01', 10000.00, 24.0, 'engineer', '吊索张力检测');

-- ----------------------------
-- 10. 故障工单数据
-- ----------------------------
INSERT INTO `fault_order` (`id`, `order_no`, `device_id`, `report_date`, `fault_desc`, `repair_cost`, `repair_content`, `status`, `close_date`, `handler`) VALUES
('fo-001', 'FLT-202605-0001', 'dev-032', '2026-04-15', '吊索张力异常，需紧急检修', NULL, NULL, 'open', NULL, NULL),
('fo-002', 'FLT-202508-0001', 'dev-007', '2025-08-01', '排水管道堵塞', 2000.00, '疏通管道、更换接头', 'closed', '2025-08-05', 'maintainer'),
('fo-003', 'FLT-202506-0001', 'dev-019', '2025-06-15', '照明灯故障', 3000.00, '更换控制模块', 'closed', '2025-06-20', 'maintainer2'),
('fo-004', 'FLT-202503-0001', 'dev-027', '2025-03-01', '振动监测数据异常', 5000.00, '传感器重新标定', 'closed', '2025-03-05', 'engineer'),
('fo-005', 'FLT-202409-0001', 'dev-004', '2024-09-15', '伸缩缝异响', 8000.00, '清理杂物、润滑处理', 'closed', '2024-09-20', 'engineer');

-- ----------------------------
-- 11. 采购订单数据
-- ----------------------------
INSERT INTO `purchase_order` (`id`, `order_no`, `category`, `quantity`, `unit_price`, `total_amount`, `supplier`, `bridge_id`, `status`, `ship_date`, `receive_date`) VALUES
('po-001', 'PO-202605-0001', 'LT', 5, 50000.00, 250000.00, '飞利浦照明', 'bridge-001', 'pending', NULL, NULL),
('po-002', 'PO-202604-0001', 'MN', 2, 150000.00, 300000.00, '中科院测控', 'bridge-004', 'shipping', '2026-04-20', NULL),
('po-003', 'PO-202603-0001', 'EX', 1, 400000.00, 400000.00, '毛勒伸缩缝', 'bridge-002', 'received', '2026-03-15', '2026-03-25'),
('po-004', 'PO-202512-0001', 'BP', 2, 350000.00, 700000.00, '桥梁支座厂', 'bridge-003', 'received', '2025-12-10', '2025-12-20'),
('po-005', 'PO-202510-0001', 'SE', 1, 200000.00, 200000.00, '安防设备厂', 'bridge-001', 'received', '2025-10-15', '2025-10-25');

-- ----------------------------
-- 12. 报废鉴定数据(部分)
-- ----------------------------
INSERT INTO `scrap_decision` (`id`, `device_id`, `tco_repair`, `tco_replace`, `recommendation`, `conclusion_notes`, `status`, `decision_date`, `decider`) VALUES
('sd-001', 'dev-032', 50000.00, 700000.00, 'repair', '吊索可修复，建议检修后继续使用', 'approved', '2026-04-20', 'scrap');

-- ----------------------------
-- 13. 报废审批数据
-- ----------------------------
INSERT INTO `scrap_approval` (`id`, `decision_id`, `approve_user`, `approve_date`, `approve_result`, `approve_notes`) VALUES
('sa-001', 'sd-001', 'admin', '2026-04-25', 'approved', '同意继续维修方案');

-- ----------------------------
-- 14. 生命周期事件数据(部分)
-- ----------------------------
-- 长江大桥设备生命周期事件
INSERT INTO `lifecycle_event` (`id`, `device_id`, `event_type`, `title`, `description`, `cost`, `operator`, `event_time`) VALUES
('le-001', 'dev-001', 'purchase', '采购入库', '飞利浦LED照明灯组采购入库', 150000.00, 'purchaser', '2011-01-10 10:00:00'),
('le-002', 'dev-001', 'commission', '投入运行', '照明灯组正式投入使用', 0, 'engineer', '2011-03-15 08:00:00'),
('le-003', 'dev-001', 'maintain', '年度保养', 'LED灯珠更换、清洁保养', 5000.00, 'maintainer', '2025-05-01 14:00:00'),
('le-004', 'dev-001', 'maintain', '年度保养', '清洁保养、线路检查', 4500.00, 'maintainer', '2024-05-01 14:00:00'),
('le-005', 'dev-003', 'purchase', '采购入库', '结构应变监测仪采购', 280000.00, 'purchaser', '2011-02-15 10:00:00'),
('le-006', 'dev-003', 'commission', '投入运行', '监测仪正式安装运行', 0, 'engineer', '2011-04-01 09:00:00'),
('le-007', 'dev-003', 'maintain', '年度校准', '传感器校准维护', 8000.00, 'engineer', '2025-03-01 10:00:00'),
('le-008', 'dev-003', 'maintain', '年度校准', '传感器校准维护', 7500.00, 'engineer', '2026-03-01 10:00:00'),
('le-009', 'dev-032', 'purchase', '采购入库', '吊索系统采购入库', 600000.00, 'purchaser', '2012-07-01 10:00:00'),
('le-010', 'dev-032', 'commission', '投入运行', '吊索系统正式投入使用', 0, 'engineer', '2012-11-01 08:00:00'),
('le-011', 'dev-032', 'repair', '故障检修', '吊索张力异常检修', 10000.00, 'engineer', '2026-01-01 10:00:00'),
('le-012', 'dev-032', 'repair', '故障申报', '吊索张力异常待修', 0, 'maintainer', '2026-04-15 09:00:00');

-- 黄河大桥设备生命周期事件
INSERT INTO `lifecycle_event` (`id`, `device_id`, `event_type`, `title`, `description`, `cost`, `operator`, `event_time`) VALUES
('le-013', 'dev-011', 'purchase', '采购入库', '桥面照明系统采购', 120000.00, 'purchaser', '2015-11-15 10:00:00'),
('le-014', 'dev-011', 'commission', '投入运行', '照明系统投入使用', 0, 'engineer', '2016-01-01 08:00:00'),
('le-015', 'dev-011', 'maintain', '日常保养', '清洁照明灯具', 3000.00, 'maintainer2', '2025-01-01 14:00:00');

-- 珠江大桥设备生命周期事件
INSERT INTO `lifecycle_event` (`id`, `device_id`, `event_type`, `title`, `description`, `cost`, `operator`, `event_time`) VALUES
('le-016', 'dev-025', 'purchase', '采购入库', '景观照明灯采购', 200000.00, 'purchaser', '2012-11-01 10:00:00'),
('le-017', 'dev-025', 'commission', '投入运行', '景观灯投入使用', 0, 'engineer', '2013-01-01 20:00:00'),
('le-018', 'dev-025', 'maintain', '年度保养', '景观灯维护检修', 6000.00, 'maintainer', '2025-01-15 14:00:00'),
('le-019', 'dev-027', 'purchase', '采购入库', '振动监测仪采购', 350000.00, 'purchaser', '2012-12-01 10:00:00'),
('le-020', 'dev-027', 'commission', '投入运行', '监测仪安装运行', 0, 'engineer', '2013-02-01 09:00:00'),
('le-021', 'dev-027', 'repair', '故障维修', '振动数据异常修复', 5000.00, 'engineer', '2025-03-05 10:00:00');

-- 松花江大桥设备生命周期事件
INSERT INTO `lifecycle_event` (`id`, `device_id`, `event_type`, `title`, `description`, `cost`, `operator`, `event_time`) VALUES
('le-022', 'dev-019', 'purchase', '采购入库', '防冻照明灯采购', 180000.00, 'purchaser', '2018-11-01 10:00:00'),
('le-023', 'dev-019', 'commission', '投入运行', '防冻灯投入使用', 0, 'engineer', '2019-01-15 08:00:00'),
('le-024', 'dev-019', 'repair', '故障维修', '照明灯故障修复', 3000.00, 'maintainer2', '2025-06-20 10:00:00');

-- 青藏高原大桥设备生命周期事件
INSERT INTO `lifecycle_event` (`id`, `device_id`, `event_type`, `title`, `description`, `cost`, `operator`, `event_time`) VALUES
('le-025', 'dev-035', 'purchase', '采购入库', '高原照明灯采购', 220000.00, 'purchaser', '2020-11-01 10:00:00'),
('le-026', 'dev-035', 'commission', '投入运行', '高原灯投入使用', 0, 'engineer', '2021-01-15 08:00:00'),
('le-027', 'dev-038', 'purchase', '采购入库', '高原桥面板采购', 3000000.00, 'purchaser', '2020-09-01 10:00:00');

-- 审计日志数据(示例)
INSERT INTO `sys_audit_log` (`id`, `operator`, `operator_name`, `action`, `target_type`, `target_id`, `detail`, `operate_time`) VALUES
('al-001', 'admin', '管理员', 'create', 'device', 'dev-038', '{"device_code": "DEV-CS-BR05-0001", "device_name": "高原桥面板"}', '2026-05-09 10:00:00'),
('al-002', 'scrap', '鉴定员周八', 'create', 'scrap_decision', 'sd-001', '{"recommendation": "repair"}', '2026-04-20 14:00:00'),
('al-003', 'admin', '管理员', 'status_change', 'scrap_decision', 'sd-001', '{"from": "pending_approval", "to": "approved"}', '2026-04-25 15:00:00');