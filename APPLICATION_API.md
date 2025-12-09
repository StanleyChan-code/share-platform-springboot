# 数据集申请接口

数据集申请分为两类接口：
- 用户申请接口（需认证）：`/api/applications`
- 管理申请接口（需认证）：`/api/manage/applications`

## 1. 用户申请接口

### 1.1 提交数据集申请

**接口地址**: `POST /api/applications`

**权限要求**: 已认证用户

**请求体**:
```json
{
  "datasetId": "数据集UUID",
  "applicantRole": "TEAM_RESEARCHER 或 COLLABORATIVE_RESEARCHER",
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
    "datasetId": "数据集ID",
    "datasetTitle": "数据集标题",
    "applicantId": "申请人ID",
    "applicantName": "申请人姓名",
    "supervisorId": "监督人ID",
    "supervisorName": "监督人姓名",
    "applicantRole": "申请角色",
    "applicantType": "申请人类别",
    "projectTitle": "项目标题",
    "projectDescription": "项目描述",
    "fundingSource": "资金来源",
    "purpose": "使用目的",
    "projectLeader": "项目负责人",
    "approvalDocumentId": "审批文件ID",
    "status": "申请状态",
    "adminNotes": "管理员备注",
    "providerNotes": "提供方备注",
    "submittedAt": "提交时间",
    "providerReviewedAt": "提供方审核时间",
    "institutionReviewedAt": "机构审核时间",
    "approvedAt": "批准时间"
  },
  "timestamp": "时间戳"
}
```

### 1.2 申请者查询自己的申请记录

**接口地址**: `GET /api/applications/my-applications`

**权限要求**: 已认证用户

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| page | Integer | 否 | 页码，默认为0 |
| size | Integer | 否 | 每页大小，默认为10 |
| sortBy | String | 否 | 排序字段，默认为submittedAt |
| sortDir | String | 否 | 排序方向(asc/desc)，默认为desc |

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "content": [
      {
        "id": "申请ID",
        "datasetId": "数据集ID",
        "datasetTitle": "数据集标题",
        "applicantId": "申请人ID",
        "applicantName": "申请人姓名",
        "supervisorId": "监督人ID",
        "supervisorName": "监督人姓名",
        "applicantRole": "申请角色",
        "applicantType": "申请人类别",
        "projectTitle": "项目标题",
        "projectDescription": "项目描述",
        "fundingSource": "资金来源",
        "purpose": "使用目的",
        "projectLeader": "项目负责人",
        "approvalDocumentId": "审批文件ID",
        "status": "申请状态",
        "adminNotes": "管理员备注",
        "providerNotes": "提供方备注",
        "submittedAt": "提交时间",
        "providerReviewedAt": "提供方审核时间",
        "institutionReviewedAt": "机构审核时间",
        "approvedAt": "批准时间"
      }
    ],
    "page": {
      "size": 10,
      "number": 0,
      "totalElements": 1,
      "totalPages": 1
    }
  },
  "timestamp": "时间戳"
}
```

### 1.3 数据集提供者查看申请记录列表

**接口地址**: `GET /api/applications/provider-applications`

**权限要求**: 已认证用户

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| page | Integer | 否 | 页码，默认为0 |
| size | Integer | 否 | 每页大小，默认为10 |
| sortBy | String | 否 | 排序字段，默认为submittedAt |
| sortDir | String | 否 | 排序方向(asc/desc)，默认为desc |

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "content": [
      {
        "id": "申请ID",
        "datasetId": "数据集ID",
        "datasetTitle": "数据集标题",
        "applicantId": "申请人ID",
        "applicantName": "申请人姓名",
        "supervisorId": "监督人ID",
        "supervisorName": "监督人姓名",
        "applicantRole": "申请角色",
        "applicantType": "申请人类别",
        "projectTitle": "项目标题",
        "projectDescription": "项目描述",
        "fundingSource": "资金来源",
        "purpose": "使用目的",
        "projectLeader": "项目负责人",
        "approvalDocumentId": "审批文件ID",
        "status": "申请状态",
        "adminNotes": "管理员备注",
        "providerNotes": "提供方备注",
        "submittedAt": "提交时间",
        "providerReviewedAt": "提供方审核时间",
        "institutionReviewedAt": "机构审核时间",
        "approvedAt": "批准时间"
      }
    ],
    "page": {
      "size": 10,
      "number": 0,
      "totalElements": 1,
      "totalPages": 1
    }
  },
  "timestamp": "时间戳"
}
```

### 1.4 数据集提供者审核申请

**接口地址**: `PUT /api/applications/{id}/provider-review`

**权限要求**: 已认证用户

**请求体**:
```json
{
  "notes": "审核备注",
  "approved": true
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "审核完成",
  "data": {
    "id": "申请ID",
    "datasetId": "数据集ID",
    "datasetTitle": "数据集标题",
    "applicantId": "申请人ID",
    "applicantName": "申请人姓名",
    "supervisorId": "监督人ID",
    "supervisorName": "监督人姓名",
    "applicantRole": "申请角色",
    "applicantType": "申请人类别",
    "projectTitle": "项目标题",
    "projectDescription": "项目描述",
    "fundingSource": "资金来源",
    "purpose": "使用目的",
    "projectLeader": "项目负责人",
    "approvalDocumentId": "审批文件ID",
    "status": "申请状态",
    "adminNotes": "管理员备注",
    "providerNotes": "提供方备注",
    "submittedAt": "提交时间",
    "providerReviewedAt": "提供方审核时间",
    "institutionReviewedAt": "机构审核时间",
    "approvedAt": "批准时间"
  },
  "timestamp": "时间戳"
}
```

## 2. 管理申请接口

### 2.1 申请审核员审核申请

**接口地址**: `PUT /api/manage/applications/{id}/approver-review`

**权限要求**: PLATFORM_ADMIN、INSTITUTION_SUPERVISOR 或 DATASET_APPROVER

**请求体**:
```json
{
  "notes": "审核备注",
  "approved": true
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "审核完成",
  "data": {
    "id": "申请ID",
    "datasetId": "数据集ID",
    "datasetTitle": "数据集标题",
    "applicantId": "申请人ID",
    "applicantName": "申请人姓名",
    "supervisorId": "监督人ID",
    "supervisorName": "监督人姓名",
    "applicantRole": "申请角色",
    "applicantType": "申请人类别",
    "projectTitle": "项目标题",
    "projectDescription": "项目描述",
    "fundingSource": "资金来源",
    "purpose": "使用目的",
    "projectLeader": "项目负责人",
    "approvalDocumentId": "审批文件ID",
    "status": "申请状态",
    "adminNotes": "管理员备注",
    "providerNotes": "提供方备注",
    "submittedAt": "提交时间",
    "providerReviewedAt": "提供方审核时间",
    "institutionReviewedAt": "机构审核时间",
    "approvedAt": "批准时间"
  },
  "timestamp": "时间戳"
}
```

### 2.2 申请审核员查看待审核申请记录列表

**接口地址**: `GET /api/manage/applications/pending-applications`

**权限要求**: PLATFORM_ADMIN、INSTITUTION_SUPERVISOR 或 DATASET_APPROVER

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| page | Integer | 否 | 页码，默认为0 |
| size | Integer | 否 | 每页大小，默认为10 |
| sortBy | String | 否 | 排序字段，默认为submittedAt |
| sortDir | String | 否 | 排序方向(asc/desc)，默认为desc |

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "content": [
      {
        "id": "申请ID",
        "datasetId": "数据集ID",
        "datasetTitle": "数据集标题",
        "applicantId": "申请人ID",
        "applicantName": "申请人姓名",
        "supervisorId": "监督人ID",
        "supervisorName": "监督人姓名",
        "applicantRole": "申请角色",
        "applicantType": "申请人类别",
        "projectTitle": "项目标题",
        "projectDescription": "项目描述",
        "fundingSource": "资金来源",
        "purpose": "使用目的",
        "projectLeader": "项目负责人",
        "approvalDocumentId": "审批文件ID",
        "status": "申请状态",
        "adminNotes": "管理员备注",
        "providerNotes": "提供方备注",
        "submittedAt": "提交时间",
        "providerReviewedAt": "提供方审核时间",
        "institutionReviewedAt": "机构审核时间",
        "approvedAt": "批准时间"
      }
    ],
    "page": {
      "size": 10,
      "number": 0,
      "totalElements": 1,
      "totalPages": 1
    }
  },
  "timestamp": "时间戳"
}
```

### 2.3 申请审核员查看已处理申请记录列表

**接口地址**: `GET /api/manage/applications/processed-applications`

**权限要求**: PLATFORM_ADMIN、INSTITUTION_SUPERVISOR 或 DATASET_APPROVER

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| page | Integer | 否 | 页码，默认为0 |
| size | Integer | 否 | 每页大小，默认为10 |
| sortBy | String | 否 | 排序字段，默认为submittedAt |
| sortDir | String | 否 | 排序方向(asc/desc)，默认为desc |

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
          "datasetId": "数据集ID",
          "datasetTitle": "数据集标题",
          "applicantId": "申请人ID",
          "applicantName": "申请人姓名",
          "supervisorId": "监督人ID",
          "supervisorName": "监督人姓名",
          "applicantRole": "申请角色",
          "applicantType": "申请人类别",
          "projectTitle": "项目标题",
          "projectDescription": "项目描述",
          "fundingSource": "资金来源",
          "purpose": "使用目的",
          "projectLeader": "项目负责人",
          "approvalDocumentId": "审批文件ID",
          "status": "申请状态",
          "adminNotes": "管理员备注",
          "providerNotes": "提供方备注",
          "submittedAt": "提交时间",
          "providerReviewedAt": "提供方审核时间",
          "institutionReviewedAt": "机构审核时间",
          "approvedAt": "批准时间"
        }
      ],
      "page": {
        "size": 10,
        "number": 0,
        "totalElements": 1,
        "totalPages": 1
      }
    },
    "deniedApplications": {
      "content": [],
      "page": {
        "size": 10,
        "number": 0,
        "totalElements": 0,
        "totalPages": 0
      }
    }
  },
  "timestamp": "时间戳"
}
```