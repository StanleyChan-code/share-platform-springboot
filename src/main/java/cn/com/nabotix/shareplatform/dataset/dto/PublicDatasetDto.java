package cn.com.nabotix.shareplatform.dataset.dto;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetType;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetVersion;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.user.entity.User;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 公共数据集DTO
 * 用于向公众展示数据集基本信息
 *
 * @author 陈雍文
 */
@Data
public class PublicDatasetDto {
    private UUID id;
    private UUID parentDatasetId;

    private String titleCn;
    private String description;
    private DatasetType type;
    private UserDto provider;

    private Instant startDate;
    private Instant endDate;

    private String datasetLeader;
    private String principalInvestigator;
    private String dataCollectionUnit;

    private Integer recordCount;
    private Integer variableCount;
    private String[] keywords;
    private SubjectAreaDto subjectArea;
    private String category;

    // 新增字段
    private String samplingMethod;
    private String contactPerson;
    private String contactInfo;
    private String demographicFields;
    private String outcomeFields;
    private UUID institutionId;

    private Boolean approved = false;
    private Boolean published = false;
    private Integer searchCount = 0;
    private Boolean shareAllData = false;

    private String versionNumber;
    private Instant firstPublishedDate;
    private Instant currentVersionDate;

    private Instant createdAt;
    private Instant updatedAt;

    // 申请机构ID列表
    private UUID[] applicationInstitutionIds;

    // 添加子数据集字段（随访数据集）
    private PublicDatasetDto[] followUpDatasets;

    // 数据集版本列表
    private DatasetVersionDto[] versions;

    /**
     * 数据集监督者DTO
     * 用于向公众展示数据集监督者的基本信息
     */
    @Data
    static class UserDto {
        private UUID id;
        private String username;
        private String realName;
        private String title;

        public static UserDto fromEntity(User user) {
            if (user == null) {
                return null;
            }
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setRealName(user.getRealName());
            userDto.setTitle(user.getTitle());
            return userDto;
        }
    }


    /**
     * 学科领域DTO
     * 用于向公众展示学科领域的基本信息
     */
    @Data
    static class SubjectAreaDto {
        private UUID id;
        private String name;
        private String nameEn;
        private String description;

        public static SubjectAreaDto fromEntity(ResearchSubject subjectArea) {
            if (subjectArea == null) {
                return null;
            }
            SubjectAreaDto subjectAreaDto = new SubjectAreaDto();
            subjectAreaDto.setId(subjectArea.getId());
            subjectAreaDto.setName(subjectArea.getName());
            subjectAreaDto.setNameEn(subjectArea.getNameEn());
            subjectAreaDto.setDescription(subjectArea.getDescription());
            return subjectAreaDto;
        }
    }

    /**
     * 数据集版本DTO
     * 用于展示数据集的版本信息
     */
    @Data
    public static class DatasetVersionDto {
        private UUID id;
        private UUID datasetId;
        private String versionNumber;
        private Instant createdAt;
        private Instant publishedDate;
        private String description;

        private UUID fileRecordId;
        private UUID dataDictRecordId;
        private UUID termsAgreementRecordId;
        private Boolean approved;
        private String rejectReason;
        private Instant approvedAt;
        private UserDto supervisor;

        public static DatasetVersionDto fromEntity(DatasetVersion version, User supervisor) {
            if (version == null) {
                return null;
            }
            DatasetVersionDto dto = new DatasetVersionDto();
            dto.id = version.getId();
            dto.datasetId = version.getDatasetId();
            dto.versionNumber = version.getVersionNumber();
            dto.createdAt = version.getCreatedAt();
            dto.publishedDate = version.getPublishedDate();
            dto.description = version.getDescription();

            dto.fileRecordId = version.getFileRecordId();
            dto.dataDictRecordId = version.getDataDictRecordId();
            dto.termsAgreementRecordId = version.getTermsAgreementRecordId();
            dto.approved = version.getApproved();
            dto.approvedAt = version.getApprovedAt();
            dto.rejectReason = version.getRejectReason();

            dto.supervisor = UserDto.fromEntity(supervisor);

            return dto;
        }
    }

    public static PublicDatasetDto fromEntity(Dataset dataset, ResearchSubject subjectArea, User provider, List<DatasetVersionDto> datasetVersionDtos) {
        return fromEntity(dataset, subjectArea, provider, datasetVersionDtos.toArray(new DatasetVersionDto[0]));
    }
    public static PublicDatasetDto fromEntity(Dataset dataset, ResearchSubject subjectArea, User provider, DatasetVersionDto[] datasetVersionDtos) {
        if (dataset == null) {
            return null;
        }

        PublicDatasetDto publicDatasetDto = new PublicDatasetDto();
        publicDatasetDto.setId(dataset.getId());
        publicDatasetDto.setParentDatasetId(dataset.getParentDatasetId());
        publicDatasetDto.setTitleCn(dataset.getTitleCn());
        publicDatasetDto.setDescription(dataset.getDescription());
        publicDatasetDto.setType(dataset.getType());
        publicDatasetDto.setCategory(dataset.getCategory());
        publicDatasetDto.setProvider(UserDto.fromEntity(provider));
        publicDatasetDto.setStartDate(dataset.getStartDate());
        publicDatasetDto.setEndDate(dataset.getEndDate());
        publicDatasetDto.setDatasetLeader(dataset.getDatasetLeader());
        publicDatasetDto.setPrincipalInvestigator(dataset.getPrincipalInvestigator());
        publicDatasetDto.setDataCollectionUnit(dataset.getDataCollectionUnit());
        publicDatasetDto.setRecordCount(dataset.getRecordCount());
        publicDatasetDto.setVariableCount(dataset.getVariableCount());
        publicDatasetDto.setKeywords(dataset.getKeywords());
        publicDatasetDto.setSubjectArea(SubjectAreaDto.fromEntity(subjectArea));
        publicDatasetDto.setPublished(dataset.getPublished());
        publicDatasetDto.setSearchCount(dataset.getSearchCount());
        publicDatasetDto.setShareAllData(dataset.getShareAllData());
        publicDatasetDto.setFirstPublishedDate(dataset.getFirstPublishedDate());
        publicDatasetDto.setCurrentVersionDate(dataset.getCurrentVersionDate());
        publicDatasetDto.setCreatedAt(dataset.getCreatedAt());
        publicDatasetDto.setUpdatedAt(dataset.getUpdatedAt());
        publicDatasetDto.setApplicationInstitutionIds(dataset.getApplicationInstitutionIds());

        publicDatasetDto.setSamplingMethod(dataset.getSamplingMethod());
        publicDatasetDto.setContactPerson(dataset.getContactPerson());
        publicDatasetDto.setContactInfo(dataset.getContactInfo());
        publicDatasetDto.setDemographicFields(dataset.getDemographicFields());
        publicDatasetDto.setOutcomeFields(dataset.getOutcomeFields());
        publicDatasetDto.setInstitutionId(dataset.getInstitutionId());

        // 设置版本信息
        publicDatasetDto.setVersions(datasetVersionDtos);

        return publicDatasetDto;
    }
}