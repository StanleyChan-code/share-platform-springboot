# 数据集管理API文档

本文档详细描述了平台数据集管理相关的API接口，包括公开数据集查询接口和管理接口。

## 1. 公开数据集查询接口

这些接口用于查询公开可见的数据集信息，适用于匿名用户和已登录用户。

### 1.1 获取所有公开数据集列表（分页）

**接口地址**: `GET /api/datasets`

**接口描述**: 获取所有公开数据集列表，支持分页和排序

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | integer | 否 | 0 | 页码 |
| size | integer | 否 | 10 | 每页大小 |
| sortBy | string | 否 | updatedAt | 排序字段 |
| sortDir | string | 否 | desc | 排序方向(asc/desc) |

**权限要求**:
- 匿名用户：只能看到已批准且已发布的数据集
- 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集

**响应示例**:
```json
{
  "success": true,
  "message": "获取公开数据集列表成功",
  "data": {
    "content": [
      {
        "id": "uuid",
        "parentDatasetId": "uuid",
        "titleCn": "数据集标题",
        "description": "数据集描述",
        "type": "COHORT",
        "provider": {
          "id": "uuid",
          "username": "用户名",
          "realName": "真实姓名",
          "title": "职称"
        },
        "startDate": "2020-01-01T00:00:00Z",
        "endDate": "2022-12-31T00:00:00Z",
        "datasetLeader": "数据集负责人",
        "principalInvestigator": "首席研究员",
        "dataCollectionUnit": "数据收集单位",
        "recordCount": 1000,
        "variableCount": 50,
        "keywords": ["关键词1", "关键词2"],
        "subjectArea": {
          "id": "uuid",
          "name": "学科名称",
          "nameEn": "Subject Name",
          "description": "学科描述"
        },
        "category": "学科领域文本",
        "samplingMethod": "抽样方法",
        "contactPerson": "联系人",
        "contactInfo": "联系方式",
        "demographicFields": "人口统计学字段信息(JSON)",
        "outcomeFields": "结果字段信息(JSON)",
        "institutionId": "uuid",
        "approved": false,
        "published": true,
        "searchCount": 0,
        "shareAllData": true,
        "versionNumber": "1.0",
        "firstPublishedDate": "2022-01-01T00:00:00Z",
        "currentVersionDate": "2022-01-01T00:00:00Z",
        "createdAt": "2022-01-01T00:00:00Z",
        "updatedAt": "2022-01-01T00:00:00Z",
        "applicationInstitutionIds": ["uuid1", "uuid2"],
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
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 10,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 1,
    "first": true,
    "empty": false
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 1.2 获取时间轴式公开数据集列表

**接口地址**: `GET /api/datasets/timeline`

**接口描述**: 获取时间轴形式的公开数据集列表，按照开始时间排序

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | integer | 否 | 0 | 页码 |
| size | integer | 否 | 10 | 每页大小 |

**权限要求**:
- 匿名用户：只能看到已批准且已发布的数据集
- 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集

**响应示例**:
```json
{
  "success": true,
  "message": "获取时间轴式公开数据集列表成功",
  "data": {
    "content": [
      {
        "id": "uuid",
        "parentDatasetId": "uuid",
        "titleCn": "数据集标题",
        "description": "数据集描述",
        "type": "COHORT",
        "provider": {
          "id": "uuid",
          "username": "用户名",
          "realName": "真实姓名",
          "title": "职称"
        },
        "startDate": "2020-01-01T00:00:00Z",
        "endDate": "2022-12-31T00:00:00Z",
        "datasetLeader": "数据集负责人",
        "principalInvestigator": "首席研究员",
        "dataCollectionUnit": "数据收集单位",
        "recordCount": 1000,
        "variableCount": 50,
        "keywords": ["关键词1", "关键词2"],
        "subjectArea": {
          "id": "uuid",
          "name": "学科名称",
          "nameEn": "Subject Name",
          "description": "学科描述"
        },
        "category": "学科领域文本",
        "samplingMethod": "抽样方法",
        "contactPerson": "联系人",
        "contactInfo": "联系方式",
        "demographicFields": "人口统计学字段信息(JSON)",
        "outcomeFields": "结果字段信息(JSON)",
        "institutionId": "uuid",
        "approved": false,
        "published": true,
        "searchCount": 0,
        "shareAllData": true,
        "versionNumber": "1.0",
        "firstPublishedDate": "2022-01-01T00:00:00Z",
        "currentVersionDate": "2022-01-01T00:00:00Z",
        "createdAt": "2022-01-01T00:00:00Z",
        "updatedAt": "2022-01-01T00:00:00Z",
        "applicationInstitutionIds": ["uuid1", "uuid2"],
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
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 10,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 1,
    "first": true,
    "empty": false
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 1.3 根据ID获取特定公开数据集

**接口地址**: `GET /api/datasets/{id}`

**接口描述**: 根据ID获取特定公开数据集的详细信息

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**权限要求**:
- 匿名用户：只能访问已批准且已发布的数据集
- 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集

**响应示例**:
```json
{
  "success": true,
  "message": "获取公开数据集成功",
  "data": {
    "id": "uuid",
    "parentDatasetId": "uuid",
    "titleCn": "数据集标题",
    "description": "数据集描述",
    "type": "COHORT",
    "provider": {
      "id": "uuid",
      "username": "用户名",
      "realName": "真实姓名",
      "title": "职称"
    },
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "首席研究员",
    "dataCollectionUnit": "数据收集单位",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectArea": {
      "id": "uuid",
      "name": "学科名称",
      "nameEn": "Subject Name",
      "description": "学科描述"
    },
    "category": "学科领域文本",
    "samplingMethod": "抽样方法",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": "人口统计学字段信息(JSON)",
    "outcomeFields": "结果字段信息(JSON)",
    "institutionId": "uuid",
    "approved": false,
    "published": true,
    "searchCount": 0,
    "shareAllData": true,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["uuid1", "uuid2"],
    "followUpDatasets": [],
    "versions": []
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 1.4 获取特定数据集的时间轴视图

**接口地址**: `GET /api/datasets/{id}/timeline`

**接口描述**: 获取特定数据集的时间轴视图（包含该数据集及其子数据集）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**权限要求**:
- 匿名用户：只能访问已批准且已发布的数据集
- 已登录用户：能看到已批准且已发布的数据集 + 已批准但未公开的用户所属机构能够申请的数据集

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集时间轴视图成功",
  "data": {
    "id": "uuid",
    "parentDatasetId": "uuid",
    "titleCn": "数据集标题",
    "description": "数据集描述",
    "type": "COHORT",
    "provider": {
      "id": "uuid",
      "username": "用户名",
      "realName": "真实姓名",
      "title": "职称"
    },
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "首席研究员",
    "dataCollectionUnit": "数据收集单位",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectArea": {
      "id": "uuid",
      "name": "学科名称",
      "nameEn": "Subject Name",
      "description": "学科描述"
    },
    "category": "学科领域文本",
    "samplingMethod": "抽样方法",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": "人口统计学字段信息(JSON)",
    "outcomeFields": "结果字段信息(JSON)",
    "institutionId": "uuid",
    "approved": false,
    "published": true,
    "searchCount": 0,
    "shareAllData": true,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["uuid1", "uuid2"],
    "followUpDatasets": [],
    "versions": []
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 1.5 根据数据集ID获取所有版本信息

**接口地址**: `GET /api/datasets/{id}/versions`

**接口描述**: 根据数据集ID获取其所有版本信息

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**权限要求**:
- 匿名用户：只能访问已批准且已发布的数据集版本
- 已登录用户：能看到已批准且已发布的数据集版本 + 已批准但未公开的用户所属机构能够申请的数据集版本

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集版本信息成功",
  "data": [
    {
      "id": "uuid",
      "datasetId": "uuid",
      "versionNumber": "1.0",
      "createdAt": "2022-01-01T00:00:00Z",
      "publishedDate": "2022-01-01T00:00:00Z",
      "description": "版本描述",
      "fileRecordId": "uuid",
      "dataDictRecordId": "uuid",
      "termsAgreementRecordId": "uuid",
      "approved": true,
      "rejectReason": null,
      "approvedAt": "2022-01-01T00:00:00Z",
      "supervisor": {
        "id": "uuid",
        "username": "审核员用户名",
        "realName": "审核员姓名",
        "title": "审核员职称"
      }
    }
  ],
  "timestamp": "2022-01-01T00:00:00Z"
}
```

## 2. 数据集管理接口

这些接口用于管理系统中的数据集，仅限具有相应权限的用户访问。

### 2.1 获取所有管理的数据集列表

**接口地址**: `GET /api/manage/datasets`

**接口描述**: 获取所有管理的数据集列表

**权限要求**:
- PLATFORM_ADMIN（平台管理员）：可以看到所有数据集
- INSTITUTION_SUPERVISOR（机构管理员）：可以看到本机构所有数据集
- DATASET_UPLOADER（数据集上传员）：只能看到自己上传的数据集

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集列表成功",
  "data": [
    {
      "id": "uuid",
      "parentDatasetId": "uuid",
      "titleCn": "数据集标题",
      "description": "数据集描述",
      "type": "COHORT",
      "provider": {
        "id": "uuid",
        "username": "用户名",
        "realName": "真实姓名",
        "title": "职称"
      },
      "startDate": "2020-01-01T00:00:00Z",
      "endDate": "2022-12-31T00:00:00Z",
      "datasetLeader": "数据集负责人",
      "principalInvestigator": "首席研究员",
      "dataCollectionUnit": "数据收集单位",
      "recordCount": 1000,
      "variableCount": 50,
      "keywords": ["关键词1", "关键词2"],
      "subjectArea": {
        "id": "uuid",
        "name": "学科名称",
        "nameEn": "Subject Name",
        "description": "学科描述"
      },
      "category": "学科领域文本",
      "samplingMethod": "抽样方法",
      "contactPerson": "联系人",
      "contactInfo": "联系方式",
      "demographicFields": "人口统计学字段信息(JSON)",
      "outcomeFields": "结果字段信息(JSON)",
      "institutionId": "uuid",
      "approved": false,
      "published": true,
      "searchCount": 0,
      "shareAllData": true,
      "versionNumber": "1.0",
      "firstPublishedDate": "2022-01-01T00:00:00Z",
      "currentVersionDate": "2022-01-01T00:00:00Z",
      "createdAt": "2022-01-01T00:00:00Z",
      "updatedAt": "2022-01-01T00:00:00Z",
      "applicationInstitutionIds": ["uuid1", "uuid2"],
      "followUpDatasets": [],
      "versions": [
        {
          "id": "uuid",
          "datasetId": "uuid",
          "versionNumber": "1.0",
          "createdAt": "2022-01-01T00:00:00Z",
          "publishedDate": "2022-01-01T00:00:00Z",
          "description": "版本描述",
          "fileRecordId": "uuid",
          "dataDictRecordId": "uuid",
          "termsAgreementRecordId": "uuid",
          "approved": true,
          "rejectReason": null,
          "approvedAt": "2022-01-01T00:00:00Z",
          "supervisor": {
            "id": "uuid",
            "username": "审核员用户名",
            "realName": "审核员姓名",
            "title": "审核员职称"
          }
        }
      ]
    }
  ],
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 2.2 根据ID获取特定管理的数据集

**接口地址**: `GET /api/manage/datasets/{id}`

**接口描述**: 根据ID获取特定管理的数据集

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**权限要求**:
- PLATFORM_ADMIN（平台管理员）：可以访问所有数据集
- INSTITUTION_SUPERVISOR（机构管理员）：只能访问本机构的数据集
- DATASET_UPLOADER（数据集上传员）：只能访问自己上传的数据集

**响应示例**:
```json
{
  "success": true,
  "message": "获取数据集成功",
  "data": {
    "id": "uuid",
    "parentDatasetId": "uuid",
    "titleCn": "数据集标题",
    "description": "数据集描述",
    "type": "COHORT",
    "provider": {
      "id": "uuid",
      "username": "用户名",
      "realName": "真实姓名",
      "title": "职称"
    },
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "首席研究员",
    "dataCollectionUnit": "数据收集单位",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectArea": {
      "id": "uuid",
      "name": "学科名称",
      "nameEn": "Subject Name",
      "description": "学科描述"
    },
    "category": "学科领域文本",
    "samplingMethod": "抽样方法",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": "人口统计学字段信息(JSON)",
    "outcomeFields": "结果字段信息(JSON)",
    "institutionId": "uuid",
    "approved": false,
    "published": true,
    "searchCount": 0,
    "shareAllData": true,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["uuid1", "uuid2"],
    "followUpDatasets": [],
    "versions": [
      {
        "id": "uuid",
        "datasetId": "uuid",
        "versionNumber": "1.0",
        "createdAt": "2022-01-01T00:00:00Z",
        "publishedDate": "2022-01-01T00:00:00Z",
        "description": "版本描述",
        "fileRecordId": "uuid",
        "dataDictRecordId": "uuid",
        "termsAgreementRecordId": "uuid",
        "approved": true,
        "rejectReason": null,
        "approvedAt": "2022-01-01T00:00:00Z",
        "supervisor": {
          "id": "uuid",
          "username": "审核员用户名",
          "realName": "审核员姓名",
          "title": "审核员职称"
        }
      }
    ]
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 2.3 创建新的数据集

**接口地址**: `POST /api/manage/datasets`

**接口描述**: 创建一个新的数据集

**请求体参数**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| titleCn | string | 是 | 数据集标题 |
| description | string | 是 | 数据集描述 |
| type | DatasetType | 否 | 研究类型 |
| datasetLeader | string | 否 | 数据集负责人 |
| principalInvestigator | string | 否 | 首席研究员 |
| dataCollectionUnit | string | 否 | 数据收集单位 |
| startDate | Instant | 否 | 研究开始日期 |
| endDate | Instant | 否 | 研究结束日期 |
| recordCount | integer | 否 | 记录数量 |
| variableCount | integer | 否 | 变量数量 |
| keywords | string[] | 否 | 关键词数组 |
| subjectAreaId | UUID | 否 | 学科领域ID |
| category | string | 否 | 学科领域文本 |
| samplingMethod | string | 否 | 抽样方法描述 |
| published | boolean | 否 | 对外发布状态，默认false |
| shareAllData | boolean | 否 | 是否共享所有数据，默认false |
| contactPerson | string | 否 | 联系人姓名 |
| contactInfo | string | 否 | 联系方式 |
| demographicFields | string | 否 | 人口统计学字段信息(JSON) |
| outcomeFields | string | 否 | 结果字段信息(JSON) |
| parentDatasetId | UUID | 否 | 父数据集ID |
| institutionId | UUID | 否 | 所属机构ID |
| applicationInstitutionIds | UUID[] | 否 | 申请机构ID列表 |
| versionNumber | string | 否 | 版本号 |
| fileRecordId | UUID | 否 | 数据文件记录ID |
| dataDictRecordId | UUID | 否 | 数据字典文件记录ID |
| termsAgreementRecordId | UUID | 否 | 条款协议文件记录ID |
| versionDescription | string | 否 | 数据集版本描述 |

**权限要求**:
- PLATFORM_ADMIN（平台管理员）
- INSTITUTION_SUPERVISOR（机构管理员）
- DATASET_UPLOADER（数据集上传员）

**请求示例**:
```json
{
  "titleCn": "新数据集",
  "description": "这是一个新数据集的描述",
  "type": "COHORT",
  "datasetLeader": "张三",
  "principalInvestigator": "李四",
  "dataCollectionUnit": "某研究所",
  "startDate": "2020-01-01T00:00:00Z",
  "endDate": "2022-12-31T00:00:00Z",
  "recordCount": 1000,
  "variableCount": 50,
  "keywords": ["关键词1", "关键词2"],
  "subjectAreaId": "uuid",
  "category": "医学研究",
  "samplingMethod": "随机抽样",
  "published": true,
  "shareAllData": true,
  "contactPerson": "王五",
  "contactInfo": "wangwu@example.com",
  "demographicFields": "{\"age\": \"年龄\", \"gender\": \"性别\"}",
  "outcomeFields": "{\"result\": \"结果\"}",
  "parentDatasetId": "uuid",
  "institutionId": "uuid",
  "applicationInstitutionIds": ["uuid1", "uuid2"],
  "versionNumber": "1.0",
  "fileRecordId": "uuid",
  "dataDictRecordId": "uuid",
  "termsAgreementRecordId": "uuid",
  "versionDescription": "初始版本"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "创建数据集成功",
  "data": {
    "id": "uuid",
    "parentDatasetId": "uuid",
    "titleCn": "新数据集",
    "description": "这是一个新数据集的描述",
    "type": "COHORT",
    "provider": {
      "id": "uuid",
      "username": "用户名",
      "realName": "真实姓名",
      "title": "职称"
    },
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "datasetLeader": "张三",
    "principalInvestigator": "李四",
    "dataCollectionUnit": "某研究所",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectArea": {
      "id": "uuid",
      "name": "学科名称",
      "nameEn": "Subject Name",
      "description": "学科描述"
    },
    "category": "医学研究",
    "samplingMethod": "随机抽样",
    "contactPerson": "王五",
    "contactInfo": "wangwu@example.com",
    "demographicFields": "{\"age\": \"年龄\", \"gender\": \"性别\"}",
    "outcomeFields": "{\"result\": \"结果\"}",
    "institutionId": "uuid",
    "approved": false,
    "published": true,
    "searchCount": 0,
    "shareAllData": true,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["uuid1", "uuid2"],
    "followUpDatasets": [],
    "versions": [
      {
        "id": "uuid",
        "datasetId": "uuid",
        "versionNumber": "1.0",
        "createdAt": "2022-01-01T00:00:00Z",
        "publishedDate": null,
        "description": "初始版本",
        "fileRecordId": "uuid",
        "dataDictRecordId": "uuid",
        "termsAgreementRecordId": "uuid",
        "approved": null,
        "rejectReason": null,
        "approvedAt": null,
        "supervisor": null
      }
    ]
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 2.4 更新现有数据集基本信息

**接口地址**: `PUT /api/manage/datasets/{id}/basic-info`

**接口描述**: 更新现有数据集的基本信息

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**请求体参数**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| description | string | 否 | 数据集描述 |
| keywords | string[] | 否 | 关键词数组 |
| published | boolean | 否 | 对外发布状态 |
| shareAllData | boolean | 否 | 是否共享所有数据 |
| contactPerson | string | 否 | 联系人姓名 |
| contactInfo | string | 否 | 联系方式 |
| demographicFields | string | 否 | 人口统计学字段信息(JSON) |
| outcomeFields | string | 否 | 结果字段信息(JSON) |
| samplingMethod | string | 否 | 抽样方法描述 |
| applicationInstitutionIds | UUID[] | 否 | 申请机构ID列表 |

**权限要求**:
- PLATFORM_ADMIN（平台管理员）：可更新任意数据集
- INSTITUTION_SUPERVISOR（机构管理员）：只能更新自己机构的数据集
- DATASET_UPLOADER（数据集上传员）：只能更新自己的数据集

**请求示例**:
```json
{
  "description": "更新后的数据集描述",
  "keywords": ["新关键词1", "新关键词2"],
  "published": true,
  "shareAllData": true,
  "contactPerson": "赵六",
  "contactInfo": "zhaoliu@example.com",
  "demographicFields": "{\"age\": \"年龄\", \"gender\": \"性别\", \"education\": \"教育程度\"}",
  "outcomeFields": "{\"result\": \"结果\", \"analysis\": \"分析\"}",
  "samplingMethod": "分层抽样",
  "applicationInstitutionIds": ["uuid3", "uuid4"]
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "更新数据集基本信息成功",
  "data": {
    "id": "uuid",
    "parentDatasetId": "uuid",
    "titleCn": "数据集标题",
    "description": "更新后的数据集描述",
    "type": "COHORT",
    "provider": {
      "id": "uuid",
      "username": "用户名",
      "realName": "真实姓名",
      "title": "职称"
    },
    "startDate": "2020-01-01T00:00:00Z",
    "endDate": "2022-12-31T00:00:00Z",
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "首席研究员",
    "dataCollectionUnit": "数据收集单位",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["新关键词1", "新关键词2"],
    "subjectArea": {
      "id": "uuid",
      "name": "学科名称",
      "nameEn": "Subject Name",
      "description": "学科描述"
    },
    "category": "学科领域文本",
    "samplingMethod": "分层抽样",
    "contactPerson": "赵六",
    "contactInfo": "zhaoliu@example.com",
    "demographicFields": "{\"age\": \"年龄\", \"gender\": \"性别\", \"education\": \"教育程度\"}",
    "outcomeFields": "{\"result\": \"结果\", \"analysis\": \"分析\"}",
    "institutionId": "uuid",
    "approved": false,
    "published": true,
    "searchCount": 0,
    "shareAllData": true,
    "versionNumber": "1.0",
    "firstPublishedDate": "2022-01-01T00:00:00Z",
    "currentVersionDate": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "applicationInstitutionIds": ["uuid3", "uuid4"],
    "followUpDatasets": [],
    "versions": [
      {
        "id": "uuid",
        "datasetId": "uuid",
        "versionNumber": "1.0",
        "createdAt": "2022-01-01T00:00:00Z",
        "publishedDate": "2022-01-01T00:00:00Z",
        "description": "版本描述",
        "fileRecordId": "uuid",
        "dataDictRecordId": "uuid",
        "termsAgreementRecordId": "uuid",
        "approved": true,
        "rejectReason": null,
        "approvedAt": "2022-01-01T00:00:00Z",
        "supervisor": {
          "id": "uuid",
          "username": "审核员用户名",
          "realName": "审核员姓名",
          "title": "审核员职称"
        }
      }
    ]
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 2.5 为现有数据集添加新版本

**接口地址**: `POST /api/manage/datasets/{id}/versions`

**接口描述**: 为现有数据集添加新版本

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |

**请求体参数**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| versionNumber | string | 否 | 版本号 |
| description | string | 否 | 版本描述 |
| fileRecordId | UUID | 否 | 数据文件记录ID |
| dataDictRecordId | UUID | 否 | 数据字典文件记录ID |
| termsAgreementRecordId | UUID | 否 | 条款协议文件记录ID |

**权限要求**:
- PLATFORM_ADMIN（平台管理员）：可为任意数据集添加版本
- INSTITUTION_SUPERVISOR（机构管理员）：只能为自己机构的数据集添加版本
- DATASET_UPLOADER（数据集上传员）：只能为自己的数据集添加版本

**请求示例**:
```json
{
  "versionNumber": "2.0",
  "description": "第二版数据集",
  "fileRecordId": "uuid",
  "dataDictRecordId": "uuid",
  "termsAgreementRecordId": "uuid"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "添加数据集新版本成功",
  "data": {
    "id": "uuid",
    "datasetId": "uuid",
    "versionNumber": "2.0",
    "createdAt": "2022-01-01T00:00:00Z",
    "publishedDate": null,
    "description": "第二版数据集",
    "fileRecordId": "uuid",
    "dataDictRecordId": "uuid",
    "termsAgreementRecordId": "uuid",
    "approved": null,
    "rejectReason": null,
    "approvedAt": null,
    "supervisor": null
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

### 2.6 修改数据集审核状态

**接口地址**: `PUT /api/manage/datasets/{id}/{datasetVersionId}/approval`

**接口描述**: 修改数据集版本的审核状态（通过、驳回）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | UUID | 是 | 数据集ID |
| datasetVersionId | UUID | 是 | 数据集版本ID |

**请求体参数**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| approved | boolean | 否 | 审核状态(true:通过, false:拒绝, null:重置) |
| rejectionReason | string | 否 | 拒绝理由 |

**权限要求**:
- PLATFORM_ADMIN（平台管理员）：可修改任意数据集的审核状态
- INSTITUTION_SUPERVISOR（机构管理员）：可修改本机构数据集的审核状态
- DATASET_APPROVER（数据集审核员）：可修改任意数据集的审核状态

**请求示例**:
```json
{
  "approved": true,
  "rejectionReason": null
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "数据集审核通过",
  "data": {
    "id": "uuid",
    "datasetId": "uuid",
    "versionNumber": "1.0",
    "createdAt": "2022-01-01T00:00:00Z",
    "publishedDate": "2022-01-01T00:00:00Z",
    "description": "版本描述",
    "fileRecordId": "uuid",
    "dataDictRecordId": "uuid",
    "termsAgreementRecordId": "uuid",
    "approved": true,
    "rejectReason": null,
    "approvedAt": "2022-01-01T00:00:00Z",
    "supervisor": {
      "id": "uuid",
      "username": "审核员用户名",
      "realName": "审核员姓名",
      "title": "审核员职称"
    }
  },
  "timestamp": "2022-01-01T00:00:00Z"
}
```

## 3. 枚举类型说明

### 3.1 DatasetType（数据集类型）

| 值 | 说明 |
|----|------|
| COHORT | 队列研究 |
| CASE_CONTROL | 病例对照研究 |
| CROSS_SECTIONAL | 横断面研究 |
| RCT | 随机对照试验 |
| REGISTRY | 登记研究 |
| BIOBANK | 生物样本库 |
| OMICS | 组学研究 |
| WEARABLE | 可穿戴设备研究 |

## 4. 错误响应格式

当API调用发生错误时，会返回以下格式的响应：

```json
{
  "success": false,
  "message": "错误信息",
  "data": null,
  "timestamp": "2022-01-01T00:00:00Z"
}
```