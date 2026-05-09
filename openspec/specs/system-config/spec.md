## ADDED Requirements

### Requirement: 系统配置查询

系统 SHALL 支持查询所有系统配置参数。

#### Scenario: 获取所有配置
- **WHEN** 前端调用 GET /api/v1/config
- **THEN** 系统返回所有配置项列表（包含 config_key, config_value, description）

### Requirement: TCO 阈值参数配置

系统 SHALL 支持 TCO 决策相关阈值参数配置。

#### Scenario: 残值警戒线配置
- **WHEN** 前端调用 PUT /api/v1/config/residual_warning_line，提供新值（如 0.3）
- **THEN** 系统更新配置，影响 TCO 决策中残值判断

#### Scenario: 修复警戒线配置
- **WHEN** 前端调用 PUT /api/v1/config/repair_warning_line，提供新值（如 0.5）
- **THEN** 系统更新配置，影响 TCO 决策中修复成本判断

#### Scenario: 寿命警戒线配置
- **WHEN** 前端调用 PUT /api/v1/config/life_warning_line，提供新值（如 0.8）
- **THEN** 系统更新配置，影响待鉴定设备筛选

### Requirement: 配置值格式

系统 SHALL 支持配置值的范围约束。

#### Scenario: 阈值范围约束
- **WHEN** 配置 TCO 阈值
- **THEN** 系统约束值范围在 0.0 - 1.0 之间

#### Scenario: 无效值拒绝
- **WHEN** 提供超出范围的配置值
- **THEN** 系统返回错误提示"配置值超出有效范围"

### Requirement: 配置实时生效

系统 SHALL 确保配置更新实时生效。

#### Scenario: 配置即时生效
- **WHEN** 更新配置成功
- **THEN** 系统后续计算立即使用新配置值

### Requirement: 默认配置值

系统 SHALL 提供默认配置值。

#### Scenario: 默认残值警戒线
- **WHEN** 系统首次初始化
- **THEN** 残值警戒线默认值 = 0.3（30%）

#### Scenario: 默认修复警戒线
- **WHEN** 系统首次初始化
- **THEN** 修复警戒线默认值 = 0.5（50%）

#### Scenario: 默认寿命警戒线
- **WHEN** 系统首次初始化
- **THEN** 寿命警戒线默认值 = 0.8（80%）

### Requirement: 配置审计记录

系统 SHALL 记录配置变更审计日志。

#### Scenario: 配置变更审计
- **WHEN** 更新配置
- **THEN** 系统自动记录审计日志（操作人、变更前后值、变更时间）