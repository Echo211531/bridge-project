# 桥梁设备全生命周期管理系统

> 华东交通大学软件学院 2026 届毕业设计
> 作者：程国忠 ｜ 指导教师：向华萍

## 📦 项目说明

本系统针对桥梁设备运维管理中存在的"重建轻管、数据孤岛、报废决策主观"等核心痛点，构建了一套覆盖**采购→投运→保养→维修→鉴定→报废**全生命周期的数字化管理平台。

系统基于 **TCO（Total Cost of Ownership，全寿命周期成本）模型**，结合气候系数 α 与交通量系数 β 的双因素动态调参机制，为设备的"修"与"换"提供量化决策依据。

## 🏗 系统架构

```
┌────────────────────────────────────────────────────────────┐
│  前端：Vue 3 单页应用                                        │
│  ├─ Vue 3.4 + Vite 5（构建工具）                              │
│  ├─ Element Plus（UI 组件库）                                 │
│  ├─ ECharts 5（图表可视化）                                   │
│  ├─ Pinia（状态管理）                                         │
│  └─ Axios（HTTP 客户端）                                      │
├────────────────────────────────────────────────────────────┤
│  后端：Spring Boot 3 RESTful API                             │
│  ├─ Spring Boot 3.2 + MyBatis-Plus 3.5                       │
│  ├─ MySQL 8.0（17 张表对应论文 E-R 设计）                      │
│  ├─ JWT Token 认证                                            │
│  └─ Knife4j API 文档                                          │
├────────────────────────────────────────────────────────────┤
│  业务层核心算法：                                             │
│  ├─ TCO 决策算法（论文 6.5.2 节伪代码完整实现）                │
│  ├─ 残值估算（直线折旧 × 市场系数）                            │
│  ├─ α×β 动态保养周期推算                                       │
│  ├─ 8 项关键率聚合统计                                         │
│  └─ 全生命周期事件流水（lifecycle_event）                      │
└────────────────────────────────────────────────────────────┘
```

## 🚀 快速开始

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问 `http://localhost:5173`

### 后端启动

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端 API 地址 `http://localhost:8080`

> ⚠ 确保已安装 MySQL 8.0，并执行数据库初始化脚本。

### 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE bridge_lifecycle CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p bridge_lifecycle < backend/src/main/resources/schema.sql
mysql -u root -p bridge_lifecycle < backend/src/main/resources/data.sql
```

## 🔑 测试账号

| 账号 | 密码 | 角色 | 权限范围 |
|------|------|------|---------|
| admin | admin123 | 系统管理员 | 全部菜单 |
| engineer | engineer123 | 工程管理员 | 9 个菜单（无配置/用户/审计） |
| purchaser | purchase123 | 采购人员 | 仪表盘 + 采购入库 |
| maintainer | maintain123 | 运维人员 | 设备 / 保养 / 故障 |
| scrap | scrap123 | 报废鉴定员 | 生命周期 + 报废鉴定 |

登录页提供"一键填充"功能，演示时可快速填入账号密码。

## 📊 核心功能模块

| 模块 | 功能说明 |
|------|---------|
| **登录认证** | JWT Token 认证 + 多角色 RBAC + 账户状态检查 |
| **首页仪表盘** | 8 项关键率指标 + 30 天费用趋势 + 待办事项 |
| **全生命周期** | 设备时间轴：采购→投运→保养→维修→报废 + 累计 TCO |
| **桥梁管理** | α 气候系数 + β AADT 系数 + 历史变更记录 |
| **设备档案** | 11 类设备 / 自动编码 DEV-类别-桥梁-序号 / 残值估算 |
| **保养管理** | α×β 动态周期计算 + 保养记录上报 + 完成率统计 |
| **故障工单** | FLT-年月-序号 编码 / 状态流转 / MTTR/MTBF 统计 |
| **采购订单** | PO-年月-序号 编码 / pending→shipping→received 状态流转 |
| **报废鉴定** | TCO 三栏决策面板（核心创新点） |
| **统计分析** | 4 张图表：分类分布 / 费用趋势 / 故障排行 / TCO 对比 |
| **系统配置** | 4 个 TCO 阈值参数动态配置 |
| **用户管理** | CRUD / 状态切换 / 密码重置 |
| **审计日志** | 所有写操作可追溯 |

## 🎯 针对答辩意见的改进点

| 老师意见 | 改进方案 |
|---------|---------|
| ① 缺登录界面 | JWT Token 认证 + 多角色 RBAC + 账户状态检查 |
| ② 没体现全生命周期 | 独立生命周期模块，时间轴展示完整事件链 + 累计 TCO |
| ③ 数据库没做好 | Spring Boot + MySQL 8，17 张表严格对应论文 E-R 设计 |
| ④ 统计率要真实算出来 | 8 项关键率全部由数据库聚合计算，无硬编码假数据 |

## 📁 项目结构

```
bridge-project/
├── backend/                          Spring Boot 后端
│   ├── src/main/java/                Java 源码
│   │   └── com/bridge/
│   │       ├── entity/               实体类（17 张表）
│   │       ├── mapper/               MyBatis-Plus Mapper
│   │       ├── service/              业务服务层
│   │       ├── controller/           REST API 控制器
│   │       ├── dto/                  数据传输对象
│   │       ├── vo/                   视图对象
│   │       └── common/               通用组件
│   ├── src/main/resources/
│   │   ├── application.yml           配置文件
│   │   ├── schema.sql                数据库建表脚本
│   │   └── data.sql                  种子数据脚本
│   └── pom.xml                       Maven 依赖配置
├── frontend/                         Vue 3 前端
│   ├── src/
│   │   ├── views/                    14 个页面组件
│   │   ├── components/               Layout / Common / Business 组件
│   │   ├── api/                      12 个 API 模块
│   │   ├── router/                   路由配置 + 守卫
│   │   └── stores/                   Pinia 状态管理
│   ├── package.json                  npm 依赖
│   └── vite.config.js                Vite 配置
├── index.html                        原版 React SPA（备份保留）
├── README.md                         本文件
├── 答辩演示指南.md                   演示话术
├── 数据库设计说明.md                 表结构定义
└── 部署说明.md                       部署文档
```

## ⚙ 技术规格

| 类别 | 规格 |
|------|------|
| 后端 | Spring Boot 3.2 + MyBatis-Plus 3.5 + MySQL 8 |
| 前端 | Vue 3.4 + Element Plus + ECharts 5 + Pinia |
| 数据库 | 17 张 InnoDB 表，UTF-8MB4 |
| 种子数据 | 5 桥梁 / 38 设备 / 11 分类 / 历史数据 |
| 核心算法 | TCO 决策 + α×β 动态周期 + 残值估算 |

## 📜 版本

v2.0 (2026-05) — Spring Boot + Vue 3 架构升级版

## 📧 联系方式

程国忠 · 华东交通大学软件学院