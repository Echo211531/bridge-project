## ADDED Requirements

### Requirement: 用户列表查询

系统 SHALL 支持分页查询用户列表。

#### Scenario: 获取用户列表
- **WHEN** 前端调用 GET /api/v1/users?page=1&size=10
- **THEN** 系统返回用户列表（包含 username, real_name, role_code, status, create_time）

#### Scenario: 搜索用户
- **WHEN** 前端调用 GET /api/v1/users?keyword=admin
- **THEN** 系统返回匹配用户名的用户列表

### Requirement: 用户详情查询

系统 SHALL 支持查询单个用户详情。

#### Scenario: 获取用户详情
- **WHEN** 前端调用 GET /api/v1/users/{id}
- **THEN** 系统返回用户完整信息（包含所有字段）

### Requirement: 用户创建

系统 SHALL 支持创建新用户。

#### Scenario: 成功创建用户
- **WHEN** 前端调用 POST /api/v1/users，提供 username, password, real_name, role_code
- **THEN** 系统创建用户，返回新用户 ID

#### Scenario: 用户名已存在
- **WHEN** 前端调用 POST /api/v1/users，提供已存在的 username
- **THEN** 系统返回错误提示"用户名已存在"

### Requirement: 用户更新

系统 SHALL 支持更新用户信息。

#### Scenario: 成功更新用户
- **WHEN** 前端调用 PUT /api/v1/users/{id}，提供 real_name, role_code
- **THEN** 系统更新用户信息，返回成功响应

### Requirement: 用户状态切换

系统 SHALL 支持启用/禁用用户。

#### Scenario: 禁用用户
- **WHEN** 前端调用 PUT /api/v1/users/{id}/status，提供 status=frozen
- **THEN** 系统更新用户状态为冻结，该用户无法登录

#### Scenario: 启用用户
- **WHEN** 前端调用 PUT /api/v1/users/{id}/status，提供 status=active
- **THEN** 系统更新用户状态为激活，该用户可正常登录

### Requirement: 密码重置

系统 SHALL 支持重置用户密码。

#### Scenario: 重置密码
- **WHEN** 前端调用 PUT /api/v1/users/{id}/password
- **THEN** 系统将密码重置为默认密码（如 username123）

### Requirement: 用户删除

系统 SHALL 支持删除用户（逻辑删除）。

#### Scenario: 删除用户
- **WHEN** 前端调用 DELETE /api/v1/users/{id}
- **THEN** 系统标记用户为已删除（is_deleted=1），用户不再出现在列表中

### Requirement: 角色管理

系统 SHALL 支持查询角色列表。

#### Scenario: 获取角色列表
- **WHEN** 前端调用 GET /api/v1/roles
- **THEN** 系统返回角色列表（包含 code, name, description）

### Requirement: 角色-菜单关联查询

系统 SHALL 支持查询角色的菜单权限。

#### Scenario: 获取角色菜单
- **WHEN** 前端调用 GET /api/v1/roles/{roleCode}/menus
- **THEN** 系统返回该角色可访问的菜单编码列表