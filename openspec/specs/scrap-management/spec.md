## ADDED Requirements

### Requirement: 待鉴定设备列表查询

系统 SHALL 支持查询待报废鉴定设备列表。

#### Scenario: 获取待鉴定设备
- **WHEN** 前端调用 GET /api/v1/scrap/candidates
- **THEN** 系统返回达到鉴定条件的设备列表（包含已用年限、故障次数、累计维修成本）

#### Scenario: 鉴定条件判断
- **WHEN** 设备满足以下任一条件：已用年限 ≥ 寿命警戒线、累计维修成本 ≥ 残值警戒线、故障频繁
- **THEN** 设备出现在待鉴定列表

### Requirement: TCO 决策计算

系统 SHALL 支持查询设备 TCO 决策分析结果（论文核心算法）。

#### Scenario: 获取 TCO 决策
- **WHEN** 前端调用 GET /api/v1/scrap/tco/{deviceId}
- **THEN** 系统返回 TCO 三栏决策数据（TCO 维修方案、TCO 更换方案、推荐结论）

#### Scenario: TCO 维修方案计算
- **WHEN** 计算 TCO 维修方案
- **THEN** 系统 TCO_repair = 当前修复费 + 未来N年保养费 + 未来N年维修费

#### Scenario: TCO 更换方案计算
- **WHEN** 计算 TCO 更换方案
- **THEN** 系统 TCO_replace = 新设备购置价 + 未来N年保养费 - 旧设备残值

#### Scenario: 推荐结论生成
- **WHEN** TCO 维修方案 < TCO 更换方案
- **THEN** 系统推荐结论 = "建议维修"

#### Scenario: 推荐结论生成 - 更换优先
- **WHEN** TCO 维修方案 > TCO 更换方案 或 设备已用年限 > 寿命警戒线
- **THEN** 系统推荐结论 = "建议更换"

### Requirement: 鉴定结论提交

系统 SHALL 支持提交报废鉴定结论。

#### Scenario: 成功提交鉴定
- **WHEN** 前端调用 POST /api/v1/scrap/decisions，提供 device_id, recommendation, conclusion_notes
- **THEN** 系统创建鉴定记录，状态为 pending_approval

#### Scenario: 鉴定结论类型
- **WHEN** 提交鉴定
- **THEN** 系统支持结论类型：repair（继续维修）、replace（报废更换）

### Requirement: 鉴定历史查询

系统 SHALL 支持查询报废鉴定历史记录。

#### Scenario: 获取鉴定历史
- **WHEN** 前端调用 GET /api/v1/scrap/decisions
- **THEN** 系统返回鉴定历史列表（包含 device_id, tco_repair, tco_replace, recommendation, status）

### Requirement: 阈值参数配置影响

系统 SHALL 根据阈值参数动态计算 TCO 决策。

#### Scenario: 残值警戒线阈值
- **WHEN** 系统配置残值警戒线阈值变更
- **THEN** 待鉴定设备列表和 TCO 计算结果相应变化

#### Scenario: 修复警戒线阈值
- **WHEN** 系统配置修复警戒线阈值变更
- **THEN** TCO 决策推荐结论相应变化

#### Scenario: 寿命警戒线阈值
- **WHEN** 系统配置寿命警戒线阈值变更
- **THEN** 待鉴定设备列表和 TCO 决策相应变化

### Requirement: 报废审批流程

系统 SHALL 支持报废审批流程。

#### Scenario: 审批通过
- **WHEN** 前端调用 PUT /api/v1/scrap/decisions/{id}/approve
- **THEN** 系统鉴定状态变为 approved，设备状态变为 scrapped，生成报废生命周期事件

#### Scenario: 审批拒绝
- **WHEN** 前端调用 PUT /api/v1/scrap/decisions/{id}/reject，提供 reject_reason
- **THEN** 系统鉴定状态变为 rejected，设备状态保持不变

### Requirement: 三栏决策面板数据

系统 SHALL 提供完整的 TCO 三栏决策面板数据。

#### Scenario: 三栏数据结构
- **WHEN** 获取 TCO 决策
- **THEN** 系统返回数据包含：当前状态栏（残值、已用年限、故障次数）、维修方案栏（修复费、未来保养费、未来维修费、TCO 维修）、更换方案栏（新设备价、未来保养费、残值回收、TCO 更换）