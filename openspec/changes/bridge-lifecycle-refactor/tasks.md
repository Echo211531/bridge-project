## 1. 项目初始化

- [ ] 1.1 创建后端项目结构（Spring Boot 3）
- [ ] 1.2 创建前端项目结构（Vue 3 + Vite）
- [ ] 1.3 配置 Maven pom.xml 依赖
- [ ] 1.4 配置前端 package.json 依赖
- [ ] 1.5 配置数据库连接（application.yml）
- [ ] 1.6 创建数据库表结构脚本（schema.sql）
- [ ] 1.7 创建种子数据脚本（data.sql）

## 2. 后端基础架构

- [ ] 2.1 创建通用响应类 Result<T>
- [ ] 2.2 创建 MyBatis-Plus 配置类（分页插件、自动填充）
- [ ] 2.3 创建跨域配置类 CorsConfig
- [ ] 2.4 创建 Knife4j API 文档配置
- [ ] 2.5 创建工具类（PasswordUtils、DateUtils、CodeGenerator）

## 3. 实体类创建（17 张表）

- [ ] 3.1 创建 SysUser 实体类
- [ ] 3.2 创建 SysRole 实体类
- [ ] 3.3 创建 SysRoleMenu 实体类
- [ ] 3.4 创建 SysConfig 实体类
- [ ] 3.5 创建 SysAuditLog 实体类
- [ ] 3.6 创建 Bridge 实体类
- [ ] 3.7 创建 BridgeAadtHistory 实体类
- [ ] 3.8 创建 MonitoringRecord 实体类
- [ ] 3.9 创建 DeviceArchive 实体类
- [ ] 3.10 创建 DeviceCategory 实体类
- [ ] 3.11 创建 MaintainPlan 实体类
- [ ] 3.12 创建 MaintainRecord 实体类
- [ ] 3.13 创建 FaultOrder 实体类
- [ ] 3.14 创建 PurchaseOrder 实体类
- [ ] 3.15 创建 ScrapDecision 实体类
- [ ] 3.16 创建 ScrapApproval 实体类
- [ ] 3.17 创建 LifecycleEvent 实体类

## 4. Mapper 接口创建

- [ ] 4.1 创建 UserMapper 接口
- [ ] 4.2 创建 RoleMapper 接口
- [ ] 4.3 创建 RoleMenuMapper 接口
- [ ] 4.4 创建 ConfigMapper 接口
- [ ] 4.5 创建 AuditLogMapper 接口
- [ ] 4.6 创建 BridgeMapper 接口
- [ ] 4.7 创建 BridgeAadtHistoryMapper 接口
- [ ] 4.8 创建 MonitoringRecordMapper 接口
- [ ] 4.9 创建 DeviceArchiveMapper 接口
- [ ] 4.10 创建 DeviceCategoryMapper 接口
- [ ] 4.11 创建 MaintainPlanMapper 接口
- [ ] 4.12 创建 MaintainRecordMapper 接口
- [ ] 4.13 创建 FaultOrderMapper 接口
- [ ] 4.14 创建 PurchaseOrderMapper 接口
- [ ] 4.15 创建 ScrapDecisionMapper 接口
- [ ] 4.16 创建 ScrapApprovalMapper 接口
- [ ] 4.17 创建 LifecycleEventMapper 接口

## 5. 登录认证模块

- [ ] 5.1 创建 LoginDTO 数据传输对象
- [ ] 5.2 创建 LoginVO 视图对象（用户信息 + 菜单权限）
- [ ] 5.3 创建 AuthService 服务类（登录校验、会话管理）
- [ ] 5.4 创建 AuthController 控制器（login、logout 接口）
- [ ] 5.5 实现用户状态检查（冻结账户拦截）
- [ ] 5.6 实现菜单权限查询（根据 role_code）

## 6. 用户管理模块

- [ ] 6.1 创建 UserDTO、UserUpdateDTO 数据传输对象
- [ ] 6.2 创建 UserVO 视图对象
- [ ] 6.3 创建 UserService 服务类（CRUD、状态切换、密码重置）
- [ ] 6.4 创建 UserController 控制器
- [ ] 6.5 实现用户列表分页查询
- [ ] 6.6 实现用户详情查询
- [ ] 6.7 实现用户创建（用户名唯一校验）
- [ ] 6.8 实现用户更新
- [ ] 6.9 实现用户启用/禁用
- [ ] 6.10 实现密码重置
- [ ] 6.11 实现用户删除（逻辑删除）
- [ ] 6.12 创建 RoleService 和 RoleController

## 7. 桥梁管理模块

- [ ] 7.1 创建 BridgeDTO、BridgeUpdateDTO 数据传输对象
- [ ] 7.2 创建 BridgeVO、BridgeDetailVO 视图对象
- [ ] 7.3 创建 BridgeService 服务类（α/β 系数计算）
- [ ] 7.4 创建 BridgeController 控制器
- [ ] 7.5 实现 α 气候系数计算（5 种气候带对应值）
- [ ] 7.6 实现 β AADT 系数计算（5 种范围对应值）
- [ ] 7.7 实现桥梁列表查询
- [ ] 7.8 实现桥梁详情查询
- [ ] 7.9 实现桥梁创建（编码唯一校验）
- [ ] 7.10 实现桥梁更新
- [ ] 7.11 实现 AADT 更新（记录历史变更）
- [ ] 7.12 实现 AADT 历史查询

## 8. 设备档案模块

- [ ] 8.1 创建 DeviceDTO、DeviceUpdateDTO 数据传输对象
- [ ] 8.2 创建 DeviceVO、DeviceDetailVO 视图对象（含残值）
- [ ] 8.3 创建 DeviceService 服务类（残值估算、编码生成）
- [ ] 8.4 创建 DeviceController 控制器
- [ ] 8.5 实现设备自动编码规则（DEV-类别-桥梁-序号）
- [ ] 8.6 实现残值估算算法（直线折旧 × 市场折扣）
- [ ] 8.7 实现设备列表分页查询（多条件筛选）
- [ ] 8.8 实现设备详情查询（含残值计算）
- [ ] 8.9 实现设备创建（自动生成生命周期事件）
- [ ] 8.10 实现设备更新
- [ ] 8.11 实现超期保养设备查询
- [ ] 8.12 实现设备分类查询
- [ ] 8.13 实现设备状态流转逻辑

## 9. 保养管理模块

- [ ] 9.1 创建 MaintainRecordDTO 数据传输对象
- [ ] 9.2 创建 MaintainPlanVO、MaintainRecordVO 视图对象
- [ ] 9.3 创建 MaintainService 服务类（动态周期计算）
- [ ] 9.4 创建 MaintainController 控制器
- [ ] 9.5 实现动态保养周期计算（标准周期 × α × β）
- [ ] 9.6 实现保养计划列表查询
- [ ] 9.7 实现保养记录列表查询
- [ ] 9.8 实现保养记录上报（生成生命周期事件）
- [ ] 9.9 实现保养完成后自动生成下次计划
- [ ] 9.10 实现保养完成率统计

## 10. 故障工单模块

- [ ] 10.1 创建 FaultOrderDTO、FaultCloseDTO 数据传输对象
- [ ] 10.2 创建 FaultOrderVO 视图对象
- [ ] 10.3 创建 FaultService 服务类（MTTR/MTBF 计算）
- [ ] 10.4 创建 FaultController 控制器
- [ ] 10.5 实现工单自动编码规则（FLT-年月-序号）
- [ ] 10.6 实现工单列表分页查询（状态筛选）
- [ ] 10.7 实现故障申报（设备状态变更、生成事件）
- [ ] 10.8 实现工单处理（状态流转）
- [ ] 10.9 实现工单关闭（设备状态恢复、MTTR 计算）
- [ ] 10.10 实现 MTTR 计算
- [ ] 10.11 实现 MTBF 计算
- [ ] 10.12 实现年故障率统计
- [ ] 10.13 实现工单关闭率统计

## 11. 采购订单模块

- [ ] 11.1 创建 PurchaseOrderDTO、PurchaseStatusDTO 数据传输对象
- [ ] 11.2 创建 PurchaseOrderVO 视图对象
- [ ] 11.3 创建 PurchaseService 服务类（订单流转）
- [ ] 11.4 创建 PurchaseController 控制器
- [ ] 11.5 实现订单自动编码规则（PO-年月-序号）
- [ ] 11.6 实现订单列表查询（状态筛选）
- [ ] 11.7 实现订单创建（总金额计算）
- [ ] 11.8 实现订单状态流转（pending→shipping→received）
- [ ] 11.9 实现订单详情查询

## 12. 报废鉴定模块（TCO 核心）

- [ ] 12.1 创建 ScrapCandidateVO、TcoDecisionVO 视图对象
- [ ] 12.2 创建 ScrapDecisionDTO 数据传输对象
- [ ] 12.3 创建 ScrapService 服务类（TCO 决策算法）
- [ ] 12.4 创建 ScrapController 控制器
- [ ] 12.5 实现待鉴定设备筛选（寿命警戒线、故障次数）
- [ ] 12.6 实现 TCO 维修方案计算（修复费 + 未来保养费 + 未来维修费）
- [ ] 12.7 实现 TCO 更换方案计算（新设备价 + 未来保养费 - 残值）
- [ ] 12.8 实现推荐结论生成逻辑
- [ ] 12.9 实现阈值参数影响（残值警戒线、修复警戒线、寿命警戒线）
- [ ] 12.10 实现三栏决策面板数据
- [ ] 12.11 实现鉴定结论提交
- [ ] 12.12 实现鉴定历史查询
- [ ] 12.13 实现报废审批流程

## 13. 生命周期模块

- [ ] 13.1 创建 LifecycleEventVO、LifecycleSummaryVO 视图对象
- [ ] 13.2 创建 LifecycleService 服务类
- [ ] 13.3 创建 LifecycleController 控制器
- [ ] 13.4 实现设备生命周期事件查询
- [ ] 13.5 实现生命周期汇总查询（累计 TCO）
- [ ] 13.6 实现事件自动生成规则（6 种事件类型）
- [ ] 13.7 实现生命周期成本追溯

## 14. 统计分析模块

- [ ] 14.1 创建 DashboardVO、StatsOverviewVO 视图对象
- [ ] 14.2 创建 StatsService 服务类（8 项关键率）
- [ ] 14.3 创建 StatsController 控制器
- [ ] 14.4 实现设备在用率计算
- [ ] 14.5 实现设备完好率计算
- [ ] 14.6 实现年故障率计算
- [ ] 14.7 实现保养完成率计算
- [ ] 14.8 实现工单关闭率计算
- [ ] 14.9 实现 MTBF 计算
- [ ] 14.10 实现 MTTR 计算
- [ ] 14.11 实现累计运维成本计算
- [ ] 14.12 实现 30 天费用趋势查询
- [ ] 14.13 实现待办事项查询
- [ ] 14.14 实现设备分类分布统计
- [ ] 14.15 实现故障排行统计
- [ ] 14.16 实现 TCO 对比统计

## 15. 系统配置模块

- [ ] 15.1 创建 ConfigVO、ConfigUpdateDTO 数据传输对象
- [ ] 15.2 创建 ConfigService 服务类
- [ ] 15.3 创建 ConfigController 控制器
- [ ] 15.4 实现配置查询
- [ ] 15.5 实现 TCO 阈值配置更新（残值、修复、寿命警戒线）
- [ ] 15.6 实现配置值范围约束校验
- [ ] 15.7 实现配置实时生效

## 16. 审计日志模块

- [ ] 16.1 创建 AuditLogVO 视图对象
- [ ] 16.2 创建 AuditService 服务类
- [ ] 16.3 创建 AuditController 控制器
- [ ] 16.4 实现审计日志分页查询
- [ ] 16.5 实现审计日志自动记录（所有写操作）
- [ ] 16.6 实现多条件筛选（操作类型、操作人、时间范围）

## 17. 前端基础架构

- [ ] 17.1 创建 Axios 实例配置（api/index.js）
- [ ] 17.2 创建路由配置（router/index.js）
- [ ] 17.3 创建路由守卫（router/guards.js）
- [ ] 17.4 创建 Pinia Store 入口（stores/index.js）
- [ ] 17.5 创建用户状态管理（stores/user.js）
- [ ] 17.6 创建菜单状态管理（stores/menu.js）
- [ ] 17.7 创建布局组件（components/layout/AppLayout.vue）
- [ ] 17.8 创建侧边栏组件（components/layout/Sidebar.vue）
- [ ] 17.9 创建顶部栏组件（components/layout/Header.vue）

## 18. 前端 API 模块

- [ ] 18.1 创建登录 API（api/auth.js）
- [ ] 18.2 创建用户管理 API（api/user.js）
- [ ] 18.3 创建桥梁管理 API（api/bridge.js）
- [ ] 18.4 创建设备管理 API（api/device.js）
- [ ] 18.5 创建保养管理 API（api/maintain.js）
- [ ] 18.6 创建故障工单 API（api/fault.js）
- [ ] 18.7 创建采购订单 API（api/purchase.js）
- [ ] 18.8 创建报废鉴定 API（api/scrap.js）
- [ ] 18.9 创建生命周期 API（api/lifecycle.js）
- [ ] 18.10 创建统计分析 API（api/stats.js）
- [ ] 18.11 创建系统配置 API（api/config.js）
- [ ] 18.12 创建审计日志 API（api/audit.js）

## 19. 前端页面开发

- [ ] 19.1 创建登录页面（views/Login.vue）
- [ ] 19.2 创建首页仪表盘（views/Dashboard.vue）
- [ ] 19.3 创建全生命周期页面（views/Lifecycle.vue）
- [ ] 19.4 创建桥梁管理页面（views/Bridges.vue）
- [ ] 19.5 创建设备档案页面（views/Devices.vue）
- [ ] 19.6 创建保养计划页面（views/Maintain.vue）
- [ ] 19.7 创建故障工单页面（views/Fault.vue）
- [ ] 19.8 创建采购订单页面（views/Purchase.vue）
- [ ] 19.9 创建报废鉴定页面（views/Scrap.vue）
- [ ] 19.10 创建报废鉴定详情页面（views/ScrapDetail.vue）
- [ ] 19.11 创建统计分析页面（views/Stats.vue）
- [ ] 19.12 创建系统配置页面（views/Config.vue）
- [ ] 19.13 创建用户管理页面（views/Users.vue）
- [ ] 19.14 创建审计日志页面（views/Audit.vue）

## 20. 前端业务组件

- [ ] 20.1 创建统计卡片组件（components/common/StatCard.vue）
- [ ] 20.2 创建 ECharts 图表容器组件（components/common/Chart.vue）
- [ ] 20.3 创建状态标签组件（components/common/Tag.vue）
- [ ] 20.4 创建通用弹窗组件（components/common/Modal.vue）
- [ ] 20.5 创建通用抽屉组件（components/common/Drawer.vue）
- [ ] 20.6 创建生命周期时间轴组件（components/business/LifecycleTimeline.vue）
- [ ] 20.7 创建 TCO 决策面板组件（components/business/TcoPanel.vue）
- [ ] 20.8 创建待办事项列表组件（components/business/TodoList.vue）

## 21. 联调测试

- [ ] 21.1 前后端接口联调测试
- [ ] 21.2 登录认证流程测试
- [ ] 21.3 TCO 决策算法精度验证（对比原版结果）
- [ ] 21.4 8 项关键率统计验证
- [ ] 21.5 α×β 动态周期计算验证
- [ ] 21.6 残值估算算法验证
- [ ] 21.7 ECharts 图表样式一致性检查
- [ ] 21.8 角色权限隔离测试
- [ ] 21.9 种子数据完整性验证

## 22. 部署准备

- [ ] 22.1 配置 Vite 生产构建
- [ ] 22.2 配置后端生产环境参数
- [ ] 22.3 编写部署说明文档
- [ ] 22.4 确保原版 index.html 作为备份保留