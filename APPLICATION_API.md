# 数据集申请接口文档

## 1. 概述

本文档详细描述了数据集申请相关的API接口，包括申请提交、审核和查询等功能。

## 2. 权限说明

| 角色 | 权限 |
|------|------|
| 已登录用户 | 可以提交数据集申请 |
| 数据集提供者 | 可以审核申请 |
| PLATFORM_ADMIN | 可以查看和审核所有申请 |
| INSTITUTION_SUPERVISOR | 可以查看和审核本机构的申请 |
| DATASET_APPROVER | 可以审核本机构的申请 |

## 3. 申请状态说明

| 状态 | 说明 |
|------|------|
| SUBMITTED | 已提交 |
| PENDING_PROVIDER_REVIEW | 待提供方审核 |
| PENDING_INSTITUTION_REVIEW | 待机构审核 |
| APPROVED | 已批准 |
| DENIED | 已拒绝 |

## 4. 申请角色说明

| 角色 | 说明 |
|------|------|
| TEAM_RESEARCHER | 团队研究人员 |
| COLLABORATIVE_RESEARCHER | 协作研究人员 |

## 5. 用户申请接口

### 5.1 用户申请数据集

**接口地址**: `POST /api/applications`

**权限要求**: 已登录用户

**请求体**:
```json
{
  "datasetVersionId": "数据集版本ID",
  "applicantRole": "TEAM_RESEARCHER",
  "applicantType": "申请人类别",
  "projectTitle": "项目标题",
  "projectDescription": "项目描述",
  "fundingSource": "资金来源",
  "purpose": "使用目的",
  "projectLeader": "项目负责人",
  "approvalDocumentId": "审批文件ID"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "申请提交成功",
  "data": {
    "id": "申请ID",
    "datasetVersionId": "数据集版本ID",
    "datasetTitle": "数据集标题",
    "applicantId": "申请人ID",
    "applicantName": "申请人姓名",
    "applicantRole": "TEAM_RESEARCHER",
    "applicantType": "申请人类别",
    "projectTitle": "项目标题",
    "projectDescription": "项目描述",
    "fundingSource": "资金来源",
    "purpose": "使用目的",
    "projectLeader": "项目负责人",
    "approvalDocumentId": "审批文件ID",
    "status": "SUBMITTED",
    "adminNotes": null,
    "providerNotes": null,
    "submittedAt": "2022-01-01T00:00:00Z",
    "providerReviewedAt": null,
    "institutionReviewedAt": null,
    "approvedAt": null
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.2 数据集提供者审核申请

**接口地址**: `PUT /api/applications/{id}/provider-review`

**权限要求**: 数据集提供者

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 申请ID |

**请求体**:
```json
{
  "notes": "审核意见",
  "approved": true
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "审核完成",
  "data": {
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.3 申请者查询自己的申请记录

**接口地址**: `GET /api/applications/my-applications`

**权限要求**: 已登录用户

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页大小 |
| sortBy | string | 否 | submittedAt | 排序字段 |
| sortDir | string | 否 | desc | 排序方向(asc/desc) |

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "content": [
      {
      }
    ]
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.4 数据集提供者查看申请记录列表

**接口地址**: `GET /api/applications/provider-applications`

**权限要求**: 数据集提供者

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页大小 |
| sortBy | string | 否 | submittedAt | 排序字段 |
| sortDir | string | 否 | desc | 排序方向(asc/desc) |

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "content": [
      {
      }
    ]
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 6. 申请管理接口

### 6.1 申请审核员审核申请

**接口地址**: `PUT /api/manage/applications/{id}/approver-review`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_APPROVER

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 申请ID |

**请求体**:
```json
{
  "notes": "审核意见",
  "approved": true
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "审核完成",
  "data": {
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.2 申请审核员查看待审核申请记录列表

**接口地址**: `GET /api/manage/applications/pending-applications`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_APPROVER

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页大小 |
| sortBy | string | 否 | submittedAt | 排序字段 |
| sortDir | string | 否 | desc | 排序方向(asc/desc) |

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "content": [
      {
      }
    ]
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 6.3 申请审核员查看已处理申请记录列表

**接口地址**: `GET /api/manage/applications/processed-applications`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_APPROVER

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页大小 |
| sortBy | string | 否 | submittedAt | 排序字段 |
| sortDir | string | 否 | desc | 排序方向(asc/desc) |

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "approvedApplications": {
      "content": [
        {
          "id": "申请ID",
          "datasetVersionId": "数据集版本ID",
          "datasetTitle": "数据集标题",
          "applicantId": "申请人ID",
          "applicantName": "申请人姓名",
          "applicantRole": "TEAM_RESEARCHER",
          "applicantType": "申请人类别",
          "projectTitle": "项目标题",
          "projectDescription": "项目描述",
          "fundingSource": "资金来源",
          "purpose": "使用目的",
          "projectLeader": "项目负责人",
          "approvalDocumentId": "审批文件ID",
          "status": "APPROVED",
          "adminNotes": null,
          "providerNotes": null,
          "submittedAt": "2022-01-01T00:00:00Z",
          "providerReviewedAt": null,
          "institutionReviewedAt": null,
          "approvedAt": "2022-01-02T00:00:00Z"
        }
      ]
    },
    "deniedApplications": {
      "content": [
        {
          "id": "申请ID",
          "datasetVersionId": "数据集版本ID",
          "datasetTitle": "数据集标题",
          "applicantId": "申请人ID",
          "applicantName": "申请人姓名",
          "applicantRole": "TEAM_RESEARCHER",
          "applicantType": "申请人类别",
          "projectTitle": "项目标题",
          "projectDescription": "项目描述",
          "fundingSource": "资金来源",
          "purpose": "使用目的",
          "projectLeader": "项目负责人",
          "approvalDocumentId": "审批文件ID",
          "status": "DENIED",
          "adminNotes": null,
          "providerNotes": null,
          "submittedAt": "2022-01-01T00:00:00Z",
          "providerReviewedAt": null,
          "institutionReviewedAt": null,
          "approvedAt": null
        }
      ]
    }
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```