# 用户管理接口

所有用户管理接口都在 `/api/users` 和 `/api/manage/users` 路径下。

## 1. 获取用户信息

**接口地址**: `GET /api/users/profile`

**权限要求**: 需要认证

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

## 2. 根据ID获取用户信息

**接口地址**: `GET /api/users/{userId}`

**权限要求**: 仅平台管理员可访问

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

## 3. 平台管理员创建用户

**接口地址**: `POST /api/manage/users`

**权限要求**: 仅平台管理员可访问

**请求体**:
```json
{
  "username": "newuser",
  "realName": "新用户",
  "phone": "13900139000",
  "email": "newuser@example.com",
  "password": "password123",
  "institutionId": "550e8400-e29b-41d4-a716-446655440001"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "用户创建成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "username": "newuser",
    "realName": "新用户",
    "phone": "13900139000",
    "email": "newuser@example.com",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```