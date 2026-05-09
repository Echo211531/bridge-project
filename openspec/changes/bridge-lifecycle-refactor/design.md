## Context

原版系统为华东交通大学软件学院 2026 届毕业设计答辩交付版，使用单文件 SPA 架构（React 18.2 + IndexedDB），代码量 3683 行 JSX。系统包含 13 个业务模块、17 张数据库表、TCO 决策算法（论文核心）等完整功能。

当前架构约束：
- 前端技术栈已确定迁移至 Vue 3 + Element Plus + Vite
- 后端技术栈采用 Java 21 + Spring Boot 3 + MyBatis-Plus
- 数据库已就绪：云 MySQL（需创建表结构）
- 认证方式：简化逻辑（用户名密码直接校验，无 JWT Token）
- 开发环境：Windows 11 + IntelliJ IDEA

## Goals / Non-Goals

**Goals:**
- 完整重构为前后端分离架构，保留原版全部 13 个业务模块功能
- 实现标准 RESTful API 设计，约 40+ 接口
- 迁移 TCO 决策算法、α×β 动态保养周期、残值估算等核心业务逻辑
- 生成 MySQL 表结构与种子数据（约 600+ 条记录）
- 确保前后端可独立部署、联调

**Non-Goals:**
- 不引入 JWT Token 认证（保留简化逻辑，适合答辩演示）
- 不重构 UI 设计（保留原版 Element Plus/Tailwind 样式风格）
- 不新增功能模块（仅迁移原版功能）
- 不考虑容器化部署（Docker/K8s 超出答辩范围）

## Decisions

### 1. 代码仓库组织 — Monorepo

**选择**：前后端同一目录（bridge-project/frontend/ + bridge-project/backend/）

**原因**：
- 便于统一版本管理和代码追踪
- 方便答辩时展示完整项目结构
- 避免多仓库的同步问题

**替代方案**：Polyrepo（前后端独立仓库）
- 缺点：增加联调复杂度，答辩演示需切换仓库

### 2. 前端 UI 组件库 — Element Plus

**选择**：Element Plus（Vue 3 官方推荐）

**原因**：
- Vue 3 最流行组件库，文档完善、社区活跃
- 组件丰富（Table、Form、Dialog、Drawer 等），开发效率高
- 与 Tailwind CSS 兼容良好

**替代方案**：Ant Design Vue
- 缺点：组件 API 与 Element Plus 不同，迁移成本更高

### 3. 前端构建工具 — Vite

**选择**：Vite（Vue 3 官方推荐）

**原因**：
- 开发启动快（ES Module 原生支持）
- 热更新响应快
- Vue 3 生态首选

**替代方案**：Webpack
- 缺点：配置复杂、启动慢

### 4. 后端架构分层 — 经典三层架构

**选择**：Controller → Service → Mapper

**原因**：
- 结构清晰，职责分明
- 适合中小型项目（约 40 接口）
- 符合 Spring Boot 最佳实践

**替代方案**：DDD 领域驱动设计
- 缺点：架构复杂，适合大型项目，本项目规模不需要

### 5. ORM 框架 — MyBatis-Plus

**选择**：MyBatis-Plus

**原因**：
- 国内最流行 ORM，文档丰富
- 支持 Lambda 查询、自动分页、逻辑删除
- 简化 CRUD 代码，提升开发效率

**替代方案**：Spring Data JPA
- 缺点：复杂查询需要 JPQL，国内社区不如 MyBatis-Plus 活跃

### 6. 认证方式 — 简化 Session 登录

**选择**：用户名密码直接校验，Session 存储用户信息

**原因**：
- 与原版 IndexedDB 版本一致
- 适合答辩演示场景
- 无需前端处理 Token 刷新逻辑

**替代方案**：JWT Token
- 缺点：增加前后端复杂度，答辩时间有限

### 7. 数据库表命名 — 小写下划线风格

**选择**：sys_user, bridge, device_archive 等（小写下划线）

**原因**：
- MySQL 标准命名风格
- 符合阿里巴巴 Java 开发手册规范
- 与原版 IndexedDB ObjectStore 命名一致

### 8. API 文档 — Knife4j

**选择**：Knife4j（Swagger 增强）

**原因**：
- 支持 Spring Boot 3
- UI 美观，支持离线文档
- 国内流行，文档丰富

**替代方案**：SpringDoc OpenAPI
- 缺点：UI 不如 Knife4j 美观

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 前后端接口联调复杂 | 开发进度延后 | 先完成 API 设计规范，后端先行开发 Mock 数据 |
| ECharts 图表样式迁移不一致 | UI 差异 | 保留原版 Tailwind 样式，按需引入 ECharts |
| TCO 算法精度验证失败 | 答辩演示失败 | 对比原版计算结果，单元测试覆盖关键算法 |
| 种子数据生成量大导致初始化慢 | 用户体验 | SQL 批量插入，优化脚本执行效率 |
| Vue 3 学习曲线（原版 React） | 开发效率 | 参考 Element Plus 示例，快速上手 |

## Migration Plan

### 开发顺序（10 阶段）

| 阶段 | 模块 | 说明 | 依赖 |
|------|------|------|------|
| 1 | 登录认证 + 用户管理 | 基础功能 | 无 |
| 2 | 桥梁管理 | α/β 系数计算 | 阶段 1 |
| 3 | 设备档案 | 残值估算 | 阶段 2 |
| 4 | 保养管理 | 动态周期计算 | 阶段 3 |
| 5 | 故障工单 | 工单流转 | 阶段 3 |
| 6 | 采购订单 | 订单流转 | 阶段 1 |
| 7 | 报废鉴定（TCO 核心） | 论文核心功能 | 阶段 3, 4, 5 |
| 8 | 生命周期 | 事件流水 | 阶段 3, 4, 5, 7 |
| 9 | 统计分析 + 仪表盘 | 8 项关键率 | 阶段 1-8 |
| 10 | 系统配置 + 审计日志 | 系统管理 | 阶段 1 |

### 部署流程

```bash
# 1. 后端启动
cd backend
mvn spring-boot:run
# → http://localhost:8080

# 2. 前端启动
cd frontend
npm install
npm run dev
# → http://localhost:5173

# 3. 数据库初始化
# Spring Boot 启动时自动执行 schema.sql + data.sql
```

### 回滚策略

- Git 分支管理：开发分支 `develop`，主干分支 `main`
- 每个阶段完成后合并到 `develop`
- 答辩前合并到 `main`
- 原版 `index.html` 保留作为备份

## Open Questions

1. **云数据库连接信息**：需用户提供 MySQL 主机、端口、用户名、密码
2. **前端路由模式**：Hash 模式或 History 模式（History 需后端配置）
3. **文件上传功能**：原版无文件上传，是否需要新增？
4. **导出功能**：Excel/PDF 导出是否需要？