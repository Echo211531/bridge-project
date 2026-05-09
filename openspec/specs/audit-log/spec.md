## ADDED Requirements

### Requirement: 审计日志列表查询

系统 SHALL 支持分页查询审计日志列表。

#### Scenario: 获取审计日志列表
- **WHEN** 前端调用 GET /api/v1/audit?page=1&size=20
- **THEN** 系统返回审计日志列表（包含 operator, action, target, detail, operate_time）

#### Scenario: 按操作类型筛选
- **WHEN** 前端调用 GET /api/v1/audit?action=create
- **THEN** 系统返回创建操作的审计日志

#### Scenario: 按操作人筛选
- **WHEN** 前端调用 GET /api/v1/audit?operator=admin
- **THEN** 系统返回指定操作人的审计日志

#### Scenario: 按时间范围筛选
- **WHEN** 前端调用 GET /api/v1/audit?startDate=xxx&endDate=xxx
- **THEN** 系统返回指定时间范围内的审计日志

### Requirement: 审计日志自动记录

系统 SHALL 自动记录所有写操作审计日志。

#### Scenario: 创建操作审计
- **WHEN** 用户执行创建操作（新建设备、新建工单等）
- **THEN** 系统自动记录审计日志，action = 'create'

#### Scenario: 更新操作审计
- **WHEN** 用户执行更新操作（更新设备、更新配置等）
- **THEN** 系统自动记录审计日志，action = 'update'，记录变更前后值

#### Scenario: 删除操作审计
- **WHEN** 用户执行删除操作
- **THEN** 系统自动记录审计日志，action = 'delete'

#### Scenario: 状态变更审计
- **WHEN** 用户变更状态（工单状态、设备状态等）
- **THEN** 系统自动记录审计日志，action = 'status_change'

### Requirement: 审计日志数据结构

系统 SHALL 提供完整的审计日志数据结构。

#### Scenario: 审计日志字段
- **WHEN** 返回审计日志
- **THEN** 系统包含字段：log_id, operator, operator_name, action, target_type, target_id, detail, operate_time

#### Scenario: 详细信息格式
- **WHEN** 记录审计日志
- **THEN** 系统 detail 字段包含操作详细描述（JSON 格式）

### Requirement: 审计日志不可删除

系统 SHALL 禁止删除审计日志。

#### Scenario: 审计日志保留
- **WHEN** 审计日志创建后
- **THEN** 系统不允许删除审计日志，确保可追溯

### Requirement: 审计日志排序

系统 SHALL 支持审计日志按时间排序。

#### Scenario: 默认排序
- **WHEN** 查询审计日志
- **THEN** 系统默认按操作时间倒序排列（最新操作在前）

### Requirement: 审计日志导出

系统 SHALL 支持审计日志导出功能（可选）。

#### Scenario: 导出审计日志
- **WHEN** 前端调用 GET /api/v1/audit/export
- **THEN** 系统导出审计日志为 CSV 或 Excel 格式