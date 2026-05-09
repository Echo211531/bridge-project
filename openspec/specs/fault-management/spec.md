## ADDED Requirements

### Requirement: 故障工单列表查询

系统 SHALL 支持分页查询故障工单列表。

#### Scenario: 获取工单列表
- **WHEN** 前端调用 GET /api/v1/faults?page=1&size=10
- **THEN** 系统返回工单列表（包含 order_no, device_id, report_date, fault_desc, repair_cost, status）

#### Scenario: 按状态筛选工单
- **WHEN** 前端调用 GET /api/v1/faults?status=open
- **THEN** 系统返回状态为"待处理"的工单列表

#### Scenario: 按设备筛选工单
- **WHEN** 前端调用 GET /api/v1/faults?deviceId={deviceId}
- **THEN** 系统返回指定设备的故障工单

### Requirement: 故障申报

系统 SHALL 支持故障申报创建工单。

#### Scenario: 成功申报故障
- **WHEN** 前端调用 POST /api/v1/faults，提供 device_id, fault_desc, report_date
- **THEN** 系统创建故障工单，自动生成工单编码，设备状态变为 fault，生成"维修"生命周期事件

#### Scenario: 工单自动编码
- **WHEN** 创建故障工单
- **THEN** 系统生成编码格式：FLT-{年月}-{序号}，如 FLT-202605-0001

### Requirement: 工单处理

系统 SHALL 支持工单处理状态流转。

#### Scenario: 开始处理工单
- **WHEN** 前端调用 PUT /api/v1/faults/{id}/process
- **THEN** 系统工单状态变为 processing，记录处理开始时间

#### Scenario: 记录维修费用
- **WHEN** 处理工单时提供 repair_cost
- **THEN** 系统记录维修费用，累加到设备生命周期成本

### Requirement: 工单关闭

系统 SHALL 支持关闭工单。

#### Scenario: 成功关闭工单
- **WHEN** 前端调用 PUT /api/v1/faults/{id}/close，提供 close_date
- **THEN** 系统工单状态变为 closed，设备状态恢复为 in_use，计算 MTTR

#### Scenario: 关闭时自动更新设备状态
- **WHEN** 关闭工单成功
- **THEN** 系统将设备状态从 fault 变为 in_use

### Requirement: MTTR 计算

系统 SHALL 计算平均修复时间（MTTR）。

#### Scenario: MTTR 计算公式
- **WHEN** 计算统计指标
- **THEN** 系统 MTTR = Σ(关闭时间 - 申报时间) / 已关闭工单数（分钟）

#### Scenario: MTTR 显示
- **WHEN** 查询仪表盘数据
- **THEN** 系统返回 MTTR 数值（单位：小时）

### Requirement: MTBF 计算

系统 SHALL 计算平均故障间隔时间（MTBF）。

#### Scenario: MTBF 计算公式
- **WHEN** 计算统计指标
- **THEN** 系统 MTBF = 总运行时间 / 故障次数

#### Scenario: MTBF 显示
- **WHEN** 查询仪表盘数据
- **THEN** 系统返回 MTBF 数值（单位：小时）

### Requirement: 年故障率统计

系统 SHALL 计算年故障率。

#### Scenario: 年故障率计算
- **WHEN** 计算统计指标
- **THEN** 系统年故障率 = 近1年故障工单数 / 在用设备数 × 100%

### Requirement: 工单关闭率统计

系统 SHALL 计算工单关闭率。

#### Scenario: 工单关闭率计算
- **WHEN** 计算统计指标
- **THEN** 系统工单关闭率 = 已关闭工单数 / 总工单数 × 100%