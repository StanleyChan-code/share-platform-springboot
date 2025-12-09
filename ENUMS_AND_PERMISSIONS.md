# 枚举类型和权限说明

## 1. 枚举类型

### 1.1 UserAuthority (用户权限)

系统目前定义了四种用户权限：

| 枚举值 | 描述                  |
| --- |---------------------|
| PLATFORM_ADMIN | 平台管理员，拥有最高权限        |
| INSTITUTION_SUPERVISOR | 机构管理员，管理本机构用户和数据    |
| DATASET_UPLOADER | 数据集上传员，可上传数据集       |
| DATASET_APPROVER | 数据集审核员，可审核数据集的上传和申请 |

### 1.2 OutputType (研究成果类型)

用于标识研究成果的类型：

| 枚举值 | 描述 |
| --- | --- |
| PAPER | 论文 |
| PATENT | 专利 |
| PUBLICATION | 出版物 |
| SOFTWARE | 软件 |
| PROJECT | 项目 |
| INVENTION_PATENT | 发明专利 |
| UTILITY_PATENT | 实用新型专利 |
| SOFTWARE_COPYRIGHT | 软件著作权 |
| OTHER_AWARD | 其他奖项 |

### 1.3 ApplicantRole (申请人角色)

在数据集申请中的角色：

| 枚举值 | 描述 |
| --- | --- |
| TEAM_RESEARCHER | 团队研究员 |
| COLLABORATIVE_RESEARCHER | 协同研究人员 |

### 1.4 ApplicationStatus (申请状态)

应用审批流程中的各种状态：

| 枚举值 | 描述 |
| --- | --- |
| SUBMITTED | 已提交 |
| PENDING_PROVIDER_REVIEW | 等待提供方审核 |
| PENDING_INSTITUTION_REVIEW | 等待机构审核 |
| APPROVED | 已批准 |
| DENIED | 已拒绝 |

### 1.5 DatasetType (数据集类型)

数据集的分类：

| 枚举值 | 描述 |
| --- | --- |
| COHORT | 队列研究 |
| CASE_CONTROL | 病例对照研究 |
| CROSS_SECTIONAL | 横断面研究 |
| RCT | 随机对照试验 |
| REGISTRY | 登记研究 |
| BIOBANK | 生物样本库 |
| OMICS | 组学数据 |
| WEARABLE | 可穿戴设备数据 |

### 1.6 InstitutionType (机构类型)

机构的分类：

| 枚举值 | 描述 |
| --- | --- |
| HOSPITAL | 医院 |
| UNIVERSITY | 大学 |
| RESEARCH_CENTER | 研究中心 |
| LAB | 实验室 |
| GOVERNMENT | 政府机构 |
| ENTERPRISE | 企业 |
| OTHER | 其他 |

### 1.7 IdType (证件类型)

用户身份证件类型：

| 枚举值 | 描述 |
| --- | --- |
| NATIONAL_ID | 身份证 |
| PASSPORT | 护照 |
| OTHER | 其他 |

## 2. 权限控制规则

### 2.1 权限层次结构

平台采用四层权限控制模型：
1. 平台管理员(PLATFORM_ADMIN) - 最高权限级别
2. 机构管理员(INSTITUTION_SUPERVISOR) - 中层管理权限
3. 数据集审核员(DATASET_APPROVER) - 数据集的上传和申请审核权限
4. 数据集上传员(DATASET_UPLOADER) - 数据集的上传权限

### 2.2 权限访问规则

#### 2.2.1 平台管理员权限
- 可以管理所有机构、用户和数据集
- 可以授予任何类型的权限给用户
- 可以查看系统内所有资源
- 可以审核任何数据集

#### 2.2.2 机构管理员权限
- 可以管理本机构内的用户和数据集
- 可以授予除平台管理员外的任何权限给用户
- 只能查看本机构内的资源
- 可以审核本机构内的数据集

#### 2.2.3 数据集审核员权限
- 可以审核数据集的上传和申请
- 可以查看公开数据和自己所在机构的数据集

#### 2.2.4 数据集上传员权限
- 只能上传和管理自己的数据集
- 无法管理用户或机构
- 只能查看公开数据和自己上传的数据

## 3. 权限管理API

### 3.1 获取当前用户权限

**接口地址**: `GET /api/users/authorities`

**权限要求**: 任意已认证用户

**说明**: 
- 返回当前登录用户所拥有的所有权限

### 3.2 获取权限列表

**接口地址**: `GET /api/manage/authorities`

**权限要求**: 平台管理员或机构管理员

**说明**: 
- 平台管理员可以获取所有权限列表
- 机构管理员只能获取除平台管理员外的所有权限

### 3.3 获取指定用户权限

**接口地址**: `GET /api/manage/authorities/{userId}`

**权限要求**: 平台管理员或机构管理员

**说明**: 
- 平台管理员可以查看任意用户的权限
- 机构管理员只能查看本机构内用户的权限

### 3.4 更新用户权限

**接口地址**: `PUT /api/manage/authorities`

**权限要求**: 平台管理员或机构管理员

**请求体**:
```json
{
  "userId": "用户UUID",
  "authorities": ["权限枚举值列表"]
}
```

**说明**:
- 平台管理员可以为用户设置任何权限
- 机构管理员不能为用户添加平台管理员权限
- 机构管理员只能操作本机构内的用户