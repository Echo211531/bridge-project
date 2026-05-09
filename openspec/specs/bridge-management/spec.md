## ADDED Requirements

### Requirement: 桥梁列表查询

系统 SHALL 支持查询桥梁列表。

#### Scenario: 获取桥梁列表
- **WHEN** 前端调用 GET /api/v1/bridges
- **THEN** 系统返回桥梁列表（包含 bridge_code, bridge_name, climate_zone, aadt, alpha_coef, beta_coef）

### Requirement: 桥梁详情查询

系统 SHALL 支持查询单个桥梁详情。

#### Scenario: 获取桥梁详情
- **WHEN** 前端调用 GET /api/v1/bridges/{id}
- **THEN** 系统返回桥梁完整信息

### Requirement: 桥梁创建

系统 SHALL 支持创建新桥梁。

#### Scenario: 成功创建桥梁
- **WHEN** 前端调用 POST /api/v1/bridges，提供 bridge_code, bridge_name, climate_zone, aadt
- **THEN** 系统自动计算 α 气候系数和 β AADT 系数，创建桥梁记录

#### Scenario: 桥梁编码已存在
- **WHEN** 前端调用 POST /api/v1/bridges，提供已存在的 bridge_code
- **THEN** 系统返回错误提示"桥梁编码已存在"

### Requirement: 桥梁更新

系统 SHALL 支持更新桥梁信息。

#### Scenario: 成功更新桥梁
- **WHEN** 前端调用 PUT /api/v1/bridges/{id}
- **THEN** 系统更新桥梁信息，如 climate_zone 变化则重新计算 α 系数

### Requirement: AADT 更新

系统 SHALL 支持更新桥梁 AADT（年平均日交通量）。

#### Scenario: 更新 AADT
- **WHEN** 前端调用 PUT /api/v1/bridges/{id}/aadt，提供新 AADT 值
- **THEN** 系统更新 AADT，重新计算 β 系数，并记录历史变更

### Requirement: AADT 历史查询

系统 SHALL 支持查询桥梁 AADT 历史记录。

#### Scenario: 获取 AADT 历史
- **WHEN** 前端调用 GET /api/v1/bridges/{id}/aadt-history
- **THEN** 系统返回该桥梁的 AADT 变更历史列表

### Requirement: α 气候系数计算

系统 SHALL 根据气候带自动计算 α 系数。

#### Scenario: 寒冷气候带
- **WHEN** 桥梁 climate_zone = 'cold'
- **THEN** 系统 α_coef = 1.30

#### Scenario: 严寒气候带
- **WHEN** 桥梁 climate_zone = 'severe_cold'
- **THEN** 系统 α_coef = 1.20

#### Scenario: 温和气候带
- **WHEN** 桥梁 climate_zone = 'temperate'
- **THEN** 系统 α_coef = 1.00

#### Scenario: 湿热气候带
- **WHEN** 桥梁 climate_zone = 'humid'
- **THEN** 系统 α_coef = 1.15

#### Scenario: 沿海气候带
- **WHEN** 桥梁 climate_zone = 'coastal'
- **THEN** 系统 α_coef = 1.25

### Requirement: β AADT 系数计算

系统 SHALL 根据 AADT 范围自动计算 β 系数。

#### Scenario: AADT < 1万
- **WHEN** 桥梁 aadt < 10000
- **THEN** 系统 beta_coef = 0.85

#### Scenario: AADT 1-3万
- **WHEN** 桥梁 aadt 在 10000-30000 范围
- **THEN** 系统 beta_coef = 1.00

#### Scenario: AADT 3-6万
- **WHEN** 桥梁 aadt 在 30000-60000 范围
- **THEN** 系统 beta_coef = 1.15

#### Scenario: AADT 6-10万
- **WHEN** 桥梁 aadt 在 60000-100000 范围
- **THEN** 系统 beta_coef = 1.30

#### Scenario: AADT > 10万
- **WHEN** 桥梁 aadt > 100000
- **THEN** 系统 beta_coef = 1.50