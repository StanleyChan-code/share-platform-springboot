# 数据集管理接口

数据集管理分为两类接口：
- 公共接口（部分需要认证）：`/api/datasets`
- 管理接口（需认证）：`/api/manage/datasets`

## 1. 公共数据集接口

### 1.1 获取所有公开数据集

**接口地址**: `GET /api/datasets`

**权限要求**: 匿名用户可访问公开数据集，认证用户可额外访问本机构未公开但已审核的数据集

**响应示例**:
```json
{
  "success": true,
  "message": "获取公开数据集列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "titleCn": "数据集标题",
      "description": "数据集描述",
      "type": "cohort",
      "provider": {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "username": "provider_user",
        "realName": "提供者姓名",
        "title": "职称"
      },
      "supervisor": {
        "id": "550e8400-e29b-41d4-a716-446655440002",
        "username": "supervisor_user",
        "realName": "监管者姓名",
        "title": "职称"
      },
      "startDate": "2025-01-01T00:00:00Z",
      "endDate": "2025-12-31T00:00:00Z",
      "recordCount": 1000,
      "variableCount": 50,
      "keywords": ["关键词1", "关键词2"],
      "subjectArea": {
        "id": "550e8400-e29b-41d4-a716-446655440003",
        "name": "学科名称",
        "nameEn": "Subject Name",
        "description": "学科描述"
      },
      "approved": true,
      "published": true,
      "searchCount": 10
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 1.2 根据ID获取特定公开数据集

**接口地址**: `GET /api/datasets/{id}`

**权限要求**: 匿名用户可访问公开数据集，认证用户可额外访问本机构未公开但已审核的数据集

**响应示例**:
```json
{
  "success": true,
  "message": "获取公开数据集成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "titleCn": "数据集标题",
    "description": "数据集描述",
    "type": "cohort",
    "provider": {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "username": "provider_user",
      "realName": "提供者姓名",
      "title": "职称"
    },
    "supervisor": {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "username": "supervisor_user",
      "realName": "监管者姓名",
      "title": "职称"
    },
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectArea": {
      "id": "550e8400-e29b-41d4-a716-446655440003",
      "name": "学科名称",
      "nameEn": "Subject Name",
      "description": "学科描述"
    },
    "approved": true,
    "published": true,
    "searchCount": 10
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

## 2. 管理数据集接口

### 2.1 获取所有管理的数据集列表

**接口地址**: `GET /api/manage/datasets`

**权限要求**: 平台管理员和机构管理员可访问所有数据集，数据集上传员只能看到自己上传的数据集

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
      "providerId": "550e8400-e29b-41d4-a716-446655440001",
      "supervisorId": "550e8400-e29b-41d4-a716-446655440002",
      "startDate": "2025-01-01T00:00:00Z",
      "endDate": "2025-12-31T00:00:00Z",
      "recordCount": 1000,
      "variableCount": 50,
      "keywords": ["关键词1", "关键词2"],
      "subjectAreaId": "550e8400-e29b-41d4-a716-446655440003",
      "fileUrl": "https://example.com/dataset-file.csv",
      "dataDictUrl": "https://example.com/data-dict.pdf",
      "approved": true,
      "published": true,
      "searchCount": 10,
      "shareAllData": true,
      "datasetLeader": "数据集负责人",
      "principalInvestigator": "首席研究员",
      "dataCollectionUnit": "数据收集单位",
      "contactPerson": "联系人",
      "contactInfo": "联系方式",
      "demographicFields": "人口统计学字段",
      "outcomeFields": "结局字段",
      "termsAgreementUrl": "https://example.com/terms.pdf",
      "samplingMethod": "抽样方法",
      "versionNumber": "1.0",
      "firstPublishedDate": "2025-01-01T00:00:00Z",
      "currentVersionDate": "2025-01-01T00:00:00Z",
      "parentDatasetId": "550e8400-e29b-41d4-a716-446655440004",
      "institutionId": "550e8400-e29b-41d4-a716-446655440005",
      "createdAt": "2025-01-01T00:00:00Z",
      "updatedAt": "2025-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 2.2 根据ID获取特定管理的数据集

**接口地址**: `GET /api/manage/datasets/{id}`

**权限要求**: 平台管理员和机构管理员可访问所有数据集，数据集上传员只能看到自己上传的数据集

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
    "providerId": "550e8400-e29b-41d4-a716-446655440001",
    "supervisorId": "550e8400-e29b-41d4-a716-446655440002",
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectAreaId": "550e8400-e29b-41d4-a716-446655440003",
    "fileUrl": "https://example.com/dataset-file.csv",
    "dataDictUrl": "https://example.com/data-dict.pdf",
    "approved": true,
    "published": true,
    "searchCount": 10,
    "shareAllData": true,
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "首席研究员",
    "dataCollectionUnit": "数据收集单位",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": "人口统计学字段",
    "outcomeFields": "结局字段",
    "termsAgreementUrl": "https://example.com/terms.pdf",
    "samplingMethod": "抽样方法",
    "versionNumber": "1.0",
    "firstPublishedDate": "2025-01-01T00:00:00Z",
    "currentVersionDate": "2025-01-01T00:00:00Z",
    "parentDatasetId": "550e8400-e29b-41d4-a716-446655440004",
    "institutionId": "550e8400-e29b-41d4-a716-446655440005",
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-01-01T00:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 2.3 创建新的数据集

**接口地址**: `POST /api/manage/datasets`

**权限要求**: 平台管理员、机构管理员和数据集上传员可创建数据集

**请求体**:
```json
{
  "titleCn": "新数据集",
  "description": "这是一个新数据集",
  "type": "cohort",
  "category": "科研数据",
  "supervisorId": "550e8400-e29b-41d4-a716-446655440000",
  "startDate": "2025-01-01T00:00:00Z",
  "endDate": "2025-12-31T00:00:00Z",
  "recordCount": 1000,
  "variableCount": 50,
  "keywords": ["关键词1", "关键词2"],
  "subjectAreaId": "550e8400-e29b-41d4-a716-446655440001",
  "fileUrl": "https://example.com/dataset-file.csv",
  "dataDictUrl": "https://example.com/data-dict.pdf",
  "published": true,
  "shareAllData": true,
  "datasetLeader": "数据集负责人",
  "principalInvestigator": "首席研究员",
  "dataCollectionUnit": "数据收集单位",
  "contactPerson": "联系人",
  "contactInfo": "联系方式",
  "demographicFields": "人口统计学字段",
  "outcomeFields": "结局字段",
  "termsAgreementUrl": "https://example.com/terms.pdf",
  "samplingMethod": "抽样方法",
  "versionNumber": "1.0",
  "firstPublishedDate": "2025-01-01T00:00:00Z",
  "currentVersionDate": "2025-01-01T00:00:00Z",
  "parentDatasetId": "550e8400-e29b-41d4-a716-446655440002"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "创建数据集成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "titleCn": "新数据集",
    "description": "这是一个新数据集",
    "type": "cohort",
    "category": "科研数据",
    "providerId": "550e8400-e29b-41d4-a716-446655440004",
    "supervisorId": "550e8400-e29b-41d4-a716-446655440000",
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectAreaId": "550e8400-e29b-41d4-a716-446655440001",
    "fileUrl": "https://example.com/dataset-file.csv",
    "dataDictUrl": "https://example.com/data-dict.pdf",
    "approved": null,
    "published": true,
    "searchCount": 0,
    "shareAllData": true,
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "首席研究员",
    "dataCollectionUnit": "数据收集单位",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": "人口统计学字段",
    "outcomeFields": "结局字段",
    "termsAgreementUrl": "https://example.com/terms.pdf",
    "samplingMethod": "抽样方法",
    "versionNumber": "1.0",
    "firstPublishedDate": "2025-01-01T00:00:00Z",
    "currentVersionDate": "2025-01-01T00:00:00Z",
    "parentDatasetId": "550e8400-e29b-41d4-a716-446655440002",
    "institutionId": "550e8400-e29b-41d4-a716-446655440005",
    "createdAt": "2025-12-01T10:00:00Z",
    "updatedAt": "2025-12-01T10:00:00Z"
  },
  "timestamp": "2025-12-01T10:00:00Z"
}
```

### 2.4 更新现有数据集

**接口地址**: `PUT /api/manage/datasets/{id}`

**权限要求**: 平台管理员可更新任意数据集，机构管理员和数据集上传员只能更新自己机构的数据集

**请求体**:
```json
{
  "titleCn": "更新后的数据集",
  "description": "这是更新后的数据集描述",
  "type": "cohort",
  "category": "科研数据",
  "providerId": "550e8400-e29b-41d4-a716-446655440000",
  "supervisorId": "550e8400-e29b-41d4-a716-446655440001",
  "startDate": "2025-01-01T00:00:00Z",
  "endDate": "2025-12-31T00:00:00Z",
  "recordCount": 1000,
  "variableCount": 50,
  "keywords": ["关键词1", "关键词2"],
  "subjectAreaId": "550e8400-e29b-41d4-a716-446655440002",
  "fileUrl": "https://example.com/dataset-file.csv",
  "dataDictUrl": "https://example.com/data-dict.pdf",
  "published": true,
  "shareAllData": true,
  "datasetLeader": "数据集负责人",
  "principalInvestigator": "首席研究员",
  "dataCollectionUnit": "数据收集单位",
  "contactPerson": "联系人",
  "contactInfo": "联系方式",
  "demographicFields": "人口统计学字段",
  "outcomeFields": "结局字段",
  "termsAgreementUrl": "https://example.com/terms.pdf",
  "samplingMethod": "抽样方法",
  "versionNumber": "1.0",
  "firstPublishedDate": "2025-01-01T00:00:00Z",
  "currentVersionDate": "2025-01-01T00:00:00Z",
  "parentDatasetId": "550e8400-e29b-41d4-a716-446655440003"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "更新数据集成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440004",
    "titleCn": "更新后的数据集",
    "description": "这是更新后的数据集描述",
    "type": "cohort",
    "category": "科研数据",
    "providerId": "550e8400-e29b-41d4-a716-446655440000",
    "supervisorId": "550e8400-e29b-41d4-a716-446655440001",
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["关键词1", "关键词2"],
    "subjectAreaId": "550e8400-e29b-41d4-a716-446655440002",
    "fileUrl": "https://example.com/dataset-file.csv",
    "dataDictUrl": "https://example.com/data-dict.pdf",
    "approved": null,
    "published": true,
    "searchCount": 0,
    "shareAllData": true,
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "首席研究员",
    "dataCollectionUnit": "数据收集单位",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": "人口统计学字段",
    "outcomeFields": "结局字段",
    "termsAgreementUrl": "https://example.com/terms.pdf",
    "samplingMethod": "抽样方法",
    "versionNumber": "1.0",
    "firstPublishedDate": "2025-01-01T00:00:00Z",
    "currentVersionDate": "2025-01-01T00:00:00Z",
    "parentDatasetId": "550e8400-e29b-41d4-a716-446655440003",
    "institutionId": "550e8400-e29b-41d4-a716-446655440005",
    "createdAt": "2025-12-01T10:00:00Z",
    "updatedAt": "2025-12-01T11:00:00Z"
  },
  "timestamp": "2025-12-01T11:00:00Z"
}
```

### 2.5 修改数据集审核状态

**接口地址**: `PUT /api/manage/datasets/{id}/approval`

**权限要求**: 平台管理员可修改任意数据集的审核状态，机构管理员只能修改自己机构数据集的审核状态

**请求体**:
```json
{
  "approved": true
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "数据集审核通过",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "titleCn": "中国心血管病风险队列研究数据集",
    "description": "该数据集包含了中国心血管病风险队列研究的核心变量数据",
    "type": "cohort",
    "category": "心血管病学",
    "providerId": "550e8400-e29b-41d4-a716-446655440001",
    "supervisorId": "550e8400-e29b-41d4-a716-446655440002",
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-12-31T00:00:00Z",
    "recordCount": 1000,
    "variableCount": 50,
    "keywords": ["心血管", "队列研究"],
    "subjectAreaId": "550e8400-e29b-41d4-a716-446655440003",
    "fileUrl": "https://example.com/dataset-file.csv",
    "dataDictUrl": "https://example.com/data-dict.pdf",
    "approved": true,
    "published": false,
    "searchCount": 0,
    "shareAllData": true,
    "datasetLeader": "数据集负责人",
    "principalInvestigator": "首席研究员",
    "dataCollectionUnit": "数据收集单位",
    "contactPerson": "联系人",
    "contactInfo": "联系方式",
    "demographicFields": "人口统计学字段",
    "outcomeFields": "结局字段",
    "termsAgreementUrl": "https://example.com/terms.pdf",
    "samplingMethod": "抽样方法",
    "versionNumber": "1.0",
    "firstPublishedDate": "2025-01-01T00:00:00Z",
    "currentVersionDate": "2025-01-01T00:00:00Z",
    "parentDatasetId": "550e8400-e29b-41d4-a716-446655440004",
    "institutionId": "550e8400-e29b-41d4-a716-446655440005",
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-12-01T11:00:00Z"
  },
  "timestamp": "2025-12-01T11:00:00Z"
}
```