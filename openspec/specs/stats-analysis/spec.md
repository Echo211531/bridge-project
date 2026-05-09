## ADDED Requirements

### Requirement: 仪表盘汇总数据查询

系统 SHALL 支持查询首页仪表盘汇总数据。

#### Scenario: 获取仪表盘数据
- **WHEN** 前端调用 GET /api/v1/stats/dashboard
- **THEN** 系统返回仪表盘汇总数据（包含 8 项关键率、30天费用趋势、待办事项）

### Requirement: 8 项关键率统计

系统 SHALL 计算并返回 8 项关键率指标。

#### Scenario: 设备在用率
- **WHEN** 计算设备在用率
- **THEN** 系统在用率 = 在用设备数（status=in_use） / 总设备数 × 100%

#### Scenario: 设备完好率
- **WHEN** 计算设备完好率
- **THEN** 系统完好率 = (在用设备数 + 库存设备数) / 总设备数 × 100%

#### Scenario: 年故障率
- **WHEN** 计算年故障率
- **THEN** 系统年故障率 = 近1年故障工单数 / 在用设备数 × 100%

#### Scenario: 保养完成率
- **WHEN** 计算保养完成率
- **THEN** 系统保养完成率 = 近1年实际保养次数 / 应保养次数 × 100%

#### Scenario: 工单关闭率
- **WHEN** 计算工单关闭率
- **THEN** 系统工单关闭率 = 已关闭工单数 / 总工单数 × 100%

#### Scenario: MTBF（平均故障间隔时间）
- **WHEN** 计算 MTBF
- **THEN** 系统 MTBF = Σ设备运行时间 / 故障次数（小时）

#### Scenario: MTTR（平均修复时间）
- **WHEN** 计算 MTTR
- **THEN** 系统 MTTR = Σ(关闭时间 - 申报时间) / 已关闭工单数（小时）

#### Scenario: 累计运维成本
- **WHEN** 计算累计运维成本
- **THEN** 系统成本 = Σ保养费用 + Σ维修费用（元）

### Requirement: 费用趋势查询

系统 SHALL 支持查询费用趋势数据。

#### Scenario: 30天费用趋势
- **WHEN** 前端调用 GET /api/v1/stats/dashboard
- **THEN** 系统返回近30天每日费用数据（保养费用 + 维修费用）

#### Scenario: 费用趋势图表数据
- **WHEN** 前端请求费用趋势图表
- **THEN** 系统返回数据格式：[{date, maintainCost, repairCost, totalCost}]

### Requirement: 待办事项查询

系统 SHALL 支持查询待办事项列表。

#### Scenario: 获取待办事项
- **WHEN** 前端调用 GET /api/v1/stats/dashboard
- **THEN** 系统返回待办事项（超期保养设备数、待处理工单数、待审批报废鉴定数）

### Requirement: 统计分析数据查询

系统 SHALL 支持查询统计分析页面数据。

#### Scenario: 获取统计分析数据
- **WHEN** 前端调用 GET /api/v1/stats/overview?startDate=xxx&endDate=xxx
- **THEN** 系统返回统计分析数据（设备分类分布、费用趋势、故障排行、TCO 对比）

### Requirement: 设备分类分布统计

系统 SHALL 支持按分类统计设备分布。

#### Scenario: 分类分布数据
- **WHEN** 查询统计分析
- **THEN** 系统返回各分类设备数量占比

### Requirement: 故障排行统计

系统 SHALL 支持故障排行统计。

#### Scenario: 故障排行数据
- **WHEN** 查询统计分析
- **THEN** 系统返回故障次数最多的设备排行（Top 10）

### Requirement: TCO 对比统计

系统 SHALL 支持设备 TCO 对比统计。

#### Scenario: TCO 对比数据
- **WHEN** 查询统计分析
- **THEN** 系统返回各设备累计 TCO 对比数据

### Requirement: 时间范围筛选

系统 SHALL 支持统计分析时间范围筛选。

#### Scenario: 时间范围筛选
- **WHEN** 前端提供 startDate 和 endDate 参数
- **THEN** 系统统计该时间范围内的数据