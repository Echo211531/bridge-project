---
name: bridge-lifecycle-refactor-design
description: 桥梁设备全生命周期管理系统前后端分离重构设计方案
type: project
---

# 桥梁设备全生命周期管理系统 — 前后端分离重构设计方案

> 创建日期：2026-05-09
> 项目背景：华东交通大学软件学院 2026 届毕业设计答辩交付版重构

## 一、项目概述

### 1.1 重构目标

将单文件 SPA（index.html，3683 行 JSX）重构为前后端分离架构：

- **前端**：Vue 3 + Element Plus + Tailwind CSS + Vite
- **后端**：Java 21 + Spring Boot 3 + Maven + MyBatis-Plus
- **数据库**：云 MySQL（已就绪）
- **认证方式**：沿用原版简化逻辑（用户名密码直接校验，无 Token）

### 1.2 原版系统分析

| 特性 | 原版实现 |
|------|---------|
| 技术栈 | React 18.2 + IndexedDB + Tailwind CSS + ECharts |
| 数据存储 | 17 张 IndexedDB ObjectStore |
| 业务模块 | 13 个页面模块 |
| 核心功能 | TCO 决策算法、α×β 动态保养周期、8 项关键率统计 |
| 种子数据 | 5 桥梁 / 38 设备 / 8 用户 / 36 月历史 |

---

## 二、项目整体架构

### 2.1 目录结构（Monorepo）

```
bridge-project/
├── frontend/                    # Vue 3 前端项目
│   ├── src/
│   │   ├── api/                 # API 请求模块
│   │   ├── components/          # 公共组件
│   │   ├── views/               # 页面视图（13 个模块）
│   │   ├── router/              # 路由配置
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── styles/              # Tailwind CSS 样式
│   │   └ utils/                 # 工具函数
│   │   └ App.vue
│   │   └ main.js
│   ├── vite.config.js
│   ├── package.json
│   └── tailwind.config.js
│
├── backend/                     # Spring Boot 3 后端项目
│   ├── src/main/java/
│   │   └ com/bridge/lifecycle/
│   │     ├── controller/        # 控制层（13 个模块）
│   │     ├── service/           # 业务层（TCO 算法等）
│   │     ├── mapper/            # MyBatis-Plus Mapper
│   │     ├── entity/            # 实体类（17 张表）
│   │     ├── dto/               # 数据传输对象
│   │     ├── vo/                # 视图对象
│   │     ├── config/            # 配置类
│   │     └ utils/               # 工具类
│   │     └ BridgeLifecycleApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml      # 配置文件
│   │   ├── db/                  # 数据库脚本
│   │   │   ├── schema.sql      # 表结构
│   │   │   └ data.sql          # 种子数据
│   │   ├── mapper/              # XML 映射文件
│   ├── pom.xml                  # Maven 依赖
│
├── docs/                        # 文档目录
│   ├── superpowers/specs/       # 设计规格文档
│   ├── api/                     # API 文档
│
├── README.md                    # 项目说明
└── .gitignore
```

### 2.2 运行架构

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端开发服务器 | 5173 | Vite 启动，代理 `/api` 到后端 |
| 后端服务 | 8080 | Spring Boot 启动 |
| API 文档 | 8080/doc.html | Knife4j Swagger UI |
| 云 MySQL | 云服务商端口 | 生产数据库 |

---

## 三、数据库设计

### 3.1 表结构总览（17 张表）

| 序号 | 表名 | 中文名 | 主键 | 核心字段 |
|------|------|-------|------|---------|
| 1 | sys_user | 系统用户 | VARCHAR(36) | username, password, real_name, role_code, status |
| 2 | sys_role | 系统角色 | VARCHAR(36) | code, name, description |
| 3 | sys_role_menu | 角色-菜单 | VARCHAR(36) | role_code, menu_code |
| 4 | sys_config | 系统配置 | VARCHAR(64) | config_key, config_value, description |
| 5 | sys_audit_log | 审计日志 | VARCHAR(36) | operator, action, target, detail, operate_time |
| 6 | bridge | 桥梁档案 | VARCHAR(36) | bridge_code, bridge_name, climate_zone, aadt, alpha_coef, beta_coef |
| 7 | bridge_aadt_history | AADT 历史 | VARCHAR(36) | bridge_id, aadt, source, changed_at |
| 8 | monitoring_record | 监测推送 | VARCHAR(36) | bridge_id, reported_aadt, pushed_at |
| 9 | device_archive | 设备档案 | VARCHAR(36) | device_code, category, bridge_id, status, purchase_cost, in_use_date |
| 10 | device_category | 设备分类 | VARCHAR(20) | code, name, design_life_years, maintain_cycle_days |
| 11 | maintain_plan | 保养计划 | VARCHAR(36) | device_id, plan_date, cycle_days, status |
| 12 | maintain_record | 保养记录 | VARCHAR(36) | device_id, record_date, actual_cost, manhour, operator |
| 13 | fault_order | 故障工单 | VARCHAR(36) | device_id, report_date, fault_desc, repair_cost, status |
| 14 | purchase_order | 采购订单 | VARCHAR(36) | order_no, category, quantity, total_amount, status |
| 15 | scrap_decision | 报废鉴定 | VARCHAR(36) | device_id, tco_repair, tco_replace, recommendation, status |
| 16 | scrap_approval | 报废审批 | VARCHAR(36) | decision_id, approve_user, approve_date |
| 17 | lifecycle_event | 生命周期事件 | VARCHAR(36) | device_id, event_type, title, cost, event_time |

### 3.2 数据库配置规范

| 配置项 | 值 | 说明 |
|-------|-----|------|
| 字符集 | UTF-8MB4 | 支持中文和特殊字符 |
| 存储引擎 | InnoDB | 支持事务和外键 |
| 主键类型 | VARCHAR(36) | UUID 格式 |
| 外键策略 | 逻辑关联 | 不建物理外键，便于迁移 |
| 逻辑删除 | is_deleted | 业务表统一使用 |
| 时间字段 | DATETIME | 默认 CURRENT_TIMESTAMP |

---

## 四、后端架构设计

### 4.1 Maven 依赖配置

| 类别 | 依赖 | 版本 | 用途 |
|------|------|------|------|
| 核心 | spring-boot-starter-web | 3.x | Web 服务 |
| 核心 | spring-boot-starter-validation | 3.x | 参数校验 |
| 数据库 | mysql-connector-java | 8.x | MySQL 驱动 |
| 数据库 | mybatis-plus-boot-starter | 3.5.x | ORM 框架 |
| 工具 | lombok | 最新 | 简化代码 |
| 工具 | hutool | 5.x | Java 工具库 |
| 文档 | knife4j | 4.x | API 文档 |

### 4.2 分层结构

```
com.bridge.lifecycle/
├── controller/           # 控制层 — 接收请求、参数校验
│   ├── UserController
│   ├── BridgeController
│   ├── DeviceController
│   ├── MaintainController
│   ├── FaultController
│   ├── PurchaseController
│   ├── ScrapController
│   ├── LifecycleController
│   ├── StatsController
│   ├── ConfigController
│   ├── AuditController
│   └── AuthController     # 登录认证
│
├── service/              # 业务层 — 核心业务逻辑
│   ├── UserService
│   ├── BridgeService
│   ├── DeviceService
│   ├── MaintainService
│   ├── FaultService
│   ├── PurchaseService
│   ├── ScrapService       # TCO 决策算法核心
│   ├── LifecycleService
│   ├── StatsService       # 统计分析、8 项关键率计算
│   ├── ConfigService
│   ├── AuditService
│   └ AuthService          # 登录校验
│
├── mapper/               # 数据访问层 — MyBatis-Plus Mapper
│   ├── UserMapper
│   ├── BridgeMapper
│   ├── ...（17 个 Mapper）
│
├── entity/               # 实体类 — 对应数据库表
│   ├── SysUser.java
│   ├── Bridge.java
│   ├── ...（17 个 Entity）
│
├── dto/                  # 数据传输对象 — 接收请求参数
│   ├── LoginDTO.java
│   ├── DeviceCreateDTO.java
│   ├── ScrapDecisionDTO.java
│   ├── ...
│
├── vo/                   # 视图对象 — 返回给前端
│   ├── DashboardVO.java
│   ├── DeviceDetailVO.java
│   ├── TcoDecisionVO.java
│   ├── ...
│
├── config/               # 配置类
│   ├── MyBatisPlusConfig.java    # 分页插件、自动填充
│   ├── CorsConfig.java           # 跨域配置
│   ├── Knife4jConfig.java        # API 文档配置
│
└── utils/                # 工具类
    ├── PasswordUtils.java        # 密码哈希
    ├── DateUtils.java            # 日期处理
    ├── CodeGenerator.java        # 编码生成
```

---

## 五、前端架构设计

### 5.1 项目结构

```
frontend/
├── public/
│   └── favicon.ico
│
├── src/
│   ├── api/                      # API 请求模块
│   │   ├── index.js              # Axios 实例配置
│   │   ├── auth.js               # 登录相关 API
│   │   ├── user.js               # 用户管理 API
│   │   ├── bridge.js             # 桥梁管理 API
│   │   ├── device.js             # 设备管理 API
│   │   ├── maintain.js           # 保养管理 API
│   │   ├── fault.js              # 故障工单 API
│   │   ├── purchase.js           # 采购订单 API
│   │   ├── scrap.js              # 报废鉴定 API
│   │   ├── lifecycle.js          # 生命周期 API
│   │   ├── stats.js              # 统计分析 API
│   │   ├── config.js             # 系统配置 API
│   │   └── audit.js              # 审计日志 API
│   │
│   ├── components/               # 公共组件
│   │   ├── layout/
│   │   │   ├── AppLayout.vue     # 整体布局
│   │   │   ├── Sidebar.vue       # 侧边栏菜单
│   │   │   └── Header.vue        # 顶部栏
│   │   ├── common/
│   │   │   ├── StatCard.vue      # 统计卡片
│   │   │   ├── Chart.vue         # ECharts 图表容器
│   │   │   ├── Tag.vue           # 状态标签
│   │   │   ├── Modal.vue         # 通用弹窗
│   │   │   └── Drawer.vue        # 通用抽屉
│   │   └── business/
│   │   │   ├── LifecycleTimeline.vue  # 生命周期时间轴
│   │   │   ├── TcoPanel.vue           # TCO 决策面板
│   │   │   └── TodoList.vue           # 待办事项列表
│   │
│   ├── views/                    # 页面视图（13 个模块）
│   │   ├── Login.vue             # 登录页
│   │   ├── Dashboard.vue         # 首页仪表盘
│   │   ├── Lifecycle.vue         # 全生命周期
│   │   ├── Bridges.vue           # 桥梁基础参数
│   │   ├── Devices.vue           # 设备档案管理
│   │   ├── Maintain.vue          # 保养计划管理
│   │   ├── Fault.vue             # 故障维修工单
│   │   ├── Purchase.vue          # 采购入库
│   │   ├── Scrap.vue             # 报废技术鉴定
│   │   ├── ScrapDetail.vue       # 报废鉴定详情
│   │   ├── Stats.vue             # 统计分析
│   │   ├── Config.vue            # 阈值参数配置
│   │   ├── Users.vue             # 用户与角色
│   │   └── Audit.vue             # 审计日志
│   │
│   ├── router/
│   │   ├── index.js              # 路由配置
│   │   └── guards.js             # 路由守卫
│   │
│   ├── stores/                   # Pinia 状态管理
│   │   ├── index.js              # Store 入口
│   │   ├── user.js               # 用户状态
│   │   └── menu.js               # 菜单状态
│   │
│   ├── utils/
│   │   ├── format.js             # 格式化工具
│   │   ├── constants.js          # 常量定义
│   │   ├── climate.js            # α 气候系数
│   │   └── aadt.js               # β AADT 系数
│   │
│   ├── styles/
│   │   ├── main.css              # Tailwind 入口
│   │   └── element-vars.css      # Element Plus 主题
│   │
│   ├── App.vue
│   └── main.js
│
├── vite.config.js                # Vite 配置
├── tailwind.config.js            # Tailwind 配置
├── package.json
└── index.html
```

### 5.2 页面与原版对照

| 原版 React 组件 | Vue 3 页面 |
|----------------|-----------|
| LoginPage | Login.vue |
| DashboardPage | Dashboard.vue |
| LifecyclePage + LifecycleTimeline | Lifecycle.vue |
| BridgesPage + BridgeEditDrawer | Bridges.vue |
| DevicesPage + DeviceEditDrawer | Devices.vue |
| MaintainPage | Maintain.vue |
| FaultPage | Fault.vue |
| PurchasePage | Purchase.vue |
| ScrapPage + ScrapDetailPage | Scrap.vue + ScrapDetail.vue |
| StatsPage | Stats.vue |
| ConfigPage | Config.vue |
| UsersPage | Users.vue |
| AuditPage | Audit.vue |

---

## 六、RESTful API 接口设计

### 6.1 基础规范

- 基础路径：`/api/v1`
- 资源命名：复数形式
- 操作风格：RESTful（GET/POST/PUT/DELETE）

### 6.2 统一响应格式

```json
// 成功响应
{
  "code": 200,
  "message": "success",
  "data": { ... }
}

// 失败响应
{
  "code": 400,
  "message": "参数校验失败",
  "data": null
}
```

### 6.3 核心接口清单

#### 认证模块 `/api/v1/auth`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/login` | 用户登录 |
| POST | `/logout` | 用户登出 |

#### 用户管理 `/api/v1/users`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 用户列表（分页） |
| GET | `/{id}` | 用户详情 |
| POST | `/` | 新建用户 |
| PUT | `/{id}` | 更新用户 |
| PUT | `/{id}/status` | 启用/禁用用户 |
| PUT | `/{id}/password` | 重置密码 |
| DELETE | `/{id}` | 删除用户 |

#### 桥梁管理 `/api/v1/bridges`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 桥梁列表 |
| GET | `/{id}` | 桥梁详情 |
| POST | `/` | 新建桥梁 |
| PUT | `/{id}` | 更新桥梁 |
| PUT | `/{id}/aadt` | 更新 AADT |
| GET | `/{id}/aadt-history` | AADT 历史记录 |

#### 设备管理 `/api/v1/devices`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 设备列表（分页） |
| GET | `/{id}` | 设备详情（含残值） |
| POST | `/` | 新增设备 |
| PUT | `/{id}` | 更新设备 |
| GET | `/overdue-maintain` | 超期保养设备 |

#### 保养管理 `/api/v1/maintains`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/plans` | 保养计划列表 |
| GET | `/records` | 保养记录列表 |
| POST | `/records` | 上报保养记录 |

#### 故障工单 `/api/v1/faults`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 工单列表（分页） |
| POST | `/` | 故障申报 |
| PUT | `/{id}/close` | 关闭工单 |

#### 采购管理 `/api/v1/purchases`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 订单列表 |
| POST | `/` | 新建订单 |
| PUT | `/{id}/status` | 推进订单状态 |

#### 报废鉴定 `/api/v1/scrap`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/candidates` | 待鉴定设备列表 |
| GET | `/decisions` | 鉴定历史 |
| GET | `/tco/{deviceId}` | TCO 决策计算 |
| POST | `/decisions` | 提交鉴定结论 |

#### 生命周期 `/api/v1/lifecycle`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/events/{deviceId}` | 设备生命周期事件 |
| GET | `/summary/{deviceId}` | 设备生命周期汇总 |

#### 统计分析 `/api/v1/stats`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/dashboard` | 仪表盘汇总数据 |
| GET | `/overview` | 统计分析数据 |

#### 系统配置 `/api/v1/config`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 获取所有配置 |
| PUT | `/{key}` | 更新配置值 |

#### 审计日志 `/api/v1/audit`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 审计日志列表（分页） |

---

## 七、核心业务逻辑迁移

### 7.1 TCO 决策算法（论文核心）

**算法位置**：`ScrapService.tcoDecision()`

**计算逻辑**：

1. 获取设备档案与系统阈值配置
2. 计算已用年限与寿命比率
3. 计算当前残值（直线折旧 × 市场折扣）
4. 阈值前置判断：修复成本超过残值阈值 → 直接建议报废
5. 计算 TCO 维修方案：修复费 + N 年保养费 + N 年维修费
6. 计算 TCO 更换方案：新设备购置价 + N 年保养费 - 旧设备残值
7. 寿命警戒线判断：超过警戒阈值 → 建议更换
8. 经济性对比判断：TCO 差额分析，输出推荐方案

### 7.2 残值估算算法

**公式**：`残值 = 购置价 × (1 - 已用年限/设计寿命) × 市场折扣系数`

**实现位置**：`DeviceService.calcResidual()`

### 7.3 α × β 动态保养周期

**公式**：`实际保养周期 = 标准周期 × α × β`

**α 气候系数**：

| 气候带 | α 值 |
|-------|------|
| 寒冷 | 1.30 |
| 严寒 | 1.20 |
| 温和 | 1.00 |
| 湿热 | 1.15 |
| 沿海 | 1.25 |

**β AADT 系数**：

| AADT 范围 | β 值 |
|----------|------|
| < 1万 | 0.85 |
| 1-3万 | 1.00 |
| 3-6万 | 1.15 |
| 6-10万 | 1.30 |
| > 10万 | 1.50 |

### 7.4 统计分析 — 8 项关键率

| 指标 | 计算公式 | 实现位置 |
|------|---------|---------|
| 设备在用率 | 在用设备数 / 总设备数 × 100% | `StatsService.calcInUseRate()` |
| 设备完好率 | (在用+在库) / 总设备数 × 100% | `StatsService.calcIntactRate()` |
| 年故障率 | 近1年故障数 / 设备数 × 100% | `StatsService.calcAnnualFaultRate()` |
| 保养完成率 | 实际保养次数 / 应保养次数 × 100% | `StatsService.calcMaintainCompleteRate()` |
| 工单关闭率 | 已关闭工单 / 总工单 × 100% | `StatsService.calcFaultCloseRate()` |
| MTBF | 总运行小时 / 故障次数 | `StatsService.calcMtbf()` |
| MTTR | 累计停机时长 / 已关闭工单数 | `StatsService.calcMttr()` |
| 累计运维成本 | Σ保养费用 + Σ维修费用 | `StatsService.calcTotalOpCost()` |

---

## 八、种子数据与开发部署

### 8.1 种子数据规模

| 数据类型 | 数量 | 说明 |
|---------|------|------|
| 用户账号 | 8 | admin / engineer / purchaser / maintainer / scrap + 冻结测试账号 |
| 角色 | 5 | 对应 5 种菜单权限范围 |
| 桥梁 | 5 | 覆盖 5 个气候带 |
| 设备分类 | 11 | 照明、监测、伸缩缝、支座等 |
| 设备档案 | 38 | 分布在 5 座桥，覆盖多种状态 |
| 保养记录 | ~200 | 近 36 个月历史 |
| 故障工单 | ~50 | 模拟历史故障 |
| 采购订单 | 5 | 不同状态流转 |
| 生命周期事件 | ~300 | 每台设备的完整事件链 |
| 系统配置 | 3 | TCO 阈值参数 |

### 8.2 开发环境启动流程

```bash
# 1. 启动后端
cd backend
mvn spring-boot:run
# → http://localhost:8080

# 2. 启动前端
cd frontend
npm install
npm run dev
# → http://localhost:5173

# 3. 访问系统
# 浏览器打开 http://localhost:5173
# 使用 admin / admin123 登录
```

### 8.3 数据库连接配置

```yaml
# application.yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://[云数据库主机]:[端口]/bridge_lifecycle?useSSL=true
    username: [用户名]
    password: [密码]
```

### 8.4 模块开发顺序

| 阶段 | 模块 | 说明 |
|------|------|------|
| 第 1 阶段 | 登录认证 + 用户管理 | 基础功能 |
| 第 2 阶段 | 桥梁管理 | α/β 系数计算 |
| 第 3 阶段 | 设备档案 | 残值估算 |
| 第 4 阶段 | 保养管理 | 动态周期计算 |
| 第 5 阶段 | 故障工单 | 工单流转 |
| 第 6 阶段 | 采购订单 | 订单流转 |
| 第 7 阶段 | 报废鉴定（TCO 核心） | 论文核心功能 |
| 第 8 阶段 | 生命周期 | 事件流水 |
| 第 9 阶段 | 统计分析 + 仪表盘 | 8 项关键率 |
| 第 10 阶段 | 系统配置 + 审计日志 | 系统管理 |

---

## 九、设计决策记录

| 决策项 | 选择方案 | 原因 |
|-------|---------|------|
| 代码仓库组织 | Monorepo（前后端同一目录） | 方便统一管理和版本控制 |
| 前端 UI 组件库 | Element Plus | Vue 3 最流行组件库，文档完善 |
| 前端构建工具 | Vite | Vue 3 官方推荐，开发体验好 |
| 认证方式 | 简化逻辑（无 Token） | 适合演示/答辩，与原版一致 |
| 后端架构分层 | 经典三层架构 | 结构清晰，适合中小型项目 |
| 迁移范围 | 完整迁移（13 模块） | 保持功能完整性，与论文一致 |
| 数据库部署 | 云 MySQL | 已就绪，生产环境可用 |

---

## 十、风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 前后端接口联调复杂 | 开发进度 | 先设计完整 API 规范，后端先行开发 |
| ECharts 图表样式迁移 | UI 一致性 | 保留原版 Tailwind 样式，按需引入 ECharts |
| TCO 算法精度验证 | 答辩演示 | 对比原版计算结果，确保一致性 |
| 种子数据生成量大 | 初始化时间 | SQL 批量插入，优化脚本执行效率 |

---

## 十一、附录

### 测试账号

| 账号 | 密码 | 角色 | 权限范围 |
|------|------|------|---------|
| admin | admin123 | 系统管理员 | 全部 12 个菜单 |
| engineer | engineer123 | 工程管理员 | 9 个菜单 |
| purchaser | purchase123 | 采购人员 | 仪表盘 + 采购入库 |
| maintainer | maintain123 | 运维人员 | 设备 / 保养 / 故障 |
| scrap | scrap123 | 报废鉴定员 | 生命周期 + 报废鉴定 |
| frozen | frozen | 已冻结账户 | 用于演示禁用提示 |

### 设备状态枚举

| 状态码 | 中文名 |
|-------|-------|
| in_use | 使用中 |
| in_stock | 库存中 |
| maintaining | 保养中 |
| fault | 故障中 |
| scrapped | 已报废 |
| disabled | 已停用 |

### 生命周期事件类型

| 事件类型 | 中文名 |
|---------|-------|
| purchase | 采购入库 |
| commission | 投入运行 |
| maintain | 保养 |
| repair | 维修 |
| inspect | 检查 |
| scrap | 报废 |