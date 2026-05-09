## ADDED Requirements

### Requirement: 保养计划列表查询

系统 SHALL 支持查询保养计划列表。

#### Scenario: 获取保养计划列表
- **WHEN** 前端调用 GET /api/v1/maintains/plans
- **THEN** 系统返回保养计划列表（包含 device_id, plan_date, cycle_days, status）

#### Scenario: 按状态筛选计划
- **WHEN** 前端调用 GET /api/v1/maintains/plans?status=pending
- **THEN** 系统返回待执行的保养计划

### Requirement: 保养记录列表查询

系统 SHALL 支持查询保养记录列表。

#### Scenario: 获取保养记录列表
- **WHEN** 前端调用 GET /api/v1/maintains/records
- **THEN** 系统返回保养记录列表（包含 device_id, record_date, actual_cost, manhour, operator）

#### Scenario: 按设备筛选记录
- **WHEN** 前端调用 GET /api/v1/maintains/records?deviceId={deviceId}
- **THEN** 系统返回指定设备的保养记录

### Requirement: 保养记录上报

系统 SHALL 支持上报保养记录。

#### Scenario: 成功上报保养记录
- **WHEN** 前端调用 POST /api/v1/maintains/records，提供 device_id, record_date, actual_cost, manhour, operator
- **THEN** 系统创建保养记录，更新设备下次保养计划日期，生成"保养"生命周期事件

#### Scenario: 上报时自动更新设备状态
- **WHEN** 上报保养记录成功且设备状态为 maintaining
- **THEN** 系统将设备状态恢复为 in_use

### Requirement: 动态保养周期计算

系统 SHALL 根据桥梁 α×β 系数动态计算保养周期。

#### Scenario: 动态周期公式
- **WHEN** 计算设备保养周期
- **THEN** 系统实际周期 = 标准周期 × α_coef × β_coef

#### Scenario: 寒冷高交通桥梁
- **WHEN** 桥梁 α=1.30, β=1.50，设备标准周期 90 天
- **THEN** 系统实际周期 = 90 × 1.30 × 1.50 = 175.5 天

#### Scenario: 温和低交通桥梁
- **WHEN** 桥梁 α=1.00, β=0.85，设备标准周期 90 天
- **THEN** 系统实际周期 = 90 × 1.00 × 0.85 = 76.5 天

### Requirement: 保养计划生成

系统 SHALL 自动生成设备保养计划。

#### Scenario: 新设备投运时生成计划
- **WHEN** 设备投入运行（状态变为 in_use）
- **THEN** 系统自动生成首次保养计划，计划日期 = in_use_date + 动态周期

#### Scenario: 保养完成后生成下次计划
- **WHEN** 上报保养记录成功
- **THEN** 系统自动生成下次保养计划，计划日期 = 本次保养日期 + 动态周期

### Requirement: 保养超期提醒

系统 SHALL 提示超期未保养设备。

#### Scenario: 超期设备统计
- **WHEN** 查询仪表盘数据
- **THEN** 系统返回超期保养设备数量和超期天数

### Requirement: 保养完成率统计

系统 SHALL 计算保养完成率。

#### Scenario: 保养完成率计算
- **WHEN** 计算统计指标
- **THEN** 系统保养完成率 = 实际保养次数 / 应保养次数 × 100%