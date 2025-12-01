# Share Platform 接口文档

## 1. 概述

本文档详细描述了Share Platform平台提供的RESTful API接口，包括用户管理和数据集管理两大模块。

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

## 3. 用户管理接口

所有用户管理接口都在 `/api/users` 路径下。

### 3.1 发送验证码

**接口地址**: `POST /api/users/send-verification-code`

**请求参数**:

| 参数名        | 类型   | 必填 | 描述     |
| ------------- | ------ | ---- | -------- |
| phoneNumber   | String | 是   | 手机号码 |
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

### 3.2 用户注册

**接口地址**: `POST /api/users/register`

**请求体**:
```json
{
  "phoneNumber": "13800138000",
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

### 3.4 修改密码（通过请求体）

**接口地址**: `PUT /api/users/{userId}/password/body`

**请求参数**:

| 参数名          | 类型   | 必填 | 描述             |
| --------------- | ------ | ---- | ---------------- |
| userId          | UUID   | 是   | 用户ID（路径参数） |
| phoneNumber     | String | 是   | 手机号码 |
| verificationCode| String | 是   | 验证码  |
| newPassword     | String | 是   | 新密码  |

**请求体**:
```json
{
  "phoneNumber": "13800138000",
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

### 3.5 获取用户信息

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

## 4. 数据集管理接口

所有数据集管理接口都在 `/api/datasets` 路径下。

### 4.1 获取所有数据集

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

### 4.2 根据ID获取特定数据集

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

### 4.3 创建新数据集

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

### 4.4 更新数据集

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

### 4.5 删除数据集

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

## 5. 枚举类型

### 5.1 DatasetType (数据集类型)

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

### 5.2 IdType (证件类型)

| 枚举值 | 描述 |
| ------ | ---- |
| national_id | 身份证 |
| passport | 护照 |
| other | 其他 |

### 5.3 EducationLevel (教育程度)

| 枚举值 | 描述 |
| ------ | ---- |
| bachelor | 学士 |
| master | 硕士 |
| phd | 博士 |
| postdoc | 博士后 |
| professor | 教授 |
| other | 其他 |

### 5.4 ApplicantRole (申请者角色)

| 枚举值 | 描述 |
| ------ | ---- |
| team_researcher | 团队研究员 |
| collaborative_researcher | 协同研究员 |

### 5.5 ApplicationStatus (申请状态)

| 枚举值 | 描述 |
| ------ | ---- |
| submitted | 已提交 |
| under_review | 审核中 |
| approved | 已批准 |
| denied | 已拒绝 |

### 5.6 InstitutionType (机构类型)

| 枚举值 | 描述 |
| ------ | ---- |
| hospital | 医院 |
| university | 大学 |
| research_center | 研究中心 |
| lab | 实验室 |
| government | 政府机构 |
| enterprise | 企业 |
| other | 其他 |

### 5.7 OutputType (成果类型)

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

### 5.8 UserRole (用户角色)

| 枚举值 | 描述 |
| ------ | ---- |
| public_visitor | 公共访客 |
| registered_researcher | 注册研究员 |
| data_provider | 数据提供者 |
| institution_supervisor | 机构监管员 |
| platform_admin | 平台管理员 |

## 6. 错误处理

当发生错误时，API会返回相应的HTTP状态码和错误信息：
- 400: 请求参数错误
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

## 7. 注意事项

1. 所有时间字段均采用ISO 8601格式（UTC时间）
2. 所有ID字段均为UUID格式
3. 验证码具有时效性，过期后无法使用
