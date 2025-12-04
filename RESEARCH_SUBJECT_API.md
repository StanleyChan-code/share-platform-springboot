# 研究学科API文档

## 1. 管理端API

### 1.1 获取所有研究学科

**接口地址**: `GET /api/manage/research-subjects`

**权限要求**: 平台管理员

**请求参数**: 无

**响应示例**:
``json
{
  "success": true,
  "message": "获取研究学科列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "临床医学",
      "nameEn": "Clinical Medicine",
      "description": "研究疾病预防、诊断和治疗的学科",
      "active": true,
      "createdAt": "2025-01-01T00:00:00Z"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "基础医学",
      "nameEn": "Basic Medicine",
      "description": "研究人体结构和功能的基础学科",
      "active": false,
      "createdAt": "2025-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 1.2 根据ID获取研究学科

**接口地址**: `GET /api/manage/research-subjects/{id}`

**权限要求**: 平台管理员

**请求参数**:
- id (UUID, 路径参数): 研究学科ID

**响应示例**:
``json
{
  "success": true,
  "message": "获取研究学科成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "临床医学",
    "nameEn": "Clinical Medicine",
    "description": "研究疾病预防、诊断和治疗的学科",
    "active": true,
    "createdAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 1.3 创建研究学科

**接口地址**: `POST /api/manage/research-subjects`

**权限要求**: 平台管理员

**请求体**:
```json
{
  "name": "生物医学工程",
  "nameEn": "Biomedical Engineering",
  "description": "结合生物学、医学和工程学的交叉学科",
  "active": true
}
```

**响应示例**:
``json
{
  "success": true,
  "message": "创建研究学科成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "生物医学工程",
    "nameEn": "Biomedical Engineering",
    "description": "结合生物学、医学和工程学的交叉学科",
    "active": true,
    "createdAt": "2025-12-01T10:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 1.4 更新研究学科

**接口地址**: `PUT /api/manage/research-subjects/{id}`

**权限要求**: 平台管理员

**请求参数**:
- id (UUID, 路径参数): 研究学科ID

**请求体**:
```json
{
  "name": "生物医学工程",
  "nameEn": "Biomedical Engineering",
  "description": "结合生物学、医学和工程学的交叉学科，专注于医疗设备研发",
  "active": true
}
```

**响应示例**:
``json
{
  "success": true,
  "message": "更新研究学科成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "生物医学工程",
    "nameEn": "Biomedical Engineering",
    "description": "结合生物学、医学和工程学的交叉学科，专注于医疗设备研发",
    "active": true,
    "createdAt": "2025-12-01T10:00:00Z"
  },
  "timestamp": "2025-12-01T11:00:00Z"
}
```

### 1.5 删除研究学科

**接口地址**: `DELETE /api/manage/research-subjects/{id}`

**权限要求**: 平台管理员

**请求参数**:
- id (UUID, 路径参数): 研究学科ID

**响应示例**:
```json
{
  "success": true,
  "message": "删除研究学科成功",
  "data": null,
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 2. 公共API

### 2.1 获取所有激活的研究学科

**接口地址**: `GET /api/research-subjects`

**权限要求**: 无需登录

**请求参数**: 无

**响应示例**:
```json
{
  "success": true,
  "message": "获取研究学科列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "临床医学",
      "nameEn": "Clinical Medicine",
      "description": "研究疾病预防、诊断和治疗的学科",
      "active": true,
      "createdAt": "2025-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 2.2 根据ID获取研究学科

**接口地址**: `GET /api/research-subjects/{id}`

**权限要求**: 无需登录

**请求参数**:
- id (UUID, 路径参数): 研究学科ID

**说明**: 此接口可以获取任何研究学科（无论是否激活），与管理端相同。

**响应示例**:
```json
{
  "success": true,
  "message": "获取研究学科成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "临床医学",
    "nameEn": "Clinical Medicine",
    "description": "研究疾病预防、诊断和治疗的学科",
    "active": true,
    "createdAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```