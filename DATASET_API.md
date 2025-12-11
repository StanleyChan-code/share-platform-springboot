# 数据集管理接口文档

## 1. 概述

本文档详细描述了数据集管理相关的API接口，包括数据集的创建、查询、更新和审核等操作。

## 2. 权限说明

| 角色 | 权限 |
|------|------|
| PLATFORM_ADMIN | 平台管理员可以管理所有数据集 |
| INSTITUTION_SUPERVISOR | 机构管理员可以管理本机构的数据集 |
| DATASET_UPLOADER | 数据集上传员可以创建和管理自己的数据集 |
| DATASET_APPROVER | 数据集审核员可以审核本机构的数据集 |

## 3. 数据集公共查询接口

### 3.1 获取所有公开数据集列表

**接口地址**: `GET /api/datasets`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页大小 |
| sortBy | string | 否 | updatedAt | 排序字段 |
| sortDir | string | 否 | desc | 排序方向(asc/desc) |

**响应示例**:
```json
{
  "success": true,
  "message": "获取公开数据集列表成功",
  "data": {
    "content": [
      {
        "id": "uuid",
        "titleCn": "数据集中文标题",
        "description": "数据集描述",
        "type": "COHORT",
        "datasetLeader": "数据集负责人",
        "principalInvestigator": "主要研究者",
        "institutionId": "机构ID",
        "startDate": "2020-01-01T00:00:00Z",
        "endDate": "2022-12-31T00:00:00Z",
        "recordCount": 1000,
        "variableCount": 50,
        "keywords": ["关键词1", "关键词2"],
        "subjectAreaId": "学科领域ID",
        "category": "数据集分类",
        "samplingMethod": "抽样方法",
        "contactPerson": "联系人",
        "contactInfo": "联系方式",
        "demographicFields": ["人口统计字段1", "人口统计字段2"],
        "outcomeFields": ["结果字段1", "结果字段2"],
        "approved": true,
        "published": true,
        "searchCount": 0,
        "shareAllData": false,
        "versionNumber": "1.0",
        "firstPublishedDate": "2022-01-01T00:00:00Z",
        "currentVersionDate": "2022-01-01T00:00:00Z",
        "createdAt": "2022-01-01T00:00:00Z",
        "updatedAt": "2022-01-01T00:00:00Z",
        "applicationInstitutionIds": ["申请机构ID1"],
        "followUpDatasets": [],
        "versions": []
      }
    ],
    "pageable": {
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "pageNumber": 0,
      "pageSize": 10,
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "numberOfElements": 1,
    "first": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "empty": false
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 3.2 获取时间轴形式的公开数据集列表

**接口地址**: `GET /api/datasets/timeline`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 10 | 每页大小 |

**响应示例**:
```json
{
  "success": true,
  "message": "获取时间轴式公开数据集列表成功",
  "data": {
    "content": [
      {
      }
    ]
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 3.3 根据ID获取特定公开数据集

**接口地址**: `GET /api/datasets/{id}`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**响应示例**:
```json
{
  "success": true,
  "message": "获取公开数据集成功",
  "data": {
    "id": "uuid",
    "titleCn": "数据集中文标题",
    "description": "数据集描述",
    "type": "COHORT",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "主要研究者",
    "institutionId": "机构ID",
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectAreaId": "学科领域ID",
    "category": "数据集分类",
    "samplingMethod": "抽样方法",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": ["人口统计字段1", "人口统计字段2"],
    "outcomeFields": ["结果字段1", "结果字段2"],
    "approved": true,
    "published": true,
    "searchCount": 0,
    "shareAllData": false,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["申请机构ID1"],
    "followUpDatasets": [],
    "versions": []
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 3.4 获取特定数据集的时间轴视图

**接口地址**: `GET /api/datasets/{id}/timeline`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集时间轴视图成功",
  "data": {
    "id": "uuid",
    "titleCn": "数据集中文标题",
    "description": "数据集描述",
    "type": "COHORT",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "主要研究者",
    "institutionId": "机构ID",
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectAreaId": "学科领域ID",
    "category": "数据集分类",
    "samplingMethod": "抽样方法",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": ["人口统计字段1", "人口统计字段2"],
    "outcomeFields": ["结果字段1", "结果字段2"],
    "approved": true,
    "published": true,
    "searchCount": 0,
    "shareAllData": false,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["申请机构ID1"],
    "followUpDatasets": [],
    "versions": []
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 3.5 根据数据集ID获取所有版本信息

**接口地址**: `GET /api/datasets/{id}/versions`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集版本信息成功",
  "data": [
    {
      "id": "uuid",
      "datasetId": "数据集ID",
      "versionNumber": "1.0",
      "description": "版本描述",
      "fileRecordId": "数据文件记录ID",
      "dataDictRecordId": "数据字典文件记录ID",
      "termsAgreementRecordId": "条款协议文件记录ID",
      "dataSharingRecordId": "数据分享文件记录ID",
      "approved": true,
      "approvedAt": "2022-01-01T00:00:00Z",
      "rejectReason": "拒绝原因",
      "supervisor": {
        "id": "审核员ID",
        "realName": "审核员姓名"
      },
      "createdAt": "2022-01-01T00:00:00Z",
      "updatedAt": "2022-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 4. 数据集文件下载接口

### 4.1 下载数据字典文件

**接口地址**: `GET /api/datasets/{datasetId}/versions/{versionId}/data-dictionary`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| datasetId | UUID | 是 | 数据集ID |
| versionId | UUID | 是 | 版本ID |

**响应**: 文件流

### 4.2 下载使用协议文件

**接口地址**: `GET /api/datasets/{datasetId}/versions/{versionId}/terms-agreement`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| datasetId | UUID | 是 | 数据集ID |
| versionId | UUID | 是 | 版本ID |

**响应**: 文件流

### 4.3 下载数据分享文件

**接口地址**: `GET /api/datasets/{datasetId}/versions/{versionId}/data-sharing`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| datasetId | UUID | 是 | 数据集ID |
| versionId | UUID | 是 | 版本ID |

**响应**: 文件流

## 5. 数据集管理接口

### 5.1 获取所有管理的数据集列表

**接口地址**: `GET /api/manage/datasets`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_UPLOADER

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集列表成功",
  "data": [
    {
      "id": "uuid",
      "titleCn": "数据集中文标题",
      "description": "数据集描述",
      "type": "COHORT",
      "datasetLeader": "数据集负责人",
      "principalInvestigator": "主要研究者",
      "institutionId": "机构ID",
      "startDate": "2020-01-01T00:00:00Z",
      "endDate": "2022-12-31T00:00:00Z",
      "recordCount": 1000,
      "variableCount": 50,
      "keywords": ["关键词1", "关键词2"],
      "subjectAreaId": "学科领域ID",
      "category": "数据集分类",
      "samplingMethod": "抽样方法",
      "contactPerson": "联系人",
      "contactInfo": "联系方式",
      "demographicFields": ["人口统计字段1", "人口统计字段2"],
      "outcomeFields": ["结果字段1", "结果字段2"],
      "approved": true,
      "published": true,
      "searchCount": 0,
      "shareAllData": false,
      "versionNumber": "1.0",
      "firstPublishedDate": "2022-01-01T00:00:00Z",
      "currentVersionDate": "2022-01-01T00:00:00Z",
      "createdAt": "2022-01-01T00:00:00Z",
      "updatedAt": "2022-01-01T00:00:00Z",
      "applicationInstitutionIds": ["申请机构ID1"],
      "followUpDatasets": [],
      "versions": []
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.2 根据ID获取特定管理的数据集

**接口地址**: `GET /api/manage/datasets/{id}`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_UPLOADER

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集成功",
  "data": {
    "id": "uuid",
    "titleCn": "数据集中文标题",
    "description": "数据集描述",
    "type": "COHORT",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "主要研究者",
    "institutionId": "机构ID",
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectAreaId": "学科领域ID",
    "category": "数据集分类",
    "samplingMethod": "抽样方法",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": ["人口统计字段1", "人口统计字段2"],
    "outcomeFields": ["结果字段1", "结果字段2"],
    "approved": true,
    "published": true,
    "searchCount": 0,
    "shareAllData": false,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["申请机构ID1"],
    "followUpDatasets": [],
    "versions": []
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.3 创建新的数据集

**接口地址**: `POST /api/manage/datasets`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_UPLOADER

**请求体**:
```json
{
  "titleCn": "数据集中文标题",
  "description": "数据集描述",
  "type": "COHORT",
  "datasetLeader": "数据集负责人",
  "principalInvestigator": "主要研究者",
  "dataCollectionUnit": "数据收集单位",
  "startDate": "2020-01-01",
  "endDate": "2022-12-31",
  "recordCount": 1000,
  "variableCount": 50,
  "keywords": ["关键词1", "关键词2"],
  "subjectAreaId": "学科领域ID",
  "category": "数据集分类",
  "samplingMethod": "抽样方法",
  "published": true,
  "shareAllData": false,
  "contactPerson": "联系人",
  "contactInfo": "联系方式",
  "demographicFields": ["人口统计字段1", "人口统计字段2"],
  "outcomeFields": ["结果字段1", "结果字段2"],
  "parentDatasetId": "父数据集ID",
  "applicationInstitutionIds": ["申请机构ID1"],
  "versionNumber": "1.0",
  "fileRecordId": "数据文件记录ID",
  "dataDictRecordId": "数据字典文件记录ID",
  "termsAgreementRecordId": "条款协议文件记录ID",
  "dataSharingRecordId": "数据分享文件记录ID",
  "versionDescription": "版本描述"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "创建数据集成功",
  "data": {
    "id": "uuid",
    "titleCn": "数据集中文标题",
    "description": "数据集描述",
    "type": "COHORT",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "主要研究者",
    "institutionId": "机构ID",
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectAreaId": "学科领域ID",
    "category": "数据集分类",
    "samplingMethod": "抽样方法",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": ["人口统计字段1", "人口统计字段2"],
    "outcomeFields": ["结果字段1", "结果字段2"],
    "approved": true,
    "published": true,
    "searchCount": 0,
    "shareAllData": false,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["申请机构ID1"],
    "followUpDatasets": [],
    "versions": []
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.4 更新数据集基本信息

**接口地址**: `PUT /api/manage/datasets/{id}/basic-info`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_UPLOADER

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**请求体**: 同创建数据集的请求体，仅包含需要更新的基础信息字段

**响应示例**:
```json
{
  "success": true,
  "message": "更新数据集基本信息成功",
  "data": {
    "id": "uuid",
    "titleCn": "数据集中文标题",
    "description": "数据集描述",
    "type": "COHORT",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "主要研究者",
    "institutionId": "机构ID",
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectAreaId": "学科领域ID",
    "category": "数据集分类",
    "samplingMethod": "抽样方法",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": ["人口统计字段1", "人口统计字段2"],
    "outcomeFields": ["结果字段1", "结果字段2"],
    "approved": true,
    "published": true,
    "searchCount": 0,
    "shareAllData": false,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["申请机构ID1"],
    "followUpDatasets": [],
    "versions": []
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.5 为现有数据集添加新版本

**接口地址**: `POST /api/manage/datasets/{id}/versions`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_UPLOADER

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**请求体**:
```json
{
  "versionNumber": "2.0",
  "description": "版本描述",
  "fileRecordId": "数据文件记录ID",
  "dataDictRecordId": "数据字典文件记录ID",
  "termsAgreementRecordId": "条款协议文件记录ID",
  "dataSharingRecordId": "数据分享文件记录ID",
  "versionDescription": "版本描述"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "添加数据集新版本成功",
  "data": {
    "id": "uuid",
    "datasetId": "数据集ID",
    "versionNumber": "2.0",
    "description": "版本描述",
    "fileRecordId": "数据文件记录ID",
    "dataDictRecordId": "数据字典文件记录ID",
    "termsAgreementRecordId": "条款协议文件记录ID",
    "dataSharingRecordId": "数据分享文件记录ID",
    "approved": null,
    "approvedAt": null,
    "rejectReason": null,
    "supervisor": null,
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 5.6 修改数据集审核状态

**接口地址**: `PUT /api/manage/datasets/{id}/{datasetVersionId}/approval`

**权限要求**: PLATFORM_ADMIN, INSTITUTION_SUPERVISOR, DATASET_APPROVER

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |
| datasetVersionId | UUID | 是 | 数据集版本ID |

**请求体**:
```json
{
  "approved": true,
  "rejectionReason": "拒绝原因（仅在approved为false时需要）"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "数据集审核通过",
  "data": {
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```