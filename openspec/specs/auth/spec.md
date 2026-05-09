## ADDED Requirements

### Requirement: 用户登录认证

系统 SHALL 允许用户通过用户名和密码进行登录认证。

#### Scenario: 成功登录
- **WHEN** 用户输入正确的用户名和密码
- **THEN** 系统验证成功，创建会话，返回用户信息和菜单权限列表

#### Scenario: 登录失败 - 用户不存在
- **WHEN** 用户输入不存在的用户名
- **THEN** 系统返回错误提示"用户不存在"

#### Scenario: 登录失败 - 密码错误
- **WHEN** 用户输入正确的用户名但错误的密码
- **THEN** 系统返回错误提示"密码错误"

#### Scenario: 登录失败 - 账户已冻结
- **WHEN** 用户输入已被冻结的账户（status = 'frozen'）
- **THEN** 系统返回错误提示"账户已被冻结，请联系管理员"

### Requirement: 会话管理

系统 SHALL 维护用户登录会话状态。

#### Scenario: 会话保持
- **WHEN** 用户登录成功
- **THEN** 系统在 Session 中存储用户信息，有效期 24 小时

#### Scenario: 会话过期
- **WHEN** 用户会话超过 24 小时未活动
- **THEN** 系统自动清除会话，用户需重新登录

#### Scenario: 用户登出
- **WHEN** 用户点击登出按钮
- **THEN** 系统清除会话，跳转到登录页面

### Requirement: 菜单权限控制

系统 SHALL 根据用户角色动态展示可访问的菜单。

#### Scenario: 管理员菜单
- **WHEN** 系统管理员（role_code = 'admin'）登录
- **THEN** 系统展示全部 12 个菜单

#### Scenario: 工程管理员菜单
- **WHEN** 工程管理员（role_code = 'engineer'）登录
- **THEN** 系统展示 9 个菜单（无配置/用户/审计）

#### Scenario: 采购人员菜单
- **WHEN** 采购人员（role_code = 'purchaser'）登录
- **THEN** 系统展示仪表盘 + 采购入库菜单

#### Scenario: 运维人员菜单
- **WHEN** 运维人员（role_code = 'maintainer'）登录
- **THEN** 系统展示设备/保养/故障相关菜单

#### Scenario: 报废鉴定员菜单
- **WHEN** 报废鉴定员（role_code = 'scrap'）登录
- **THEN** 系统展示生命周期 + 报废鉴定菜单

### Requirement: API 接口

认证模块 SHALL 提供以下 RESTful API 接口：

#### Scenario: 登录接口
- **WHEN** 前端调用 POST /api/v1/auth/login
- **THEN** 系统验证用户名密码，返回用户信息和菜单权限

#### Scenario: 登出接口
- **WHEN** 前端调用 POST /api/v1/auth/logout
- **THEN** 系统清除会话，返回成功响应