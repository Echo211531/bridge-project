## ADDED Requirements

### Requirement: 采购订单列表查询

系统 SHALL 支持查询采购订单列表。

#### Scenario: 获取订单列表
- **WHEN** 前端调用 GET /api/v1/purchases
- **THEN** 系统返回订单列表（包含 order_no, category, quantity, unit_price, total_amount, status）

#### Scenario: 按状态筛选订单
- **WHEN** 前端调用 GET /api/v1/purchases?status=pending
- **THEN** 系统返回状态为"待发货"的订单

### Requirement: 采购订单创建

系统 SHALL 支持创建采购订单。

#### Scenario: 成功创建订单
- **WHEN** 前端调用 POST /api/v1/purchases，提供 category, quantity, unit_price, supplier
- **THEN** 系统创建订单，自动生成订单编码，计算总金额，状态初始为 pending

#### Scenario: 订单自动编码
- **WHEN** 创建采购订单
- **THEN** 系统生成编码格式：PO-{年月}-{序号}，如 PO-202605-0001

#### Scenario: 总金额计算
- **WHEN** 创建订单
- **THEN** 系统总金额 = quantity × unit_price

### Requirement: 订单状态流转

系统 SHALL 支持采购订单状态流转。

#### Scenario: 待发货 → 运输中
- **WHEN** 前端调用 PUT /api/v1/purchases/{id}/status，提供 status=shipping
- **THEN** 系统订单状态变为 shipping，记录发货时间

#### Scenario: 运输中 → 已入库
- **WHEN** 前端调用 PUT /api/v1/purchases/{id}/status，提供 status=received
- **THEN** 系统订单状态变为 received，记录入库时间，可创建设备档案

#### Scenario: 无效状态流转
- **WHEN** 尝试非法状态流转（如 pending 直接变为 received）
- **THEN** 系统返回错误提示"无效的状态流转"

### Requirement: 订单详情查询

系统 SHALL 支持查询采购订单详情。

#### Scenario: 获取订单详情
- **WHEN** 前端调用 GET /api/v1/purchases/{id}
- **THEN** 系统返回订单完整信息，包含状态流转历史

### Requirement: 入库后设备创建

系统 SHALL 支持从采购订单创建设备档案。

#### Scenario: 入库时创建设备
- **WHEN** 订单状态变为 received
- **THEN** 系统支持从订单创建设备档案（设备数量 = quantity）

#### Scenario: 设备初始状态
- **WHEN** 从订单创建设备
- **THEN** 设备初始状态为 in_stock（库存中）