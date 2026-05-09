## Why

原版系统为单文件 SPA（index.html，3683 行 JSX），使用 IndexedDB 作为数据存储，仅适合本地演示。为满足毕业设计答辩要求并提升系统架构水平，需要重构为前后端分离架构，实现真正的生产级部署能力。

核心问题：
- 单文件架构无法进行生产部署
- IndexedDB 数据不持久、无法共享
- 前后端耦合导致代码难以维护
- 缺乏标准的 RESTful API 设计

## What Changes

将单文件 SPA 重构为前后端分离架构：

- **前端重构**：从 React 18.2 重构为 Vue 3 + Element Plus + Tailwind CSS + Vite
- **后端新建**：Java 21 + Spring Boot 3 + Maven + MyBatis-Plus 后端服务
- **数据库迁移**：从 IndexedDB（17 张 ObjectStore）迁移到云 MySQL（17 张物理表）
- **API 设计**：完整 RESTful API 接口规范（约 40+ 接口）
- **认证迁移**：保留简化登录逻辑（用户名密码直接校验），适配后端 Session
- **核心算法迁移**：TCO 决策算法、α×β 动态保养周期、残值估算、8 项关键率统计
- **种子数据迁移**：5 桥梁 / 38 设备 / 8 用户 / 36 月历史数据

**保留功能**：13 个业务模块完整迁移，包括登录认证、仪表盘、全生命周期、桥梁参数、设备档案、保养计划、故障工单、采购入库、报废鉴定、统计分析、阈值配置、用户管理、审计日志。

## Capabilities

### New Capabilities

- `auth`: 用户登录认证与会话管理
- `user-management`: 用户 CRUD、角色权限、密码重置、状态切换
- `bridge-management`: 桥梁档案管理、α 气候系数、β AADT 系数、AADT 历史记录
- `device-management`: 设备档案管理、设备分类、残值估算、状态流转
- `maintain-management`: 保养计划、保养记录、动态周期计算（α×β）
- `fault-management`: 故障工单申报、处理、关闭、MTTR/MTBF 统计
- `purchase-management`: 采购订单创建、状态流转（待发货→运输中→已入库）
- `scrap-management`: 报废技术鉴定、TCO 决策算法（论文核心）、三栏决策面板
- `lifecycle-management`: 设备生命周期事件流水、时间轴展示、累计 TCO
- `stats-analysis`: 仪表盘 8 项关键率、费用趋势图表、故障排行、TCO 对比
- `system-config`: TCO 阈值参数配置（残值警戒线、修复警戒线、寿命警戒线）
- `audit-log`: 操作审计日志、可追溯查询

### Modified Capabilities

无。此为全新架构重构，所有 capabilities 为新建。

## Impact

**代码影响**：
- 新建 `frontend/` 目录（Vue 3 项目）
- 新建 `backend/` 目录（Spring Boot 3 项目）
- 原版 `index.html` 作为参考保留

**API 影响**：
- 新增约 40+ RESTful API 接口
- 基础路径 `/api/v1`
- 统一响应格式 `{code, message, data}`

**数据库影响**：
- 新建 17 张 MySQL 表（sys_user, sys_role, bridge, device_archive 等）
- 种子数据约 600+ 条记录

**依赖影响**：
- 前端：Vue 3、Element Plus、Tailwind CSS、ECharts、Axios、Pinia
- 后端：Spring Boot 3、MyBatis-Plus、MySQL Connector、Lombok、Hutool、Knife4j

**运行环境**：
- 前端开发服务器：Vite（端口 5173）
- 后端服务：Spring Boot（端口 8080）
- API 文档：Knife4j Swagger（端口 8080/doc.html）