## ADDED Requirements

### Requirement: 设备生命周期事件查询

系统 SHALL 支持查询设备完整生命周期事件流水。

#### Scenario: 获取生命周期事件
- **WHEN** 前端调用 GET /api/v1/lifecycle/events/{deviceId}
- **THEN** 系统返回该设备所有生命周期事件列表（按时间排序）

#### Scenario: 事件类型包含
- **WHEN** 查询生命周期事件
- **THEN** 系统返回事件类型包含：purchase（采购入库）、commission（投入运行）、maintain（保养）、repair（维修）、inspect（检查）、scrap（报废）

### Requirement: 生命周期事件数据结构

系统 SHALL 提供完整的事件数据结构。

#### Scenario: 事件字段
- **WHEN** 返回生命周期事件
- **THEN** 系统包含字段：event_type, title, description, cost, event_time, operator

### Requirement: 设备生命周期汇总

系统 SHALL 支持查询设备生命周期汇总数据。

#### Scenario: 获取生命周期汇总
- **WHEN** 前端调用 GET /api/v1/lifecycle/summary/{deviceId}
- **THEN** 系统返回设备生命周期汇总（包含累计 TCO、事件总数、各类型事件统计）

#### Scenario: 累计 TCO 计算
- **WHEN** 计算累计 TCO
- **THEN** 系统 TCO = Σ采购成本 + Σ保养成本 + Σ维修成本

### Requirement: 生命周期时间轴展示

系统 SHALL 支持时间轴形式展示生命周期事件。

#### Scenario: 时间轴数据
- **WHEN** 前端请求时间轴展示
- **THEN** 系统返回事件按时间排序，每个事件包含时间点、事件类型图标、事件描述、成本

### Requirement: 事件自动生成规则

系统 SHALL 根据业务操作自动生成生命周期事件。

#### Scenario: 设备采购入库
- **WHEN** 设备从采购订单入库创建
- **THEN** 系统自动生成 event_type=purchase 事件

#### Scenario: 设备投入运行
- **WHEN** 设备状态变为 in_use
- **THEN** 系统自动生成 event_type=commission 事件

#### Scenario: 设备保养完成
- **WHEN** 上报保养记录
- **THEN** 系统自动生成 event_type=maintain 事件

#### Scenario: 设备维修完成
- **WHEN** 故障工单关闭
- **THEN** 系统自动生成 event_type=repair 事件

#### Scenario: 设备报废
- **WHEN** 设备状态变为 scrapped
- **THEN** 系统自动生成 event_type=scrap 事件

### Requirement: 生命周期成本追溯

系统 SHALL 支持追溯设备全生命周期成本。

#### Scenario: 成本分项统计
- **WHEN** 查询生命周期汇总
- **THEN** 系统返回成本分项：采购成本、保养成本合计、维修成本合计

### Requirement: 设备状态历史追溯

系统 SHALL 支持追溯设备状态变更历史。

#### Scenario: 状态变更记录
- **WHEN** 设备状态变更
- **THEN** 系统在生命周期事件中记录状态变更前后状态