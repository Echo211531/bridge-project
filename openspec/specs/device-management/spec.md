## ADDED Requirements

### Requirement: 设备列表查询

系统 SHALL 支持分页查询设备列表。

#### Scenario: 获取设备列表
- **WHEN** 前端调用 GET /api/v1/devices?page=1&size=10
- **THEN** 系统返回设备列表（包含 device_code, category, bridge_id, status, purchase_cost, in_use_date）

#### Scenario: 按桥梁筛选设备
- **WHEN** 前端调用 GET /api/v1/devices?bridgeId={bridgeId}
- **THEN** 系统返回指定桥梁的设备列表

#### Scenario: 按状态筛选设备
- **WHEN** 前端调用 GET /api/v1/devices?status=in_use
- **THEN** 系统返回状态为"使用中"的设备列表

#### Scenario: 搜索设备
- **WHEN** 前端调用 GET /api/v1/devices?keyword=照明
- **THEN** 系统返回设备名称或编码匹配关键词的设备列表

### Requirement: 设备详情查询

系统 SHALL 支持查询单个设备详情，包含残值计算。

#### Scenario: 获取设备详情
- **WHEN** 前端调用 GET /api/v1/devices/{id}
- **THEN** 系统返回设备完整信息，包含计算后的残值（residual_value）

### Requirement: 设备创建

系统 SHALL 支持创建新设备，自动生成设备编码。

#### Scenario: 成功创建设备
- **WHEN** 前端调用 POST /api/v1/devices，提供 category, bridge_id, purchase_cost, manufacturer 等
- **THEN** 系统自动生成设备编码（格式：DEV-类别码-桥梁码-序号），创建设备记录

#### Scenario: 创建时自动生成生命周期事件
- **WHEN** 设备创建成功
- **THEN** 系统自动生成"采购入库"生命周期事件（event_type=purchase）

### Requirement: 设备更新

系统 SHALL 支持更新设备信息。

#### Scenario: 成功更新设备
- **WHEN** 前端调用 PUT /api/v1/devices/{id}
- **THEN** 系统更新设备信息，返回成功响应

### Requirement: 设备状态流转

系统 SHALL 支持设备状态变更。

#### Scenario: 投入使用
- **WHEN** 设备状态从 in_stock 变为 in_use，且设置 in_use_date
- **THEN** 系统生成"投入运行"生命周期事件（event_type=commission）

#### Scenario: 开始保养
- **WHEN** 设备状态变为 maintaining
- **THEN** 系统标记设备正在保养

#### Scenario: 故障标记
- **WHEN** 设备状态变为 fault
- **THEN** 系统标记设备故障中

#### Scenario: 报废标记
- **WHEN** 设备状态变为 scrapped
- **THEN** 系统生成"报废"生命周期事件（event_type=scrap）

### Requirement: 设备残值估算

系统 SHALL 自动计算设备残值。

#### Scenario: 残值计算公式
- **WHEN** 查询设备详情
- **THEN** 系统计算残值 = 购置价 × (1 - 已用年限/设计寿命) × 市场折扣系数

#### Scenario: 已用年限超过设计寿命
- **WHEN** 设备已用年限 ≥ 设计寿命
- **THEN** 系统残值 = 0（或最小残值阈值）

### Requirement: 超期保养设备查询

系统 SHALL 支持查询超期未保养设备。

#### Scenario: 获取超期保养设备
- **WHEN** 前端调用 GET /api/v1/devices/overdue-maintain
- **THEN** 系统返回超过保养周期的设备列表（含上次保养日期、超期天数）

### Requirement: 设备分类管理

系统 SHALL 支持查询设备分类列表。

#### Scenario: 获取设备分类列表
- **WHEN** 前端调用 GET /api/v1/device-categories
- **THEN** 系统返回设备分类列表（包含 code, name, design_life_years, maintain_cycle_days）

### Requirement: 设备自动编码规则

系统 SHALL 自动生成设备编码。

#### Scenario: 编码格式
- **WHEN** 创建设备
- **THEN** 系统生成编码格式：DEV-{类别码}-{桥梁码}-{4位序号}，如 DEV-LT-BR01-0001

#### Scenario: 序号递增
- **WHEN** 同类别同桥梁已有设备
- **THEN** 系统序号自动递增，不重复