package cn.com.nabotix.shareplatform.dataset.dto;

import cn.com.nabotix.shareplatform.dataset.entity.Dataset;
import cn.com.nabotix.shareplatform.dataset.entity.DatasetType;
import cn.com.nabotix.shareplatform.researchsubject.dto.SubjectAreaDto;
import cn.com.nabotix.shareplatform.researchsubject.entity.ResearchSubject;
import cn.com.nabotix.shareplatform.user.dto.UserDto;
import cn.com.nabotix.shareplatform.user.entity.User;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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

    private List<String> keywords;
    private SubjectAreaDto subjectArea;
    private String category;

    // 新增字段
    private String samplingMethod;
    private String contactPerson;
    private String contactInfo;
    private Map<String, Object> demographicFields;
    private Map<String, Object> outcomeFields;
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
    private List<UUID> applicationInstitutionIds;

    // 添加子数据集字段（随访数据集）
    private List<PublicDatasetDto> followUpDatasets;

    // 数据集版本列表
    private List<DatasetVersionDto> versions;

    public static PublicDatasetDto fromEntity(Dataset dataset) {
        if (dataset == null) {
            return null;
        }



        UUID subjectAreaId = dataset.getSubjectAreaId();
        ResearchSubject subjectArea = new ResearchSubject();
        subjectArea.setId(subjectAreaId);

        UUID providerId = dataset.getProviderId();
        User provider = new User();
        provider.setId(providerId);

        return fromEntity(dataset, subjectArea, provider, null);
    }

    public static PublicDatasetDto fromEntity(Dataset dataset, ResearchSubject subjectArea, User provider, List<DatasetVersionDto> datasetVersionDtos) {
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