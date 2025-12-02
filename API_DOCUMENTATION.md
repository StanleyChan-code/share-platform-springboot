# Share Platform 接口文档

## 1. 概述

本文档详细描述了Share Platform平台提供的RESTful API接口，包括用户认证、用户管理、数据集管理和机构管理四大模块。

## 2. 公共响应格式

所有API接口均使用统一的响应格式：

```json
{
  "success": true,
  "message": "响应消息",
  "data": {},
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 3. 用户认证接口

所有用户认证接口都在 `/api/auth` 路径下。

### 3.1 发送验证码

**接口地址**: `POST /api/auth/send-verification-code`

**请求参数**:

| 参数名        | 类型   | 必填 | 描述                           |
| ------------- | ------ | ---- | ------------------------------ |
| phone         | String | 是   | 手机号码                       |
| businessType  | String | 否   | 业务类型，默认为 LOGIN         |

**businessType 可选值**:
- LOGIN: 登录
- REGISTER: 注册
- RESET_PASSWORD: 重置密码

**响应示例**:
```json
{
  "success": true,
  "message": "验证码已发送",
  "data": "验证码已发送",
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 3.2 用户登录

**接口地址**: `POST /api/auth/login`

支持两种登录方式：
1. 手机号 + 密码登录
2. 手机号 + 验证码登录

**请求体（密码登录）**:
```json
{
  "phone": "13800138000",
  "password": "password123",
  "loginType": "PASSWORD"
}
```

**请求体（验证码登录）**:
```json
{
  "phone": "13800138000",
  "verificationCode": "123456",
  "loginType": "VERIFICATION_CODE"
}
```

**请求参数**:

| 参数名           | 类型   | 必填 | 描述                                    |
| ---------------- | ------ | ---- | --------------------------------------- |
| phone            | String | 是   | 手机号                                  |
| password         | String | 否   | 密码（密码登录时必填）                  |
| verificationCode | String | 否   | 验证码（验证码登录时必填）              |
| loginType        | String | 是   | 登录类型：PASSWORD 或 VERIFICATION_CODE |

**响应示例**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.xxxx",
    "phone": "13800138000",
    "authorities": ["registered_researcher"]
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 3.3 用户注册

**接口地址**: `POST /api/auth/register`

**请求体**:
```json
{
  "phone": "13800138000",
  "verificationCode": "123456",
  "username": "testuser",
  "realName": "测试用户",
  "email": "test@example.com",
  "password": "password123"
}
```

**请求参数**:

| 参数名           | 类型   | 必填 | 描述         |
| ---------------- | ------ | ---- | ------------ |
| phone            | String | 是   | 手机号码     |
| verificationCode | String | 是   | 验证码       |
| username         | String | 是   | 用户名       |
| realName         | String | 是   | 真实姓名     |
| email            | String | 否   | 邮箱地址     |
| password         | String | 是   | 密码         |

**响应示例**:
```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "realName": "测试用户",
    "phone": "13800138000",
    "email": "test@example.com",
    "createdAt": "2025-12-01T10:00:00Z",
    "updatedAt": "2025-12-01T10:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 3.4 用户登出

**接口地址**: `POST /api/auth/logout`

**权限要求**: 需要认证（携带有效的JWT Token）

**响应示例**:
```json
{
  "success": true,
  "message": "登出成功",
  "data": "登出成功",
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 4. 用户管理接口

所有用户管理接口都在 `/api/users` 路径下。

### 4.1 发送验证码

**接口地址**: `POST /api/users/send-verification-code`

**请求参数**:

| 参数名        | 类型   | 必填 | 描述     |
| ------------- | ------ | ---- | -------- |
| phone         | String | 是   | 手机号码 |
| businessType  | String | 是   | 业务类型 |

**响应示例**:
```json
{
  "success": true,
  "message": "验证码已发送",
  "data": {},
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 4.2 用户注册

**接口地址**: `POST /api/users/register`

**请求体**:
```json
{
  "phone": "13800138000",
  "verificationCode": "123456",
  "username": "testuser",
  "realName": "测试用户",
  "email": "test@example.com",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "realName": "测试用户",
    "phone": "13800138000",
    "email": "test@example.com"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 4.3 修改密码（通过请求体）

**接口地址**: `PUT /api/users/{userId}/password/body`

**请求参数**:

| 参数名           | 类型   | 必填 | 描述             |
| ---------------- | ------ | ---- | ---------------- |
| userId           | UUID   | 是   | 用户ID（路径参数） |
| phone            | String | 是   | 手机号码         |
| verificationCode | String | 是   | 验证码           |
| newPassword      | String | 是   | 新密码           |

**请求体**:
```json
{
  "phone": "13800138000",
  "verificationCode": "123456",
  "newPassword": "newpassword123"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "密码修改成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "realName": "测试用户",
    "phone": "13800138000",
    "email": "test@example.com"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 4.4 获取用户信息

**接口地址**: `GET /api/users/{userId}`

**响应示例**:
```json
{
  "success": true,
  "message": "获取用户信息成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "realName": "测试用户",
    "phone": "13800138000",
    "email": "test@example.com"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 5. 数据集管理接口

所有数据集管理接口都在 `/api/datasets` 路径下。

### 5.1 获取所有数据集

**接口地址**: `GET /api/datasets`

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "titleCn": "数据集标题",
      "description": "数据集描述",
      "type": "cohort",
      "category": "科研数据",
      "providerId": "550e8400-e29b-41d4-a716-446655440000",
      "startDate": "2025-01-01T00:00:00Z",
      "endDate": "2025-12-31T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.2 根据ID获取特定数据集

**接口地址**: `GET /api/datasets/{id}`

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "titleCn": "数据集标题",
    "description": "数据集描述",
    "type": "cohort",
    "category": "科研数据",
    "providerId": "550e8400-e29b-41d4-a716-446655440000",
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-12-31T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.3 创建新数据集

**接口地址**: `POST /api/datasets`

**请求体**:
```json
{
  "titleCn": "新数据集",
  "description": "这是一个新数据集",
  "type": "cohort",
  "category": "科研数据",
  "providerId": "550e8400-e29b-41d4-a716-446655440000",
  "startDate": "2025-01-01T00:00:00Z",
  "endDate": "2025-12-31T00:00:00Z"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "创建数据集成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "titleCn": "新数据集",
    "description": "这是一个新数据集",
    "type": "cohort",
    "category": "科研数据",
    "providerId": "550e8400-e29b-41d4-a716-446655440000",
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-12-31T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.4 更新数据集

**接口地址**: `PUT /api/datasets/{id}`

**请求体**:
```json
{
  "titleCn": "更新后的数据集",
  "description": "这是更新后的数据集描述",
  "type": "cohort",
  "category": "科研数据",
  "providerId": "550e8400-e29b-41d4-a716-446655440000",
  "startDate": "2025-01-01T00:00:00Z",
  "endDate": "2025-12-31T00:00:00Z"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "更新数据集成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "titleCn": "更新后的数据集",
    "description": "这是更新后的数据集描述",
    "type": "cohort",
    "category": "科研数据",
    "providerId": "550e8400-e29b-41d4-a716-446655440000",
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-12-31T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.5 删除数据集

**接口地址**: `DELETE /api/datasets/{id}`

**响应示例**:
```json
{
  "success": true,
  "message": "删除数据集成功",
  "data": null,
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 6. 机构管理接口

机构管理分为两类接口：
- 公共接口（无需认证）：`/api/institutions`
- 管理接口（需认证）：`/api/manage/institutions`

### 6.1 获取所有机构列表（公共接口）

**接口地址**: `GET /api/institutions`

**权限要求**: 无需认证，所有用户均可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取机构列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "fullName": "某某医院",
      "shortName": "某医",
      "type": "hospital",
      "contactPerson": "张三",
      "contactIdType": "national_id",
      "contactIdNumber": "11010119900307XXXX",
      "contactPhone": "13800138000",
      "contactEmail": "contact@example.com",
      "verified": true,
      "createdAt": "2025-01-01T00:00:00Z",
      "updatedAt": "2025-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.2 根据ID获取特定机构（公共接口）

**接口地址**: `GET /api/institutions/{id}`

**权限要求**: 无需认证，所有用户均可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取机构成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "fullName": "某某医院",
    "shortName": "某医",
    "type": "hospital",
    "contactPerson": "张三",
    "contactIdType": "national_id",
    "contactIdNumber": "11010119900307XXXX",
    "contactPhone": "13800138000",
    "contactEmail": "contact@example.com",
    "verified": true,
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.3 获取所有机构列表（管理接口）

**接口地址**: `GET /api/manage/institutions`

**权限要求**: 仅平台管理员可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取机构列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "fullName": "某某医院",
      "shortName": "某医",
      "type": "hospital",
      "contactPerson": "张三",
      "contactIdType": "national_id",
      "contactIdNumber": "11010119900307XXXX",
      "contactPhone": "13800138000",
      "contactEmail": "contact@example.com",
      "verified": true,
      "createdAt": "2025-01-01T00:00:00Z",
      "updatedAt": "2025-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.4 根据ID获取特定机构（管理接口）

**接口地址**: `GET /api/manage/institutions/{id}`

**权限要求**: 仅平台管理员可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取机构成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "fullName": "某某医院",
    "shortName": "某医",
    "type": "hospital",
    "contactPerson": "张三",
    "contactIdType": "national_id",
    "contactIdNumber": "11010119900307XXXX",
    "contactPhone": "13800138000",
    "contactEmail": "contact@example.com",
    "verified": true,
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.5 创建新机构

**接口地址**: `POST /api/manage/institutions`

**权限要求**: 仅平台管理员可访问

**请求体**:
```json
{
  "fullName": "某某研究中心",
  "shortName": "某研",
  "type": "research_center",
  "contactPerson": "李四",
  "contactIdType": "national_id",
  "contactIdNumber": "11010119900307XXXX",
  "contactPhone": "13900139000",
  "contactEmail": "contact@example.org"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "创建机构成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "fullName": "某某研究中心",
    "shortName": "某研",
    "type": "research_center",
    "contactPerson": "李四",
    "contactIdType": "national_id",
    "contactIdNumber": "11010119900307XXXX",
    "contactPhone": "13900139000",
    "contactEmail": "contact@example.org",
    "verified": false,
    "createdAt": "2025-12-01T10:00:00Z",
    "updatedAt": "2025-12-01T10:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.6 更新机构信息

**接口地址**: `PUT /api/manage/institutions/{id}`

**权限要求**: 平台管理员可更新任意机构，机构管理员只能更新自己所属的机构

**请求体**:
```json
{
  "fullName": "某某研究中心（更新后）",
  "shortName": "某研中",
  "type": "research_center",
  "contactPerson": "王五",
  "contactIdType": "national_id",
  "contactIdNumber": "11010119900307XXXX",
  "contactPhone": "13700137000",
  "contactEmail": "contact_updated@example.org"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "更新机构成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "fullName": "某某研究中心（更新后）",
    "shortName": "某研中",
    "type": "research_center",
    "contactPerson": "王五",
    "contactIdType": "national_id",
    "contactIdNumber": "11010119900307XXXX",
    "contactPhone": "13700137000",
    "contactEmail": "contact_updated@example.org",
    "verified": false,
    "createdAt": "2025-12-01T10:00:00Z",
    "updatedAt": "2025-12-01T11:00:00Z"
  },
  "timestamp": "2025-12-01T11:00:00Z"
}
```

### 6.7 删除机构

**接口地址**: `DELETE /api/manage/institutions/{id}`

**权限要求**: 仅平台管理员可访问

**响应示例**:
```json
{
  "success": true,
  "message": "删除机构成功",
  "data": null,
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.8 获取当前机构管理员所属机构信息

**接口地址**: `GET /api/manage/institutions/own`

**权限要求**: 仅机构管理员可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取机构信息成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "fullName": "某某医院",
    "shortName": "某医",
    "type": "hospital",
    "contactPerson": "张三",
    "contactIdType": "national_id",
    "contactIdNumber": "11010119900307XXXX",
    "contactPhone": "13800138000",
    "contactEmail": "contact@example.com",
    "verified": true,
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.9 更新当前机构管理员所属机构信息

**接口地址**: `PUT /api/manage/institutions/own`

**权限要求**: 仅机构管理员可访问

**请求体**:
```json
{
  "fullName": "某某医院（更新后）",
  "shortName": "某医新",
  "type": "hospital",
  "contactPerson": "赵六",
  "contactIdType": "national_id",
  "contactIdNumber": "11010119900307XXXX",
  "contactPhone": "13600136000",
  "contactEmail": "contact_new@example.com"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "更新机构信息成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "fullName": "某某医院（更新后）",
    "shortName": "某医新",
    "type": "hospital",
    "contactPerson": "赵六",
    "contactIdType": "national_id",
    "contactIdNumber": "11010119900307XXXX",
    "contactPhone": "13600136000",
    "contactEmail": "contact_new@example.com",
    "verified": true,
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-12-01T11:00:00Z"
  },
  "timestamp": "2025-12-01T11:00:00Z"
}
```

## 7. 枚举类型

### 7.1 DatasetType (数据集类型)

| 枚举值 | 描述 |
| ------ | ---- |
| cohort | 队列研究 |
| case_control | 病例对照研究 |
| cross_sectional | 横断面研究 |
| rct | 随机对照试验 |
| registry | 登记研究 |
| biobank | 生物样本库 |
| omics | 组学研究 |
| wearable | 可穿戴设备研究 |

### 7.2 IdType (证件类型)

| 枚举值 | 描述 |
| ------ | ---- |
| national_id | 身份证 |
| passport | 护照 |
| other | 其他 |

### 7.3 EducationLevel (教育程度)

| 枚举值 | 描述 |
| ------ | ---- |
| bachelor | 学士 |
| master | 硕士 |
| phd | 博士 |
| postdoc | 博士后 |
| professor | 教授 |
| other | 其他 |

### 7.4 ApplicantRole (申请者角色)

| 枚举值 | 描述 |
| ------ | ---- |
| team_researcher | 团队研究员 |
| collaborative_researcher | 协同研究员 |

### 7.5 ApplicationStatus (申请状态)

| 枚举值 | 描述 |
| ------ | ---- |
| submitted | 已提交 |
| under_review | 审核中 |
| approved | 已批准 |
| denied | 已拒绝 |

### 7.6 InstitutionType (机构类型)

| 枚举值 | 描述 |
| ------ | ---- |
| hospital | 医院 |
| university | 大学 |
| research_center | 研究中心 |
| lab | 实验室 |
| government | 政府机构 |
| enterprise | 企业 |
| other | 其他 |

### 7.7 OutputType (成果类型)

| 枚举值 | 描述 |
| ------ | ---- |
| paper | 论文 |
| patent | 专利 |
| publication | 出版物 |
| software | 软件 |
| project | 项目 |
| invention_patent | 发明专利 |
| utility_patent | 实用新型专利 |
| software_copyright | 软件著作权 |
| other_award | 其他奖项 |

### 7.8 UserAuthority (用户权限)

| 枚举值 | 描述 |
| ------ | ---- |
| public_visitor | 公共访客 |
| registered_researcher | 注册研究员 |
| data_provider | 数据提供者 |
| institution_supervisor | 机构监管员 |
| platform_admin | 平台管理员 |

## 8. 错误处理

当发生错误时，API会返回相应的HTTP状态码和错误信息：
- 400: 请求参数错误
- 401: 未认证
- 403: 权限不足
- 404: 资源未找到
- 500: 服务器内部错误

错误响应示例:
```json
{
  "success": false,
  "message": "验证码无效或已过期",
  "data": null,
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 9. 注意事项

1. 所有时间字段均采用ISO 8601格式（UTC时间）
2. 所有ID字段均为UUID格式
3. 验证码具有时效性，过期后无法使用
4. 登录成功后，客户端需在后续请求的Authorization头部中携带JWT Token：`Bearer <token>`