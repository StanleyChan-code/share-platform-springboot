# 枚举类型和权限说明

## 1. 枚举类型

### 1.1 DatasetType (数据集类型)

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

### 1.2 IdType (证件类型)

| 枚举值 | 描述 |
| ------ | ---- |
| national_id | 身份证 |
| passport | 护照 |
| other | 其他 |

### 1.3 EducationLevel (教育程度)

| 枚举值 | 描述 |
| ------ | ---- |
| bachelor | 学士 |
| master | 硕士 |
| phd | 博士 |
| postdoc | 博士后 |
| professor | 教授 |
| other | 其他 |

### 1.4 InstitutionType (机构类型)

| 枚举值 | 描述 |
| ------ | ---- |
| hospital | 医院 |
| university | 大学 |
| research_center | 研究中心 |
| lab | 实验室 |
| government | 政府机构 |
| enterprise | 企业 |
| other | 其他 |

### 1.5 OutputType (成果类型)

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

## 2. 权限说明

系统中有以下几种用户权限角色：

### 2.1 institution_supervisor (机构监管员)
- 拥有注册研究员的所有权限
- 可以管理本机构的数据集和用户
- 可以更新本机构信息

### 2.2 platform_admin (平台管理员)
- 拥有系统最高权限
- 可以管理所有数据集、机构和用户
- 可以创建和管理研究学科

### 2.3 dataset_uploader (数据集上传员)
- 可以上传和管理自己的数据集