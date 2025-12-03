# 研究学科管理接口

研究学科管理分为两类接口：
- 公共接口（无需认证）：`/api/research-subjects`
- 管理接口（需认证）：`/api/manage/research-subjects`

## 1. 公共研究学科接口

### 1.1 获取所有激活的研究学科列表

**接口地址**: `GET /api/research-subjects`

**权限要求**: 所有用户均可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取研究学科列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "心血管病学",
      "nameEn": "Cardiology",
      "description": "心血管系统疾病研究",
      "active": true,
      "createdAt": "2025-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 1.2 根据ID获取特定激活的研究学科

**接口地址**: `GET /api/research-subjects/{id}`

**权限要求**: 所有用户均可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取研究学科成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "心血管病学",
    "nameEn": "Cardiology",
    "description": "心血管系统疾病研究",
    "active": true,
    "createdAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 2. 管理研究学科接口

### 2.1 获取所有研究学科列表（包括激活和非激活的）

**接口地址**: `GET /api/manage/research-subjects`

**权限要求**: 仅平台管理员可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取研究学科列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "心血管病学",
      "nameEn": "Cardiology",
      "description": "心血管系统疾病研究",
      "active": true,
      "createdAt": "2025-01-01T00:00:00Z"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "肿瘤学",
      "nameEn": "Oncology",
      "description": "肿瘤研究",
      "active": false,
      "createdAt": "2025-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 2.2 根据ID获取特定研究学科（无论是否激活）

**接口地址**: `GET /api/manage/research-subjects/{id}`

**权限要求**: 仅平台管理员可访问

**响应示例**:
```json
{
  "success": true,
  "message": "获取研究学科成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "心血管病学",
    "nameEn": "Cardiology",
    "description": "心血管系统疾病研究",
    "active": true,
    "createdAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 2.3 创建新的研究学科

**接口地址**: `POST /api/manage/research-subjects`

**权限要求**: 仅平台管理员可访问

**请求体**:
```json
{
  "name": "新学科",
  "nameEn": "New Subject",
  "description": "这是一个新学科",
  "active": true
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "创建研究学科成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "新学科",
    "nameEn": "New Subject",
    "description": "这是一个新学科",
    "active": true,
    "createdAt": "2025-12-01T10:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 2.4 更新现有研究学科

**接口地址**: `PUT /api/manage/research-subjects/{id}`

**权限要求**: 仅平台管理员可访问

**请求体**:
```json
{
  "name": "更新后的学科",
  "nameEn": "Updated Subject",
  "description": "这是更新后的学科描述",
  "active": false
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "更新研究学科成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "更新后的学科",
    "nameEn": "Updated Subject",
    "description": "这是更新后的学科描述",
    "active": false,
    "createdAt": "2025-12-01T10:00:00Z"
  },
  "timestamp": "2025-12-01T11:00:00Z"
}
```

### 2.5 删除研究学科

**接口地址**: `DELETE /api/manage/research-subjects/{id}`

**权限要求**: 仅平台管理员可访问

**响应示例**:
```json
{
  "success": true,
  "message": "删除研究学科成功",
  "data": null,
  "timestamp": "2025-12-01T10:00:00Z"
}
```